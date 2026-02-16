import { Router } from "express"
import { TransactionV2Service } from "../services/TransactionV2Service"

export function mountTransactionRoutes(router: Router) {
    router.get('/transactions', async (req, res) => {
      try {
        const service = new TransactionV2Service()
        const response = await service.get('/transactions', req.query)
    
        res.status(response.status).json(response.data)
      } catch (error: any) {
        console.error(error)
        res.status(error?.status || 500).json({
          error: "Failed to call transactions v2",
          cause: error?.response?.data ?? error,
        })
      }
    })

    router.post("/transactions", async (req, res) => {
      try {
        console.info("Using TransactionV2Service for POST /v2/transactions")
        const transactionV2Service = new TransactionV2Service()
        const response = await transactionV2Service.post("/transactions", req.body)
        res.status(response.status).json(response.data)
      } catch (error: any) {
        console.error(error)
        res.status(error?.status || 500).json({
          error: "Failed to proxy request to POST /v2/transactions",
          cause: error?.response?.data ?? error,
        })
      }
    })

    router.delete("/transactions/:id", async (req, res) => {
      try {
        console.info("DELETE to /transactions")
    
        const transactionV2Service = new TransactionV2Service()
        const parsedBody = transactionV2Service.parseBody(req.body)
    
        const response = await transactionV2Service.delete(`/transactions/${req.params.id}`, parsedBody)
    
        res.status(response.status).json(response.data)
      } catch (error: any) {
        console.error(error)
        res.status(error?.status || 500).json({
          error: "Failed to proxy request to /transactions",
          cause: error?.response?.data ?? error,
        })
      }
    })

    router.get("/transactions/latest", async (req, res) => {
        try {
            const transactionV2Service = new TransactionV2Service()
            const response = await transactionV2Service.get("/transactions/latest", req.query)
            res.status(response.status).json(response.data)
        } catch (error: any) {
            console.error(error)
            res.status(error?.status || 500).json({
            error: "Failed to proxy request to GET /v2/transactions/latest",
            cause: error?.response?.data ?? error,
            })
        }
    })

    router.get("/transactions/biggest", async (req, res) => {
        try {
            const transactionV2Service = new TransactionV2Service()
            const response = await transactionV2Service.get("/transactions/biggest", req.query)
            res.status(response.status).json(response.data)
        } catch (error: any) {
            console.error(error)
            res.status(error?.status || 500).json({
            error: "Failed to proxy request to GET /v2/transactions/biggest",
            cause: error?.response?.data ?? error,
            })
        }
    })

    router.get("/overview/by-month", async (req, res) => {
      try {
        const service =  new TransactionV2Service()
        const response = await service.overviewByMonth()
        res.json(response)
      } catch (error) {
        console.error(error)
        res.status(500).json({ error: "Failed to fetch data /overview/by-month", cause: error })
      }
    })
    
    router.get("/expense-comparsion-history", async (req, res) => {
      try {
        const service = new TransactionV2Service()
        const monthlyData = await service.getIncomeAndExpenseComparisonHistory()
        
        res.json(monthlyData.reverse())
      } catch (error) {
        console.error(error)
        res.status(500).json({ error: "Failed to fetch data /expense-comparsion-history, process.env.USE_TRANSACTIONS_V2 is: " + process.env.USE_TRANSACTIONS_V2, cause: error })
      }
    })
}