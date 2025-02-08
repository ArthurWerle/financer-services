package org.financer.transactionservice.combinedTransactions

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

interface CombinedTransaction {
    val amount: BigDecimal
    val description: String
    val sortDate: LocalDateTime
    val date: LocalDateTime?
    val typeName: String
    val categoryName: String?
    val id: String
    val startDate: LocalDateTime?
    val endDate: LocalDateTime?
    val frequency: String?
}

data class RegularCombinedTransaction(
    override val amount: BigDecimal,
    override val description: String,
    override val sortDate: LocalDateTime,
    override val date: LocalDateTime,
    override val typeName: String,
    override val categoryName: String?,
    override val id: String,
    override val startDate: LocalDateTime? = null,
    override val endDate: LocalDateTime? = null,
    override val frequency: String? = null
) : CombinedTransaction, Serializable

data class RecurringCombinedTransaction(
    override val amount: BigDecimal,
    override val description: String,
    override val sortDate: LocalDateTime,
    override val date: LocalDateTime?,
    override val typeName: String,
    override val categoryName: String?,
    override val id: String,
    override val startDate: LocalDateTime?,
    override val endDate: LocalDateTime?,
    override val frequency: String?
) : CombinedTransaction, Serializable
