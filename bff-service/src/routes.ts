import { Router } from "express"
import { TransactionService } from "./services/TransactionService"
import { CategoryService } from "./services/CategoryService"
import { Category } from "./types/category"
import { TransactionV2Service } from "./services/TransactionV2Service"
import { AnalyticsService } from "./services/AnalyticsService"

const router = Router()

router.get("/overview/by-month", async (req, res) => {
  try {
    const service = process.env.USE_TRANSACTIONS_V2 === "true" ? new TransactionV2Service() : new TransactionService()
    const response = await service.overviewByMonth()
    res.json(response)
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /overview/by-month", cause: error })
  }
})

router.get("/overview/by-week", async (req, res) => {
  try { 
    const service = new TransactionService()

    const currentWeek = new Date(new Date().setDate(new Date().getDate() - new Date().getDay())).toISOString().slice(0, 10)
    const { totalExpenseValue, totalIncomeValue } = await service.getTotalValuesByPeriod({ period: 'by-week', date: currentWeek })

    const lastWeek = new Date(new Date().setDate(new Date().getDate() - 7)).toISOString().slice(0, 10)
    const { 
      totalExpenseValue: lastWeekTotalExpenseValue, 
      totalIncomeValue: lastWeekTotalIncomeValue 
    } = await service.getTotalValuesByPeriod({ period: 'by-week', date: lastWeek })

    res.json({
      income: {
        currentWeek: totalIncomeValue,
        lastWeek: lastWeekTotalIncomeValue,
        percentageVariation: ((totalIncomeValue - lastWeekTotalIncomeValue) / lastWeekTotalIncomeValue) * 100
      },
      expense: {
        currentWeek: totalExpenseValue,
        lastWeek: lastWeekTotalExpenseValue,
        percentageVariation: ((totalExpenseValue - lastWeekTotalExpenseValue) / lastWeekTotalExpenseValue) * 100
      },
    })
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /overview/by-week", cause: error })
  }
})

router.get("/expense-comparsion-history", async (req, res) => {
  try {
    const service = process.env.USE_TRANSACTIONS_V2 === "true" ? new TransactionV2Service() : new TransactionService()
    const monthlyData = await service.getIncomeAndExpenseComparisonHistory()
    
    res.json(monthlyData.reverse())
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /expense-comparsion-history, process.env.USE_TRANSACTIONS_V2 is: " + process.env.USE_TRANSACTIONS_V2, cause: error })
  }
})

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

router.get("/all-values", async (req, res) => {
  try {
    const service = new TransactionService()
    const { data: valuesByDay } = await service.get<number>("/combined-transactions/value/by-day")
    const { data: valuesByWeek } = await service.get<number>("/combined-transactions/value/by-week")
    const { data: valuesByMonth } = await service.get<number>("/combined-transactions/value/by-month")

    res.json({
      day: valuesByDay,
      week: valuesByWeek,
      month: valuesByMonth,
    })
  } catch (error) {
    console.error(error)
    res.status(500).json({ error: "Failed to fetch data /all-values", cause: error })
  }
})

router.get('/v2/transactions', async (req, res) => {
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

router.post("/recurring-transactions", async (req, res) => {
  try {
    const transactionService = new TransactionService()
    const response = await transactionService.post("/recurring-transactions", req.body)

    res.status(response.status).json(response.data)
  } catch (error: any) {
    console.error(error)
    res.status(error?.status || 500).json({
      error: "Failed to proxy request to /recurring-transactions",
      cause: error?.response?.data ?? error,
    })
  }
})

router.post("/transactions", async (req, res) => {
  try {
    console.info("POST to /transactions")
    const transactionService = new TransactionService()
    const response = await transactionService.post("/transactions", req.body)

    const transactionV2Service = new TransactionV2Service()
    const parsedBody = transactionV2Service.parseBody(req.body)

    void transactionV2Service.post("/transactions", parsedBody)
      .then((responseFromV2) => {
        if (responseFromV2.status < 300) console.log('[debug] post to v2/transactions was successful')
        else console.log('[debug] post to v2/transactions has an error', responseFromV2.data)
      })
      .catch((v2Error: any) => {
        console.error('[transactions] failed to post to v2/transactions', v2Error?.response?.data ?? v2Error)
      })

    res.status(response.status).json(response.data)
  } catch (error: any) {
    console.error(error)
    res.status(error?.status || 500).json({
      error: "Failed to proxy request to /transactions",
      cause: error?.response?.data ?? error,
    })
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

router.get("/type", async (req, res) => {
  try {
    const categoryService = new CategoryService()
    const response = await categoryService.get("/type")
    res.status(response.status).json(response.data)
  } catch (error: any) {
    console.error(error)
    res.status(error?.status || 500).json({
      error: "Failed to proxy request to GET /type",
      cause: error?.response?.data ?? error,
    })
  }
})

// Transaction Service V1 Proxy Routes
router.get("/combined-transactions/all", async (req, res) => {
  try {
    const transactionService = new TransactionService()
    const response = await transactionService.get("/combined-transactions/all", req.query)
    res.status(response.status).json(response.data)
  } catch (error: any) {
    console.error(error)
    res.status(error?.status || 500).json({
      error: "Failed to proxy request to GET /combined-transactions/all",
      cause: error?.response?.data ?? error,
    })
  }
})

router.get("/combined-transactions/latest/3", async (req, res) => {
  try {
    const transactionService = new TransactionService()
    const response = await transactionService.get("/combined-transactions/latest/3")
    res.status(response.status).json(response.data)
  } catch (error: any) {
    console.error(error)
    res.status(error?.status || 500).json({
      error: "Failed to proxy request to GET /combined-transactions/latest/3",
      cause: error?.response?.data ?? error,
    })
  }
})

router.get("/combined-transactions/biggest/3", async (req, res) => {
  try {
    const transactionService = new TransactionService()
    const response = await transactionService.get("/combined-transactions/biggest/3")
    res.status(response.status).json(response.data)
  } catch (error: any) {
    console.error(error)
    res.status(error?.status || 500).json({
      error: "Failed to proxy request to GET /combined-transactions/biggest/3",
      cause: error?.response?.data ?? error,
    })
  }
})

// Transaction Service V2 Proxy Routes
router.post("/v2/transactions", async (req, res) => {
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

router.get("/v2/transactions/latest", async (req, res) => {
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

router.get("/v2/transactions/biggest", async (req, res) => {
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

// Analytics Service Proxy Routes
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

router.get("/types/average", async (req, res) => {
  try {
    let result: any

    if (process.env.USE_TRANSACTIONS_V2 === "true") {
      const service = new TransactionV2Service()
      result = await service.get("/transactions/average/by-type", req.query)
    } else {
      const analyticsService = new AnalyticsService()
      result = await analyticsService.get("/types/average", req.query)
    }

    res.status(result.status).json(result.data)
  } catch (error: any) {
    const status = error?.response?.status || 502
    const cause = error?.response?.data ?? error?.message ?? "Unknown error"
    console.error("Failed to proxy request to GET /types/average:", cause)
    res.status(status).json({
      error: "Failed to proxy request to GET /types/average",
      cause,
    })
  }
})

export default router
