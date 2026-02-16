import { Router } from "express";
import { CategoryService } from "../services/CategoryService";
import { TransactionV2Service } from "../services/TransactionV2Service";
import { AnalyticsService } from "../services/AnalyticsService";

export function mountTypeRoutes(router: Router) {
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
}