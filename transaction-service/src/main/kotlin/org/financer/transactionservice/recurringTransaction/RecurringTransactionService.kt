package org.financer.transactionservice.recurringTransaction

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Service
class RecurringTransactionService(private val db: RecurringTransactionRepository) {
    fun findAllRecurringTransactions(): List<RecurringTransaction> {
        return db.findAll().toList()
    }

    fun findRecurringTransactionById(id: String): RecurringTransaction? = db.findByIdOrNull(id)

    fun save(transaction: RecurringTransaction): RecurringTransaction = db.save(transaction)

    fun findRecurringTransactionsByMonth(yearMonth: YearMonth): List<RecurringTransaction> {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return db.findTransactionsForDateRange(startDate, endDate)
    }

    fun findRecurringTransactionsByWeek(startDate: LocalDate): List<RecurringTransaction> {
        val endDate = startDate.plusDays(6)
        return db.findTransactionsForDateRange(startDate, endDate)
    }

    fun findRecurringTransactionsByDay(date: LocalDate): List<RecurringTransaction> {
        return db.findActiveTransactionsAtDate(date)
    }
}