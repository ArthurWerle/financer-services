package org.financer.transactionservice.transaction

import org.springframework.data.repository.CrudRepository

interface TransactionRepository : CrudRepository<Transaction, String>