import client from 'prom-client';
import type { Request, Response, NextFunction } from 'express';

// Registry with default Node process/runtime metrics (CPU, memory, event loop
// lag, GC) plus the custom HTTP metrics below.
export const register = new client.Registry();
client.collectDefaultMetrics({ register });

const httpRequestsTotal = new client.Counter({
  name: 'http_requests_total',
  help: 'Total HTTP requests processed, by method, route and status.',
  labelNames: ['method', 'route', 'status'],
  registers: [register],
});

const httpRequestDuration = new client.Histogram({
  name: 'http_request_duration_seconds',
  help: 'HTTP request latency in seconds, by method and route.',
  labelNames: ['method', 'route'],
  buckets: [0.01, 0.05, 0.1, 0.3, 0.5, 1, 2, 5],
  registers: [register],
});

// Records request count and latency. Uses the matched route template
// (req.route.path) so per-id URLs don't blow up label cardinality.
export function metricsMiddleware(req: Request, res: Response, next: NextFunction): void {
  const end = httpRequestDuration.startTimer();
  res.on('finish', () => {
    const route = req.route?.path
      ? `${req.baseUrl}${req.route.path}`
      : req.baseUrl || 'unmatched';
    const labels = { method: req.method, route };
    httpRequestsTotal.inc({ ...labels, status: String(res.statusCode) });
    end(labels);
  });
  next();
}

// GET /metrics handler (Prometheus scrape target).
export async function metricsHandler(_req: Request, res: Response): Promise<void> {
  res.set('Content-Type', register.contentType);
  res.end(await register.metrics());
}
