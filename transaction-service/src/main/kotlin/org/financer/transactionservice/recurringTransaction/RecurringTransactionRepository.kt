package org.financer.transactionservice.recurringTransaction

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDate
import java.time.LocalDateTime

interface RecurringTransactionRepository : CrudRepository<RecurringTransaction, String> {
    @Query("""
        SELECT t.*, ty.name as type_name, c.name as category_name
         FROM recurring_transactions t, types ty, category c
         WHERE t.type_id = ty.id and t.category_id = c.id
    """)
    fun findAllWithTypeAndCategory(): List<RecurringTransactionDto>

    @Query("""
       SELECT t.*, ty.name as type_name, c.name as category_name
         FROM recurring_transactions t, types ty, category c
         WHERE t.type_id = ty.id
          AND start_date <= :date 
          and t.category_id = c.id
        AND (end_date IS NULL OR end_date >= :date)
    """)
    fun findActiveTransactionsAtDateWithTypeAndCategory(date: LocalDate): List<RecurringTransactionDto>

    @Query("""
       SELECT t.*, ty.name as type_name, c.name as category_name
         FROM recurring_transactions t, types ty, category c
         WHERE t.type_id = ty.id
        AND start_date <= :endDate 
        and t.category_id = c.id
        AND (end_date IS NULL OR end_date >= :startDate)
    """)
    fun findTransactionsForDateRangeWithTypeAndCategory(startDate: LocalDate, endDate: LocalDate): List<RecurringTransactionDto>
}