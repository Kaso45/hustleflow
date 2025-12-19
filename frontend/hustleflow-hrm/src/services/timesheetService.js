import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// Mock Data Generator
const generateMockTimesheets = () => {
  const timesheets = [];
  const today = new Date();
  const daysInMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0).getDate();

  // Tạo dữ liệu cho 15 nhân viên (ID 1-15)
  for (let empId = 1; empId <= 15; empId++) {
    // Tạo dữ liệu 5 ngày gần nhất cho mỗi người
    for (let d = 0; d < 5; d++) {
      const date = new Date(today);
      date.setDate(today.getDate() - d);
      const dateStr = date.toISOString().split('T')[0];

      // Logic ngẫu nhiên: Có người đi làm đúng giờ, có người đi trễ
      const isLate = Math.random() > 0.8;
      const checkIn = isLate ? "09:15" : "08:00";
      const checkOut = "17:30";
      
      timesheets.push({
        id: `${empId}-${dateStr}`,
        employeeId: empId,
        date: dateStr,
        clockIn: checkIn,
        clockOut: checkOut,
        totalHours: isLate ? 7.25 : 8.5,
        status: isLate ? "LATE" : "PRESENT", // PRESENT, LATE, ABSENT
        notes: isLate ? "Traffic jam" : ""
      });
    }
  }
  return timesheets;
};

let mockTimesheets = generateMockTimesheets();

export const getTimesheets = async (params = {}) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200));
    // Logic filter giả lập
    let result = [...mockTimesheets];
    if (params.employeeId) {
      result = result.filter(t => t.employeeId === params.employeeId);
    }
    return { data: result };
  }
  return apiClient.get('/timesheets', { params });
};

export const clockIn = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const newEntry = {
      id: Date.now(),
      ...data,
      clockOut: null,
      status: 'PRESENT',
      totalHours: 0
    };
    mockTimesheets.unshift(newEntry);
    return { data: newEntry };
  }
  return apiClient.post('/timesheets/clock-in', data);
};

export const clockOut = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    // Tìm bản ghi chưa clockout của user hôm nay (giả lập)
    return { data: { success: true, msg: "Clocked out" } };
  }
  return apiClient.patch('/timesheets/clock-out', data);
};

export default { getTimesheets, clockIn, clockOut };