package org.financer.transactionservice.combinedTransactions

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/combined-transactions")
class CombinedTransactionsController(
    private val service: CombinedTransactionService
) {
    @GetMapping("/latest/by-date")
    fun getLatest() = ResponseEntity.ok(service.getLatestCombinedTransactions())

    @GetMapping("/latest/by-amount")
    fun getBiggest() = ResponseEntity.ok(service.getBiggestCombinedTransactions())
}