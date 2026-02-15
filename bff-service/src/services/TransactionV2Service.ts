import { TransactionRequestParams } from "../types/transaction";
import { TransactionV2, TransactionV2BaseResponse, TransactionV2RequestParams, CategoryV2 } from "../types/transactions-v2";
import { Service } from "./Service";

export class TransactionV2Service extends Service {
  constructor() {
    super(process.env.TRANSACTION_V2_SERVICE_URL || "http://transaction-service-v2:8080/api/v2")
  }

  async getTransactionsByDateRange(startDate: string, endDate: string) {
    const { data } = await this.post<TransactionV2BaseResponse>('/transactions/by-date-range', {}, {
      start_date: startDate,
      end_date: endDate
    })

    return data
  }

  getTotalValueGroupedByType(transactions: TransactionV2[]) {
    return transactions.reduce((acc, curr) => {
      if (curr.type === 'income') {
        acc.income += curr.amount
      } else {
        acc.expense += curr.amount
      }
      return acc
    }, {
      income: 0,
      expense: 0
    })
  }

  async getIncomeAndExpenseComparisonHistory() {
    const monthlyData: { month: string, expense: number, income: number }[] = []

    // Start from January 2025
    const startDate = new Date(2025, 0, 1) 
    const today = new Date()
    const currentMonth = new Date(today.getFullYear(), today.getMonth(), 1)

    let currentDate = new Date(startDate)
    while (currentDate <= currentMonth) {
      // must be format YYYY-MM-DD
      const year = currentDate.getFullYear()
      const month = currentDate.getMonth()
      const startDateStr = `${year}-${String(month + 1).padStart(2, '0')}-01`
      const lastDay = new Date(year, month + 1, 0).getDate()
      const endDateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`
      const { transactions } = await this.getTransactionsByDateRange(startDateStr, endDateStr)
      const { income, expense } = this.getTotalValueGroupedByType(transactions)
      
      if (expense || income) {
        const monthLabel = currentDate.toLocaleString('default', { month: 'short' })
        const yearLabel = currentDate.getFullYear().toString().slice(-2)
        monthlyData.push({
          month: `${monthLabel} ${yearLabel}`,
          expense,
          income
        })
      }

      currentDate.setMonth(currentDate.getMonth() + 1)
    }

    return monthlyData
  }

  async overviewByMonth() {
    console.info("Using TransactionV2Service for overviewByMonth")
    const today = new Date()
    const currentYear = today.getFullYear()
    const currentMonth = today.getMonth()

    const currentMonthStart = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-01`
    const currentMonthLastDay = new Date(currentYear, currentMonth + 1, 0).getDate()
    const currentMonthEnd = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(currentMonthLastDay).padStart(2, '0')}`

    const { transactions: currentMonthTransactions } = await this.getTransactionsByDateRange(currentMonthStart, currentMonthEnd)
    const { income: totalIncomeValue, expense: totalExpenseValue } = this.getTotalValueGroupedByType(currentMonthTransactions)

    const lastMonthDate = new Date(currentYear, currentMonth - 1, 1)
    const lastMonthYear = lastMonthDate.getFullYear()
    const lastMonth = lastMonthDate.getMonth()

    const lastMonthStart = `${lastMonthYear}-${String(lastMonth + 1).padStart(2, '0')}-01`
    const lastMonthLastDay = new Date(lastMonthYear, lastMonth + 1, 0).getDate()
    const lastMonthEnd = `${lastMonthYear}-${String(lastMonth + 1).padStart(2, '0')}-${String(lastMonthLastDay).padStart(2, '0')}`

    const { transactions: lastMonthTransactions } = await this.getTransactionsByDateRange(lastMonthStart, lastMonthEnd)
    const { income: lastMonthTotalIncomeValue, expense: lastMonthTotalExpenseValue } = this.getTotalValueGroupedByType(lastMonthTransactions)

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

  async getCategories() {
    const { data } = await this.get<{ categories: CategoryV2[], count: number }>('/categories')
    return data
  }

  async getMonthlyExpensesByCategory() {
    const today = new Date()
    const currentYear = today.getFullYear()
    const currentMonth = today.getMonth()

    const startDate = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-01`
    const lastDay = new Date(currentYear, currentMonth + 1, 0).getDate()
    const endDate = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`

    const [{ transactions }, { categories }] = await Promise.all([
      this.getTransactionsByDateRange(startDate, endDate),
      this.getCategories()
    ])

    const expenses = transactions.filter(t => t.type === 'expense')

    const totalValuesByCategory = expenses.reduce((acc, transaction) => {
      const category = categories.find(category => category.id === transaction.category_id)
      if (!category) return acc

      const categoryName = category.name
      const categoryValue = acc[categoryName] || 0
      return { ...acc, [categoryName]: categoryValue + transaction.amount }
    }, {} as Record<string, number>)

    const sortedEntries = Object.entries(totalValuesByCategory)
      .sort(([, a], [, b]) => b - a)

    return Object.fromEntries(sortedEntries)
  }

  parseBody(request: TransactionRequestParams) {
    const parsedParams: TransactionV2RequestParams = {
        amount: request.amount,
        type: request.typeId === 3 ? 'expense' : 'income',
        is_recurring: false,
        created_by_id: 1,
        category_id: request.categoryId,
        description: request.description,
        date: request.date,
    }

    return parsedParams
  }
}