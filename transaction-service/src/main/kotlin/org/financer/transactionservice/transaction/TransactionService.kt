package org.financer.transactionservice.transaction

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Service
class TransactionService(private val db: TransactionRepository) {
    fun findTransactions(): List<TransactionDTO> {
        return db.findAllWithType().toList()
    }

    fun findTransactionById(id: String): Transaction? = db.findByIdOrNull(id)

    fun save(transaction: Transaction): Transaction = db.save(transaction)

    fun findTransactionsByMonth(yearMonth: YearMonth): List<TransactionDTO> {
        val startDateTime = yearMonth.atDay(1).atStartOfDay()
        val endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX)
        return db.findByDateBetweenWithType(startDateTime, endDateTime)
    }

    fun findTransactionsByWeek(startDate: LocalDate): List<TransactionDTO> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = startDate.plusDays(6).atTime(LocalTime.MAX)
        return db.findByDateBetweenWithType(startDateTime, endDateTime)
    }

    fun findTransactionsByDay(date: LocalDate): List<TransactionDTO> {
        val startDateTime = date.atStartOfDay()
        val endDateTime = date.atTime(LocalTime.MAX)
        return db.findByDateBetweenWithType(startDateTime, endDateTime)
    }
}