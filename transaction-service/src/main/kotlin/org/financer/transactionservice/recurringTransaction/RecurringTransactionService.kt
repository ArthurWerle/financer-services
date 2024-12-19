package org.financer.transactionservice.recurringTransaction

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RecurringTransactionService(private val db: RecurringTransactionRepository) {
    fun findMessages(): List<RecurringTransaction> {
        return db.findAll().toList()
    }

    fun findRecurringTransactionById(id: String): RecurringTransaction? = db.findByIdOrNull(id)

    fun save(message: RecurringTransaction): RecurringTransaction = db.save(message)
}