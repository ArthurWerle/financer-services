package org.financer.transactionservice.combinedTransactions

import org.financer.transactionservice.recurringTransaction.RecurringTransactionDto
import org.financer.transactionservice.recurringTransaction.RecurringTransactionService
import org.financer.transactionservice.transaction.TransactionDTO
import org.financer.transactionservice.transaction.TransactionService
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

const val TRANSACTIONS_LIMIT = 5

private fun TransactionDTO.toCombinedTransaction() = RegularCombinedTransaction(
    amount = this.amount,
    description = this.description.toString(),
    sortDate = this.date ?: LocalDateTime.now(),
    date = this.date ?: LocalDateTime.now(),
    typeName = this.typeName.toString(),
    categoryName = this.categoryName,
    id = this.id.toString()
)

private fun RecurringTransactionDto.toCombinedTransaction() = RecurringCombinedTransaction(
    amount = this.amount,
    description = this.description.toString(),
    sortDate = this.startDate.atStartOfDay(),
    date = null,
    typeName = this.typeName.toString(),
    categoryName = this.categoryName,
    id = this.id.toString(),
    startDate = this.startDate.atStartOfDay(),
    endDate = this.endDate?.atStartOfDay(),
    frequency = this.frequency
)

@Service
class CombinedTransactionService(
    private val transactionService: TransactionService,
    private val recurringTransactionService: RecurringTransactionService
) {
    @Cacheable(value = ["getLatestCombinedTransactions"], key = "#limit")
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

    @Cacheable(value = ["getBiggestCombinedTransactions"], key = "#limit")
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

    @Cacheable(value = ["getAllTransactions"], key = "#filters")
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
