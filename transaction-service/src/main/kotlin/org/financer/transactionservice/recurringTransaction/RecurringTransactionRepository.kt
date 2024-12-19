package org.financer.transactionservice.recurringTransaction

import org.springframework.data.repository.CrudRepository

interface RecurringTransactionRepository : CrudRepository<RecurringTransaction, String>