package ru.pay.server
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Passwords {
    private const val ITERATIONS = 600_000
    fun hash(password: String): String {
        val salt = ByteArray(16).also(SecureRandom()::nextBytes)
        return "$ITERATIONS:${Base64.getEncoder().encodeToString(salt)}:${Base64.getEncoder().encodeToString(derive(password, salt, ITERATIONS))}"
    }
    fun verify(password: String, encoded: String): Boolean {
        val parts = encoded.split(':')
        return MessageDigest.isEqual(Base64.getDecoder().decode(parts[2]), derive(password, Base64.getDecoder().decode(parts[1]), parts[0].toInt()))
    }
    private fun derive(password: String, salt: ByteArray, rounds: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, rounds, 256)
        return try { SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded } finally { spec.clearPassword() }
    }
}
class Tokens(secret: String) {
    init { require(secret.length >= 32) { "JWT_SECRET must contain at least 32 characters" } }
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).withIssuer("eps").withAudience("eps-android").build()
    fun issue(user: UUID, session: UUID, expires: Long): String = JWT.create()
        .withIssuer("eps").withAudience("eps-android").withSubject(user.toString())
        .withJWTId(session.toString()).withIssuedAt(Date()).withExpiresAt(Date(expires)).sign(algorithm)
}
