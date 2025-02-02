package org.financer.transactionservice.combinedTransactions

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/combined-transactions")
class CombinedTransactionsController(
    private val service: CombinedTransactionService
) {
    data class TransactionFilters(
        val categories: List<Int>? = null,
        val currentMonth: Boolean = false,
    )

    @GetMapping("/latest/{limit}")
    fun getLatest(@PathVariable limit: Int?) = ResponseEntity.ok(service.getLatestCombinedTransactions(limit))

    @GetMapping("/all")
    fun getAll(
        @RequestParam("category", required = false) categoryParam: String?,
        @RequestParam("currentMonth", required = false) currentMonth: Boolean = false
    ): ResponseEntity<List<CombinedTransaction>> {
        val filters = TransactionFilters(
            categories = categoryParam?.split(",")?.mapNotNull { it.toIntOrNull() },
            currentMonth = currentMonth,
        )

        return ResponseEntity.ok(service.getAllTransactions(filters))
    }

    @GetMapping("/biggest/{limit}")
    fun getBiggest(@PathVariable limit: Int?) = ResponseEntity.ok(service.getBiggestCombinedTransactions(limit))
}