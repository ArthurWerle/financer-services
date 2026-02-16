import { Router } from "express";
import { TransactionV2Service } from "../services/TransactionV2Service";
import { CategoryService } from "../services/CategoryService";
import { Category } from "../types/category";
import { TransactionService } from "../services/TransactionService";
import { AnalyticsService } from "../services/AnalyticsService";

export function mountCategoryRoutes(router: Router) {
    router.get("/monthly-expenses-by-category", async (req, res) => {
      try {
        let result: Record<string, number>
    
        if (process.env.USE_TRANSACTIONS_V2 === "true") {
          const service = new TransactionV2Service()
          result = await service.getMonthlyExpensesByCategory()
        } else {
          const categoryService = new CategoryService()
          const { data: categories } = await categoryService.get<Category[]>("/category")
          const service = new TransactionService()
          result = await service.getMonthlyExpensesByCategory(categories)
        }
    
        res.json(result)
      } catch (error) {
        console.error(error)
        res.status(500).json({ error: "Failed to fetch data /total-values-by-category", cause: error })
      }
    })


    // Category Service Proxy Routes
    router.get("/category", async (req, res) => {
      try {
        if (process.env.USE_TRANSACTIONS_V2 === "true") {
          const transactionV2Service = new TransactionV2Service()
          const response = await transactionV2Service.get("/categories")
          res.status(response.status).json(response.data)
        } else {
          const categoryService = new CategoryService()
          const response = await categoryService.get("/category")
          res.status(response.status).json(response.data)
        }
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
        if (process.env.USE_TRANSACTIONS_V2 === "true") {
          const transactionV2Service = new TransactionV2Service()
          const response = await transactionV2Service.post("/categories", req.body)
          res.status(response.status).json(response.data)
        } else {
          const categoryService = new CategoryService()
          const response = await categoryService.post("/category", req.body)
          res.status(response.status).json(response.data)
        }
      } catch (error: any) {
        console.error(error)
        res.status(error?.status || 500).json({
          error: "Failed to proxy request to POST /category",
          cause: error?.response?.data ?? error,
        })
      }
    })

    router.get("/categories/average", async (req, res) => {
      try {
        const analyticsService = new AnalyticsService()
        const response = await analyticsService.get("/categories/average", req.query)
        res.status(response.status).json(response.data)
      } catch (error: any) {
        const status = error?.response?.status || 502
        const cause = error?.response?.data ?? error?.message ?? "Unknown error"
        console.error("Failed to proxy request to GET /categories/average:", cause)
        res.status(status).json({
          error: "Failed to proxy request to GET /categories/average",
          cause,
        })
      }
    })
}