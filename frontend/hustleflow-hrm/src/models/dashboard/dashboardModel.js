// src/models/dashboard/dashboardModel.js

export function toEmployeeModel(raw) {
  return {
    id: raw.EmpNumber ?? null,
    empNumber: raw.EmpNumber ?? null,
    name: raw.fullName ?? raw.EmpName ?? `Employee ${raw.EmpNumber}`,
    department: raw.EmpDepartment ?? 'Unknown',
    gender: raw.Gender ?? '',
    performanceScore: raw.Performance_Score ?? raw.PerformanceRating ?? null,
    attrition: raw.Attrition ?? raw.Resigned ?? false,
    createdAt: raw.createdAt ?? null,
    raw
  };
}
