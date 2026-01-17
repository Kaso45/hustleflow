import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

const mockDisabledError = () =>
  Promise.reject(new Error('Mock payroll API has been disabled. Please connect to the backend.'));

const sanitizePayrollFilters = (params = {}) => {
  const sanitized = { ...params };
  if (sanitized.status !== undefined && sanitized.status !== null) {
    const s = String(sanitized.status).trim();
    if (!s || s.toLowerCase() === 'all') {
      delete sanitized.status;
    } else {
      sanitized.status = s.toUpperCase();
    }
  }
  return sanitized;
};

export const getPayrolls = async (params = {}) => {
  if (USE_MOCK_API) return { data: [] };
  const sanitized = sanitizePayrollFilters(params);
  try {
    return await apiClient.get('/payrolls', { params: sanitized });
  } catch (e) {
    console.error('Failed to fetch payrolls', e);
    return { data: [] };
  }
};

export const generatePayroll = async (data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/payrolls/generate', data);
};

export const createPayroll = async (data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/payrolls', data);
};

export const updatePayroll = async (id, data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.put(`/payrolls/${id}`, data);
};

export const markAsPaid = async (id) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.patch(`/payrolls/${id}/pay`);
};

export default { getPayrolls, generatePayroll, updatePayroll, markAsPaid };