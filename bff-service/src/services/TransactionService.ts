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

  async getPriceComparisonHistory() {
    const monthlyData: any = []

    for (let i = 0; i < 12; i++) {
      const month = new Date(new Date().setMonth(new Date().getMonth() - i)).toISOString().slice(0, 7)
      const { totalExpenseValue } = await this.getTotalValuesByPeriod({ period: 'by-month', date: month })

      const monthFromLastYear = new Date(new Date().setMonth(new Date().getMonth() - i - 12)).toISOString().slice(0, 7)
      const { totalExpenseValue: lastYearTotalExpenseValue } = await this.getTotalValuesByPeriod({ period: 'by-month', date: monthFromLastYear })
      
      if (totalExpenseValue || lastYearTotalExpenseValue) {
        monthlyData.push({
          month: new Date(new Date().setMonth(new Date().getMonth() - i)).toLocaleString('default', { month: 'short' }),
          currentYear: totalExpenseValue,
          lastYear: lastYearTotalExpenseValue
        })
      }
    }

    return monthlyData
  }
}