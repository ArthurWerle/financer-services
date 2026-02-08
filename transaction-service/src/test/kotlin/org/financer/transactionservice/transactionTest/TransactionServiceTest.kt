package org.financer.transactionservice.transactionTest

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import io.mockk.MockKAnnotations
import io.mockk.junit5.MockKExtension
import org.financer.transactionservice.combinedTransactions.CombinedTransactionsController
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.financer.transactionservice.config.TestConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import org.assertj.core.api.Assertions.*
import org.financer.transactionservice.transaction.Transaction
import org.financer.transactionservice.transaction.TransactionDTO
import org.financer.transactionservice.transaction.TransactionRepository
import org.financer.transactionservice.transaction.TransactionService
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class, MockKExtension::class)
@SpringBootTest
@Import(TestConfig::class)
@TestPropertySource(properties = [
    "spring.cache.type=none",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.sql.init.mode=never"
])
class TransactionServiceTest {
    init {
        MockKAnnotations.init(this)  // Initialize MockK annotations
    }

    @InjectMockKs
    private lateinit var transactionService: TransactionService

    @MockK
    private lateinit var transactionRepository: TransactionRepository

    private val transaction = Transaction(
        id = 1,
        typeId = 1,
        categoryId = 1,
        date = LocalDateTime.now(),
        amount = BigDecimal("60.0"),
        description = "Pizza"
    )
    private val transactionDTO = TransactionDTO(
        id = 1,
        typeId = 1,
        categoryId = 1,
        date = LocalDateTime.now(),
        amount = BigDecimal("60.0"),
        description = "Pizza",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        categoryName = "Food",
        typeName = "expense"
    )

    @Test
    fun `findTransactions should return all transactions`() {
        every { transactionRepository.findAllWithTypeAndCategory() } returns listOf(transactionDTO)

        val transactions = transactionService.findTransactions()

        assertThat(transactions).hasSize(1)
        assertThat(transactions[0]).isEqualTo(transactionDTO)
    }

    @Test
    fun `findTransactionById should return transaction by id`() {
        every { transactionRepository.findById("1") } returns Optional.of(transaction)

        val foundTransaction = transactionService.findTransactionById("1")

        assertThat(foundTransaction).isEqualTo(transaction)
    }

    @Test
    fun `save should save transaction and return it`() {
        every { transactionRepository.save(transaction) } returns transaction

        val savedTransaction = transactionService.save(transaction)

        assertThat(savedTransaction).isEqualTo(transaction)
    }

    @Test
    fun `delete should delete transaction by id`() {
        every { transactionRepository.deleteById("1") } just runs

        transactionService.delete("1")

        verify(exactly = 1) { transactionRepository.deleteById("1") }
    }

    @Test
    fun `update should update transaction and return it`() {
        val updatedTransaction = Transaction(
            id = 1,
            typeId = 1,
            categoryId = 1,
            date = LocalDateTime.now(),
            amount = BigDecimal("60.0"),
            description = "Updated Pizza"
        )
        val transactionToSave = transaction.copy(
            typeId = updatedTransaction.typeId,
            categoryId = updatedTransaction.categoryId,
            date = updatedTransaction.date,
            amount = updatedTransaction.amount,
            description = updatedTransaction.description
        )
        every { transactionRepository.findById("1") } returns Optional.of(transaction)
        every { transactionRepository.save(transactionToSave) } returns transactionToSave

        val returnedTransaction = transactionService.update("1", updatedTransaction)

        assertThat(returnedTransaction).isEqualTo(transactionToSave)
    }

    @Test
    fun `update should throw exception if transaction not found`() {
        every { transactionRepository.findById("1") } returns Optional.empty()

        assertThatThrownBy { transactionService.update("1", transaction) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Transaction with id 1 not found")
    }

    @Test
    fun `findTransactionsByMonth should return transactions by month`() {
        val yearMonth = YearMonth.of(2024, 1)
        val startDateTime = yearMonth.atDay(1).atStartOfDay()
        val endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX)
        every { transactionRepository.findByDateBetweenWithTypeAndCategory(startDateTime, endDateTime) } returns listOf(transactionDTO)

        val transactions = transactionService.findTransactionsByMonth(yearMonth)

        assertThat(transactions).hasSize(1)
        assertThat(transactions[0]).isEqualTo(transactionDTO)
    }

    @Test
    fun `findTransactionsByWeek should return transactions by week`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = startDate.plusDays(6).atTime(LocalTime.MAX)
        every { transactionRepository.findByDateBetweenWithTypeAndCategory(startDateTime, endDateTime) } returns listOf(transactionDTO)

        val transactions = transactionService.findTransactionsByWeek(startDate)

        assertThat(transactions).hasSize(1)
        assertThat(transactions[0]).isEqualTo(transactionDTO)
    }

    @Test
    fun `findTransactionsByDay should return transactions by day`() {
        val date = LocalDate.of(2024, 1, 1)
        val startDateTime = date.atStartOfDay()
        val endDateTime = date.atTime(LocalTime.MAX)
        every { transactionRepository.findByDateBetweenWithTypeAndCategory(startDateTime, endDateTime) } returns listOf(transactionDTO)

        val transactions = transactionService.findTransactionsByDay(date)

        assertThat(transactions).hasSize(1)
        assertThat(transactions[0]).isEqualTo(transactionDTO)
    }

    @Test
    fun `findLastTransactionsWithTypeAndCategory should return last transactions`() {
        val limit = 5
        every { transactionRepository.findLastTransactionsWithTypeAndCategory(limit) } returns listOf(transactionDTO)

        val transactions = transactionService.findLastTransactionsWithTypeAndCategory(limit)

        assertThat(transactions).hasSize(1)
        assertThat(transactions[0]).isEqualTo(transactionDTO)
    }

    @Test
    fun `findBiggestTransactionsWithTypeAndCategory should return biggest transactions`() {
        val limit = 5
        every { transactionRepository.findBiggestTransactionsWithTypeAndCategory(limit) } returns listOf(transactionDTO)

        val transactions = transactionService.findBiggestTransactionsWithTypeAndCategory(limit)

        assertThat(transactions).hasSize(1)
        assertThat(transactions[0]).isEqualTo(transactionDTO)
    }

    @Test
    fun `findTransactionsByCategoryAndDateBetween should return transactions by category and date`() {
        val filters = CombinedTransactionsController.TransactionFilters(
            currentMonth = true,
            categories = listOf(1)
        )
        every { transactionRepository.findTransactionsByCategoryAndDateBetween(filters.currentMonth, filters.categories) } returns listOf(transactionDTO)

        val transactions = transactionService.findTransactionsByCategoryAndDateBetween(filters)

        assertThat(transactions).hasSize(1)
        assertThat(transactions[0]).isEqualTo(transactionDTO)
    }
}