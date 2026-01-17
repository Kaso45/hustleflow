import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

const mockEmployees = [
  { id: 1, name: 'Alice Nguyen', empDepartmentId: 2, empDepartment: 'Engineering', empDepartmentCode: 'ENG' },
  { id: 2, name: 'Bao Tran', empDepartmentId: 1, empDepartment: 'Sales', empDepartmentCode: 'SLS' },
  { id: 3, name: 'Chi Pham', empDepartmentId: 3, empDepartment: 'HR', empDepartmentCode: 'HR' },
  { id: 4, name: 'Dat Le', empDepartmentId: 4, empDepartment: 'Finance', empDepartmentCode: 'FIN' },
  { id: 5, name: 'Emmy Do', empDepartmentId: 5, empDepartment: 'Marketing', empDepartmentCode: 'MKT' },
  { id: 6, name: 'Huy Vo', empDepartmentId: 2, empDepartment: 'Engineering', empDepartmentCode: 'ENG' },
];

export const getEmployees = async (params = {}) => {
  if (USE_MOCK_API) return { data: mockEmployees };
  try {
    const res = await apiClient.get('/employees', { params });
    if (Array.isArray(res?.data) && res.data.length > 0) return res;
    return { data: mockEmployees };
  } catch (e) {
    return { data: mockEmployees };
  }
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