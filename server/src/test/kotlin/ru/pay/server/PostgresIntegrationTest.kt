package ru.pay.server
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.pay.contracts.*
import kotlinx.coroutines.*
import kotlin.test.*
import java.util.UUID
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*

// Docker required: do not silently skip database tests when infrastructure is missing.
class PostgresIntegrationTest {
    companion object {
        private var postgres: PostgreSQLContainer<Nothing>?=null
        @BeforeAll @JvmStatic fun startDatabase() {
            if(System.getenv("TEST_DB_URL")==null) postgres=PostgreSQLContainer<Nothing>("postgres:16-alpine").also {it.start()}
        }
        @AfterAll @JvmStatic fun stopDatabase() {postgres?.stop()}
    }
    private lateinit var db: Database
    private lateinit var service: PaymentService
    private val tokens=Tokens("integration-secret-32-characters-long")
    @BeforeEach fun setup()=runBlocking {
        db=Database(System.getenv("TEST_DB_URL") ?: postgres!!.jdbcUrl, System.getenv("TEST_DB_USER") ?: postgres!!.username, System.getenv("TEST_DB_PASSWORD") ?: postgres!!.password)
        db.tx { it.exec("TRUNCATE users,accounts,cards,services,transactions,search_history,sessions CASCADE") }
        service=PaymentService(db,tokens); service.seedDemo()
    }
    @AfterEach fun close() { db.close() }
    private suspend fun alice(): UUID = user("alice@example.test")
    private suspend fun user(email: String): UUID = db.tx { c -> c.query("SELECT id FROM users WHERE email=?",email) { it.getObject(1,UUID::class.java) }.first() }
    @Test fun `registration creates zero balance wallet`()=runBlocking {
        service.register(Registration("new@example.test","Password123456","Новый","+79990000009"))
        assertEquals(0L,service.wallet(user("new@example.test")).balance)
    }
    @Test fun `duplicate registration conflicts`()=runBlocking {
        val e=assertFailsWith<Problem> { service.register(Registration("alice@example.test","Password123456","Алиса","+79990000009")) }
        assertEquals(409,e.status)
    }
    @Test fun `wrong password fails`()=runBlocking {
        assertEquals(401,assertFailsWith<Problem> { service.login(Credentials("alice@example.test","wrong")) }.status)
    }
    @Test fun `logout revokes session`()=runBlocking {
        val session=service.login(Credentials("alice@example.test","DemoPassword123!"))
        val jwt=tokens.verifier.verify(session.token)
        assertTrue(service.active(uuid(jwt.subject),uuid(jwt.id)))
        service.logout(uuid(jwt.id))
        assertFalse(service.active(uuid(jwt.subject),uuid(jwt.id)))
    }
    @Test fun `transfer conserves money and produces history`()=runBlocking {
        service.transfer(alice(),UUID.randomUUID().toString(),Transfer("+79990000002",12345,"Обед"))
        assertEquals(87655L,service.wallet(alice()).balance)
        assertEquals(112345L,service.wallet(user("bob@example.test")).balance)
        assertEquals(1,service.history(alice(),"Обед","OUT",0).items.size)
        assertTrue(service.history(alice(),"","IN",0).items.isEmpty())
    }
    @Test fun `insufficient funds rolls back all effects`()=runBlocking {
        assertFailsWith<Problem> { service.transfer(alice(),UUID.randomUUID().toString(),Transfer("+79990000002",100001)) }
        assertEquals(100000L,service.wallet(alice()).balance)
        assertTrue(service.history(alice(),"","ALL",0).items.isEmpty())
    }
    @Test fun `same idempotency key produces one debit`()=runBlocking {
        val key=UUID.randomUUID().toString(); val request=Transfer("+79990000002",1000)
        val a=service.transfer(alice(),key,request); val b=service.transfer(alice(),key,request)
        assertEquals(a.id,b.id); assertEquals(99000L,service.wallet(alice()).balance)
    }
    @Test fun `idempotency payload mismatch conflicts`()=runBlocking {
        val key=UUID.randomUUID().toString()
        service.transfer(alice(),key,Transfer("+79990000002",1000))
        assertEquals(409,assertFailsWith<Problem> { service.transfer(alice(),key,Transfer("+79990000002",2000)) }.status)
    }
    @Test fun `concurrent overspend allows exactly one payment`()=runBlocking {
        val sender=alice()
        val results=(1..2).map { async { runCatching {service.transfer(sender,UUID.randomUUID().toString(),Transfer("+79990000002",75000))} } }.awaitAll()
        assertEquals(1,results.count {it.isSuccess}); assertEquals(25000L,service.wallet(sender).balance)
    }
    @Test fun `concurrent retries produce one transaction`()=runBlocking {
        val sender=alice(); val key=UUID.randomUUID().toString()
        val results=(1..5).map { async {service.transfer(sender,key,Transfer("+79990000002",1000))} }.awaitAll()
        assertEquals(1,results.map {it.id}.distinct().size); assertEquals(99000L,service.wallet(sender).balance)
    }
    @Test fun `self and zero transfers rejected`()=runBlocking {
        assertFailsWith<IllegalArgumentException> { service.transfer(alice(),UUID.randomUUID().toString(),Transfer("+79990000001",100)) }
        assertFailsWith<IllegalArgumentException> { service.transfer(alice(),UUID.randomUUID().toString(),Transfer("+79990000002",0)) }
    }
    @Test fun `card ownership enforced`()=runBlocking {
        val card=service.addCard(alice(),NewCard("Моя карта"))
        assertEquals(404,assertFailsWith<Problem> {service.removeCard(user("bob@example.test"),card.id)}.status)
        assertEquals(1,service.cards(alice()).size)
    }
    @Test fun `search history selection deduplicates and clears`()=runBlocking {
        val item=service.search(alice(),"+79990000002").single()
        service.select(alice(),item.id); service.select(alice(),item.id)
        assertEquals(listOf(item),service.searchHistory(alice()))
        service.clearHistory(alice()); assertTrue(service.searchHistory(alice()).isEmpty())
    }
    @Test fun `history limit enforced by database operation`()=runBlocking {
        val owner=alice()
        db.tx { c ->
            val account=uuid(serviceAccount(c))
            (1..12).forEach { c.exec("INSERT INTO services(id,title,account_id) VALUES (?,?,?)","s$it","Услуга $it",account) }
        }
        (1..12).forEach {service.select(owner,"service:s$it")}
        val result=service.searchHistory(owner)
        assertEquals(10,result.size); assertEquals("service:s12",result.first().id)
    }
    private fun serviceAccount(c: java.sql.Connection)=c.query("SELECT account_id FROM services LIMIT 1") {it.getString(1)}.first()
    @Test fun `settings isolated per user`()=runBlocking {
        service.settings(alice(),Settings(true))
        assertTrue(service.settings(alice()).darkTheme); assertFalse(service.settings(user("bob@example.test")).darkTheme)
    }
    @Test fun `REST authentication and malformed request status`()=testApplication {
        application {module(service,tokens)}
        val c=createClient {install(ContentNegotiation) {json()}}
        assertEquals(HttpStatusCode.Unauthorized,c.get("/api/wallet").status)
        assertEquals(HttpStatusCode.BadRequest,c.post("/api/auth/login") {contentType(ContentType.Application.Json);setBody("{")}.status)
        val session=c.post("/api/auth/login") {contentType(ContentType.Application.Json);setBody(Credentials("alice@example.test","DemoPassword123!"))}.body<Session>()
        assertEquals(100000L,c.get("/api/wallet") {bearerAuth(session.token)}.body<Wallet>().balance)
    }

