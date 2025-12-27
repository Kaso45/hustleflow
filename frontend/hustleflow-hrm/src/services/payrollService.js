import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

// Mock Data Global
let mockPayrolls = [];

// Hàm tạo dữ liệu giả: Sinh ra bảng lương cho 12 tháng của năm hiện tại
const seedMockData = () => {
  if (mockPayrolls.length > 0) return;
  
  const currentYear = new Date().getFullYear();

  // Tạo dữ liệu cho 12 tháng
  for (let month = 1; month <= 12; month++) {
    // Cho 15 nhân viên
    for (let empId = 1; empId <= 15; empId++) {
      // Logic random lương để trông thật hơn
      // Lương cơ bản dao động tùy cấp bậc (ID càng cao lương càng cao giả định)
      const baseSalary = 10000000 + (empId * 1500000); 
      
      // Thưởng ngẫu nhiên cho một số người, một số tháng
      const hasBonus = Math.random() > 0.6;
      const bonus = hasBonus ? Math.floor(Math.random() * 5000000) : 0;
      
      // Phạt đi muộn (random ít thôi)
      const deduction = Math.random() > 0.8 ? 200000 : 0;
      
      // Trạng thái: Các tháng cũ (1 -> tháng hiện tại -1) thì PAID, tháng này thì Random, tháng sau thì chưa có
      let status = 'UNPAID';
      const thisMonth = new Date().getMonth() + 1;
      if (month < thisMonth) status = 'PAID';
      else if (month === thisMonth) status = Math.random() > 0.5 ? 'PAID' : 'UNPAID';

      mockPayrolls.push({
        id: parseInt(`${currentYear}${month}${empId}`), // Unique fake ID: 20251201
        employeeId: empId,
        month: month,
        year: currentYear,
        baseSalary: baseSalary,
        bonus: bonus,
        deduction: deduction,
        netSalary: baseSalary + bonus - deduction,
        status: status,
        paymentDate: status === 'PAID' ? `${currentYear}-${String(month).padStart(2,'0')}-28` : null
      });
    }
  }
};

seedMockData();

// --- API METHODS ---

export const getPayrolls = async (params = {}) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 200)); // Delay nhẹ tạo cảm giác load
    
    // Logic filter giả lập (Lưu ý: Dept filter sẽ làm ở Frontend Page vì bảng này chỉ có empId)
    let result = [...mockPayrolls];
    if (params.month) result = result.filter(p => p.month === parseInt(params.month));
    if (params.year) result = result.filter(p => p.year === parseInt(params.year));
    if (params.status && params.status !== 'All') result = result.filter(p => p.status === params.status);
    
    return { data: result };
  }
  return apiClient.get('/payrolls', { params });
};

// API Generate (Backend tính toán)
// Vì mình đã seed data 12 tháng rồi nên hàm này mock chỉ trả về success
export const generatePayroll = async (data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 500));
    return { data: { message: "Generated successfully" } };
  }
  return apiClient.post('/payrolls/generate', data);
};

export const updatePayroll = async (id, data) => {
  if (USE_MOCK_API) {
    await new Promise(r => setTimeout(r, 300));
    const index = mockPayrolls.findIndex(p => p.id === id);
    if (index !== -1) {
      // Merge data
      const updated = { ...mockPayrolls[index], ...data };
      // Recalculate Net Salary
      updated.netSalary = Number(updated.baseSalary) + Number(updated.bonus || 0) - Number(updated.deduction || 0);
      
      mockPayrolls[index] = updated;
      return { data: mockPayrolls[index] };
    }
    return { error: "Not found" };
  }
  return apiClient.put(`/payrolls/${id}`, data);
};

export const markAsPaid = async (id) => {
  if (USE_MOCK_API) {
    const idx = mockPayrolls.findIndex(p => p.id === id);
    if (idx !== -1) {
      mockPayrolls[idx].status = 'PAID';
      mockPayrolls[idx].paymentDate = new Date().toISOString().split('T')[0];
    }
    return { data: { success: true } };
  }
  return apiClient.patch(`/payrolls/${id}/pay`);
};

export default { getPayrolls, generatePayroll, updatePayroll, markAsPaid };