package org.financer.transactionservice.transaction

import org.financer.transactionservice.recurringTransaction.RecurringTransactionDto
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface TransactionRepository : CrudRepository<Transaction, String> {
    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.date AT TIME ZONE 'America/Sao_Paulo' AS date,
             ty.name as type_name, 
             c.name as category_name
        FROM transactions t
        JOIN types ty ON t.type_id = ty.id
        JOIN categories c ON t.category_id = c.id
    """)
    fun findAllWithTypeAndCategory(): List<TransactionDTO>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.date AT TIME ZONE 'America/Sao_Paulo' AS date,
             ty.name as type_name, 
             c.name as category_name
        FROM transactions t
        JOIN types ty ON t.type_id = ty.id
        JOIN categories c ON t.category_id = c.id
        WHERE date >= :startDate 
        AND date <= :endDate
    """)
    fun findByDateBetweenWithTypeAndCategory(startDate: LocalDateTime, endDate: LocalDateTime): List<TransactionDTO>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.date AT TIME ZONE 'America/Sao_Paulo' AS date,
             ty.name as type_name, 
             c.name as category_name
        FROM transactions t
        JOIN types ty ON t.type_id = ty.id
        JOIN categories c ON t.category_id = c.id
        WHERE ty.name != 'income'
        ORDER BY t.date DESC
        LIMIT :limit
    """)
    fun findLastTransactionsWithTypeAndCategory(limit: Int): List<TransactionDTO>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.date AT TIME ZONE 'America/Sao_Paulo' AS date,
             ty.name as type_name, 
             c.name as category_name
        FROM transactions t
        JOIN types ty ON t.type_id = ty.id
        JOIN categories c ON t.category_id = c.id
        WHERE ty.name != 'income'
          AND date_trunc('month', CURRENT_DATE) = date_trunc('month', t.date)
        ORDER BY t.amount DESC
        LIMIT :limit
    """)
    fun findBiggestTransactionsWithTypeAndCategory(limit: Int): List<TransactionDTO>

    @Query("""
      SELECT t.id,
             t.category_id,
             t.amount,
             t.type_id,
             t.description,
             t.created_at,
             t.updated_at,
             t.date AT TIME ZONE 'America/Sao_Paulo' AS date,
             ty.name as type_name, 
             c.name as category_name
        FROM transactions t
        JOIN types ty ON t.type_id = ty.id
        JOIN categories c ON t.category_id = c.id
        WHERE (:#{#categories == null} OR t.category_id IN (:#{#categories}))
          AND (
              :currentMonth = false
              OR (
                  date_trunc('month', CURRENT_DATE) = date_trunc('month', t.date)
              )
          )
        ORDER BY t.amount DESC
    """)
    fun findTransactionsByCategoryAndDateBetween(currentMonth: Boolean?, categories: List<Int>?): List<TransactionDTO>

}