import { TransactionRequestParams } from "../types/transaction";
import { TransactionV2RequestParams } from "../types/transactions-v2";
import { Service } from "./Service";

export class TransactionV2Service extends Service {
  constructor() {
    super("http://transaction-service-v2:8080/api/v2")
  }

  parseBody(request: TransactionRequestParams) {
    const parsedParams: TransactionV2RequestParams = {
        amount: request.amount,
        type: request.typeId === 3 ? 'expense' : 'income',
        is_recurring: false,
        created_by_id: 1,
        category_id: request.categoryId,
        description: request.description,
        date: request.date,
    }

    return parsedParams
  }
}