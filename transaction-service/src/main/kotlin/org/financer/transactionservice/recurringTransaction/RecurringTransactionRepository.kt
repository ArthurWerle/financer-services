package org.financer.transactionservice.recurringTransaction

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDate

interface RecurringTransactionRepository : CrudRepository<RecurringTransaction, String> {
    @Query("""
        SELECT t.*, ty.name as type_name, c.name as category_name
         FROM recurring_transactions t
         JOIN types ty ON t.type_id = ty.id
         JOIN categories c ON t.category_id = c.id
    """)
    fun findAllWithTypeAndCategory(): List<RecurringTransactionDto>

    @Query("""
       SELECT t.*, ty.name as type_name, c.name as category_name
         FROM recurring_transactions t
         JOIN types ty ON t.type_id = ty.id
         JOIN categories c ON t.category_id = c.id
        WHERE (end_date IS NULL OR end_date >= :date)
    """)
    fun findActiveTransactionsAtDateWithTypeAndCategory(date: LocalDate): List<RecurringTransactionDto>

    @Query("""
       SELECT t.*, ty.name as type_name, c.name as category_name
         FROM recurring_transactions t, types ty, categories c
         WHERE t.type_id = ty.id
        AND start_date <= :endDate 
        and t.category_id = c.id
        AND (end_date IS NULL OR end_date >= :startDate)
    """)
    fun findTransactionsForDateRangeWithTypeAndCategory(startDate: LocalDate, endDate: LocalDate): List<RecurringTransactionDto>

    @Query("""
        SELECT t.*, ty.name as type_name, c.name as category_name
        FROM recurring_transactions t
        JOIN types ty ON t.type_id = ty.id
        JOIN categories c ON t.category_id = c.id
    ORDER BY t.start_date DESC
       LIMIT :limit
    """)
    fun findLastTransactionsWithTypeAndCategory(limit: Int): List<RecurringTransactionDto>

    @Query("""
        SELECT t.*, ty.name AS type_name, c.name AS category_name
          FROM recurring_transactions t
          JOIN types ty ON t.type_id = ty.id
          JOIN categories c ON t.category_id = c.id
         WHERE ty.name != 'income'
      ORDER BY t.amount DESC
         LIMIT :limit;

    """)
    fun findBiggestTransactionsWithTypeAndCategory(limit: Int): List<RecurringTransactionDto>

    @Query("""
        SELECT t.*, ty.name AS type_name, c.name AS category_name
          FROM recurring_transactions t
          JOIN types ty ON t.type_id = ty.id
          JOIN categories c ON t.category_id = c.id
        WHERE (:#{#categories == null} OR t.category_id IN (:#{#categories}))
           AND (
               :currentMonth = false 
               OR (
                   CURRENT_DATE BETWEEN t.start_date AND t.end_date
               )
           )
      ORDER BY t.amount DESC
    """)
    fun findTransactionsByCategoryAndDateBetween(currentMonth: Boolean?, categories: List<Int>?): List<RecurringTransactionDto>
}