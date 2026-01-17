import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

export const getEmployees = async (params = {}) => {
  if (USE_MOCK_API) return { data: [] };
  try {
    return await apiClient.get('/employees', { params });
  } catch (e) {
    console.error('Failed to fetch employees', e);
    return { data: [] };
  }
};

const mockDisabledError = () =>
  Promise.reject(new Error('Mock employee API has been disabled. Please connect to the backend.'));

export const createEmployee = async (employeeData) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/employees', employeeData);
};

export const updateEmployee = async (id, employeeData) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.put(`/employees/${id}`, employeeData);
};

export const deleteEmployee = async (id) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.delete(`/employees/${id}`);
};

export default { getEmployees, createEmployee, updateEmployee, deleteEmployee };