package ru.pay.server
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import ru.pay.contracts.*
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

fun main() {
    fun env(key: String)=System.getenv(key) ?: error("Required environment variable: $key")
    val db=Database(env("DB_URL"),env("DB_USER"),env("DB_PASSWORD"))
    val tokens=Tokens(env("JWT_SECRET")); val service=PaymentService(db,tokens)
    if(System.getenv("DEMO_MODE")=="true") runBlocking { service.seedDemo() }
    embeddedServer(Netty,port=System.getenv("PORT")?.toInt() ?: 8080,host="0.0.0.0") {
        module(service,tokens)
        environment.monitor.subscribe(ApplicationStopped) { db.close() }
    }.start(wait=true)
}
fun Application.module(service: PaymentService,tokens: Tokens) {
    install(ContentNegotiation) { json() }
    install(StatusPages) {
        exception<Problem> { call,e -> call.respond(HttpStatusCode.fromValue(e.status),ApiError(e.code,e.message)) }
        exception<IllegalArgumentException> { call,e -> call.respond(HttpStatusCode.BadRequest,ApiError("validation",e.message ?: "Некорректные данные")) }
        exception<BadRequestException> { call,_ -> call.respond(HttpStatusCode.BadRequest,ApiError("bad_request","Некорректный JSON")) }
        exception<Throwable> { call,e ->
            if(e is kotlinx.coroutines.CancellationException) throw e
            this@module.log.error("Request failed: ${call.request.path()}",e)
            call.respond(HttpStatusCode.InternalServerError,ApiError("internal","Ошибка сервера. Повторите позже"))
        }
    }
    install(RateLimit) {
        register(RateLimitName("auth")) { rateLimiter(limit=10,refillPeriod=1.minutes); requestKey { it.request.origin.remoteHost } }
        register(RateLimitName("api")) { rateLimiter(limit=120,refillPeriod=1.minutes); requestKey { it.principal<JWTPrincipal>()?.subject ?: it.request.origin.remoteHost } }
    }
    install(Authentication) {
        jwt("jwt") {
            verifier(tokens.verifier)
            validate { credential ->
                val user=runCatching { UUID.fromString(credential.payload.subject) }.getOrNull()
                val session=runCatching { UUID.fromString(credential.payload.id) }.getOrNull()
                if(user!=null && session!=null && service.active(user,session)) JWTPrincipal(credential.payload) else null
            }
            challenge { _,_ -> call.respond(HttpStatusCode.Unauthorized,ApiError("unauthorized","Войдите в аккаунт")) }
        }
    }
    routing {
        get("/health") { service.db.tx { c -> c.query("SELECT 1") { it.getInt(1) } }; call.respond(Health("ok")) }
        route("/api") {
            rateLimit(RateLimitName("auth")) {
                post("/auth/register") { call.respond(HttpStatusCode.Created,service.register(call.receive())) }
                post("/auth/login") { call.respond(service.login(call.receive())) }
            }
            authenticate("jwt") { rateLimit(RateLimitName("api")) {
                post("/auth/logout") { service.logout(uuid(call.principal<JWTPrincipal>()!!.payload.id)); call.respond(HttpStatusCode.NoContent) }
                get("/profile") { call.respond(service.profile(call.user())) }
                get("/wallet") { call.respond(service.wallet(call.user())) }
                get("/settings") { call.respond(service.settings(call.user())) }
                put("/settings") { call.respond(service.settings(call.user(),call.receive())) }
                get("/cards") { call.respond(service.cards(call.user())) }
                post("/cards") { call.respond(HttpStatusCode.Created,service.addCard(call.user(),call.receive())) }
                delete("/cards/{id}") { service.removeCard(call.user(),call.parameters["id"]!!); call.respond(HttpStatusCode.NoContent) }
                get("/transactions") { call.respond(service.history(call.user(),call.request.queryParameters["q"] ?: "",call.request.queryParameters["direction"] ?: "ALL",call.request.queryParameters["offset"]?.let { it.toIntOrNull() ?: throw Problem(400,"offset","Некорректная страница") } ?: 0)) }
                post("/transfers") {
                    val key=call.request.headers["Idempotency-Key"] ?: throw Problem(400,"missing_key","Необходим ключ перевода")
                    call.respond(service.transfer(call.user(),key,call.receive()))
                }
                get("/search") { call.respond(service.search(call.user(),call.request.queryParameters["q"] ?: "")) }
                get("/search/history") { call.respond(service.searchHistory(call.user())) }
                post("/search/history") { call.respond(service.select(call.user(),call.receive<Selection>().id)) }
                delete("/search/history") { service.clearHistory(call.user()); call.respond(HttpStatusCode.NoContent) }
            } }
        }
    }
}
private fun ApplicationCall.user(): UUID = uuid(principal<JWTPrincipal>()!!.subject!!)
