package org.financer.transactionservice.transaction

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TransactionService(private val db: TransactionRepository) {
    fun findMessages(): List<Transaction> {
        return db.findAll().toList()
    }

    fun findTransactionById(id: String): Transaction? = db.findByIdOrNull(id)

    fun save(message: Transaction): Transaction = db.save(message)
}