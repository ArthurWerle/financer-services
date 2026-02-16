import morgan from 'morgan';
import express from 'express';
import dotenv from 'dotenv';
import cors from 'cors';
import routes from './routes';
import { errorHandler } from './errorMiddleware';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(
  cors({
    origin: true, // This allows all origins
    credentials: true,
  })
);

app.use(morgan('combined'));
app.use(errorHandler);

app.use(express.json());
app.use('/api/bff', routes);

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
