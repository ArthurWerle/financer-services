package org.financer.transactionservice.recurringTransaction

import org.slf4j.LoggerFactory
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDate

interface RecurringTransactionRepository : CrudRepository<RecurringTransaction, String> {
    companion object {
        private val logger = LoggerFactory.getLogger(RecurringTransactionRepository::class.java)
    }

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.frequency,
             t.last_occurrence,
             t.start_date AT TIME ZONE 'America/Sao_Paulo' AS start_date,
             t.end_date AT TIME ZONE 'America/Sao_Paulo' AS end_date,
             ty.name as type_name, 
             c.name as category_name
         FROM recurring_transactions t
         JOIN types ty ON t.type_id = ty.id
         JOIN categories c ON t.category_id = c.id
    """)
    fun findAllWithTypeAndCategory(): List<RecurringTransactionDto>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.frequency,
             t.last_occurrence,
             t.start_date AT TIME ZONE 'America/Sao_Paulo' AS start_date,
             t.end_date AT TIME ZONE 'America/Sao_Paulo' AS end_date,
             ty.name as type_name, 
             c.name as category_name
         FROM recurring_transactions t
         JOIN types ty ON t.type_id = ty.id
         JOIN categories c ON t.category_id = c.id
        WHERE (end_date IS NULL OR end_date >= :date)
    """)
    fun findActiveTransactionsAtDateWithTypeAndCategory(date: LocalDate): List<RecurringTransactionDto>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.frequency,
             t.last_occurrence,
             t.start_date AT TIME ZONE 'America/Sao_Paulo' AS start_date,
             t.end_date AT TIME ZONE 'America/Sao_Paulo' AS end_date,
             ty.name as type_name, 
             c.name as category_name
         FROM recurring_transactions t, types ty, categories c
         WHERE t.type_id = ty.id
        AND start_date <= :endDate 
        and t.category_id = c.id
        AND (end_date IS NULL OR end_date >= :startDate)
    """)
    fun findTransactionsForDateRangeWithTypeAndCategory(startDate: LocalDate, endDate: LocalDate): List<RecurringTransactionDto>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.frequency,
             t.last_occurrence,
             t.start_date AT TIME ZONE 'America/Sao_Paulo' AS start_date,
             t.end_date AT TIME ZONE 'America/Sao_Paulo' AS end_date,
             ty.name as type_name, 
             c.name as category_name
        FROM recurring_transactions t
        JOIN types ty ON t.type_id = ty.id
        JOIN categories c ON t.category_id = c.id
    ORDER BY t.start_date DESC
       LIMIT :limit
    """)
    fun findLastTransactionsWithTypeAndCategory(limit: Int): List<RecurringTransactionDto> {
        val results = findLastTransactionsWithTypeAndCategoryInternal(limit)
        logger.info("Raw SQL query results for findLastTransactionsWithTypeAndCategory - limit: {} - Found {} results", 
            limit, results.size)
        logger.debug("Raw SQL query detailed results: {}", results)
        return results
    }

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.frequency,
             t.last_occurrence,
             t.start_date AT TIME ZONE 'America/Sao_Paulo' AS start_date,
             t.end_date AT TIME ZONE 'America/Sao_Paulo' AS end_date,
             ty.name as type_name, 
             c.name as category_name
          FROM recurring_transactions t
          JOIN types ty ON t.type_id = ty.id
          JOIN categories c ON t.category_id = c.id
         WHERE ty.name != 'income' 
           AND ( 
              CASE
                WHEN t.end_date IS NULL THEN CURRENT_DATE >= t.start_date 
                ELSE CURRENT_DATE BETWEEN t.start_date AND t.end_date
              END )
      ORDER BY t.amount DESC
         LIMIT :limit
    """)
    fun findBiggestTransactionsWithTypeAndCategory(limit: Int): List<RecurringTransactionDto> {
        val results = findBiggestTransactionsWithTypeAndCategoryInternal(limit)
        logger.info("Raw SQL query results for findBiggestTransactionsWithTypeAndCategory - limit: {} - Found {} results", 
            limit, results.size)
        logger.debug("Raw SQL query detailed results: {}", results)
        return results
    }

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.frequency,
             t.last_occurrence,
             t.start_date AT TIME ZONE 'America/Sao_Paulo' AS start_date,
             t.end_date AT TIME ZONE 'America/Sao_Paulo' AS end_date,
             ty.name as type_name, 
             c.name as category_name
        FROM recurring_transactions t
        JOIN types ty ON t.type_id = ty.id
        JOIN categories c ON t.category_id = c.id
    ORDER BY t.start_date DESC
       LIMIT :limit
    """)
    fun findLastTransactionsWithTypeAndCategoryInternal(limit: Int): List<RecurringTransactionDto>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.frequency,
             t.last_occurrence,
             t.start_date AT TIME ZONE 'America/Sao_Paulo' AS start_date,
             t.end_date AT TIME ZONE 'America/Sao_Paulo' AS end_date,
             ty.name as type_name, 
             c.name as category_name
          FROM recurring_transactions t
          JOIN types ty ON t.type_id = ty.id
          JOIN categories c ON t.category_id = c.id
         WHERE ty.name != 'income' 
           AND ( 
              CASE
                WHEN t.end_date IS NULL THEN CURRENT_DATE >= t.start_date 
                ELSE CURRENT_DATE BETWEEN t.start_date AND t.end_date
              END )
      ORDER BY t.amount DESC
         LIMIT :limit
    """)
    fun findBiggestTransactionsWithTypeAndCategoryInternal(limit: Int): List<RecurringTransactionDto>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.frequency,
             t.last_occurrence,
             t.start_date AT TIME ZONE 'America/Sao_Paulo' AS start_date,
             t.end_date AT TIME ZONE 'America/Sao_Paulo' AS end_date,
             ty.name as type_name, 
             c.name as category_name
          FROM recurring_transactions t
          JOIN types ty ON t.type_id = ty.id
          JOIN categories c ON t.category_id = c.id
        WHERE (:#{#categories == null} OR t.category_id IN (:#{#categories}))
           AND (
               :currentMonth = false OR (
                  CASE
                    WHEN t.end_date IS NULL THEN CURRENT_DATE >= t.start_date 
                    ELSE CURRENT_DATE BETWEEN t.start_date AND t.end_date
                  END
                )
           )
      ORDER BY t.amount DESC
    """)
    fun findTransactionsByCategoryAndDateBetween(currentMonth: Boolean?, categories: List<Int>?): List<RecurringTransactionDto> {
        val results = findTransactionsByCategoryAndDateBetweenInternal(currentMonth, categories)
        logger.info("Raw SQL query results for findTransactionsByCategoryAndDateBetween - currentMonth: {}, categories: {} - Found {} results", 
            currentMonth, categories, results.size)
        logger.debug("Raw SQL query detailed results: {}", results)
        return results
    }

    // Internal methods for actual query execution
    @Query("""...""")
    fun findTransactionsByCategoryAndDateBetweenInternal(currentMonth: Boolean?, categories: List<Int>?): List<RecurringTransactionDto>
}