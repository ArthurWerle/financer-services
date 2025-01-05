package org.financer.transactionservice.transaction

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface TransactionRepository : CrudRepository<Transaction, String> {
    @Query("""
        SELECT t.*, ty.name as type_name, c.name as category_name
        FROM transactions t, types ty, category c
        WHERE t.type_id = ty.id
         and t.category_id = c.id
    """)
    fun findAllWithTypeAndCategory(): List<TransactionDTO>

    @Query("""
        SELECT t.*, ty.name as type_name, c.name as category_name
        FROM transactions t, types ty, category c 
        WHERE t.type_id = ty.id
        and t.category_id = c.id
        AND date >= :startDate 
        AND date <= :endDate
    """)
    fun findByDateBetweenWithTypeAndCategory(startDate: LocalDateTime, endDate: LocalDateTime): List<TransactionDTO>
}