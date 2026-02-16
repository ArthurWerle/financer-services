import { Router } from "express";
import { TransactionV2Service } from "../services/TransactionV2Service";
import { Category } from "../types/category";

export function mountCategoryRoutes(router: Router) {
    router.get("/monthly-expenses-by-category", async (req, res) => {
      try {
        let result: Record<string, number>
        const service = new TransactionV2Service()
        result = await service.getMonthlyExpensesByCategory()
    
        res.json(result)
      } catch (error) {
        console.error(error)
        res.status(500).json({ error: "Failed to fetch data /total-values-by-category", cause: error })
      }
    })


    // Category Service Proxy Routes
    router.get("/category", async (req, res) => {
      try {
        const transactionV2Service = new TransactionV2Service()
        const response = await transactionV2Service.get("/categories")
        res.status(response.status).json(response.data)
      } catch (error: any) {
        console.error(error)
        res.status(error?.status || 500).json({
          error: "Failed to proxy request to GET /category",
          cause: error?.response?.data ?? error,
        })
      }
    })
    
    router.post("/category", async (req, res) => {
      try {
        const transactionV2Service = new TransactionV2Service()
        const response = await transactionV2Service.post("/categories", req.body)
        res.status(response.status).json(response.data)
      } catch (error: any) {
        console.error(error)
        res.status(error?.status || 500).json({
          error: "Failed to proxy request to POST /category",
          cause: error?.response?.data ?? error,
        })
      }
    })
}