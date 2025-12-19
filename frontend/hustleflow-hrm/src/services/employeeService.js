import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// --- MOCK DATA CHUẨN (15 Nhân viên) ---
// Dùng ID từ 1 -> 15 để dễ map với các bên khác
let mockEmployees = [
  { id: 1, name: "John Doe", empDepartment: "Sales", empJobRole: "Sales Executive", empJobLevel: 2, performanceScore: 85, email: "john.doe@hustleflow.com" },
  { id: 2, name: "Jane Smith", empDepartment: "Engineering", empJobRole: "Software Engineer", empJobLevel: 3, performanceScore: 78, email: "jane.smith@hustleflow.com" },
  { id: 3, name: "Michael Brown", empDepartment: "Engineering", empJobRole: "DevOps Engineer", empJobLevel: 2, performanceScore: 65, email: "michael.b@hustleflow.com" },
  { id: 4, name: "Alice Green", empDepartment: "HR", empJobRole: "HR Manager", empJobLevel: 4, performanceScore: 88, email: "alice.g@hustleflow.com" },
  { id: 5, name: "Robert White", empDepartment: "Finance", empJobRole: "Accountant", empJobLevel: 5, performanceScore: 92, email: "robert.w@hustleflow.com" },
  { id: 6, name: "Emily Davis", empDepartment: "Sales", empJobRole: "Sales Rep", empJobLevel: 1, performanceScore: 70, email: "emily.d@hustleflow.com" },
  { id: 7, name: "David Wilson", empDepartment: "Engineering", empJobRole: "Frontend Dev", empJobLevel: 2, performanceScore: 80, email: "david.w@hustleflow.com" },
  { id: 8, name: "Sarah Taylor", empDepartment: "Marketing", empJobRole: "Content Writer", empJobLevel: 3, performanceScore: 75, email: "sarah.t@hustleflow.com" },
  { id: 9, name: "James Miller", empDepartment: "Finance", empJobRole: "Finance Head", empJobLevel: 5, performanceScore: 95, email: "james.m@hustleflow.com" },
  { id: 10, name: "Daniel Anderson", empDepartment: "HR", empJobRole: "Recruiter", empJobLevel: 2, performanceScore: 40, email: "daniel.a@hustleflow.com" },
  { id: 11, name: "Jessica Thomas", empDepartment: "Engineering", empJobRole: "QA Engineer", empJobLevel: 3, performanceScore: 82, email: "jessica.t@hustleflow.com" },
  { id: 12, name: "Paul Moore", empDepartment: "Sales", empJobRole: "Sales Lead", empJobLevel: 4, performanceScore: 74, email: "paul.m@hustleflow.com" },
  { id: 13, name: "Laura Jackson", empDepartment: "Marketing", empJobRole: "SEO Specialist", empJobLevel: 2, performanceScore: 83, email: "laura.j@hustleflow.com" },
  { id: 14, name: "Kevin Martin", empDepartment: "Engineering", empJobRole: "CTO", empJobLevel: 5, performanceScore: 98, email: "kevin.m@hustleflow.com" },
  { id: 15, name: "Lisa Thompson", empDepartment: "Finance", empJobRole: "Analyst", empJobLevel: 2, performanceScore: 71, email: "lisa.t@hustleflow.com" }
];

export const getEmployees = async (params = {}) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 150));
    return { data: JSON.parse(JSON.stringify(mockEmployees)) }; // Deep copy để tránh mutation
  }
  return apiClient.get('/employees', { params });
};

export const createEmployee = async (employeeData) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const newEmp = { ...employeeData, id: Date.now() };
    mockEmployees.unshift(newEmp);
    return { data: newEmp };
  }
  return apiClient.post('/employees', employeeData);
};

export const updateEmployee = async (id, employeeData) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const index = mockEmployees.findIndex(e => e.id === id);
    if (index !== -1) mockEmployees[index] = { ...mockEmployees[index], ...employeeData };
    return { data: mockEmployees[index] };
  }
  return apiClient.put(`/employees/${id}`, employeeData);
};

export const deleteEmployee = async (id) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    mockEmployees = mockEmployees.filter(e => e.id !== id);
    return { data: { success: true } };
  }
  return apiClient.delete(`/employees/${id}`);
};

export default { getEmployees, createEmployee, updateEmployee, deleteEmployee };