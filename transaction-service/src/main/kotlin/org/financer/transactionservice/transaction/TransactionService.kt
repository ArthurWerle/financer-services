package org.financer.transactionservice.transaction

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Service
class TransactionService(private val db: TransactionRepository) {
    fun findTransactions(): List<TransactionDTO> {
        return db.findAllWithTypeAndCategory().toList()
    }

    fun findTransactionById(id: String): Transaction? = db.findByIdOrNull(id)

    fun save(transaction: Transaction): Transaction = db.save(transaction)

    fun delete(id: String): Unit = db.deleteById(id)

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

    fun findTransactionsByMonth(yearMonth: YearMonth): List<TransactionDTO> {
        val startDateTime = yearMonth.atDay(1).atStartOfDay()
        val endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX)
        return db.findByDateBetweenWithTypeAndCategory(startDateTime, endDateTime)
    }

    fun findTransactionsByWeek(startDate: LocalDate): List<TransactionDTO> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = startDate.plusDays(6).atTime(LocalTime.MAX)
        return db.findByDateBetweenWithTypeAndCategory(startDateTime, endDateTime)
    }

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
}