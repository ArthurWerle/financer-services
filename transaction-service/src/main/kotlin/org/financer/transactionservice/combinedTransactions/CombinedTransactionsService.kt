package org.financer.transactionservice.combinedTransactions

import org.financer.transactionservice.recurringTransaction.RecurringTransactionDto
import org.financer.transactionservice.recurringTransaction.RecurringTransactionService
import org.financer.transactionservice.transaction.TransactionDTO
import org.financer.transactionservice.transaction.TransactionService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

const val TRANSACTIONS_LIMIT = 5

interface CombinedTransaction {
    val amount: BigDecimal
    val description: String
    val sortDate: LocalDateTime
    val typeName: String
    val categoryName: String
}

private fun TransactionDTO.toCombinedTransaction() = object : CombinedTransaction {
    override val amount = this@toCombinedTransaction.amount
    override val description: String = this@toCombinedTransaction.description.toString()
    override val sortDate: LocalDateTime = this@toCombinedTransaction.date ?: LocalDateTime.now()
    override val typeName: String = this@toCombinedTransaction.typeName.toString()
    override val categoryName = this@toCombinedTransaction.categoryName
}

private fun RecurringTransactionDto.toCombinedTransaction() = object : CombinedTransaction {
    override val amount = this@toCombinedTransaction.amount
    override val description: String = this@toCombinedTransaction.description.toString()
    override val sortDate: LocalDateTime = this@toCombinedTransaction.startDate.atStartOfDay()
    override val typeName: String = this@toCombinedTransaction.typeName.toString()
    override val categoryName = this@toCombinedTransaction.categoryName
}

@Service
class CombinedTransactionService(
    private val transactionService: TransactionService,
    private val recurringTransactionService: RecurringTransactionService
) {
    fun getLatestCombinedTransactions(): List<CombinedTransaction> {
        val regularTransactions = transactionService
            .findLastTransactionsWithTypeAndCategory(TRANSACTIONS_LIMIT)
            .map { it.toCombinedTransaction() }

        val recurringTransactions = recurringTransactionService
            .findLastRecurringTransactionsWithTypeAndCategory(TRANSACTIONS_LIMIT)
            .map { it.toCombinedTransaction() }

        return (regularTransactions + recurringTransactions)
            .sortedByDescending { it.sortDate }
            .take(TRANSACTIONS_LIMIT)
    }

    fun getBiggestCombinedTransactions(): List<CombinedTransaction> {
        val regularTransactions = transactionService
            .findBiggestTransactionsWithTypeAndCategory(TRANSACTIONS_LIMIT)
            .map { it.toCombinedTransaction() }

        val recurringTransactions = recurringTransactionService
            .findBiggestRecurringTransactionsWithTypeAndCategory(TRANSACTIONS_LIMIT)
            .map { it.toCombinedTransaction() }

        return (regularTransactions + recurringTransactions)
            .sortedBy { it.amount }
            .take(TRANSACTIONS_LIMIT)
    }
}
