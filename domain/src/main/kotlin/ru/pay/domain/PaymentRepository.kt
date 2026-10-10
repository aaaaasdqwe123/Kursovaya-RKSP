package ru.pay.domain
interface PaymentRepository {
    fun hasSession(): Boolean
    suspend fun login(email: String,password: String)
    suspend fun register(email: String,password: String,name: String,phone: String)
    suspend fun logout()
    suspend fun profile(): Profile
    suspend fun wallet(): Wallet
    suspend fun cards(): List<Card>
    suspend fun addCard(alias: String): Card
    suspend fun removeCard(id: String)
    suspend fun transactions(query: String,direction: String,offset: Int): TransactionPage
    suspend fun transfer(request: Transfer): Transaction
    fun pendingTransfer(): Transfer?
    suspend fun search(query: String): List<SearchItem>
    suspend fun history(): List<SearchItem>
    suspend fun select(id: String): List<SearchItem>
    suspend fun clearHistory()
    suspend fun settings(): Settings
    suspend fun setDarkTheme(dark: Boolean): Settings
}
class SendPayment(private val repository: PaymentRepository) {
    suspend operator fun invoke(request: Transfer): Transaction {
        Money.validate(request.amount)
        require(request.recipient.isNotBlank()) { "РЈРєР°Р¶РёС‚Рµ РїРѕР»СѓС‡Р°С‚РµР»СЏ" }
        return repository.transfer(request)
    }
}
class SearchRecipients(private val repository: PaymentRepository) {
    suspend operator fun invoke(query: String): List<SearchItem> {
        require(query.trim().length in 1..100) { "Р—Р°РїСЂРѕСЃ: 1вЂ“100 СЃРёРјРІРѕР»РѕРІ" }
        return repository.search(query.trim())
    }
}

class SessionExpired: Exception("Сессия истекла. Войдите снова")
