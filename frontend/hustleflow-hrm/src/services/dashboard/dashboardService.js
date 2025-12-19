// src/services/dashboard/dashboardService.js
import { USE_MOCK_API } from '@/config/appConfig';
import { groupBy } from '@/utils/groupBy';

// Import các Services chuyên biệt (Single Source of Truth)
import employeeService from '@/services/employeeService';
import departmentService from '@/services/departmentService';
import leaveService from '@/services/leaveService';
import taskService from '@/services/taskService';
import projectService from '@/services/projectService';
import payrollService from '@/services/payrollService';

/* --------------------------------------------------------
   FETCHERS: Gọi trực tiếp từ các Service đã chuẩn hóa
-------------------------------------------------------- */

async function getEmployees() {
  const res = await employeeService.getEmployees();
  return res.data || [];
}

async function getDepartments() {
  const res = await departmentService.getDepartments();
  return res.data || [];
}

async function getLeaves(params = {}) {
  const res = await leaveService.getLeaves(params);
  return res.data || [];
}

async function getTasks(params = {}) {
  const res = await taskService.getTasks(params);
  return res.data || [];
}

async function getProjects() {
  const res = await projectService.getProjects();
  return res.data || [];
}

async function getPayrolls(params = {}) {
  // Payroll thường cần filter theo tháng, ở đây dashboard lấy mặc định hoặc all
  const res = await payrollService.getPayrolls(params);
  return res.data || [];
}

/* --------------------------------------------------------
    DASHBOARD ANALYTICS (Logic tính toán KPI)
    Giữ nguyên logic cũ để không làm hỏng giao diện Dashboard
-------------------------------------------------------- */

function computeKPIs({ employees = [], departments = [], leaves = [], tasks = [], projects = [] }) {
  return {
    totalEmployees: employees.length,
    totalDepartments: departments.length,
    // Tính số đơn nghỉ phép đang chờ duyệt
    pendingLeaves: leaves.filter(l => l.status === 'PENDING').length,
    // Tính task đang active (chưa DONE)
    activeTasks: tasks.filter(t => t.status !== 'DONE').length, 
    // Tính project đang chạy
    activeProjects: projects.filter(p => p.status === 'ACTIVE' || p.status === 'IN_PROGRESS').length,
  };
}

function computeAttrition(list = []) {
  // Logic giả định: đếm số người có field attrition=true (nếu có trong data mới)
  // Nếu data mới không có field này, trả về mặc định
  const resigned = list.filter(e => e.status === 'RESIGNED' || e.attrition === true).length;
  const active = list.length - resigned;
  return { resigned, active: Math.max(active, 0) };
}

function computeEmployeesByDept(list = []) {
  // Group theo 'empDepartment'
  const grouped = groupBy(list, 'empDepartment');
  return Object.keys(grouped).map(key => ({
    department: key || 'Unknown',
    count: grouped[key].length,
  }));
}

function computePerfGroups(list = []) {
  // Group theo performanceScore hoặc JobLevel (Giả lập scale 1-5)
  // Logic cũ của bạn map theo Rating 1-5, ta tận dụng logic tương đương
  return list.reduce((acc, e) => {
    // Chuyển score 0-100 thành rating 1-5 để vẽ biểu đồ tròn cũ
    let rating = 3; 
    if (e.performanceScore >= 90) rating = 5;
    else if (e.performanceScore >= 80) rating = 4;
    else if (e.performanceScore >= 60) rating = 3;
    else if (e.performanceScore >= 40) rating = 2;
    else rating = 1;

    const key = rating.toString();
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
}

function computeLeaveTypeDistribution(list = []) {
  return list.reduce((acc, e) => {
    const type = e.leaveType || 'OTHER';
    acc[type] = (acc[type] || 0) + 1;
    return acc;
  }, {});
}

export default {
  getEmployees,
  getDepartments,
  getLeaves,
  getTasks,
  getProjects,
  getPayrolls,

  computeKPIs,
  computeAttrition,
  computeEmployeesByDept,
  computePerfGroups,
  computeLeaveTypeDistribution
};