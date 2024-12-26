import morgan from "morgan"
import express from "express"
import dotenv from "dotenv"
import routes from "./routes"
import cors from "cors"
import { errorHandler } from "./errorMiddleware"

dotenv.config()

const app = express()
const PORT = process.env.PORT || 3000

app.use(morgan("combined"))
app.use(errorHandler)

const corsOptions = {
  origin: 'http://localhost:3000',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  credentials: true
}

app.use(cors(corsOptions))

app.use(express.json())
app.use("/api/bff", routes)

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`)
})
