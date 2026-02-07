import { Category } from "../types/category";
import { Period } from "../types/period";
import { RecurringTransaction } from "../types/recurring-transaction";
import { Transaction } from "../types/transaction";
import { Service } from "./Service";

export class TransactionService extends Service {
  constructor() {
    super(process.env.TRANSACTION_SERVICE_URL || "http://transaction-service:8080/api")
  }

  async getTotalValuesByPeriod({ period, date }: { period: Period, date: string }) {
    const currentPeriodTransactions = await this.get<Transaction[]>(`/transactions/${period}/${date}`)
    const currentPeriodRecurrentTransactions = await this.get<RecurringTransaction[]>(`/recurring-transactions/${period}/${date}`)
    const currentPeriodAllTransactions = [...currentPeriodTransactions.data, ...currentPeriodRecurrentTransactions.data]
    const totalExpenseValue = currentPeriodAllTransactions.filter(transaction => transaction.typeName === "expense").reduce((acc, transaction) => acc + transaction.amount, 0)
    const totalIncomeValue = currentPeriodAllTransactions.filter(transaction => transaction.typeName === "income").reduce((acc, transaction) => acc + transaction.amount, 0)

    return {
      totalExpenseValue,
      totalIncomeValue
    }
  }

  async getIncomeAndExpenseComparisonHistory() {
    const monthlyData: { month: string, expense: number, income: number }[] = []

    // Start from January 2025
    const startDate = new Date(2025, 0, 1) 
    const today = new Date()
    const currentMonth = new Date(today.getFullYear(), today.getMonth(), 1)

    let currentDate = new Date(startDate)
    while (currentDate <= currentMonth) {
      const month = currentDate.toISOString().slice(0, 7)
      const { totalExpenseValue, totalIncomeValue } = await this.getTotalValuesByPeriod({ period: 'by-month', date: month })
      
      if (totalExpenseValue || totalIncomeValue) {
        const monthLabel = currentDate.toLocaleString('default', { month: 'short' })
        const yearLabel = currentDate.getFullYear().toString().slice(-2)
        monthlyData.push({
          month: `${monthLabel} ${yearLabel}`,
          expense: totalExpenseValue,
          income: totalIncomeValue
        })
      }

      currentDate.setMonth(currentDate.getMonth() + 1)
    }

    return monthlyData
  }

  async overviewByMonth() {
    const currentMonth = new Date().toISOString().slice(0, 7)
    const currentMonthTransactions = await this.get<Transaction[]>(`/transactions/by-month/${currentMonth}`)
    const currentMonthRecurrentTransactions = await this.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${currentMonth}`)
    const currentMonthAllTransactions = [...currentMonthTransactions.data, ...currentMonthRecurrentTransactions.data]
    const totalExpenseValue = currentMonthAllTransactions.filter(transaction => transaction.typeName === "expense").reduce((acc, transaction) => acc + transaction.amount, 0)
    const totalIncomeValue = currentMonthAllTransactions.filter(transaction => transaction.typeName === "income").reduce((acc, transaction) => acc + transaction.amount, 0)

    const lastMonth = new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().slice(0, 7)
    const lastMonthTransactions = await this.get<Transaction[]>(`/transactions/by-month/${lastMonth}`)
    const lastMonthRecurrentTransactions = await this.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${lastMonth}`)
    const lastMonthAllTransactions = [...lastMonthTransactions.data, ...lastMonthRecurrentTransactions.data]
    const lastMonthTotalExpenseValue = lastMonthAllTransactions.filter(transaction => transaction.typeName === "expense").reduce((acc, transaction) => acc + transaction.amount, 0)
    const lastMonthTotalIncomeValue = lastMonthAllTransactions.filter(transaction => transaction.typeName === "income").reduce((acc, transaction) => acc + transaction.amount, 0)

    return {
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
    }
  }

  async getMonthlyExpensesByCategory(categories: Category[]) {
    const currentMonth = new Date().toISOString().slice(0, 7)
    const transactions = await this.get<Transaction[]>(`/transactions/by-month/${currentMonth}`)
    const recurringTransactions = await this.get<RecurringTransaction[]>(`/recurring-transactions/by-month/${currentMonth}`)
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

    return Object.fromEntries(sortedEntries)
  }
}