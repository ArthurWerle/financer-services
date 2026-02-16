import { Router } from 'express';
import { mountTransactionRoutes } from './handlers/transaction';
import { mountCategoryRoutes } from './handlers/category';
import { mountTypeRoutes } from './handlers/type';

const router = Router();

mountTransactionRoutes(router);
mountCategoryRoutes(router);
mountTypeRoutes(router);

export default router;
