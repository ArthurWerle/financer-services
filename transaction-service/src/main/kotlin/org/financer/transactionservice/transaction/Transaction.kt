package org.financer.transactionservice.transaction

import org.financer.transactionservice.combinedTransactions.CombinedTransaction
import org.financer.transactionservice.combinedTransactions.RegularCombinedTransaction
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("transactions")
data class Transaction(
    @Id
    val id: Long? = null,
    val categoryId: Long,
    val amount: BigDecimal,
    val typeId: Long,
    val date: LocalDateTime?,
    val description: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class TransactionDTO(
    val id: Long?,
    val categoryId: Long?,
    val categoryName: String,
    val amount: BigDecimal,
    val typeId: Long?,
    val typeName: String?,
    val date: LocalDateTime?,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
): Serializable
