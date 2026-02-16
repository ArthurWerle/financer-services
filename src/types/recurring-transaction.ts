export interface RecurringTransaction {
  id: number
  categoryId: number
  amount: number
  typeId: number
  typeName: 'income' | 'expense'
  description: string
  frequency: string
  startDate: string
  endDate: string
  lastOccurrence: string | null
  createdAt: string
  updatedAt: string
}
  