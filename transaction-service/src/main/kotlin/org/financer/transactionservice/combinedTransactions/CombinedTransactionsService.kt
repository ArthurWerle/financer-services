package org.financer.transactionservice.combinedTransactions

import org.financer.transactionservice.recurringTransaction.RecurringTransactionDto
import org.financer.transactionservice.recurringTransaction.RecurringTransactionService
import org.financer.transactionservice.transaction.TransactionController
import org.financer.transactionservice.transaction.TransactionDTO
import org.financer.transactionservice.transaction.TransactionService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

const val TRANSACTIONS_LIMIT = 5

interface CombinedTransaction {
    val amount: BigDecimal
    val description: String
    val sortDate: LocalDateTime
    val typeName: String
    val categoryName: String
    val id: String
    val date: LocalDateTime?
    val startDate: LocalDateTime?
    val endDate: LocalDateTime?
    val frequency: String?
}

private fun TransactionDTO.toCombinedTransaction() = object : CombinedTransaction {
    override val amount = this@toCombinedTransaction.amount
    override val description: String = this@toCombinedTransaction.description.toString()
    override val sortDate: LocalDateTime = this@toCombinedTransaction.date ?: LocalDateTime.now()
    override val date: LocalDateTime = this@toCombinedTransaction.date ?: LocalDateTime.now()
    override val typeName: String = this@toCombinedTransaction.typeName.toString()
    override val categoryName = this@toCombinedTransaction.categoryName
    override val id: String = this@toCombinedTransaction.id.toString()
    override val startDate = null
    override val endDate = null
    override val frequency = null
}

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

@Service
class CombinedTransactionService(
    private val transactionService: TransactionService,
    private val recurringTransactionService: RecurringTransactionService
) {
    fun getLatestCombinedTransactions(limit: Int?): List<CombinedTransaction> {
        val transactionsLimit = limit ?: TRANSACTIONS_LIMIT

        val regularTransactions = transactionService
            .findLastTransactionsWithTypeAndCategory(transactionsLimit)
            .map { it.toCombinedTransaction() }

        val recurringTransactions = recurringTransactionService
            .findLastRecurringTransactionsWithTypeAndCategory(transactionsLimit)
            .map { it.toCombinedTransaction() }

        return (regularTransactions + recurringTransactions)
            .sortedByDescending { it.sortDate }
            .take(transactionsLimit)
    }

    fun getBiggestCombinedTransactions(limit: Int?): List<CombinedTransaction> {
        val transactionsLimit = limit ?: TRANSACTIONS_LIMIT

        val regularTransactions = transactionService
            .findBiggestTransactionsWithTypeAndCategory(transactionsLimit)
            .map { it.toCombinedTransaction() }

        val recurringTransactions = recurringTransactionService
            .findBiggestRecurringTransactionsWithTypeAndCategory(transactionsLimit)
            .map { it.toCombinedTransaction() }

        return (regularTransactions + recurringTransactions)
            .sortedByDescending { it.amount }
            .take(transactionsLimit)
    }

    fun getAllTransactions(filters: CombinedTransactionsController.TransactionFilters): List<CombinedTransaction> {
        val regularTransactions = transactionService
            .findTransactionsByCategoryAndDateBetween(filters)
            .map { it.toCombinedTransaction() }

        val recurringTransactions = recurringTransactionService
            .findTransactionsByCategoryAndDateBetween(filters)
            .map { it.toCombinedTransaction() }

        return (regularTransactions + recurringTransactions)
            .sortedByDescending { it.sortDate }
    }
}
