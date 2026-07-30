import morgan from 'morgan';
import express from 'express';
import dotenv from 'dotenv';
import cors from 'cors';
import cookieParser from 'cookie-parser';
import routes from './routes';
import { errorHandler } from './errorMiddleware';
import { metricsMiddleware, metricsHandler } from './metrics';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(
  cors({
    origin: true,
    credentials: true,
  })
);

app.use(morgan('combined'));
// Chat attachments are base64-encoded images/audio, well past the default
// 100kb JSON limit — allow larger bodies so /ai/scan uploads don't 413.
app.use(express.json({ limit: '15mb' }));
app.use(cookieParser());

// Observability: record request metrics for all routes and expose /metrics.
// Kept at the app root (outside /api/bff) so the scrape endpoint is not behind
// the session auth middleware. LAN-only, so no auth is needed on it.
app.use(metricsMiddleware);
app.get('/metrics', metricsHandler);

app.use('/api/bff', routes);

// Express error middleware must be registered after the routes it covers.
app.use(errorHandler);

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
