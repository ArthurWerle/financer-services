package org.financer.transactionservice.recurringTransaction

import org.financer.transactionservice.combinedTransactions.CombinedTransaction
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Table("recurring_transactions")
data class RecurringTransaction(
    @Id
    val id: Long? = null,
    val categoryId: Long,
    val amount: BigDecimal,
    val typeId: Long,
    val description: String?,
    val frequency: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val lastOccurrence: LocalDate?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class RecurringTransactionDto(
    val id: Long? = null,
    val categoryId: Long?,
    val categoryName: String,
    val amount: BigDecimal,
    val typeId: Long?,
    val description: String?,
    val frequency: String?,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val lastOccurrence: LocalDate?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val typeName: String?,
): Serializable

private fun RecurringTransactionDto.toCombinedTransaction() = object : CombinedTransaction {
    override val amount = this@toCombinedTransaction.amount
    override val description: String = this@toCombinedTransaction.description.toString()
    override val sortDate: LocalDateTime = this@toCombinedTransaction.startDate.atStartOfDay()
    override val date = null
    override val typeName: String = this@toCombinedTransaction.typeName.toString()
    override val categoryName = this@toCombinedTransaction.categoryName
    override val id: String = this@toCombinedTransaction.id.toString()
    override val startDate: LocalDateTime? = this@toCombinedTransaction.startDate.atStartOfDay()
    override val endDate: LocalDateTime? = this@toCombinedTransaction.endDate?.atStartOfDay()
    override val frequency = this@toCombinedTransaction.frequency
}