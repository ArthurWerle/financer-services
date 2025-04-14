package org.financer.transactionservice.transaction

import org.financer.transactionservice.combinedTransactions.CombinedTransactionsController
import org.financer.transactionservice.recurringTransaction.RecurringTransactionDto
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Service
class TransactionService(private val db: TransactionRepository) {
    @Cacheable(value = ["findTransactions"])
    fun findTransactions(): List<TransactionDTO> {
        return db.findAllWithTypeAndCategory().toList()
    }

    @Cacheable(value = ["findTransactionById"], key = "#id")
    fun findTransactionById(id: String): Transaction? = db.findByIdOrNull(id)

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
    fun save(transaction: Transaction): Transaction = db.save(transaction)

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
    fun update(id: String, updatedTransaction: Transaction): Transaction {
        val existingTransaction = db.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Transaction with id $id not found")

        val transactionToSave = existingTransaction.copy(
            typeId = updatedTransaction.typeId,
            categoryId = updatedTransaction.categoryId,
            date = updatedTransaction.date,
            amount = updatedTransaction.amount,
            description = updatedTransaction.description
        )

        return db.save(transactionToSave)
    }

    @Cacheable(value = ["findTransactionsByMonth"], key = "#yearMonth")
    fun findTransactionsByMonth(yearMonth: YearMonth): List<TransactionDTO> {
        val startDateTime = yearMonth.atDay(1).atStartOfDay()
        val endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX)
        return db.findByDateBetweenWithTypeAndCategory(startDateTime, endDateTime)
    }

    @Cacheable(value = ["findTransactionsByWeek"], key = "#startDate")
    fun findTransactionsByWeek(startDate: LocalDate): List<TransactionDTO> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = startDate.plusDays(6).atTime(LocalTime.MAX)
        return db.findByDateBetweenWithTypeAndCategory(startDateTime, endDateTime)
    }

    @Cacheable(value = ["findTransactionsByDay"], key = "#date")
    fun findTransactionsByDay(date: LocalDate): List<TransactionDTO> {
        val startDateTime = date.atStartOfDay()
        val endDateTime = date.atTime(LocalTime.MAX)
        return db.findByDateBetweenWithTypeAndCategory(startDateTime, endDateTime)
    }

    fun findLastTransactionsWithTypeAndCategory(limit: Int): List<TransactionDTO> {
        return db.findLastTransactionsWithTypeAndCategory(limit)
    }

    fun findBiggestTransactionsWithTypeAndCategory(limit: Int): List<TransactionDTO> {
        return db.findBiggestTransactionsWithTypeAndCategory(limit)
    }

    fun findTransactionsByCategoryAndDateBetween(filters: CombinedTransactionsController.TransactionFilters): List<TransactionDTO> {
        return db.findTransactionsByCategoryAndDateBetween(filters.currentMonth, filters.categories)
    }

    fun findTotalByMonth(): BigDecimal {
        return db.findTotalByMonth()
    }

    fun findTotalByWeek(): BigDecimal {
        return db.findTotalByWeek()
    }

    fun findTotalByDay(): BigDecimal {
        return db.findTotalByDay()
    }
}