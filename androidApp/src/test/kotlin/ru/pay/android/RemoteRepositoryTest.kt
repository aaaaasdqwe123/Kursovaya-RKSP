package ru.pay.android
import io.mockk.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import ru.pay.android.data.*
import ru.pay.domain.Transfer
import ru.pay.domain.SessionExpired

class RemoteRepositoryTest {
    private val values=mutableMapOf<String,String>("owner" to "alice@example.test","token" to "test-token")
    private val store=mockk<SecureStore>()
    private val pending="pending:alice@example.test"
    private val request=Transfer("+79990000002",100)
    private val receipt="""{"id":"tx","fromAccount":"a","toAccount":"b","amount":100,"note":"","createdAt":"2026-10-06T00:00:00Z","kind":"PHONE","counterparty":"Борис","incoming":false}"""
    @Before fun setup() {
        every {store.get(any())} answers {values[firstArg()]}
        every {store.set(any(),any())} answers {
            val key=firstArg<String>();val value=secondArg<String?>()
            if(value==null) values.remove(key) else values[key]=value
            Unit
        }
    }
    private fun client(handler: MockRequestHandler)=HttpClient(MockEngine(handler)) {install(ContentNegotiation) {json()}}
    @Test fun `payment persists key before sending and clears after receipt`()=runTest {
        val http=client { req ->
            assertNotNull(values[pending]);assertEquals(values[pending]!!.substringBefore('\n'),req.headers["Idempotency-Key"])
            respond(receipt,HttpStatusCode.OK,headersOf(HttpHeaders.ContentType,"application/json"))
        }
        try {assertEquals("tx",RemoteRepository(http,store).transfer(request).id);assertNull(values[pending])} finally {http.close()}
    }
    @Test fun `timeout and recreated repository reuse payment key`()=runTest {
        var firstKey: String?=null
        val failing=client {req -> firstKey=req.headers["Idempotency-Key"];throw java.io.IOException("timeout")}
        try {assertTrue(runCatching {RemoteRepository(failing,store).transfer(request)}.isFailure)} finally {failing.close()}
        assertNotNull(values[pending])
        val retry=client {req -> assertEquals(firstKey,req.headers["Idempotency-Key"]);respond(receipt,HttpStatusCode.OK,headersOf(HttpHeaders.ContentType,"application/json"))}
        try {val repo=RemoteRepository(retry,store);assertEquals(request,repo.pendingTransfer());repo.transfer(request)} finally {retry.close()}
        assertNull(values[pending])
    }
    @Test fun `uncertain payment prevents different request`()=runTest {
        val http=client {throw java.io.IOException("timeout")}
        try {
            val repo=RemoteRepository(http,store)
            runCatching {repo.transfer(request)}
            assertTrue(runCatching {repo.transfer(request.copy(amount=200))}.exceptionOrNull() is IllegalArgumentException)
            assertEquals(request,repo.pendingTransfer())
        } finally {http.close()}
    }
    @Test fun `definitive insufficient funds rejection clears pending`()=runTest {
        val http=client {respond("""{"code":"insufficient_funds","message":"Недостаточно средств"}""",HttpStatusCode.Conflict,headersOf(HttpHeaders.ContentType,"application/json"))}
        try {assertTrue(runCatching {RemoteRepository(http,store).transfer(request)}.exceptionOrNull() is ApiFailure);assertNull(values[pending])} finally {http.close()}
    }
    @Test fun `expired session retains pending payment for next login`()=runTest {
        val http=client {respond("",HttpStatusCode.Unauthorized)}
        try {assertTrue(runCatching {RemoteRepository(http,store).transfer(request)}.exceptionOrNull() is SessionExpired);assertNotNull(values[pending]);assertNull(values["token"])} finally {http.close()}
    }
}
