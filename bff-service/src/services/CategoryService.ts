import { Service } from "./Service";

export class CategoryService extends Service {
  constructor() {
    super(process.env.CATEGORY_SERVICE_URL || "http://category-service:8080/api")
  }
}