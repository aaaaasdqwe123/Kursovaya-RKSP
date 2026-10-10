package ru.pay.contracts
import kotlinx.serialization.Serializable

@Serializable data class Credentials(val email: String, val password: String)
@Serializable data class Registration(val email: String, val password: String, val name: String, val phone: String)
@Serializable data class Session(val token: String, val expiresAt: Long)
@Serializable data class ApiError(val code: String, val message: String)
@Serializable data class Profile(val id: String, val email: String, val name: String, val phone: String)
@Serializable data class Wallet(val id: String, val balance: Long, val currency: String = "RUB")
@Serializable data class Card(val id: String, val alias: String, val last4: String)
@Serializable data class NewCard(val alias: String)
@Serializable data class Settings(val darkTheme: Boolean = false)
@Serializable data class Transfer(val recipient: String, val amount: Long, val note: String = "", val kind: String = "PHONE")
@Serializable data class Transaction(
    val id: String, val fromAccount: String, val toAccount: String,
    val amount: Long, val note: String, val createdAt: String,
    val kind: String, val counterparty: String, val incoming: Boolean
)
@Serializable data class TransactionPage(val items: List<Transaction>, val nextOffset: Int? = null)
@Serializable data class SearchItem(val id: String, val title: String, val subtitle: String, val kind: String, val recipient: String)
@Serializable data class Selection(val id: String)
@Serializable data class Health(val status: String)
