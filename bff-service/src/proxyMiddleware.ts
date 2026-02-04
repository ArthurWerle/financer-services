import { createProxyMiddleware, Options } from "http-proxy-middleware"
import { Request, Response, NextFunction, RequestHandler } from "express"

// Service URLs - these should match your docker-compose service names
const CATEGORY_SERVICE_URL =
  process.env.CATEGORY_SERVICE_URL || "http://category-service:8080"
const TRANSACTION_SERVICE_URL =
  process.env.TRANSACTION_SERVICE_URL || "http://transaction-service:8080"
const TRANSACTION_SERVICE_V2_URL =
  process.env.TRANSACTION_SERVICE_V2_URL || "http://transaction-service-v2:8080"

// Route patterns and their target services
interface ProxyRoute {
  pattern: RegExp
  target: string
  pathRewrite?: Record<string, string>
}

const proxyRoutes: ProxyRoute[] = [
  // V2 transaction service routes
  {
    pattern: /^\/api\/v2\//,
    target: TRANSACTION_SERVICE_V2_URL
  },
  // Category service routes
  {
    pattern: /^\/api\/category/,
    target: CATEGORY_SERVICE_URL
  },
  {
    pattern: /^\/api\/healthcheck/,
    target: CATEGORY_SERVICE_URL
  },
  // Transaction service routes (v1)
  {
    pattern: /^\/api\/transactions/,
    target: TRANSACTION_SERVICE_URL
  },
  {
    pattern: /^\/api\/recurring-transactions/,
    target: TRANSACTION_SERVICE_URL
  },
  {
    pattern: /^\/api\/combined-transactions/,
    target: TRANSACTION_SERVICE_URL
  },
  {
    pattern: /^\/actuator/,
    target: TRANSACTION_SERVICE_URL
  }
]

// Find the target service for a given path
function getTargetForPath(path: string): ProxyRoute | undefined {
  return proxyRoutes.find((route) => route.pattern.test(path))
}

// Common proxy options
function getProxyOptions(target: string): Options {
  return {
    target,
    changeOrigin: true,
    // Preserve the original headers
    xfwd: true,
    // Log proxy events
    on: {
      proxyReq: (proxyReq, req) => {
        console.log(`[Proxy] ${req.method} ${req.url} -> ${target}${req.url}`)
      },
      proxyRes: (proxyRes, req) => {
        console.log(
          `[Proxy] ${req.method} ${req.url} <- ${proxyRes.statusCode}`
        )
      },
      error: (err, req, res) => {
        console.error(`[Proxy] Error proxying ${req.method} ${req.url}:`, err)
        if (res && "writeHead" in res) {
          const response = res as Response
          response.status(502).json({
            error: "Bad Gateway",
            message: `Failed to proxy request to upstream service`,
            path: req.url
          })
        }
      }
    }
  }
}

// Create a map of proxies for each unique target
const proxyCache = new Map<string, RequestHandler>()

function getOrCreateProxy(target: string): RequestHandler {
  if (!proxyCache.has(target)) {
    proxyCache.set(target, createProxyMiddleware(getProxyOptions(target)))
  }
  return proxyCache.get(target)!
}

// The main proxy middleware - acts as a catch-all
export function proxyMiddleware(
  req: Request,
  res: Response,
  next: NextFunction
): void {
  const route = getTargetForPath(req.path)

  if (route) {
    const proxy = getOrCreateProxy(route.target)
    proxy(req, res, next)
  } else {
    // No matching proxy route, return 404
    res.status(404).json({
      error: "Not Found",
      message: `No route found for ${req.method} ${req.path}`,
      hint: "This endpoint is not handled by the BFF or any proxied service"
    })
  }
}

// Export for testing/debugging
export { proxyRoutes, CATEGORY_SERVICE_URL, TRANSACTION_SERVICE_URL }
