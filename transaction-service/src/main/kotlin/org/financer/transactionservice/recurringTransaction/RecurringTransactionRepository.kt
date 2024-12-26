package org.financer.transactionservice.recurringTransaction

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDate
import java.time.LocalDateTime

interface RecurringTransactionRepository : CrudRepository<RecurringTransaction, String> {
    @Query("""
        SELECT t.*, ty.name as type_name
         FROM recurring_transactions t, types ty
         WHERE t.type_id = ty.id
    """)
    override fun findAll(): List<RecurringTransaction>

    @Query("""
       SELECT t.*, ty.name as type_name
         FROM recurring_transactions t, types ty
         WHERE t.type_id = ty.id
          AND start_date <= :date 
        AND (end_date IS NULL OR end_date >= :date)
    """)
    fun findActiveTransactionsAtDate(date: LocalDate): List<RecurringTransaction>

    @Query("""
       SELECT t.*, ty.name as type_name
         FROM recurring_transactions t, types ty
         WHERE t.type_id = ty.id
        AND start_date <= :endDate 
        AND (end_date IS NULL OR end_date >= :startDate)
    """)
    fun findTransactionsForDateRange(startDate: LocalDate, endDate: LocalDate): List<RecurringTransaction>
}