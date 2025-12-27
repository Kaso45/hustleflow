import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// --- MOCK DEPARTMENTS (Đồng bộ ID Manager với Employee Service) ---
let mockDepartments = [
  { id: 1, departmentName: "Sales", code: "SLS", description: "Driving revenue & growth", managerId: 12 }, // Paul Moore (Sales Lead)
  { id: 2, departmentName: "Engineering", code: "ENG", description: "Product development", managerId: 14 }, // Kevin Martin (CTO)
  { id: 3, departmentName: "HR", code: "HR", description: "People & Culture", managerId: 4 }, // Alice Green (HR Manager)
  { id: 4, departmentName: "Finance", code: "FIN", description: "Financial planning", managerId: 9 }, // James Miller (Finance Head)
  { id: 5, departmentName: "Marketing", code: "MKT", description: "Brand & Communication", managerId: 8 } // Sarah Taylor
];

export const getDepartments = async () => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200));
    return { data: mockDepartments };
  }
  return apiClient.get('/departments');
};

export const createDepartment = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const newDept = { ...data, id: Date.now() };
    mockDepartments.push(newDept);
    return { data: newDept };
  }
  return apiClient.post('/departments', data);
};

export const updateDepartment = async (id, data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const index = mockDepartments.findIndex(d => d.id === id);
    if (index !== -1) mockDepartments[index] = { ...mockDepartments[index], ...data };
    return { data: mockDepartments[index] };
  }
  return apiClient.put(`/departments/${id}`, data);
};

export const deleteDepartment = async (id) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    mockDepartments = mockDepartments.filter(d => d.id !== id);
    return { data: { success: true } };
  }
  return apiClient.delete(`/departments/${id}`);
};

export default { getDepartments, createDepartment, updateDepartment, deleteDepartment };