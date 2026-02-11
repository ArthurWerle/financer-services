import { Service } from "./Service";

export class AnalyticsService extends Service {
  constructor() {
    super(process.env.ANALYTICS_SERVICE_URL || "http://localhost:1234/api/v1")
  }
}
