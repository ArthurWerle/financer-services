package org.financer.transactionservice.recurringTransaction

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
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
    val date: LocalDateTime,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val lastOccurrence: LocalDate?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class RecurringTransactionDto(
    val id: Long? = null,
    val categoryId: Long,
    val categoryName: String,
    val amount: BigDecimal,
    val typeId: Long,
    val description: String?,
    val frequency: String,
    val date: LocalDateTime,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val lastOccurrence: LocalDate?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val typeName: String?,
)