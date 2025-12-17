// src/services/dashboard/dashboard.mock.js
// Mock data/responses that match the API docs.
// Each exported function simulates the corresponding endpoint.

const nowIso = () => new Date().toISOString();

/* ================================
   EMPLOYEES (15 records)
   - GIỮ NGUYÊN 1–3
   - BỔ SUNG 4–15
================================ */

const mockEmployees = [
  // ===== EXISTING (1–3) =====
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

  // ===== ADDED (4–15) =====
  { EmpNumber: 4, EmpDepartment: "HR", Gender: "Female", Age: 28, Performance_Score: 4, Attrition: false, PerformanceRating: 4, createdAt: nowIso() },
  { EmpNumber: 5, EmpDepartment: "Finance", Gender: "Male", Age: 42, Performance_Score: 5, Attrition: false, PerformanceRating: 5, createdAt: nowIso() },
  { EmpNumber: 6, EmpDepartment: "Sales", Gender: "Female", Age: 33, Performance_Score: 3, Attrition: false, PerformanceRating: 3, createdAt: nowIso() },
  { EmpNumber: 7, EmpDepartment: "Engineering", Gender: "Male", Age: 26, Performance_Score: 4, Attrition: false, PerformanceRating: 4, createdAt: nowIso() },
  { EmpNumber: 8, EmpDepartment: "Marketing", Gender: "Female", Age: 30, Performance_Score: 4, Attrition: true, PerformanceRating: 3, createdAt: nowIso() },
  { EmpNumber: 9, EmpDepartment: "Finance", Gender: "Female", Age: 37, Performance_Score: 5, Attrition: false, PerformanceRating: 5, createdAt: nowIso() },
  { EmpNumber: 10, EmpDepartment: "HR", Gender: "Male", Age: 45, Performance_Score: 2, Attrition: true, PerformanceRating: 2, createdAt: nowIso() },
  { EmpNumber: 11, EmpDepartment: "Engineering", Gender: "Female", Age: 34, Performance_Score: 4, Attrition: false, PerformanceRating: 4, createdAt: nowIso() },
  { EmpNumber: 12, EmpDepartment: "Sales", Gender: "Male", Age: 39, Performance_Score: 3, Attrition: false, PerformanceRating: 3, createdAt: nowIso() },
  { EmpNumber: 13, EmpDepartment: "Marketing", Gender: "Male", Age: 27, Performance_Score: 4, Attrition: false, PerformanceRating: 4, createdAt: nowIso() },
  { EmpNumber: 14, EmpDepartment: "Engineering", Gender: "Male", Age: 41, Performance_Score: 5, Attrition: false, PerformanceRating: 5, createdAt: nowIso() },
  { EmpNumber: 15, EmpDepartment: "Finance", Gender: "Female", Age: 32, Performance_Score: 3, Attrition: false, PerformanceRating: 3, createdAt: nowIso() }
];

/* ================================
   DEPARTMENTS
================================ */

const mockDepartments = [
  { id: 1, departmentName: "Sales", code: "SALES", description: "Sales team", managerId: 10 },
  { id: 2, departmentName: "Engineering", code: "ENG", description: "Engineering", managerId: 12 },
  { id: 3, departmentName: "HR", code: "HR", description: "Human Resources", managerId: 11 },
  { id: 4, departmentName: "Finance", code: "FIN", description: "Finance team", managerId: 9 },
  { id: 5, departmentName: "Marketing", code: "MKT", description: "Marketing team", managerId: 8 }
];

/* ================================
   LEAVES
================================ */

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

/* ================================
   TASKS
================================ */

const mockTasks = [
  { id: 201, projectId: 10, assigneeId: 1, title: "Fix API Bug", description: "Login API returns 500", deadline: "2025-12-15T12:00:00", priority: "HIGH", status: "IN_PROGRESS", assigneeName: "John Doe" },
  { id: 202, projectId: 11, assigneeId: 3, title: "Design UI", description: "Create dashboard UI", deadline: "2025-12-20T12:00:00", priority: "MEDIUM", status: "TODO", assigneeName: "Alex" }
];

/* ================================
   PROJECTS
================================ */

const mockProjects = [
  { id: 10, projectName: "HustleFlow Web", description: "HRM Web", startDate: "2025-01-01", endDate: "2025-06-01", status: "ACTIVE", managerId: 1 },
  { id: 11, projectName: "HustleFlow Mobile", description: "Mobile App", startDate: "2025-02-01", endDate: null, status: "ACTIVE", managerId: 2 }
];

/* ================================
   PAYROLLS
================================ */

const mockPayrolls = [
  { id: 301, employeeId: 1, month: 12, year: 2025, baseSalary: 10000000, bonus: 500000, deduction: 0, netSalary: 10500000, status: "PAID" },
  { id: 302, employeeId: 2, month: 12, year: 2025, baseSalary: 12000000, bonus: 0, deduction: 0, netSalary: 12000000, status: "UNPAID" }
];

/* ================================
   API FUNCTIONS
================================ */

export async function GET_employees() {
  await new Promise(r => setTimeout(r, 120));
  return JSON.parse(JSON.stringify(mockEmployees));
}

export async function GET_departments() {
  await new Promise(r => setTimeout(r, 80));
  return JSON.parse(JSON.stringify(mockDepartments));
}

export async function GET_leaves(params = {}) {
  await new Promise(r => setTimeout(r, 80));
  if (params.status) return mockLeaves.filter(l => l.status === params.status);
  return JSON.parse(JSON.stringify(mockLeaves));
}

export async function GET_tasks(params = {}) {
  await new Promise(r => setTimeout(r, 80));
  if (params.status) return mockTasks.filter(t => t.status === params.status);
  return JSON.parse(JSON.stringify(mockTasks));
}

export async function GET_projects() {
  await new Promise(r => setTimeout(r, 80));
  return JSON.parse(JSON.stringify(mockProjects));
}

export async function GET_payrolls(query = {}) {
  await new Promise(r => setTimeout(r, 80));
  const { month, year } = query;
  if (month && year) {
    return mockPayrolls.filter(p => p.month === Number(month) && p.year === Number(year));
  }
  return JSON.parse(JSON.stringify(mockPayrolls));
}

/* ================================
   DEFAULT EXPORT
================================ */

export default {
  GET_employees,
  GET_departments,
  GET_leaves,
  GET_tasks,
  GET_projects,
  GET_payrolls
};
