import { Router } from 'express';
import { TransactionService } from '../services/TransactionService';

export function mountTypeRoutes(router: Router) {
  router.get('/types/average', async (req, res) => {
    try {
      const service = new TransactionService();
      const result: any = await service.get(
        '/transactions/average/by-type',
        req.query
      );

      res.status(result.status).json(result.data);
    } catch (error: any) {
      const status = error?.response?.status || 502;
      const cause = error?.response?.data ?? error?.message ?? 'Unknown error';
      console.error('Failed to proxy request to GET /types/average:', cause);
      res.status(status).json({
        error: 'Failed to proxy request to GET /types/average',
        cause,
      });
    }
  });
}
