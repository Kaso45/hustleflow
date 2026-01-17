import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

const mockDisabledError = () =>
  Promise.reject(new Error('Mock contract API has been disabled. Please connect to the backend.'));

export const getContracts = async () => {
  if (USE_MOCK_API) return { data: [] };
  try {
    return await apiClient.get('/contracts');
  } catch (e) {
    console.error('Failed to fetch contracts', e);
    return { data: [] };
  }
};

export const createContract = async (data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/contracts', data);
};

export const updateContract = async (id, data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.put(`/contracts/${id}`, data);
};

export const deleteContract = async (id) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.delete(`/contracts/${id}`);
};

export default { getContracts, createContract, updateContract, deleteContract };