package org.financer.transactionservice.transaction

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate
import java.time.YearMonth

@RestController
@RequestMapping("/api/transactions")
class TransactionController(private val service: TransactionService) {
    @GetMapping
    fun listTransactions() = ResponseEntity.ok(service.findTransactions())

    @PostMapping
    fun post(@RequestBody transaction: Transaction): ResponseEntity<Transaction> {
        val savedTransaction = service.save(transaction)
        return ResponseEntity.created(URI("/${savedTransaction.id}")).body(savedTransaction)
    }

    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: String): ResponseEntity<Transaction> =
        service.findTransactionById(id).toResponseEntity()

    @GetMapping("/by-month/{yearMonth}")
    fun getTransactionsByMonth(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM") yearMonth: YearMonth
    ): ResponseEntity<List<Transaction>> {
        val transactions = service.findTransactionsByMonth(yearMonth)
        return ResponseEntity.ok(transactions)
    }

    @GetMapping("/by-week/{startDate}")
    fun getTransactionsByWeek(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate
    ): ResponseEntity<List<Transaction>> {
        val transactions = service.findTransactionsByWeek(startDate)
        return ResponseEntity.ok(transactions)
    }

    @GetMapping("/by-day/{date}")
    fun getTransactionsByDay(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") date: LocalDate
    ): ResponseEntity<List<Transaction>> {
        val transactions = service.findTransactionsByDay(date)
        return ResponseEntity.ok(transactions)
    }

    private fun Transaction?.toResponseEntity(): ResponseEntity<Transaction> =
        this?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
}