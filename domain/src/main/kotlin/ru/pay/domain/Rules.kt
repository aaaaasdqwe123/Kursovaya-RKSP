package ru.pay.domain
import java.math.BigDecimal

object Money {
    const val MAX_TRANSFER = 100_000_000L
    fun parse(input: String): Long {
        require(Regex("[0-9]{1,9}([.,][0-9]{1,2})?").matches(input.trim())) { "Введите сумму с точностью до копеек" }
        val minor = BigDecimal(input.trim().replace(',', '.')).movePointRight(2).longValueExact()
        validate(minor)
        return minor
    }
    fun validate(amount: Long) { require(amount in 1..MAX_TRANSFER) { "Сумма от 0,01 до 1 000 000 ₽" } }
    fun format(amount: Long): String = BigDecimal.valueOf(amount, 2).toPlainString().replace('.', ',') + " ₽"
}
object Validation {
    fun email(value: String): String = value.trim().lowercase().also {
        require(it.length <= 254 && Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+").matches(it)) { "Некорректный email" }
    }
    fun password(value: String) { require(value.length in 12..128) { "Пароль: от 12 до 128 символов" } }
    fun phone(value: String) { require(Regex("\\+[1-9][0-9]{9,14}").matches(value)) { "Телефон в международном формате: +79991234567" } }
}
/** MRU selection history; repeated selection moves an item to the front. */
fun <T> recentSelection(items: List<T>, selected: T): List<T> = (listOf(selected) + items.filterNot { it == selected }).take(10)
