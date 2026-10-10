package ru.pay.server
import org.junit.jupiter.api.Test
import kotlin.test.*
import java.util.UUID
class SecurityTest {
    @Test fun `passwords salted independently`() {
        val a=Passwords.hash("correct horse battery staple")
        val b=Passwords.hash("correct horse battery staple")
        assertNotEquals(a,b)
        assertTrue(Passwords.verify("correct horse battery staple",a))
        assertFalse(Passwords.verify("incorrect",a))
    }
    @Test fun `token validates issuer audience and signature`() {
        val tokens=Tokens("a".repeat(32)); val user=UUID.randomUUID()
        val jwt=tokens.issue(user,UUID.randomUUID(),System.currentTimeMillis()+60000)
        assertEquals(user.toString(),tokens.verifier.verify(jwt).subject)
        assertFails { Tokens("b".repeat(32)).verifier.verify(jwt) }
    }
    @Test fun `expired token rejected`() {
        val tokens=Tokens("a".repeat(32))
        assertFails { tokens.verifier.verify(tokens.issue(UUID.randomUUID(),UUID.randomUUID(),System.currentTimeMillis()-60000)) }
    }
    @Test fun `short secret rejected`() { assertFailsWith<IllegalArgumentException> { Tokens("weak") } }
}
