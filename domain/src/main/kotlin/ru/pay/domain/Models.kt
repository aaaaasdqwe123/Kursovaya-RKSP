package ru.pay.domain


data class Credentials(val email: String, val password: String)
data class Registration(val email: String, val password: String, val name: String, val phone: String)
data class Session(val token: String, val expiresAt: Long)
data class ApiError(val code: String, val message: String)
data class Profile(val id: String, val email: String, val name: String, val phone: String)
data class Wallet(val id: String, val balance: Long, val currency: String = "RUB")
data class Card(val id: String, val alias: String, val last4: String)
data class NewCard(val alias: String)
data class Settings(val darkTheme: Boolean = false)
data class Transfer(val recipient: String, val amount: Long, val note: String = "", val kind: String = "PHONE")
data class Transaction(
    val id: String, val fromAccount: String, val toAccount: String,
    val amount: Long, val note: String, val createdAt: String,
    val kind: String, val counterparty: String, val incoming: Boolean
)
data class TransactionPage(val items: List<Transaction>, val nextOffset: Int? = null)
data class SearchItem(val id: String, val title: String, val subtitle: String, val kind: String, val recipient: String)
data class Selection(val id: String)
data class Health(val status: String)
