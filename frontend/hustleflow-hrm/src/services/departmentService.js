import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// --- MOCK DEPARTMENTS ---
let mockDepartments = [
  { id: 1, departmentName: "Sales", code: "SLS", description: "Driving revenue & growth", managerId: 12 },
  { id: 2, departmentName: "Engineering", code: "ENG", description: "Product development", managerId: 14 },
  { id: 3, departmentName: "HR", code: "HR", description: "People & Culture", managerId: 4 },
  { id: 4, departmentName: "Finance", code: "FIN", description: "Financial planning", managerId: 9 },
  { id: 5, departmentName: "Marketing", code: "MKT", description: "Brand & Communication", managerId: 8 }
];

export const getDepartments = async () => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200));
    return { data: mockDepartments };
  }
  // API thật: GET /api/departments
  return apiClient.get('/departments');
};

export const createDepartment = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const newDept = { ...data, id: Date.now() };
    mockDepartments.push(newDept);
    return { data: newDept };
  }
  // API thật: POST /api/departments
  return apiClient.post('/departments', data);
};

export const updateDepartment = async (id, data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const index = mockDepartments.findIndex(d => d.id === id);
    if (index !== -1) mockDepartments[index] = { ...mockDepartments[index], ...data };
    return { data: mockDepartments[index] };
  }
  // API thật: PUT /api/departments/{id} 
  // Đảm bảo không có dấu ":" ở đây
  return apiClient.put(`/departments/${id}`, data);
};

export const deleteDepartment = async (id) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    mockDepartments = mockDepartments.filter(d => d.id !== id);
    return { data: { success: true } };
  }
  // API thật: DELETE /api/departments/{id}
  return apiClient.delete(`/departments/${id}`);
};

export default { getDepartments, createDepartment, updateDepartment, deleteDepartment };