import { TransactionRequestParams } from "../types/transaction";
import { TransactionV2, TransactionV2BaseResponse, TransactionV2RequestParams } from "../types/transactions-v2";
import { Service } from "./Service";

export class TransactionV2Service extends Service {
  constructor() {
    super("http://transaction-service-v2:8080/api/v2")
  }

  async getTransactionsByDateRange(startDate: string, endDate: string) {
    const { data } = await this.post<TransactionV2BaseResponse>('/transactions/by-date-range', {
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