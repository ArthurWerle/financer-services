import { Router } from 'express';
import { TransactionService } from '../services/TransactionService';

export function mountSubcategoryRoutes(router: Router) {
  router.get('/monthly-expenses-by-subcategory', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.get<{
        subcategories: { subcategory_name: string; total: number }[];
      }>('/transactions/reports/monthly-expenses-by-subcategory', {
        month: req.query.month,
        year: req.query.year,
      });

      // Adapt the ordered array to the Record shape the frontend consumes;
      // JS objects preserve insertion order, so the chart keeps its sorting.
      res
        .status(response.status)
        .json(
          Object.fromEntries(
            response.data.subcategories.map((s) => [s.subcategory_name, s.total])
          )
        );
    } catch (error: any) {
      console.error(error);
      res.status(error?.response?.status || 500).json({
        error: 'Failed to fetch data /monthly-expenses-by-subcategory',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.get('/subcategories', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.get('/subcategories');
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to GET /subcategories',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.post('/subcategories', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.post('/subcategories', req.body);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to POST /subcategories',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.get('/subcategories/:id', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.get(`/subcategories/${req.params.id}`);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to GET /subcategories/:id',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.put('/subcategories/:id', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.put(
        `/subcategories/${req.params.id}`,
        req.body
      );
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to PUT /subcategories/:id',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.delete('/subcategories/:id', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.delete(
        `/subcategories/${req.params.id}`,
        req.body
      );
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to DELETE /subcategories/:id',
        cause: error?.response?.data ?? error,
      });
    }
  });
}
