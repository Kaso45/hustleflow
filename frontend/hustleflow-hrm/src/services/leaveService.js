import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

const mockDisabledError = () =>
  Promise.reject(new Error('Mock leave API has been disabled. Please connect to the backend.'));

export const getLeaves = async (params = {}) => {
  if (USE_MOCK_API) return { data: [] };
  try {
    return await apiClient.get('/leaves', { params });
  } catch (e) {
    console.error('Failed to fetch leaves', e);
    return { data: [] };
  }
};

export const createLeave = async (data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/leaves', data);
};

export const updateLeaveStatus = async (id, status, comment = "") => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.put(`/leaves/${id}/status`, { status, managerComment: comment });
};

export default { getLeaves, createLeave, updateLeaveStatus };