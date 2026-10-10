package ru.pay.android.presentation
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.pay.domain.*
import javax.inject.Inject

data class UiState(
    val authenticated: Boolean=false, val busy: Boolean=false, val error: String?=null,
    val message: String?=null, val profile: Profile?=null, val wallet: Wallet?=null,
    val cards: List<Card> = emptyList(), val transactions: List<Transaction> = emptyList(),
    val nextOffset: Int?=null, val dark: Boolean=false, val pending: Transfer?=null
)
data class SearchState(
    val loading: Boolean=false, val submitted: Boolean=false, val error: String?=null,
    val results: List<SearchItem> = emptyList(), val history: List<SearchItem> = emptyList()
)
@HiltViewModel class PaymentViewModel @Inject constructor(
    private val repo: PaymentRepository, private val sendPayment: SendPayment,
    private val searchRecipients: SearchRecipients, private val saved: SavedStateHandle
): ViewModel() {
    private val mutable=MutableStateFlow(UiState(authenticated=repo.hasSession()))
    val ui=mutable.asStateFlow()
    private val searchMutable=MutableStateFlow(SearchState())
    val search=searchMutable.asStateFlow()
    val query=saved.getStateFlow("query", "")
    val tab=saved.getStateFlow("tab", "Главная")
    val recipient=saved.getStateFlow("recipient", "")
    val amount=saved.getStateFlow("amount", "")
    val note=saved.getStateFlow("note", "")
    val kind=saved.getStateFlow("kind", "PHONE")
    val filter=saved.getStateFlow("filter", "ALL")
    val transactionQuery=saved.getStateFlow("transactionQuery", "")
    private var searchJob: Job?=null
    private var searchGeneration=0L
    init { if(ui.value.authenticated) refresh() }
    fun navigate(value: String) { saved["tab"]=value }
    fun field(key: String,value: String) { saved[key]=value }
    fun dismiss() { mutable.update { it.copy(error=null,message=null) } }
    private fun failure(e: Throwable) {
        if(e is CancellationException) throw e
        if(e is SessionExpired) {
            searchJob?.cancel(); searchMutable.value=SearchState()
            mutable.value=UiState(error=e.message)
        } else mutable.update { it.copy(error=if(e is java.io.IOException) "Нет соединения с сервером" else e.message ?: "Не удалось выполнить запрос") }
    }
    private fun action(block: suspend ()->Unit) {
        if(ui.value.busy) return
        mutable.update { it.copy(busy=true,error=null,message=null) }
        viewModelScope.launch {
            try { block() } catch(e: Exception) { failure(e) }
            finally { mutable.update { it.copy(busy=false) } }
        }
    }
    fun auth(register: Boolean,email: String,password: String,name: String,phone: String) = action {
        Validation.email(email)
        if(register) { Validation.password(password); Validation.phone(phone); repo.register(email,password,name,phone) }
        else repo.login(email,password)
        mutable.value=UiState(authenticated=true,busy=true)
        saved["tab"]="Главная"; saved["query"]=""; searchMutable.value=SearchState()
        listOf("recipient","amount","note","transactionQuery","lastQuery").forEach { saved[it]="" }
        saved["kind"]="PHONE"; saved["filter"]="ALL"
        loadDashboard()
    }
    private suspend fun loadDashboard() {
        val profile=repo.profile(); val wallet=repo.wallet(); val settings=repo.settings(); val pending=repo.pendingTransfer()
        mutable.update { it.copy(profile=profile,wallet=wallet,dark=settings.darkTheme,pending=pending) }
        if(pending!=null) {
            saved["recipient"]=pending.recipient; saved["amount"]=java.math.BigDecimal.valueOf(pending.amount,2).toPlainString()
            saved["note"]=pending.note; saved["kind"]=pending.kind
        }
        val cards=repo.cards(); mutable.update { it.copy(cards=cards) }
        loadTransactions()
        val history=repo.history(); searchMutable.update { it.copy(history=history) }
    }
    fun refresh()=action { loadDashboard() }
    private suspend fun loadTransactions(offset: Int=0) {
        val q=if(offset==0) transactionQuery.value else saved.get<String>("appliedQuery") ?: ""
        val direction=if(offset==0) filter.value else saved.get<String>("appliedFilter") ?: "ALL"
        val page=repo.transactions(q,direction,offset)
        saved["appliedQuery"]=q; saved["appliedFilter"]=direction
        mutable.update { it.copy(transactions=if(offset==0) page.items else (it.transactions+page.items).distinctBy { t->t.id },nextOffset=page.nextOffset) }
    }
    fun transactions()=action { loadTransactions() }
    fun more()=action { ui.value.nextOffset?.let { loadTransactions(it) } }
    fun dark(value: Boolean)=action { val result=repo.setDarkTheme(value); mutable.update { it.copy(dark=result.darkTheme) } }
    fun addCard(alias: String)=action { repo.addCard(alias); val cards=repo.cards(); mutable.update { it.copy(cards=cards) } }
    fun deleteCard(id: String)=action { repo.removeCard(id); val cards=repo.cards(); mutable.update { it.copy(cards=cards) } }
    fun pay()=action {
        try {
            val request=ui.value.pending ?: Transfer(recipient.value.trim(),Money.parse(amount.value),note.value,kind.value)
            val receipt=sendPayment(request)
            saved["amount"]=""; saved["note"]=""
            mutable.update { it.copy(pending=null,message="Перевод выполнен. Чек: ${receipt.id}") }
            val wallet=repo.wallet(); mutable.update { it.copy(wallet=wallet) }; loadTransactions()
        } finally { mutable.update { it.copy(pending=repo.pendingTransfer()) } }
    }
    fun logout()=action {
        repo.logout(); searchJob?.cancel(); searchGeneration++
        searchMutable.value=SearchState(); mutable.value=UiState()
        listOf("query","recipient","amount","note","transactionQuery","lastQuery").forEach { saved[it]="" }
        saved["tab"]="Главная"; saved["kind"]="PHONE"; saved["filter"]="ALL"
    }
    fun changeQuery(value: String) {
        saved["query"]=value.take(100); searchJob?.cancel(); searchGeneration++
        searchMutable.update { it.copy(loading=false,submitted=false,error=null,results=emptyList()) }
    }
    fun submitSearch(retry: Boolean=false) {
        val q=if(retry) saved.get<String>("lastQuery") ?: query.value else query.value
        if(q.isBlank()) return
        saved["query"]=q; saved["lastQuery"]=q
        searchJob?.cancel(); val generation=++searchGeneration
        searchMutable.update { it.copy(loading=true,error=null,submitted=true,results=emptyList()) }
        searchJob=viewModelScope.launch {
            try {
                val result=searchRecipients(q)
                if(generation==searchGeneration) searchMutable.update { it.copy(results=result,loading=false) }
            } catch(e: Exception) {
                if(e is CancellationException) throw e
                if(e is SessionExpired) failure(e)
                else if(generation==searchGeneration) searchMutable.update { it.copy(loading=false,error="Не удалось выполнить поиск. Проверьте соединение") }
            }
        }
    }
    fun history()=action { val history=repo.history(); searchMutable.update { it.copy(history=history) } }
    fun clearHistory()=action { repo.clearHistory(); searchMutable.update { it.copy(history=emptyList()) } }
    fun select(item: SearchItem)=action {
        val history=repo.select(item.id); searchMutable.update { it.copy(history=history) }
        if(ui.value.pending==null) { saved["recipient"]=item.recipient; saved["kind"]=item.kind }
        saved["tab"]="Перевод"
    }
}
