# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a microservices-based personal finance management application. It's intentionally over-engineered for educational purposes to explore microservices architecture, Docker, self-hosting with Portainer, and multiple programming languages.

## Architecture

The system consists of three microservices:

1. **category-service** (Go + Gorilla Mux + GORM)
   - Manages categories and transaction types
   - Exposes REST API at port 8085
   - Uses PostgreSQL for persistence
   - Health check: `GET /api/healthcheck`

2. **transaction-service** (Kotlin + Spring Boot)
   - Handles transactions and recurring transactions
   - Exposes REST API at port 8080 (mapped to host 8081)
   - Uses PostgreSQL for persistence and Redis for caching
   - Health check: `GET /actuator/health`
   - Includes Spring Boot Actuator for monitoring
   - Caching enabled via `@EnableCaching`

3. **bff-service** (Node.js 20 + TypeScript + Express)
   - Backend-for-frontend orchestration layer
   - Aggregates data from both microservices
   - Exposes REST API at port 3000 (mapped to host 8082)
   - Base path: `/api/bff`

### Supporting Infrastructure

- **PostgreSQL**: Shared database (port 5432)
- **Redis**: Cache for transaction-service (port 6379)
- **OpenTelemetry Collector**: Traces collection (ports 4317/4318)
- **Prometheus**: Metrics collection (port 9090)
- **Grafana**: Metrics visualization (port 3000)

### Service Communication

- BFF service calls category-service and transaction-service via HTTP
- All services emit OpenTelemetry traces to `otel-collector`
- Services are connected via Docker networks: `database` and `back-end`

## Development Commands

### Running the Application

```bash
# Start all services with Docker Compose
docker compose up --build

# Use stack.env for environment variables (required by Portainer)
```

### BFF Service (Node.js/TypeScript)

```bash
cd bff-service

# Development with hot-reload
npm run dev

# Build TypeScript
npm run build

# Production
npm start

# Format code
npm run format
```

### Category Service (Go)

```bash
cd category-service

# Run locally (requires PostgreSQL)
go run main.go

# Build binary
go build -o category-service

# Install dependencies
go mod download
```

### Transaction Service (Kotlin/Spring Boot)

```bash
cd transaction-service

# Run tests
./gradlew test

# Build JAR
./gradlew bootJar

# Run locally
./gradlew bootRun

# Enable git hooks for this service
git config core.hooksPath .githooks
```

## Key Implementation Details

### BFF Service Aggregation Pattern

The BFF service implements several aggregation endpoints:
- `/api/bff/overview/by-month` - Combines transactions and recurring transactions for monthly overview
- `/api/bff/overview/by-week` - Weekly overview with percentage variations
- `/api/bff/all-transactions` - Merges both transaction types
- `/api/bff/monthly-expenses-by-category` - Aggregates expenses by category using category-service data

### Transaction Types

Both services handle two transaction types:
- Regular transactions (one-time)
- Recurring transactions (repeating)

The BFF often merges these in responses by spreading both arrays.

### Database Configuration

All services use the same PostgreSQL instance:
- Host: `postgres` (in Docker network)
- Database: `mordor`
- User: `admin`
- Password: `admin`

### Observability

All services are instrumented with OpenTelemetry:
- Set `OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317`
- Set `OTEL_SERVICE_NAME` to the respective service name
- Traces flow to Prometheus and can be viewed in Grafana

## Port Reference

- 5432: PostgreSQL
- 6379: Redis
- 8085: category-service
- 8081: transaction-service (internal 8080)
- 8082: bff-service (internal 3000)
- 9090: Prometheus
- 3000: Grafana
- 4317/4318: OpenTelemetry Collector

## Environment Variables

Environment variables are configured in `stack.env` (required for Portainer deployment).
