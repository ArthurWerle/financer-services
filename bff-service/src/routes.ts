import { Router } from "express"
import axios from "axios"

const router = Router()

router.get("/test", async (req, res) => {
  try {
    const response1 = await axios.get(
      "http://category-service:8080/api/category"
    )
    const response2 = await axios.get(
      "http://transaction-service:8080/api/transactions"
    )

    res.json({
      categoryService: {
        status: response1.status,
        data: response1.data,
      },
      transactionsService: {
        status: response2.status,
        data: response2.data,
      },
    })
  } catch (error) {
    res.status(500).json({ error: "Failed to fetch data", cause: error })
  }
})

export default router
