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
    val categoryId: Long?,
    val amount: BigDecimal,
    val typeId: Long,
    val typeName: String,
    val description: String?,
    val frequency: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val lastOccurrence: LocalDate?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)