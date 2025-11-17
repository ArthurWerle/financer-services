export type TransactionV2RequestParams = {
    amount: number
    type: 'income' | 'expense'
    is_recurring: boolean
    category_id: number
    description: string
    frequency?: 'monthly' | 'daily' | 'weekly' | 'yearly'
    created_by_id: number
    start_date?: string
    end_date?: string
    date: string
}