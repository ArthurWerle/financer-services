export interface Transaction {
  id: number
  categoryId: number
  amount: number
  typeId: number
  typeName: 'income' | 'expense'
  date: string | null
  description: string
  createdAt: string
  updatedAt: string
}

export type TransactionRequestParams = {
  amount: number
  categoryId: number
  description: string
  typeId: number
  date: string
  startDate: string
}