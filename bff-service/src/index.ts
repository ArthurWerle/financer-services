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

const allowedOrigins = ['http://localhost:3000', 'http://192.168.2.125:3000']

const corsOptions = {
  origin: function (origin: any, callback: any) {
    if (allowedOrigins.indexOf(origin) !== -1 || !origin) {
      callback(null, true);
    } else {
      callback(new Error('Not allowed by CORS'));
    }
  },
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
