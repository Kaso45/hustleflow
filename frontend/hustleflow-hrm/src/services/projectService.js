import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// Mock Data Nâng Cao (Thêm Progress, Tasks, Category)
let mockProjects = [
  { 
    id: 1, 
    projectName: "HustleFlow HRM Revamp", 
    description: "Redesigning the entire UI/UX for HR system", 
    startDate: "2025-01-01", 
    endDate: "2025-06-30", 
    status: "ACTIVE", 
    managerId: 14, // Kevin
    progress: 65,
    completedTasks: 12,
    totalTasks: 20,
    category: "Product Design"
  },
  { 
    id: 2, 
    projectName: "Mobile App Launch", 
    description: "Flutter app for iOS and Android", 
    startDate: "2025-02-15", 
    endDate: "2025-08-20", 
    status: "PENDING", 
    managerId: 2, // Jane
    progress: 15,
    completedTasks: 3,
    totalTasks: 45,
    category: "Development"
  },
  { 
    id: 3, 
    projectName: "Q1 Marketing Campaign", 
    description: "Social media and SEO push", 
    startDate: "2025-01-10", 
    endDate: "2025-03-31", 
    status: "COMPLETED", 
    managerId: 8, // Sarah
    progress: 100,
    completedTasks: 30,
    totalTasks: 30,
    category: "Marketing"
  },
  { 
    id: 4, 
    projectName: "Internal Audit 2025", 
    description: "Financial yearly audit preparation", 
    startDate: "2025-11-01", 
    endDate: "2025-12-31", 
    status: "INACTIVE", 
    managerId: 9, // James
    progress: 0,
    completedTasks: 0,
    totalTasks: 10,
    category: "Finance"
  },
  { 
    id: 5, 
    projectName: "AI Integration Module", 
    description: "Implementing OpenAI API for chatbots", 
    startDate: "2025-03-01", 
    endDate: "2025-09-01", 
    status: "ACTIVE", 
    managerId: 14, 
    progress: 40,
    completedTasks: 8,
    totalTasks: 15,
    category: "R&D"
  }
];

export const getProjects = async () => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200));
    return { data: mockProjects };
  }
  return apiClient.get('/projects');
};

export const createProject = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const newProj = { 
      ...data, 
      id: Date.now(),
      progress: 0,
      completedTasks: 0,
      totalTasks: 10, // Mặc định
      category: "General"
    };
    mockProjects.unshift(newProj);
    return { data: newProj };
  }
  return apiClient.post('/projects', data);
};

export const updateProject = async (id, data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const index = mockProjects.findIndex(p => p.id === id);
    if (index !== -1) mockProjects[index] = { ...mockProjects[index], ...data };
    return { data: mockProjects[index] };
  }
  return apiClient.put(`/projects/${id}`, data);
};

export const deleteProject = async (id) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    mockProjects = mockProjects.filter(p => p.id !== id);
    return { data: { success: true } };
  }
  return apiClient.delete(`/projects/${id}`);
};

export default { getProjects, createProject, updateProject, deleteProject };