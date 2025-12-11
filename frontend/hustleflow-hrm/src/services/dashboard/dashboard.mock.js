// src/services/dashboard/dashboard.mock.js
// Mock data/responses that match the API docs.
// Each exported function simulates the corresponding endpoint.

const nowIso = () => new Date().toISOString();

const mockEmployees = [
  {
    EmpNumber: 1,
    EmpDepartment: "Sales",
    Gender: "Male",
    Age: 35,
    EducationBackground: "Life Sciences",
    Performance_Score: 5,
    OverTime: true,
    NumCompaniesWorked: 2,
    EmpJobLevel: 2,
    EmpJobInvolvement: 2,
    EmpHourlyRate: 48,
    EmpJobSatisfaction: 3,
    EmpEnvironmentSatisfaction: 2,
    MaritalStatus: "Married",
    EmpJobRole: "Sales Executive",
    BusinessTravelFrequency: "Travel_Frequently",
    DistanceFromHome: 5,
    EmpEducationLevel: 4,
    EmpLastSalaryHikePercent: 22,
    EmpRelationshipSatisfaction: 3,
    TotalWorkExperienceInYears: 12,
    TrainingTimesLastYear: 2,
    EmpWorkLifeBalance: 3,
    ExperienceYearsAtThisCompany: 6,
    ExperienceYearsInCurrentRole: 3,
    YearsSinceLastPromotion: 1,
    YearsWithCurrManager: 2,
    Attrition: false,
    PerformanceRating: 4,
    createdAt: nowIso()
  },
  {
    EmpNumber: 2,
    EmpDepartment: "Engineering",
    Gender: "Female",
    Age: 29,
    Performance_Score: 4,
    Attrition: true,
    PerformanceRating: 3,
    createdAt: nowIso()
  },
  {
    EmpNumber: 3,
    EmpDepartment: "Engineering",
    Gender: "Male",
    Age: 31,
    Performance_Score: 3,
    Attrition: false,
    PerformanceRating: 2,
    createdAt: nowIso()
  },
  // ... add more mock items if desired
];

const mockDepartments = [
  { id: 1, departmentName: "Sales", code: "SALES", description: "Sales team", managerId: 10 },
  { id: 2, departmentName: "Engineering", code: "ENG", description: "Engineering", managerId: 12 },
];

const mockLeaves = [
  {
    id: 100,
    employeeId: 1,
    employeeName: "John Doe",
    leaveType: "ANNUAL",
    startDate: "2025-12-20",
    endDate: "2025-12-22",
    reason: "Family matters",
    status: "PENDING",
    createdAt: nowIso()
  },
  {
    id: 101,
    employeeId: 2,
    employeeName: "Jane Smith",
    leaveType: "SICK",
    startDate: "2025-11-10",
    endDate: "2025-11-11",
    reason: "Illness",
    status: "APPROVED",
    createdAt: nowIso()
  }
];

const mockTasks = [
  { id: 201, projectId: 10, assigneeId: 1, title: "Fix API Bug", description: "Login API returns 500", deadline: "2025-12-15T12:00:00", priority: "HIGH", status: "IN_PROGRESS", assigneeName: "John Doe" },
  { id: 202, projectId: 11, assigneeId: 3, title: "Design UI", description: "Create dashboard UI", deadline: "2025-12-20T12:00:00", priority: "MEDIUM", status: "TODO", assigneeName: "Alex" }
];

const mockProjects = [
  { id: 10, projectName: "HustleFlow Web", description: "HRM Web", startDate: "2025-01-01", endDate: "2025-06-01", status: "ACTIVE", managerId: 1 },
  { id: 11, projectName: "Mobile App", description: "HustleFlow Mobile", startDate: "2025-02-01", endDate: null, status: "ACTIVE", managerId: 2 }
];

const mockPayrolls = [
  { id: 301, employeeId: 1, month: 12, year: 2025, baseSalary: 10000000, bonus: 500000, deduction: 0, netSalary: 10500000, status: "PAID" },
  { id: 302, employeeId: 2, month: 12, year: 2025, baseSalary: 12000000, bonus: 0, deduction: 0, netSalary: 12000000, status: "UNPAID" }
];

export async function GET_employees() {
  // Simulate network delay
  await new Promise(r => setTimeout(r, 120));
  return JSON.parse(JSON.stringify(mockEmployees));
}

export async function GET_departments() {
  await new Promise(r => setTimeout(r, 80));
  return JSON.parse(JSON.stringify(mockDepartments));
}

export async function GET_leaves(params = {}) {
  // params may include status filter
  await new Promise(r => setTimeout(r, 80));
  if (params && params.status) {
    return mockLeaves.filter(l => l.status === params.status);
  }
  return JSON.parse(JSON.stringify(mockLeaves));
}

export async function GET_tasks(params = {}) {
  await new Promise(r => setTimeout(r, 80));
  if (params && params.status) {
    return mockTasks.filter(t => t.status === params.status);
  }
  return JSON.parse(JSON.stringify(mockTasks));
}

export async function GET_projects() {
  await new Promise(r => setTimeout(r, 80));
  return JSON.parse(JSON.stringify(mockProjects));
}

/**
 * GET /api/payrolls?month=...&year=...
 * -> return array of payrolls for given month/year
 */
export async function GET_payrolls(query = {}) {
  await new Promise(r => setTimeout(r, 80));
  const month = Number(query.month);
  const year = Number(query.year);
  if (!isNaN(month) && !isNaN(year)) {
    return mockPayrolls.filter(p => p.month === month && p.year === year);
  }
  return JSON.parse(JSON.stringify(mockPayrolls));
}

// Export single default object for convenience
export default {
  GET_employees,
  GET_departments,
  GET_leaves,
  GET_tasks,
  GET_projects,
  GET_payrolls
};
