import morgan from "morgan"
import express from "express"
import dotenv from "dotenv"
import routes from "./routes"
import { errorHandler } from "./errorMiddleware"

dotenv.config()

const app = express()
const PORT = process.env.PORT || 3000

app.use(morgan("combined"))
app.use(errorHandler)

app.use(express.json())
app.use("/api", routes)

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`)
})
