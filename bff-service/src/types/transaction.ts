export interface Transaction {
  id: number
  categoryId: number
  amount: number
  typeId: number
  date: string | null
  description: string
  createdAt: string
  updatedAt: string
}