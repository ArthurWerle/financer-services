import "./tracing"
import morgan from "morgan"
import express from "express"
import dotenv from "dotenv"
import cors from "cors"
import routes from "./routes"
import { errorHandler } from "./errorMiddleware"
import { proxyMiddleware } from "./proxyMiddleware"

dotenv.config()

const app = express()
const PORT = process.env.PORT || 3000

app.use(
  cors({
    origin: true, // This allows all origins
    credentials: true
  })
)

app.use(morgan("combined"))
app.use(errorHandler)

// BFF-specific routes (these take priority)
app.use(express.json())
app.use("/api/bff", routes)

// Proxy middleware for all other routes (catch-all)
// This proxies requests to the appropriate backend service
// based on the URL path pattern
app.use(proxyMiddleware)

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`)
  console.log(`BFF routes available at /api/bff/*`)
  console.log(`Proxying other requests to backend services`)
})
