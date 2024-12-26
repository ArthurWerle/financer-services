import { Router } from "express"
import { TransactionService } from "./services/TransactionService"
import { Transaction } from "./types/transaction"
import { RecurringTransaction } from "./types/recurring-transaction"

const router = Router()

router.get("/current-month", async (req, res) => {
  try { 
    const service = new TransactionService()
    const currentMonth = new Date().toISOString().slice(0, 7)
    const transactions = await service.get<Transaction[]>(`/transactions/by-month/${currentMonth}`)
    const recurrentTransactions = await service.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${currentMonth}`)

    const allTransactions = [...transactions.data, ...recurrentTransactions.data]

    res.json({
      transactions: allTransactions
    })
  } catch (error) {
    res.status(500).json({ error: "Failed to fetch data /current-month", cause: error })
  }
})

export default router
