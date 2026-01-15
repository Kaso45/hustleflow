import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

export const getTimesheets = async (employeeId, month, year) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200));
    return { data: [] };
  }
  // BẮT BUỘC gửi params vì Backend Java của bạn yêu cầu @RequestParam
  return apiClient.get('/timesheets', {
    params: { 
      employeeId: employeeId, 
      month: month, 
      year: year 
    }
  });
};

export const clockIn = async (payload) => {
  if (USE_MOCK_API) return { data: { ...payload, checkIn: '08:00', status: 'ON_TIME' } };
  return apiClient.post('/timesheets/clock-in', payload);
};

export const clockOut = async (payload) => {
  if (USE_MOCK_API) return { data: true };
  return apiClient.patch('/timesheets/clock-out', payload);
};

export default { getTimesheets, clockIn, clockOut };