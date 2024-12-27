package org.financer.transactionservice.recurringTransaction

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Service
class RecurringTransactionService(private val db: RecurringTransactionRepository) {
    fun findAllRecurringTransactions(): List<RecurringTransactionDto> {
        return db.findAllWithType().toList()
    }

    fun findRecurringTransactionById(id: String): RecurringTransaction? = db.findByIdOrNull(id)

    fun save(transaction: RecurringTransaction): RecurringTransaction = db.save(transaction)

    fun findRecurringTransactionsByMonth(yearMonth: YearMonth): List<RecurringTransactionDto> {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return db.findTransactionsForDateRangeWithType(startDate, endDate)
    }

    fun findRecurringTransactionsByWeek(startDate: LocalDate): List<RecurringTransactionDto> {
        val endDate = startDate.plusDays(6)
        return db.findTransactionsForDateRangeWithType(startDate, endDate)
    }

    fun findRecurringTransactionsByDay(date: LocalDate): List<RecurringTransactionDto> {
        return db.findActiveTransactionsAtDateWithType(date)
    }
}