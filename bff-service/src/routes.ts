import { Router } from "express"
import { TransactionService } from "./services/TransactionService"
import { Transaction } from "./types/transaction"
import { RecurringTransaction } from "./types/recurring-transaction"

const router = Router()

router.get("/overview/by-month", async (req, res) => {
  try { 
    const service = new TransactionService()

    const currentMonth = new Date().toISOString().slice(0, 7)
    const currentMonthTransactions = await service.get<Transaction[]>(`/transactions/by-month/${currentMonth}`)
    const currentMonthRecurrentTransactions = await service.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${currentMonth}`)
    const currentMonthAllTransactions = [...currentMonthTransactions.data, ...currentMonthRecurrentTransactions.data]
    const totalExpenseValue = currentMonthAllTransactions.filter(transaction => transaction.typeName === "expense").reduce((acc, transaction) => acc + transaction.amount, 0)
    const totalIncomeValue = currentMonthAllTransactions.filter(transaction => transaction.typeName === "income").reduce((acc, transaction) => acc + transaction.amount, 0)

    const lastMonth = new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().slice(0, 7)
    const lastMonthTransactions = await service.get<Transaction[]>(`/transactions/by-month/${lastMonth}`)
    const lastMonthRecurrentTransactions = await service.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${lastMonth}`)
    const lastMonthAllTransactions = [...lastMonthTransactions.data, ...lastMonthRecurrentTransactions.data]
    const lastMonthTotalExpenseValue = lastMonthAllTransactions.filter(transaction => transaction.typeName === "expense").reduce((acc, transaction) => acc + transaction.amount, 0)
    const lastMonthTotalIncomeValue = lastMonthAllTransactions.filter(transaction => transaction.typeName === "income").reduce((acc, transaction) => acc + transaction.amount, 0)

    res.json({
      income: {
        currentMonth: totalIncomeValue,
        lastMonth: lastMonthTotalIncomeValue,
        percentageVariation: ((totalIncomeValue - lastMonthTotalIncomeValue) / lastMonthTotalIncomeValue) * 100
      },
      expense: {
        currentMonth: totalExpenseValue,
        lastMonth: lastMonthTotalExpenseValue,
        percentageVariation: ((totalExpenseValue - lastMonthTotalExpenseValue) / lastMonthTotalExpenseValue) * 100
      },
    })
  } catch (error) {
    res.status(500).json({ error: "Failed to fetch data /current-month", cause: error })
  }
})

router.get("/current-month", async (req, res) => {
  try { 
    const service = new TransactionService()
    const currentMonth = new Date().toISOString().slice(0, 7)
    const transactions = await service.get<Transaction[]>(`/transactions/by-month/${currentMonth}`)
    const recurrentTransactions = await service.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${currentMonth}`)

    const allTransactions = [...transactions.data, ...recurrentTransactions.data]
    const totalExpenseValue = allTransactions.filter(transaction => transaction.typeName === "expense").reduce((acc, transaction) => acc + transaction.amount, 0)
    const totalIncomeValue = allTransactions.filter(transaction => transaction.typeName === "income").reduce((acc, transaction) => acc + transaction.amount, 0)

    res.json({
      transactions: allTransactions,
      totalExpenseValue,
      totalIncomeValue
    })
  } catch (error) {
    res.status(500).json({ error: "Failed to fetch data /current-month", cause: error })
  }
})

router.get("/last-month", async (req, res) => {
  try {
    const service = new TransactionService()
    const lastMonth = new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().slice(0, 7)
    const transactions = await service.get<Transaction[]>(`/transactions/by-month/${lastMonth}`)
    const recurrentTransactions = await service.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${lastMonth}`)

    const allTransactions = [...transactions.data, ...recurrentTransactions.data]
    const totalExpenseValue = allTransactions.filter(transaction => transaction.typeName === "expense").reduce((acc, transaction) => acc + transaction.amount, 0)
    const totalIncomeValue = allTransactions.filter(transaction => transaction.typeName === "income").reduce((acc, transaction) => acc + transaction.amount, 0)

    res.json({
      transactions: allTransactions,
      totalExpenseValue,
      totalIncomeValue
    })
  } catch (error) {
    res.status(500).json({ error: "Failed to fetch data /last-month", cause: error })
  }
})

export default router
