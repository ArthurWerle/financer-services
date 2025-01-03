package org.financer.transactionservice.transaction

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface TransactionRepository : CrudRepository<Transaction, String> {
    @Query("""
        SELECT t.*, ty.name as type_name 
        FROM transactions t, types ty 
        WHERE t.type_id = ty.id
    """)
    fun findAllWithType(): List<TransactionDTO>

    @Query("""
        SELECT t.*, ty.name as type_name 
        FROM transactions t, types ty 
        WHERE t.type_id = ty.id
        AND date >= :startDate 
        AND date <= :endDate
    """)
    fun findByDateBetweenWithType(startDate: LocalDateTime, endDate: LocalDateTime): List<TransactionDTO>
}