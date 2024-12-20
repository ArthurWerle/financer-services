package org.financer.transactionservice.recurringTransaction

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/recurring-transactions")
class RecurringTransactionController(private val service: RecurringTransactionService) {
    @GetMapping
    fun listRecurringTransactions() = ResponseEntity.ok(service.findMessages())

    @PostMapping
    fun post(@RequestBody recurringTransaction: RecurringTransaction): ResponseEntity<RecurringTransaction> {
        val savedRecurringTransaction = service.save(recurringTransaction)
        return ResponseEntity.created(URI("/${savedRecurringTransaction.id}")).body(savedRecurringTransaction)
    }

    @GetMapping("/{id}")
    fun getRecurringTransaction(@PathVariable id: String): ResponseEntity<RecurringTransaction> =
        service.findRecurringTransactionById(id).toResponseEntity()

    private fun RecurringTransaction?.toResponseEntity(): ResponseEntity<RecurringTransaction> =
        this?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
}