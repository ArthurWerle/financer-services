package org.financer.transactionservice.transaction

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("transactions")
data class Transaction(
    @Id
    val id: Long? = null,
    val categoryId: Long?,
    val amount: BigDecimal,
    val typeId: Long,
    val typeName: String,
    val date: LocalDateTime?,
    val description: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)