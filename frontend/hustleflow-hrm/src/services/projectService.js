import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

const mockDisabledError = () =>
  Promise.reject(new Error('Mock project API has been disabled. Please connect to the backend.'));

export const getProjects = async () => {
  if (USE_MOCK_API) return { data: [] };
  try {
    return await apiClient.get('/projects');
  } catch (e) {
    console.error('Failed to fetch projects', e);
    return { data: [] };
  }
};

export const createProject = async (data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.post('/projects', data);
};

export const updateProject = async (id, data) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.put(`/projects/${id}`, data);
};

export const deleteProject = async (id) => {
  if (USE_MOCK_API) return mockDisabledError();
  return apiClient.delete(`/projects/${id}`);
};

export default { getProjects, createProject, updateProject, deleteProject };