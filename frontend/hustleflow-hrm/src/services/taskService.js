import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// Mock Data: Đây là SINGLE SOURCE OF TRUTH (Dữ liệu dùng chung)
// Dữ liệu sẽ tồn tại trong Ram chừng nào bạn chưa F5 lại trang
let mockTasks = [
  { id: 101, projectId: 1, assigneeId: 2, title: "Design Dashboard Mockup", deadline: "2025-01-10", priority: "HIGH", status: "DONE", progress: 100 },
  { id: 102, projectId: 1, assigneeId: 7, title: "Setup Vue 3 Project", deadline: "2025-01-12", priority: "HIGH", status: "DONE", progress: 100 },
  { id: 103, projectId: 1, assigneeId: 7, title: "Integrate Tailwind CSS", deadline: "2025-01-15", priority: "MEDIUM", status: "IN_PROGRESS", progress: 45 },
  { id: 104, projectId: 1, assigneeId: 11, title: "Write Unit Tests", deadline: "2025-02-01", priority: "LOW", status: "TODO", progress: 0 },
  
  // Dữ liệu mẫu project 2
  { id: 201, projectId: 2, assigneeId: 2, title: "Design App Icon", deadline: "2025-02-20", priority: "MEDIUM", status: "TODO", progress: 0 },
  { id: 202, projectId: 2, assigneeId: 7, title: "Setup Flutter Env", deadline: "2025-02-25", priority: "HIGH", status: "IN_PROGRESS", progress: 60 },
];

export const getTasks = async (params = {}) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200));
    let result = [...mockTasks];
    if (params.projectId) result = result.filter(t => t.projectId === params.projectId);
    if (params.assigneeId) result = result.filter(t => t.assigneeId === params.assigneeId);
    return { data: result };
  }
  return apiClient.get('/tasks', { params });
};

export const createTask = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200));
    // Tự động set progress = 0 nếu thiếu, 100 nếu status DONE
    let initProgress = data.progress || 0;
    if (data.status === 'DONE') initProgress = 100;

    const newTask = { ...data, id: Date.now(), progress: initProgress };
    mockTasks.push(newTask); // Push vào mảng dùng chung
    return { data: newTask };
  }
  return apiClient.post('/tasks', data);
};

export const updateTask = async (id, data) => {
  if (USE_MOCK_API) {
    const index = mockTasks.findIndex(t => t.id === id);
    if (index !== -1) {
        mockTasks[index] = { ...mockTasks[index], ...data };
        // Logic phụ: Nếu đổi sang DONE thì set 100%
        if (data.status === 'DONE') mockTasks[index].progress = 100;
        // Nếu set 100% thì đổi status DONE
        if (data.progress === 100) mockTasks[index].status = 'DONE';
    }
    return { data: mockTasks[index] };
  }
  return apiClient.patch(`/tasks/${id}`, data);
};

export const deleteTask = async (id) => {
  return apiClient.delete(`/tasks/${id}`);
};

export default { getTasks, createTask, updateTask, deleteTask };

