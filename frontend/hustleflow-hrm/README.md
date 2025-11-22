# Vue 3 + Vite

This template should help get you started developing with Vue 3 in Vite.  
The template uses Vue 3 `<script setup>` SFCs — check out the  
[script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about IDE Support for Vue in the  
[Vue Docs Scaling up Guide](https://vuejs.org/guide/scaling-up/tooling.html#ide-support).

---

## 🚀 Project Setup

### 1. Create project using Vite + Vue 3
npm create vite@latest hustleflow-hrm -- --template vue
cd hustleflow-hrm

### 2. Install dependencies 
npm install

### 3. Install Vue Router, Axios, Vuetify and vite-plugin-vuetify
npm install vue-router@4 axios
npm install vuetify@3 vite-plugin-vuetify
npm install sass sass-loader -D

### ▶️ Development Server

Run the app in development mode:

npm run dev

```sh

hustleflow-hrm/
├─ src/
│  ├─ assets/
│  │  └─ logo.svg
│  ├─ components/
│  │  ├─ layout/
│  │  │  ├─ MainLayout.vue          # Layout chính cho các trang sau login
│  │  │  └─ AppSidebar.vue          # Sidebar menu
│  │  └─ common/
│  │     ├─ AppTopbar.vue           # Thanh topbar (avatar, tên user, logout)
│  │     └─ AppPageHeader.vue       # Tiêu đề trang + breadcrumbs
│  ├─ pages/
│  │  ├─ auth/
│  │  │  ├─ LoginPage.vue           # /login
│  │  │  └─ RegisterPage.vue        # /register (optional)
│  │  ├─ dashboard/
│  │  │  └─ DashboardPage.vue       # /dashboard
│  │  ├─ people/
│  │  │  ├─ PeopleEmployeesPage.vue # /people/employees
│  │  │  ├─ PeopleDepartmentsPage.vue # /people/departments
│  │  │  └─ PeopleContractsPage.vue # /people/contracts
│  │  ├─ attendance/
│  │  │  ├─ AttendanceTimesheetsPage.vue # /attendance/timesheets
│  │  │  └─ AttendanceLeavesPage.vue     # /attendance/leaves
│  │  ├─ work/
│  │  │  ├─ WorkProjectsPage.vue    # /work/projects
│  │  │  └─ WorkTasksPage.vue       # /work/tasks
│  │  ├─ payroll/
│  │  │  ├─ PayrollOverviewPage.vue # /payroll
│  │  │  ├─ PayrollGeneratePage.vue # /payroll/generate
│  │  │  └─ PayrollDetailPage.vue   # /payroll/detail
│  │  ├─ performance/
│  │  │  └─ PerformancePage.vue     # /performance
│  │  └─ NotFoundPage.vue           # 404 page
│  ├─ router/
│  │  └─ index.js                   # Định nghĩa route, guard login
│  ├─ services/
│  │  ├─ apiClient.js               # axios instance + interceptors
│  │  ├─ authService.js             # gọi /auth/login, /auth/register
│  │  └─ employeeService.js         # ví dụ, gọi /employees (sau mở rộng thêm)
│  ├─ store/
│  │  └─ authStore.js               # quản lý token, user info đơn giản
│  ├─ App.vue
│  └─ main.js
├─ vite.config.js
└─ index.html
