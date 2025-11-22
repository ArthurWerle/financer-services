import { Service } from "./Service";

export class CategoryService extends Service {
  constructor() {
    super("http://category-service:8085/api")
  }
}