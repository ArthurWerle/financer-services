package org.financer.transactionservice.recurringTransaction

import org.financer.transactionservice.transaction.Transaction
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Service
class RecurringTransactionService(private val db: RecurringTransactionRepository) {
    fun findAllRecurringTransactions(): List<RecurringTransactionDto> {
        return db.findAllWithTypeAndCategory().toList()
    }

    fun findRecurringTransactionById(id: String): RecurringTransaction? = db.findByIdOrNull(id)

    fun save(transaction: RecurringTransaction): RecurringTransaction = db.save(transaction)

    fun delete(id: String): Unit = db.deleteById(id)

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
}