import { defineStore } from 'pinia';
import dashboardService from '@/services/dashboard/dashboardService';

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    employees: [],
    departments: [],
    leaves: [],
    tasks: [],
    projects: [],
    payrolls: [],

    kpis: {},
    attrition: {},
    empByDept: [],
    perfGroups: {},
    leaveByType: {},

    lastUpdated: null,
    loading: false,
    error: null
  }),

  actions: {
    async loadAll(month, year) {
      this.loading = true;
      this.error = null;

      try {
        const [
          employees,
          departments,
          leaves,
          tasks,
          projects,
          payrolls
        ] = await Promise.all([
          dashboardService.getEmployees(),
          dashboardService.getDepartments(),
          dashboardService.getLeaves(),
          dashboardService.getTasks(),
          dashboardService.getProjects(),
          dashboardService.getPayrolls({ month, year })
        ]);

        this.employees = employees;
        this.departments = departments;
        this.leaves = leaves;
        this.tasks = tasks;
        this.projects = projects;
        this.payrolls = payrolls;

        this.kpis = dashboardService.computeKPIs({
          employees,
          departments,
          leaves,
          tasks,
          projects
        });

        this.attrition = dashboardService.computeAttrition(employees);
        this.empByDept = dashboardService.computeEmployeesByDept(employees);
        this.perfGroups = dashboardService.computePerfGroups(employees);
        this.leaveByType = dashboardService.computeLeaveTypeDistribution(leaves);

        this.lastUpdated = new Date().toISOString();
      } catch (err) {
        this.error = err;
        console.error("Dashboard load failed:", err);
      }

      this.loading = false;
    }
  }
});
