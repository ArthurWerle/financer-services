package org.financer.transactionservice.recurringTransaction

import org.financer.transactionservice.transaction.Transaction
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate
import java.time.YearMonth

@RestController
@RequestMapping("/api/recurring-transactions")
class RecurringTransactionController(private val service: RecurringTransactionService) {
    @GetMapping
    fun listRecurringTransactions() = ResponseEntity.ok(service.findAllRecurringTransactions())

    @PostMapping
    fun post(@RequestBody recurringTransaction: RecurringTransaction): ResponseEntity<RecurringTransaction> {
        val savedRecurringTransaction = service.save(recurringTransaction)
        return ResponseEntity.created(URI("/${savedRecurringTransaction.id}")).body(savedRecurringTransaction)
    }

    @GetMapping("/{id}")
    fun getRecurringTransaction(@PathVariable id: String): ResponseEntity<RecurringTransaction> =
        service.findRecurringTransactionById(id).toResponseEntity()

    @PatchMapping("/{id}")
    fun updateTransaction(@PathVariable id: String, @RequestBody transaction: RecurringTransaction): RecurringTransaction =
        service.update(id, transaction)

    @DeleteMapping("/{id}")
    fun deleteRecurringTransaction(@PathVariable id: String): Unit =
        service.delete(id)

    @GetMapping("/by-month/{yearMonth}")
    fun getTransactionsByMonth(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM") yearMonth: YearMonth
    ): ResponseEntity<List<RecurringTransactionDto>> {
        val recurringTransactions = service.findRecurringTransactionsByMonth(yearMonth)
        return ResponseEntity.ok(recurringTransactions)
    }

    @GetMapping("/by-week/{startDate}")
    fun getTransactionsByWeek(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate
    ): ResponseEntity<List<RecurringTransactionDto>> {
        val recurringTransactions = service.findRecurringTransactionsByWeek(startDate)
        return ResponseEntity.ok(recurringTransactions)
    }

    @GetMapping("/by-day/{date}")
    fun getTransactionsByDay(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") date: LocalDate
    ): ResponseEntity<List<RecurringTransactionDto>> {
        val recurringTransactions = service.findRecurringTransactionsByDay(date)
        return ResponseEntity.ok(recurringTransactions)
    }

    private fun RecurringTransaction?.toResponseEntity(): ResponseEntity<RecurringTransaction> =
        this?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
}