    @Test fun `card transfer replay survives deleting destination card`()=runBlocking {
        val bob=user("bob@example.test"); val sender=alice()
        val card=service.addCard(bob,NewCard("Получатель"))
        val request=Transfer(card.id,500,"","CARD"); val key=UUID.randomUUID().toString()
        val original=service.transfer(sender,key,request)
        service.removeCard(bob,card.id)
        assertEquals(original.id,service.transfer(sender,key,request).id)
        assertEquals(99500L,service.wallet(sender).balance)
    }
    @Test fun `service payment credits provider account`()=runBlocking {
        service.transfer(alice(),UUID.randomUUID().toString(),Transfer("mobile",25000,"Лицевой счёт 123","SERVICE"))
        assertEquals(75000L,service.wallet(alice()).balance)
        assertEquals(25000L,service.wallet(user("mobile@service.invalid")).balance)
    }
    @Test fun `REST payment uses authenticated sender and requires key`()=testApplication {
        application {module(service,tokens)}
        val c=createClient {install(ContentNegotiation) {json()}}
        val session=c.post("/api/auth/login") {contentType(ContentType.Application.Json);setBody(Credentials("alice@example.test","DemoPassword123!"))}.body<Session>()
        val request=Transfer("+79990000002",1500)
        assertEquals(HttpStatusCode.BadRequest,c.post("/api/transfers") {bearerAuth(session.token);contentType(ContentType.Application.Json);setBody(request)}.status)
        val key=UUID.randomUUID().toString()
        val tx=c.post("/api/transfers") {bearerAuth(session.token);header("Idempotency-Key",key);contentType(ContentType.Application.Json);setBody(request)}.body<Transaction>()
        assertEquals(1500L,tx.amount)
        assertEquals(98500L,c.get("/api/wallet") {bearerAuth(session.token)}.body<Wallet>().balance)
        assertEquals(1,c.get("/api/transactions?direction=OUT") {bearerAuth(session.token)}.body<TransactionPage>().items.size)
    }
}
