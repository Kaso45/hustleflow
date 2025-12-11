// src/services/dashboard/dashboardService.js
import { USE_MOCK_API } from '@/config/appConfig';
import mock from './dashboard.mock';
import apiClient from '@/services/apiClient';
import { groupBy } from '@/utils/groupBy';

/**
 * All dashboard-related data fetchers.
 * When USE_MOCK_API = true → mock data is returned.
 * When USE_MOCK_API = false → apiClient calls backend.
 */

const BASE = '/api';

async function getEmployees() {
  if (USE_MOCK_API) return mock.GET_employees();
  const res = await apiClient.get(`${BASE}/employees`);
  return res.data;
}

async function getDepartments() {
  if (USE_MOCK_API) return mock.GET_departments();
  const res = await apiClient.get(`${BASE}/departments`);
  return res.data;
}

async function getLeaves(params = {}) {
  if (USE_MOCK_API) return mock.GET_leaves(params);
  const res = await apiClient.get(`${BASE}/leaves`, { params });
  return res.data;
}

async function getTasks(params = {}) {
  if (USE_MOCK_API) return mock.GET_tasks(params);
  const res = await apiClient.get(`${BASE}/tasks`, { params });
  return res.data;
}

async function getProjects() {
  if (USE_MOCK_API) return mock.GET_projects();
  const res = await apiClient.get(`${BASE}/projects`);
  return res.data;
}

async function getPayrolls(params = {}) {
  if (USE_MOCK_API) return mock.GET_payrolls(params);
  const res = await apiClient.get(`${BASE}/payrolls`, { params });
  return res.data;
}

/* ----------------------
    DASHBOARD ANALYTICS
----------------------- */

function computeKPIs({ employees = [], departments = [], leaves = [], tasks = [], projects = [] }) {
  return {
    totalEmployees: employees.length,
    totalDepartments: departments.length,
    pendingLeaves: leaves.filter(l => l.status === 'PENDING').length,
    activeTasks: tasks.filter(t => t.status === 'IN_PROGRESS').length,
    activeProjects: projects.filter(p => p.status === 'ACTIVE' || p.status === 'IN_PROGRESS').length,
  };
}

function computeAttrition(list = []) {
  const resigned = list.filter(e =>
    e.Attrition === true || e.Resigned === true || e.status === 'RESIGNED'
  ).length;

  const active = list.length - resigned;
  return { resigned, active: Math.max(active, 0) };
}

function computeEmployeesByDept(list = []) {
  const grouped = groupBy(list, 'EmpDepartment');
  return Object.keys(grouped).map(key => ({
    department: key,
    count: grouped[key].length,
  }));
}

function computePerfGroups(list = []) {
  return list.reduce((acc, e) => {
    const rating = (e.PerformanceRating ?? e.Performance_Score ?? 'Unknown').toString();
    acc[rating] = (acc[rating] || 0) + 1;
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
