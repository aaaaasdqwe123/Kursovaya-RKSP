package ru.pay.server
import ru.pay.contracts.*
import ru.pay.domain.Money
import ru.pay.domain.Validation
import java.sql.Connection
import java.security.MessageDigest
import java.util.UUID
import java.time.Instant
import java.sql.SQLException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentService(val db: Database, private val tokens: Tokens) {
    private val dummyHash = Passwords.hash("not-a-real-password")
    suspend fun register(body: Registration): Session {
        val email = Validation.email(body.email)
        Validation.password(body.password); Validation.phone(body.phone)
        require(body.name.trim().length in 2..80) { "Имя: от 2 до 80 символов" }
        val hash = withContext(Dispatchers.Default) { Passwords.hash(body.password) }
        return try { db.tx { c ->
            val user = UUID.randomUUID()
            c.exec("INSERT INTO users(id,email,password_hash,name,phone) VALUES (?,?,?,?,?)", user,email,hash,body.name.trim(),body.phone)
            c.exec("INSERT INTO accounts(id,user_id) VALUES (?,?)",UUID.randomUUID(),user)
            session(c,user)
        } } catch(e: SQLException) {
            if(e.sqlState == "23505") throw Problem(409,"user_exists","Email или телефон уже зарегистрирован") else throw e
        }
    }
    suspend fun login(body: Credentials): Session {
        val email = Validation.email(body.email)
        require(body.password.length <= 128) { "Некорректные данные" }
        val row = db.tx { c -> c.query("SELECT id,password_hash FROM users WHERE email=?",email) { it.getObject(1,UUID::class.java) to it.getString(2) }.firstOrNull() }
        val valid = withContext(Dispatchers.Default) { Passwords.verify(body.password,row?.second ?: dummyHash) }
        if(!valid || row == null) throw Problem(401,"invalid_credentials","Неверный email или пароль")
        return db.tx { session(it,row.first) }
    }
    private fun session(c: Connection,user: UUID): Session {
        val id=UUID.randomUUID(); val expires=System.currentTimeMillis()+3_600_000
        c.exec("DELETE FROM sessions WHERE expires_at < now()")
        c.exec("INSERT INTO sessions(id,user_id,expires_at) VALUES (?,?,?)",id,user,java.sql.Timestamp(expires))
        return Session(tokens.issue(user,id,expires),expires)
    }
    suspend fun active(user: UUID,session: UUID): Boolean = db.tx { c ->
        c.query("SELECT 1 FROM sessions WHERE id=? AND user_id=? AND expires_at>now()",session,user) { true }.isNotEmpty()
    }
    suspend fun logout(session: UUID) { db.tx { it.exec("DELETE FROM sessions WHERE id=?",session) } }
    suspend fun profile(user: UUID): Profile = db.tx { c -> c.query("SELECT * FROM users WHERE id=?",user) {
        Profile(it.getString("id"),it.getString("email"),it.getString("name"),it.getString("phone"))
    }.first() }
    private fun account(c: Connection,user: UUID): UUID = c.query("SELECT id FROM accounts WHERE user_id=?",user) { it.getObject(1,UUID::class.java) }.first()
    suspend fun wallet(user: UUID): Wallet = db.tx { c -> c.query("SELECT * FROM accounts WHERE user_id=?",user) { Wallet(it.getString("id"),it.getLong("balance")) }.first() }
    suspend fun settings(user: UUID): Settings = db.tx { c -> c.query("SELECT dark_theme FROM users WHERE id=?",user) { Settings(it.getBoolean(1)) }.first() }
    suspend fun settings(user: UUID,value: Settings): Settings = db.tx { c -> c.exec("UPDATE users SET dark_theme=? WHERE id=?",value.darkTheme,user); value }
    suspend fun cards(user: UUID): List<Card> = db.tx { c -> c.query("SELECT * FROM cards WHERE account_id=? ORDER BY created_at,id",account(c,user)) { Card(it.getString("id"),it.getString("alias"),it.getString("last4")) } }
    suspend fun addCard(user: UUID,body: NewCard): Card = db.tx { c ->
        require(body.alias.trim().length in 1..40) { "Название карты: 1–40 символов" }
        val a=account(c,user)
        c.query("SELECT id FROM accounts WHERE id=? FOR UPDATE",a) { it.getString(1) }
        if(c.query("SELECT count(*) FROM cards WHERE account_id=?",a) { it.getInt(1) }.first() >= 5) throw Problem(409,"card_limit","Не более пяти виртуальных карт")
        val card=Card(UUID.randomUUID().toString(),body.alias.trim(),java.security.SecureRandom().nextInt(10000).toString().padStart(4,'0'))
        c.exec("INSERT INTO cards(id,account_id,alias,last4) VALUES (?,?,?,?)",uuid(card.id),a,card.alias,card.last4)
        card
    }
    suspend fun removeCard(user: UUID,id: String) { db.tx { c ->
        if(c.exec("DELETE FROM cards WHERE id=? AND account_id=?",uuid(id),account(c,user)) == 0) throw Problem(404,"not_found","Карта не найдена")
    } }
    suspend fun transfer(user: UUID,key: String,body: Transfer): Transaction {
        Money.validate(body.amount)
        require(body.note.length<=200 && body.recipient.length in 1..80 && body.kind in listOf("PHONE","CARD","SERVICE")) { "Некорректные данные перевода" }
        val idempotency=uuid(key)
        val raw=kotlinx.serialization.json.Json.encodeToString(Transfer.serializer(),body)
        val hash=MessageDigest.getInstance("SHA-256").digest(raw.toByteArray()).joinToString("") { "%02x".format(it) }
        return db.tx { c ->
            val from=account(c,user)
            // Serialize identical requests before resolving a recipient that could be deleted later.
            c.query("SELECT pg_advisory_xact_lock(?)",(from.toString()+idempotency.toString()).hashCode().toLong()) { true }
            val old=c.query("SELECT id,request_hash FROM transactions WHERE from_account=? AND idempotency_key=?",from,idempotency) { it.getString(1) to it.getString(2) }.firstOrNull()
            if(old!=null) {
                if(old.second != hash) throw Problem(409,"idempotency_conflict","Ключ уже использован для другого перевода")
                return@tx transaction(c,uuid(old.first),from)
            }
            val to=when(body.kind) {
                "PHONE" -> c.query("SELECT a.id FROM accounts a JOIN users u ON u.id=a.user_id WHERE u.phone=?",body.recipient) { it.getObject(1,UUID::class.java) }.firstOrNull()
                "CARD" -> c.query("SELECT account_id FROM cards WHERE id=?",uuid(body.recipient)) { it.getObject(1,UUID::class.java) }.firstOrNull()
                else -> c.query("SELECT account_id FROM services WHERE id=?",body.recipient) { it.getObject(1,UUID::class.java) }.firstOrNull()
            } ?: throw Problem(404,"recipient_missing","Получатель не найден")
            require(from!=to) { "Нельзя перевести на свой кошелёк" }
            // Global ordering prevents deadlocks for concurrent A→B / B→A transfers.
            val balances=c.query("SELECT id,balance FROM accounts WHERE id IN (?,?) ORDER BY id FOR UPDATE",from,to) { it.getObject(1,UUID::class.java) to it.getLong(2) }.toMap()
            if(balances.getValue(from)<body.amount) throw Problem(409,"insufficient_funds","Недостаточно средств")
            if(balances.getValue(to)>Long.MAX_VALUE-body.amount) throw Problem(409,"balance_limit","Превышен лимит баланса получателя")
            c.exec("UPDATE accounts SET balance=balance-? WHERE id=?",body.amount,from)
            c.exec("UPDATE accounts SET balance=balance+? WHERE id=?",body.amount,to)
            val id=UUID.randomUUID()
            c.exec("INSERT INTO transactions(id,from_account,to_account,amount,note,kind,idempotency_key,request_hash) VALUES (?,?,?,?,?,?,?,?)",id,from,to,body.amount,body.note,body.kind,idempotency,hash)
            transaction(c,id,from)
        }
    }
    private val txSelect="""SELECT t.*,u.name AS counterparty FROM transactions t
        JOIN accounts a ON a.id=CASE WHEN t.from_account=? THEN t.to_account ELSE t.from_account END
        JOIN users u ON u.id=a.user_id"""
    private fun mapped(r: java.sql.ResultSet,own: UUID) = Transaction(r.getString("id"),r.getString("from_account"),r.getString("to_account"),r.getLong("amount"),r.getString("note"),r.getTimestamp("created_at").toInstant().toString(),r.getString("kind"),r.getString("counterparty"),r.getString("to_account")==own.toString())
    private fun transaction(c: Connection,id: UUID,own: UUID): Transaction = c.query("$txSelect WHERE t.id=?",own,id) { mapped(it,own) }.first()
    suspend fun history(user: UUID,q: String,direction: String,offset: Int): TransactionPage = db.tx { c ->
        require(q.length<=100 && direction in listOf("ALL","IN","OUT") && offset in 0..100000) { "Некорректный фильтр" }
        val own=account(c,user)
        val rows=c.query("""$txSelect WHERE (t.from_account=? OR t.to_account=?)
            AND (?='ALL' OR (?='IN' AND t.to_account=?) OR (?='OUT' AND t.from_account=?))
            AND (position(lower(?) in lower(t.note))>0 OR position(lower(?) in lower(u.name))>0)
            ORDER BY t.created_at DESC,t.id DESC LIMIT 31 OFFSET ?""",own,own,own,direction,direction,own,direction,own,q,q,offset) { mapped(it,own) }
        TransactionPage(rows.take(30),if(rows.size>30) offset+30 else null)
    }
    private fun searchItems(c: Connection,user: UUID,q: String): List<SearchItem> {
        // Discover people only by exact phone/email; do not expose a public user directory.
        val people=c.query("SELECT id,name,phone FROM users WHERE id<>? AND (phone=? OR email=?) AND email NOT LIKE '%@service.invalid'",user,q,q.lowercase()) {
            SearchItem("user:"+it.getString("id"),it.getString("name"),it.getString("phone"),"PHONE",it.getString("phone"))
        }
        val services=c.query("SELECT id,title FROM services WHERE position(lower(?) in lower(title))>0 ORDER BY title LIMIT 30",q) {
            SearchItem("service:"+it.getString("id"),it.getString("title"),"Оплата услуги","SERVICE",it.getString("id"))
        }
        return people+services
    }
    suspend fun search(user: UUID,q: String): List<SearchItem> = db.tx { c ->
        require(q.trim().length in 1..100) { "Запрос: 1–100 символов" }; searchItems(c,user,q.trim())
    }
    private fun resolve(c: Connection,user: UUID,id: String): SearchItem? = when {
        id.startsWith("user:") -> c.query("SELECT id,name,phone FROM users WHERE id=? AND id<>?",uuid(id.removePrefix("user:")),user) { SearchItem("user:"+it.getString("id"),it.getString("name"),it.getString("phone"),"PHONE",it.getString("phone")) }.firstOrNull()
        id.startsWith("service:") -> c.query("SELECT id,title FROM services WHERE id=?",id.removePrefix("service:")) { SearchItem("service:"+it.getString("id"),it.getString("title"),"Оплата услуги","SERVICE",it.getString("id")) }.firstOrNull()
        else -> null
    }
    private fun historyItems(c: Connection,user: UUID): List<SearchItem> = c.query("SELECT item_id FROM search_history WHERE user_id=? ORDER BY selected_at DESC,item_id",user) { it.getString(1) }.mapNotNull { resolve(c,user,it) }
    suspend fun searchHistory(user: UUID): List<SearchItem> = db.tx { historyItems(it,user) }
    suspend fun select(user: UUID,id: String): List<SearchItem> = db.tx { c ->
        require(id.length<=80) { "Некорректный результат" }
        if(resolve(c,user,id)==null) throw Problem(404,"not_found","Результат больше недоступен")
        c.query("SELECT id FROM users WHERE id=? FOR UPDATE",user) { true }
        c.exec("INSERT INTO search_history(user_id,item_id) VALUES (?,?) ON CONFLICT(user_id,item_id) DO UPDATE SET selected_at=clock_timestamp()",user,id)
        c.exec("DELETE FROM search_history WHERE user_id=? AND item_id NOT IN (SELECT item_id FROM search_history WHERE user_id=? ORDER BY selected_at DESC,item_id LIMIT 10)",user,user)
        historyItems(c,user)
    }
    suspend fun clearHistory(user: UUID) { db.tx { c ->
        c.query("SELECT id FROM users WHERE id=? FOR UPDATE",user) { true }
        c.exec("DELETE FROM search_history WHERE user_id=?",user)
    } }
    /** Explicit local demo provisioning only; no funding API exists. */
    suspend fun seedDemo() {
        val hash=withContext(Dispatchers.Default) { Passwords.hash("DemoPassword123!") }
        db.tx { c ->
            listOf(Triple("alice@example.test","Алиса","+79990000001"),Triple("bob@example.test","Борис","+79990000002"),Triple("mobile@service.invalid","Мобильная связь","+79990000003")).forEach { (email,name,phone) ->
                val id=UUID.randomUUID()
                val inserted=c.exec("INSERT INTO users(id,email,password_hash,name,phone) VALUES (?,?,?,?,?) ON CONFLICT DO NOTHING",id,email,hash,name,phone)
                if(inserted>0) {
                    val a=UUID.randomUUID()
                    c.exec("INSERT INTO accounts(id,user_id,balance) VALUES (?,?,?)",a,id,if(email.endsWith("@service.invalid")) 0L else 100_000L)
                    if(email.endsWith("@service.invalid")) c.exec("INSERT INTO services(id,title,account_id) VALUES ('mobile','Мобильная связь',?)",a)
                }
            }
        }
    }
}


