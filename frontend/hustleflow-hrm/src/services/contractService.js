import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// --- GENERATE MOCK CONTRACTS ---
// Tạo hợp đồng giả cho 15 nhân viên (ID 1-15) để UI đẹp
const generateMockContracts = () => {
  const contracts = [];
  const types = ["FULL_TIME", "FULL_TIME", "FULL_TIME", "PART_TIME", "FREELANCE"];
  const statuses = ["ACTIVE", "ACTIVE", "ACTIVE", "PENDING", "EXPIRED"];
  
  for (let i = 1; i <= 15; i++) {
    contracts.push({
      id: 100 + i,
      employeeId: i, // Mapping 1-1 với ID nhân viên
      contractType: types[i % 5],
      startDate: "2024-01-01",
      endDate: "2026-01-01",
      baseSalary: 15000000 + (i * 1000000), // Lương ngẫu nhiên tăng dần
      status: statuses[i % 5],
      fileUrl: i % 2 === 0 ? `/docs/contract_${i}.pdf` : "" // Một số có file, một số không
    });
  }
  return contracts;
};

let mockContracts = generateMockContracts();

export const getContracts = async () => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 250));
    return { data: mockContracts };
  }
  return apiClient.get('/contracts');
};

export const createContract = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const newContract = { ...data, id: Date.now() };
    mockContracts.unshift(newContract);
    return { data: newContract };
  }
  return apiClient.post('/contracts', data);
};

export const updateContract = async (id, data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const index = mockContracts.findIndex(c => c.id === id);
    if (index !== -1) mockContracts[index] = { ...mockContracts[index], ...data };
    return { data: mockContracts[index] };
  }
  return apiClient.put(`/contracts/${id}`, data);
};

export const deleteContract = async (id) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    mockContracts = mockContracts.filter(c => c.id !== id);
    return { data: { success: true } };
  }
  return apiClient.delete(`/contracts/${id}`);
};

export default { getContracts, createContract, updateContract, deleteContract };