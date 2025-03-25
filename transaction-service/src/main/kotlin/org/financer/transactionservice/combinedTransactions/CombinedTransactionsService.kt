package org.financer.transactionservice.combinedTransactions

import org.financer.transactionservice.recurringTransaction.RecurringTransactionDto
import org.financer.transactionservice.recurringTransaction.RecurringTransactionService
import org.financer.transactionservice.transaction.TransactionDTO
import org.financer.transactionservice.transaction.TransactionService
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

const val TRANSACTIONS_LIMIT = 5

@Service
class CombinedTransactionService(
    private val transactionService: TransactionService,
    private val recurringTransactionService: RecurringTransactionService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CombinedTransactionService::class.java)
    }

    private fun TransactionDTO.toCombinedTransaction(): RegularCombinedTransaction {
        return RegularCombinedTransaction(
            amount = this.amount,
            description = this.description.toString(),
            sortDate = this.date ?: LocalDateTime.now(),
            date = this.date ?: LocalDateTime.now(),
            typeName = this.typeName.toString(),
            categoryName = this.categoryName,
            id = this.id.toString()
        ).also { 
            logger.debug("Transformed TransactionDTO to RegularCombinedTransaction: {} -> {}", this, it)
        }
    }

    private fun RecurringTransactionDto.toCombinedTransaction(): RecurringCombinedTransaction {
        return RecurringCombinedTransaction(
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
        ).also {
            logger.debug("Transformed RecurringTransactionDto to RecurringCombinedTransaction: {} -> {}", this, it)
        }
    }

    @Cacheable(value = ["getLatestCombinedTransactions"], key = "#limit")
    fun getLatestCombinedTransactions(limit: Int?): List<CombinedTransaction> {
        logger.info("Fetching latest combined transactions with limit: {}", limit)
        val transactionsLimit = limit ?: TRANSACTIONS_LIMIT

        val regularTransactions = transactionService
            .findLastTransactionsWithTypeAndCategory(transactionsLimit)
            .map { it.toCombinedTransaction() }
        logger.debug("Found {} regular transactions", regularTransactions.size)

        val recurringTransactions = recurringTransactionService
            .findLastRecurringTransactionsWithTypeAndCategory(transactionsLimit)
            .map { it.toCombinedTransaction() }
        logger.debug("Found {} recurring transactions", recurringTransactions.size)

        return (regularTransactions + recurringTransactions)
            .sortedByDescending { it.sortDate }
            .take(transactionsLimit)
            .also { 
                logger.info("Returning {} combined transactions after merging and sorting", it.size)
                logger.debug("Final combined transactions: {}", it)
            }
    }

    @Cacheable(value = ["getBiggestCombinedTransactions"], key = "#limit")
    fun getBiggestCombinedTransactions(limit: Int?): List<CombinedTransaction> {
        logger.info("Fetching biggest combined transactions with limit: {}", limit)
        val transactionsLimit = limit ?: TRANSACTIONS_LIMIT

        val regularTransactions = transactionService
            .findBiggestTransactionsWithTypeAndCategory(transactionsLimit)
            .map { it.toCombinedTransaction() }
        logger.debug("Found {} regular transactions", regularTransactions.size)

        val recurringTransactions = recurringTransactionService
            .findBiggestRecurringTransactionsWithTypeAndCategory(transactionsLimit)
            .map { it.toCombinedTransaction() }
        logger.debug("Found {} recurring transactions", recurringTransactions.size)

        return (regularTransactions + recurringTransactions)
            .sortedByDescending { it.amount }
            .take(transactionsLimit)
            .also { 
                logger.info("Returning {} combined transactions after merging and sorting", it.size)
                logger.debug("Final combined transactions: {}", it)
            }
    }

    // @Cacheable(value = ["getAllTransactions"], key = "#filters")
    fun getAllTransactions(filters: CombinedTransactionsController.TransactionFilters): List<CombinedTransaction> {
        logger.info("Fetching all combined transactions with filters: {}", filters)

        val regularTransactions = transactionService
            .findTransactionsByCategoryAndDateBetween(filters)
            .map { it.toCombinedTransaction() }
        logger.debug("Found {} regular transactions", regularTransactions.size)

        val recurringTransactions = recurringTransactionService
            .findTransactionsByCategoryAndDateBetween(filters)
            .map { it.toCombinedTransaction() }
        logger.debug("Found {} recurring transactions", recurringTransactions.size)

        return (regularTransactions + recurringTransactions)
            .sortedByDescending { it.sortDate }
            .also { 
                logger.info("Returning {} combined transactions after merging and sorting", it.size)
                logger.debug("Final combined transactions: {}", it)
            }
    }
}
