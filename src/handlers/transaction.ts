import { Router } from 'express';
import { TransactionService } from '../services/TransactionService';

export function mountTransactionRoutes(router: Router) {
  router.get('/transactions', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.get('/transactions', req.query);

      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to call transactions v2',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.post('/transactions', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.post('/transactions', req.body);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to POST /transactions',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.get('/transactions/:id', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.get(`/transactions/${req.params.id}`);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to GET /transactions/:id',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.put('/transactions/:id', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.put(
        `/transactions/${req.params.id}`,
        req.body
      );
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to POST /transactions/:id',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.delete('/transactions/:id', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.delete(
        `/transactions/${req.params.id}`,
        req.body
      );

      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to /transactions/:id',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.post('/transactions/:id/prepay', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.post(
        `/transactions/${req.params.id}/prepay`,
        req.body
      );

      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to POST /transactions/:id/prepay',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.get('/transactions/latest', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.get('/transactions/latest', req.query);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to GET /transactions/latest',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.get('/transactions/biggest', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.get('/transactions/biggest', req.query);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error(error);
      res.status(error?.status || 500).json({
        error: 'Failed to proxy request to GET /transactions/biggest',
        cause: error?.response?.data ?? error,
      });
    }
  });

  router.get('/overview/by-month', async (req, res) => {
    try {
      const service = new TransactionService();
      const response = await service.overviewByMonth();
      res.json(response);
    } catch (error) {
      console.error(error);
      res.status(500).json({
        error: 'Failed to fetch data /overview/by-month',
        cause: error,
      });
    }
  });

  router.get('/expense-comparsion-history', async (req, res) => {
    try {
      const service = new TransactionService();
      const monthlyData = await service.getIncomeAndExpenseComparisonHistory();

      res.json(monthlyData.reverse());
    } catch (error) {
      console.error(error);
      res.status(500).json({
        error: 'Failed to fetch data /expense-comparsion-history',
        cause: error,
      });
    }
  });
}
