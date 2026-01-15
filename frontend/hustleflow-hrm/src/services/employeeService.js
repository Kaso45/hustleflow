import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

export const getEmployees = async (params = {}) => {
  if (USE_MOCK_API) return { data: [] };
  return apiClient.get('/employees', { params });
};

export const createEmployee = async (employeeData) => {
  if (USE_MOCK_API) return { data: { ...employeeData, id: Date.now() } };
  return apiClient.post('/employees', employeeData);
};

export const updateEmployee = async (id, employeeData) => {
  if (USE_MOCK_API) return { data: employeeData };
  // SỬA LỖI 404: Khớp với API /{employeeId}
  return apiClient.put(`/employees/${id}`, employeeData);
};

export const deleteEmployee = async (id) => {
  if (USE_MOCK_API) return { data: { success: true } };
  return apiClient.delete(`/employees/${id}`);
};

export default { getEmployees, createEmployee, updateEmployee, deleteEmployee };