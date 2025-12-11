// src/services/employeeService.js
import apiClient from './apiClient';

export const getEmployees = (params = {}) => {
  // params: optional query params (e.g., ?page=...&department=...)
  return apiClient.get('/employees', { params });
};

export const getEmployeeById = (id) => {
  return apiClient.get(`/employees/${id}`);
};

export const createEmployee = (employeeData) => {
  // employeeData should follow defaultEmployee keys (see src/models/employeeModel.js)
  return apiClient.post('/employees', employeeData);
};

export const updateEmployee = (id, employeeData) => {
  return apiClient.put(`/employees/${id}`, employeeData);
};

export const deleteEmployee = (id) => {
  return apiClient.delete(`/employees/${id}`);
};
