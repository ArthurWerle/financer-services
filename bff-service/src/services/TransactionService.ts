import { Service } from "./Service";

export class TransactionService extends Service {
  constructor() {
    super("http://transaction-service:8080/api")
  }
}