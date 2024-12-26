package org.financer.transactionservice.transaction

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface TransactionRepository : CrudRepository<Transaction, String> {
    @Query("""
        SELECT * FROM transactions 
        WHERE date >= :startDate 
        AND date <= :endDate
    """)
    fun findByDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Transaction>
}