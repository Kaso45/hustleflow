import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

const mockDisabledError = () =>
  Promise.reject(new Error('Mock task API has been disabled. Please connect to the backend.'));

export const getTasks = async (params = {}) => {
  if (USE_MOCK_API) return { data: [] };
  try {
    return await apiClient.get('/tasks', { params });
  } catch (e) {
    console.error('Failed to fetch tasks', e);
    return { data: [] };
  }
};

export const createTask = async (data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/tasks', data);
};

export const updateTask = async (id, data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.patch(`/tasks/${id}`, data);
};

export const deleteTask = async (id) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.delete(`/tasks/${id}`);
};

export default { getTasks, createTask, updateTask, deleteTask };

