import { Router } from "express"
import { TransactionService } from "./services/TransactionService"
import { Transaction } from "./types/transaction"
import { RecurringTransaction } from "./types/recurring-transaction"
import { CategoryService } from "./services/CategoryService"
import { Category } from "./types/category"

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
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /current-month", cause: error })
  }
})

router.get("/overview/by-week", async (req, res) => {
  try { 
    const service = new TransactionService()

    const currentWeek = new Date(new Date().setDate(new Date().getDate() - new Date().getDay())).toISOString().slice(0, 10)
    const { totalExpenseValue, totalIncomeValue } = await service.getTotalValuesByPeriod({ period: 'by-week', date: currentWeek })

    const lastWeek = new Date(new Date().setDate(new Date().getDate() - 7)).toISOString().slice(0, 10)
    const { 
      totalExpenseValue: lastWeekTotalExpenseValue, 
      totalIncomeValue: lastWeekTotalIncomeValue 
    } = await service.getTotalValuesByPeriod({ period: 'by-week', date: lastWeek })

    res.json({
      income: {
        currentWeek: totalIncomeValue,
        lastWeek: lastWeekTotalIncomeValue,
        percentageVariation: ((totalIncomeValue - lastWeekTotalIncomeValue) / lastWeekTotalIncomeValue) * 100
      },
      expense: {
        currentWeek: totalExpenseValue,
        lastWeek: lastWeekTotalExpenseValue,
        percentageVariation: ((totalExpenseValue - lastWeekTotalExpenseValue) / lastWeekTotalExpenseValue) * 100
      },
    })
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /overview/by-week", cause: error })
  }
})

router.get("/expense-comparsion-history", async (req, res) => {
  try {
    const service = new TransactionService()
    const monthlyData = await service.getPriceComparisonHistory()
    
    res.json(monthlyData)
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /income-comparsion-chart", cause: error })
  }
})

router.get("/all-transactions", async (req, res) => {
  try {
    const service = new TransactionService()
    const transactions = await service.get<Transaction[]>("/transactions")
    const recurringTransactions = await service.get<RecurringTransaction[]>("/recurring-transactions")

    res.json([...transactions.data, ...recurringTransactions.data])
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /all-transactions", cause: error })
  }
})

router.get("/monthly-expenses-by-category", async (req, res) => {
  try {
    const transactionService = new TransactionService()
    const categoryService = new CategoryService()
    const { data: categories } = await categoryService.get<Category[]>("/category")

    const currentMonth = new Date().toISOString().slice(0, 7)
    const transactions = await transactionService.get<Transaction[]>(`/transactions/by-month/${currentMonth}`)
    const recurringTransactions = await transactionService.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${currentMonth}`)
    const allTransactions = [...transactions.data, ...recurringTransactions.data].filter(transaction => transaction.typeName === "expense")

    const totalValuesByCategory = allTransactions.reduce((acc, transaction) => {
      const category = categories.find(category => category.ID === transaction.categoryId)
      if (!category) return acc

      const categoryName = category.Name
      const categoryValue = acc[categoryName] || 0
      return { ...acc, [categoryName]: categoryValue + transaction.amount }
    }, {} as Record<string, number>)

    const sortedEntries = Object.entries(totalValuesByCategory)
      .sort(([, a], [, b]) => b - a)

    const sortedObject = Object.fromEntries(sortedEntries)

    res.json(sortedObject)
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /total-values-by-category", cause: error })
  }
})

router.get("/all-values", async (req, res) => {
  try {
    const service = new TransactionService()
    const { data: valuesByDay } = await service.get<number>("/combined-transactions/value/by-day")
    const { data: valuesByWeek } = await service.get<number>("/combined-transactions/value/by-week")
    const { data: valuesByMonth } = await service.get<number>("/combined-transactions/value/by-month")

    res.json({
      day: valuesByDay,
      week: valuesByWeek,
      month: valuesByMonth,
    })
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /all-values", cause: error })
  }
})

// router.get("/", async (req, res) => {
//   try {
//     const service = new TransactionService()
//     const { data: valuesByDay } = await service.get<number>("/combined-transactions/value/by-day")
//     const { data: valuesByWeek } = await service.get<number>("/combined-transactions/value/by-week")
//     const { data: valuesByMonth } = await service.get<number>("/combined-transactions/value/by-month")

//     res.json({
//       day: valuesByDay,
//       week: valuesByWeek,
//       month: valuesByMonth,
//     })
//   } catch (error) {
//     console.error(error)
//     res.status(500).json({ error: "Failed to fetch data /all-values", cause: error })
//   }
// })

export default router
