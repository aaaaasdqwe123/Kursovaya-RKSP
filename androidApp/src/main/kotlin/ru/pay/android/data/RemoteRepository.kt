package ru.pay.android.data
import ru.pay.domain.*
import ru.pay.android.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

class ApiFailure(val status: Int,override val message: String): Exception(message)
@Singleton class RemoteRepository @Inject constructor(private val client: HttpClient,private val store: SecureStore): PaymentRepository {
    private val json=Json { ignoreUnknownKeys=true }
    private val paymentMutex=Mutex()
    override fun hasSession()=store.get("token")!=null
    private suspend inline fun <reified T> request(path: String, method: HttpMethod=HttpMethod.Get, body: Any?=null, key: String?=null, query: Map<String,String> = emptyMap()): T {
        val response=client.request(BuildConfig.API_URL+path) {
            this.method=method
            store.get("token")?.let { bearerAuth(it) }
            contentType(ContentType.Application.Json)
            query.forEach { (k,v)->parameter(k,v) }
            if(key!=null) header("Idempotency-Key",key)
            if(body!=null) setBody(body)
        }
        if(response.status.value !in 200..299) {
            if(response.status.value==401 && !path.startsWith("auth/")) { store.set("token",null); throw SessionExpired() }
            val message=runCatching { response.body<ru.pay.contracts.ApiError>().message }.getOrDefault("Ошибка сервера (${response.status.value}). Повторите запрос")
            throw ApiFailure(response.status.value,message)
        }
        return if(T::class==Unit::class) Unit as T else response.body()
    }
    override suspend fun login(email: String,password: String) = withContext(Dispatchers.IO) {
        val s=request<ru.pay.contracts.Session>("auth/login",HttpMethod.Post,ru.pay.contracts.Credentials(email,password))
        store.set("owner",email.trim().lowercase()); store.set("token",s.token)
    }
    override suspend fun register(email: String,password: String,name: String,phone: String) = withContext(Dispatchers.IO) {
        val s=request<ru.pay.contracts.Session>("auth/register",HttpMethod.Post,ru.pay.contracts.Registration(email,password,name,phone))
        store.set("owner",email.trim().lowercase()); store.set("token",s.token)
    }
    override suspend fun logout() = withContext(Dispatchers.IO) {
        try { request<Unit>("auth/logout",HttpMethod.Post) }
        catch(e: ApiFailure) { if(e.status!=401) throw e }
        store.set("token",null)
    }
    override suspend fun profile()=request<ru.pay.contracts.Profile>("profile").let { Profile(it.id,it.email,it.name,it.phone) }
    override suspend fun wallet()=request<ru.pay.contracts.Wallet>("wallet").let { Wallet(it.id,it.balance,it.currency) }
    override suspend fun settings()=request<ru.pay.contracts.Settings>("settings").let { Settings(it.darkTheme) }
    override suspend fun setDarkTheme(dark: Boolean)=request<ru.pay.contracts.Settings>("settings",HttpMethod.Put,ru.pay.contracts.Settings(dark)).let { Settings(it.darkTheme) }
    override suspend fun cards()=request<List<ru.pay.contracts.Card>>("cards").map { Card(it.id,it.alias,it.last4) }
    override suspend fun addCard(alias: String)=request<ru.pay.contracts.Card>("cards",HttpMethod.Post,ru.pay.contracts.NewCard(alias)).let { Card(it.id,it.alias,it.last4) }
    override suspend fun removeCard(id: String)=request<Unit>("cards/$id",HttpMethod.Delete)
    override suspend fun transactions(query: String,direction: String,offset: Int)=request<ru.pay.contracts.TransactionPage>("transactions",query=mapOf("q" to query,"direction" to direction,"offset" to offset.toString())).let { TransactionPage(it.items.map(::mapTransaction),it.nextOffset) }
    private fun pendingKey()="pending:"+requireNotNull(store.get("owner"))
    override fun pendingTransfer(): Transfer? = store.get("owner")?.let {
        store.get(pendingKey())?.substringAfter('\n')?.let { json.decodeFromString(ru.pay.contracts.Transfer.serializer(),it) }?.let { Transfer(it.recipient,it.amount,it.note,it.kind) }
    }
    override suspend fun transfer(request: Transfer): Transaction = withContext(Dispatchers.IO) { paymentMutex.withLock {
        val data=ru.pay.contracts.Transfer(request.recipient,request.amount,request.note,request.kind)
        val serialized=json.encodeToString(ru.pay.contracts.Transfer.serializer(),data)
        val pending=store.get(pendingKey())
        if(pending!=null) require(pending.substringAfter('\n')==serialized) { "Сначала завершите предыдущий перевод" }
        val key=pending?.substringBefore('\n') ?: java.util.UUID.randomUUID().toString()
        // Persist BEFORE sending. Keep the same key across rotation, timeout, process death and re-login.
        store.set(pendingKey(),key+"\n"+serialized)
        try {
            val result=request<ru.pay.contracts.Transaction>("transfers",HttpMethod.Post,data,key)
            store.set(pendingKey(),null)
            mapTransaction(result)
        } catch(e: ApiFailure) {
            if(e.status in listOf(400,404,409,422)) store.set(pendingKey(),null)
            throw e
        }
    } }
    override suspend fun search(query: String)=request<List<ru.pay.contracts.SearchItem>>("search",query=mapOf("q" to query)).map(::mapSearch)
    override suspend fun history()=request<List<ru.pay.contracts.SearchItem>>("search/history").map(::mapSearch)
    override suspend fun select(id: String)=request<List<ru.pay.contracts.SearchItem>>("search/history",HttpMethod.Post,ru.pay.contracts.Selection(id)).map(::mapSearch)
    override suspend fun clearHistory()=request<Unit>("search/history",HttpMethod.Delete)
    private fun mapSearch(it: ru.pay.contracts.SearchItem)=SearchItem(it.id,it.title,it.subtitle,it.kind,it.recipient)
    private fun mapTransaction(it: ru.pay.contracts.Transaction)=Transaction(it.id,it.fromAccount,it.toAccount,it.amount,it.note,it.createdAt,it.kind,it.counterparty,it.incoming)
}
