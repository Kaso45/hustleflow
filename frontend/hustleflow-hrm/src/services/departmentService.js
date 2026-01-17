import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

export const getDepartments = async () => {
  if (USE_MOCK_API) return { data: [] };
  try {
    return await apiClient.get('/departments');
  } catch (e) {
    console.error('Failed to fetch departments', e);
    return { data: [] };
  }
};

const mockDisabledError = () =>
  Promise.reject(new Error('Mock department API has been disabled. Please connect to the backend.'));

export const createDepartment = async (data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/departments', data);
};

export const updateDepartment = async (id, data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.put(`/departments/${id}`, data);
};

export const deleteDepartment = async (id) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.delete(`/departments/${id}`);
};

export default { getDepartments, createDepartment, updateDepartment, deleteDepartment };