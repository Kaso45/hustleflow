import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '../store/authStore'

// Auth pages
import LoginPage from '../pages/auth/LoginPage.vue'
import RegisterPage from '../pages/auth/RegisterPage.vue'

// Layout & pages sau login
import MainLayout from '../components/layout/MainLayout.vue'
import DashboardPage from '../pages/dashboard/DashboardPage.vue'
import PeopleEmployeesPage from '../pages/people/PeopleEmployeesPage.vue'
import PeopleDepartmentsPage from '../pages/people/PeopleDepartmentsPage.vue'
import PeopleContractsPage from '../pages/people/PeopleContractsPage.vue'
import AttendanceTimesheetsPage from '../pages/attendance/AttendanceTimesheetsPage.vue'
import AttendanceLeavesPage from '../pages/attendance/AttendanceLeavesPage.vue'
import WorkProjectsPage from '../pages/work/WorkProjectsPage.vue'
import WorkTasksPage from '../pages/work/WorkTasksPage.vue'
import PayrollOverviewPage from '../pages/payroll/PayrollOverviewPage.vue'
import PayrollGeneratePage from '../pages/payroll/PayrollGeneratePage.vue'
import PayrollDetailPage from '../pages/payroll/PayrollDetailPage.vue'
import PerformancePage from '../pages/performance/PerformancePage.vue'
import NotFoundPage from '../pages/NotFoundPage.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginPage,
    meta: { guestOnly: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterPage,
    meta: { guestOnly: true },
  },

  // Các route sau login dùng MainLayout
  {
    path: '/',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard',
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: DashboardPage,
      },
      {
        path: 'people/employees',
        name: 'PeopleEmployees',
        component: PeopleEmployeesPage,
      },
      {
        path: 'people/departments',
        name: 'PeopleDepartments',
        component: PeopleDepartmentsPage,
      },
      {
        path: 'people/contracts',
        name: 'PeopleContracts',
        component: PeopleContractsPage,
      },
      {
        path: 'attendance/timesheets',
        name: 'AttendanceTimesheets',
        component: AttendanceTimesheetsPage,
      },
      {
        path: 'attendance/leaves',
        name: 'AttendanceLeaves',
        component: AttendanceLeavesPage,
      },
      {
        path: 'work/projects',
        name: 'WorkProjects',
        component: WorkProjectsPage,
      },
      {
        path: 'work/tasks',
        name: 'WorkTasks',
        component: WorkTasksPage,
      },
      {
        path: 'payroll',
        name: 'PayrollOverview',
        component: PayrollOverviewPage,
      },
      {
        path: 'payroll/generate',
        name: 'PayrollGenerate',
        component: PayrollGeneratePage,
      },
      {
        path: 'payroll/detail',
        name: 'PayrollDetail',
        component: PayrollDetailPage,
      },
      {
        path: 'performance',
        name: 'Performance',
        component: PerformancePage,
      },
    ],
  },

  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFoundPage,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Guard login
router.beforeEach((to, from, next) => {
  const loggedIn = isAuthenticated()

  if (to.meta.requiresAuth && !loggedIn) {
    return next({ name: 'Login' })
  }

  if (to.meta.guestOnly && loggedIn) {
    return next({ name: 'Dashboard' })
  }

  return next()
})

export default router
