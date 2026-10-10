package ru.pay.server
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID

class Database(url: String, user: String, password: String) : AutoCloseable {
    private val pool = HikariDataSource(HikariConfig().apply {
        jdbcUrl = url; username = user; this.password = password
        maximumPoolSize = 10; connectionTimeout = 5000
    })
    init { Flyway.configure().dataSource(pool).load().migrate() }
    suspend fun <T> tx(block: (Connection) -> T): T = withContext(Dispatchers.IO) {
        pool.connection.use { c ->
            c.autoCommit = false
            try { val result = block(c); c.commit(); result }
            catch (e: Throwable) { c.rollback(); throw e }
        }
    }
    override fun close() = pool.close()
}
fun PreparedStatement.bind(args: Array<out Any?>) { args.forEachIndexed { i, a -> setObject(i + 1, a) } }
fun Connection.exec(sql: String, vararg args: Any?): Int = prepareStatement(sql).use { it.bind(args); it.executeUpdate() }
fun <T> Connection.query(sql: String, vararg args: Any?, map: (ResultSet) -> T): List<T> =
    prepareStatement(sql).use { s -> s.bind(args); s.executeQuery().use { r -> buildList { while(r.next()) add(map(r)) } } }
fun uuid(s: String): UUID = try { UUID.fromString(s) } catch (_: IllegalArgumentException) { throw Problem(400,"invalid_id","Некорректный идентификатор") }
class Problem(val status: Int, val code: String, override val message: String): RuntimeException(message)
