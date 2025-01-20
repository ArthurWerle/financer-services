package org.financer.transactionservice.combinedTransactions

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/combined-transactions")
class CombinedTransactionsController(
    private val service: CombinedTransactionService
) {
    @GetMapping("/latest/{limit}")
    fun getLatest(@PathVariable limit: Int?) = ResponseEntity.ok(service.getLatestCombinedTransactions(limit))

    @GetMapping("/biggest/{limit}")
    fun getBiggest(@PathVariable limit: Int?) = ResponseEntity.ok(service.getBiggestCombinedTransactions(limit))
}