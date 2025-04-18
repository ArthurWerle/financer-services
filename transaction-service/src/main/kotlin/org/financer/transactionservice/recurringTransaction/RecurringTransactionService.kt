package org.financer.transactionservice.recurringTransaction

import org.financer.transactionservice.combinedTransactions.CombinedTransactionsController
import org.financer.transactionservice.transaction.Transaction
import org.financer.transactionservice.transaction.TransactionDTO
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Service
class RecurringTransactionService(private val db: RecurringTransactionRepository) {
    fun findAllRecurringTransactions(): List<RecurringTransactionDto> {
        return db.findAllWithTypeAndCategory().toList()
    }

    fun findRecurringTransactionById(id: String): RecurringTransaction? = db.findByIdOrNull(id)

    @Caching(evict = [
        CacheEvict(value = ["getAllTransactions"], allEntries = true),
        CacheEvict(value = ["getLatestCombinedTransactions"], allEntries = true),
        CacheEvict(value = ["getBiggestCombinedTransactions"], allEntries = true),
        CacheEvict(value = ["findTransactionById"], allEntries = true),
        CacheEvict(value = ["findTransactionsByMonth"], allEntries = true),
        CacheEvict(value = ["findTransactionsByWeek"], allEntries = true),
        CacheEvict(value = ["findTransactionsByDay"], allEntries = true),
        CacheEvict(value = ["findTransactions"]),
    ])
    fun save(transaction: RecurringTransaction): RecurringTransaction = db.save(transaction)

    @Caching(evict = [
        CacheEvict(value = ["getAllTransactions"], allEntries = true),
        CacheEvict(value = ["getLatestCombinedTransactions"], allEntries = true),
        CacheEvict(value = ["getBiggestCombinedTransactions"], allEntries = true),
        CacheEvict(value = ["findTransactionById"], allEntries = true),
        CacheEvict(value = ["findTransactionsByMonth"], allEntries = true),
        CacheEvict(value = ["findTransactionsByWeek"], allEntries = true),
        CacheEvict(value = ["findTransactionsByDay"], allEntries = true),
        CacheEvict(value = ["findTransactions"]),
    ])
    fun delete(id: String): Unit = db.deleteById(id)

    @Caching(evict = [
        CacheEvict(value = ["getAllTransactions"], allEntries = true),
        CacheEvict(value = ["getLatestCombinedTransactions"], allEntries = true),
        CacheEvict(value = ["getBiggestCombinedTransactions"], allEntries = true),
        CacheEvict(value = ["findTransactionById"], allEntries = true),
        CacheEvict(value = ["findTransactionsByMonth"], allEntries = true),
        CacheEvict(value = ["findTransactionsByWeek"], allEntries = true),
        CacheEvict(value = ["findTransactionsByDay"], allEntries = true),
        CacheEvict(value = ["findTransactions"]),
    ])
    fun update(id: String, updatedTransaction: RecurringTransaction): RecurringTransaction {
        val existingTransaction = db.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Transaction with id $id not found")

        val transactionToSave = existingTransaction.copy(
            typeId = updatedTransaction.typeId,
            categoryId = updatedTransaction.categoryId,
            startDate = updatedTransaction.startDate,
            lastOccurrence = updatedTransaction.lastOccurrence,
            endDate = updatedTransaction.endDate,
            amount = updatedTransaction.amount,
            description = updatedTransaction.description
        )

        return db.save(transactionToSave)
    }

    fun findRecurringTransactionsByMonth(yearMonth: YearMonth): List<RecurringTransactionDto> {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return db.findTransactionsForDateRangeWithTypeAndCategory(startDate, endDate)
    }

    fun findRecurringTransactionsByWeek(startDate: LocalDate): List<RecurringTransactionDto> {
        val endDate = startDate.plusDays(6)
        return db.findTransactionsForDateRangeWithTypeAndCategory(startDate, endDate)
    }

    fun findRecurringTransactionsByDay(date: LocalDate): List<RecurringTransactionDto> {
        return db.findActiveTransactionsAtDateWithTypeAndCategory(date)
    }

    fun findLastRecurringTransactionsWithTypeAndCategory(limit: Int): List<RecurringTransactionDto> {
        return db.findLastTransactionsWithTypeAndCategory(limit)
    }

    fun findBiggestRecurringTransactionsWithTypeAndCategory(limit: Int): List<RecurringTransactionDto> {
        return db.findBiggestTransactionsWithTypeAndCategory(limit)
    }

    fun findTransactionsByCategoryAndDateBetween(filters: CombinedTransactionsController.TransactionFilters): List<RecurringTransactionDto> {
        return db.findTransactionsByCategoryAndDateBetween(filters.currentMonth, filters.categories)
    }

    fun findTotalValueByMonth(): BigDecimal {
        return db.findTotalValueByMonth()
    }

    fun findTotalValueByWeek(): BigDecimal {
        return db.findTotalValueByWeek()
    }

    fun findTotalValueByDay(): BigDecimal {
        return db.findTotalValueByDay()
    }
}