import { Period } from "../types/period";
import { RecurringTransaction } from "../types/recurring-transaction";
import { Transaction } from "../types/transaction";
import { Service } from "./Service";

export class TransactionService extends Service {
  constructor() {
    super("http://transaction-service:8080/api")
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
}