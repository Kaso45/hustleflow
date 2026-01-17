import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

const mockDisabledError = () =>
  Promise.reject(new Error('Mock timesheet API has been disabled. Please connect to the backend.'));

export const getTimesheets = async (employeeId, month, year) => {
  if (USE_MOCK_API) return { data: [] };
  return apiClient.get('/timesheets', {
    params: {
      employeeId,
      month,
      year,
    },
  });
};

export const clockIn = async (payload) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/timesheets/clock-in', payload);
};

export const clockOut = async (payload) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.patch('/timesheets/clock-out', payload);
};

export default { getTimesheets, clockIn, clockOut };