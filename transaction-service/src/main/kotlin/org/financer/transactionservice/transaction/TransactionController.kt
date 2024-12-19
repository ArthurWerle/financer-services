package org.financer.transactionservice.transaction

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/transactions")
class TransactionController(private val service: TransactionService) {
    @GetMapping
    fun listMessages() = ResponseEntity.ok(service.findMessages())

    @PostMapping
    fun post(@RequestBody transaction: Transaction): ResponseEntity<Transaction> {
        val savedTransaction = service.save(transaction)
        return ResponseEntity.created(URI("/${savedTransaction.id}")).body(savedTransaction)
    }

    @GetMapping("/{id}")
    fun getMessage(@PathVariable id: String): ResponseEntity<Transaction> =
        service.findTransactionById(id).toResponseEntity()

    private fun Transaction?.toResponseEntity(): ResponseEntity<Transaction> =
        this?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
}