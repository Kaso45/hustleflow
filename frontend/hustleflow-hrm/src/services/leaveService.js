import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// Mock Data
let mockLeaves = [
  { id: 1, employeeId: 1, leaveType: "ANNUAL", startDate: "2025-12-24", endDate: "2025-12-25", reason: "Christmas holiday", status: "APPROVED" },
  { id: 2, employeeId: 2, leaveType: "SICK", startDate: "2025-11-10", endDate: "2025-11-12", reason: "Flu", status: "PENDING" },
  { id: 3, employeeId: 3, leaveType: "UNPAID", startDate: "2025-12-01", endDate: "2025-12-05", reason: "Personal matter", status: "REJECTED" },
  { id: 4, employeeId: 4, leaveType: "ANNUAL", startDate: "2026-01-01", endDate: "2026-01-05", reason: "New Year Trip", status: "PENDING" }
];

export const getLeaves = async (params = {}) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200));
    let result = [...mockLeaves];
    if (params.status) result = result.filter(l => l.status === params.status);
    return { data: result };
  }
  return apiClient.get('/leaves', { params });
};

export const createLeave = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const newLeave = { ...data, id: Date.now(), status: 'PENDING' };
    mockLeaves.unshift(newLeave);
    return { data: newLeave };
  }
  return apiClient.post('/leaves', data);
};

export const updateLeaveStatus = async (id, status, comment = "") => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    
    // Sử dụng '==' thay vì '===' để so sánh được cả "1" và 1
    const index = mockLeaves.findIndex(l => l.id == id);
    
    if (index !== -1) {
      mockLeaves[index].status = status;
      mockLeaves[index].managerComment = comment;
      // Trả về bản sao object để đảm bảo tính nhất quán
      return { data: { ...mockLeaves[index] } };
    }
    return { error: "Not found" };
  }
  // API Docs: PUT /api/leaves/{leaveId}/status
  return apiClient.put(`/leaves/${id}/status`, { status, managerComment: comment });
};

export default { getLeaves, createLeave, updateLeaveStatus };