package ru.pay.domain
import org.junit.jupiter.api.Test
import kotlin.test.*
class RulesTest {
    @Test fun `comma decimal is exact`() { assertEquals(12345L,Money.parse("123,45")) }
    @Test fun `one kopeck is accepted`() { assertEquals(1L,Money.parse("0.01")) }
    @Test fun `zero is rejected`() { assertFailsWith<IllegalArgumentException> { Money.parse("0") } }
    @Test fun `negative is rejected`() { assertFailsWith<IllegalArgumentException> { Money.parse("-10") } }
    @Test fun `fractional kopecks are rejected`() { assertFailsWith<IllegalArgumentException> { Money.parse("1.001") } }
    @Test fun `exponent input is rejected`() { assertFailsWith<IllegalArgumentException> { Money.parse("1e3") } }
    @Test fun `transfer limit is enforced`() { assertFailsWith<IllegalArgumentException> { Money.parse("1000000.01") } }
    @Test fun `large input cannot overflow`() { assertFailsWith<IllegalArgumentException> { Money.parse("999999999999999999999") } }
    @Test fun `email is canonicalized`() { assertEquals("alice@example.test",Validation.email(" ALICE@example.test ")) }
    @Test fun `email with whitespace is rejected`() { assertFailsWith<IllegalArgumentException> { Validation.email("a b@example.test") } }
    @Test fun `short password is rejected`() { assertFailsWith<IllegalArgumentException> { Validation.password("123456") } }
    @Test fun `phone needs country prefix`() { assertFailsWith<IllegalArgumentException> { Validation.phone("79990000000") }; Validation.phone("+79990000000") }
    @Test fun `history holds latest ten selections`() { assertEquals((11 downTo 2).toList(),(1..11).fold(emptyList<Int>()) { list,item -> recentSelection(list,item) }) }
    @Test fun `reselection moves existing result to top`() { assertEquals(listOf(2,3,1),recentSelection(listOf(3,2,1),2)) }
    @Test fun `money display never uses floating point`() { assertEquals("123,45 ₽",Money.format(12345)) }
}
