// src/router/routes.app.js

import MainLayout from '@/components/layout/MainLayout.vue'

export const appRoutes = {
  path: '/',
  component: MainLayout,
  meta: { requiresAuth: true },
  children: [
    { path: '', redirect: '/dashboard' },
    { path: 'dashboard', name: 'Dashboard', component: () => import('@/pages/dashboard/DashboardPage.vue') },

    // People Module
    { path: 'people/employees', name: 'PeopleEmployees', component: () => import('@/pages/people/PeopleEmployeesPage.vue') },
    { path: 'people/departments', name: 'PeopleDepartments', component: () => import('@/pages/people/PeopleDepartmentsPage.vue') },
    { path: 'people/contracts', name: 'PeopleContracts', component: () => import('@/pages/people/PeopleContractsPage.vue') },

    // Attendance Module
    { path: 'attendance/timesheets', name: 'AttendanceTimesheets', component: () => import('@/pages/attendance/AttendanceTimesheetsPage.vue') },
    { path: 'attendance/leaves', name: 'AttendanceLeaves', component: () => import('@/pages/attendance/AttendanceLeavesPage.vue') },

    // Work Module
    { path: 'work/projects', name: 'WorkProjects', component: () => import('@/pages/work/WorkProjectsPage.vue') },
    { path: 'work/tasks', name: 'WorkTasks', component: () => import('@/pages/work/WorkTasksPage.vue') },

    // Payroll Module
    { path: 'payroll', name: 'PayrollOverview', component: () => import('@/pages/payroll/PayrollOverviewPage.vue') },
    { path: 'payroll/generate', name: 'PayrollGenerate', component: () => import('@/pages/payroll/PayrollGeneratePage.vue') },
    { path: 'payroll/detail', name: 'PayrollDetail', component: () => import('@/pages/payroll/PayrollDetailPage.vue') },

    // Performance Module
    { path: 'performance', name: 'Performance', component: () => import('@/pages/performance/PerformancePage.vue') },
  ],
}
