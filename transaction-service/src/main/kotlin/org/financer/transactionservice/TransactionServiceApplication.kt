package org.financer.transactionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class TransactionServiceApplication

fun main(args: Array<String>) {
	runApplication<TransactionServiceApplication>(*args)
}
