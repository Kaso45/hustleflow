<template>
  <div class="df-dashboard">
    <header class="df-header">
      <h1>HR Analytics Dashboard</h1>
      <div class="updated">Updated: {{ lastUpdatedDisplay }}</div>
    </header>

    <section class="summary-grid">
      <SummaryCard :icon="Users" title="Total Employees" :value="kpis.totalEmployees" />
      <SummaryCard :icon="Building2" title="Total Departments" :value="kpis.totalDepartments" />
      <SummaryCard :icon="Calendar" title="Pending Leaves" :value="kpis.pendingLeaves" />
      <SummaryCard :icon="Folder" title="Active Projects" :value="kpis.activeProjects" />
      <SummaryCard :icon="CheckCircle2" title="Active Tasks" :value="kpis.activeTasks" />
      <SummaryCard :icon="CreditCard" :title="`Payrolls (${payrollLabel})`" :value="payrollCount" />
    </section>

    <section class="charts-area">
      <div class="left-col">
        <div class="holo-card card">
          <h3>Employees by Department</h3>
          <EmployeeDeptChart :data="empByDept" />
        </div>

        <div class="row">
          <div class="holo-card card half">
            <h3>Performance Rating Distribution</h3>
            <PerformanceChart :data="perfGroups" />
          </div>
          <div class="holo-card card half">
            <h3>Leave Type Distribution</h3>
            <LeaveTypeChart :data="leaveByType" />
          </div>
        </div>
      </div>

      <div class="right-col">
        <div class="holo-card card">
          <h3>Attrition Rate</h3>
          <AttritionChart :data="attrition" />
        </div>

        
      </div>
    </section>

    <section class="recent-area">
      <RecentEmployees :employees="recentEmployees" />
      <RecentLeaves :leaves="recentLeaves" />
      <RecentTasks :tasks="recentTasks" />
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import SummaryCard from './components/SummaryCard.vue';

import { Users, Building2, Calendar, Folder, CheckCircle2, CreditCard } from 'lucide-vue-next';
import EmployeeDeptChart from '@/pages/dashboard/components/EmployeeDeptChart.vue'
import PerformanceChart from '@/pages/dashboard/components/PerformanceChart.vue'
import LeaveTypeChart from '@/pages/dashboard/components/LeaveTypeChart.vue'
import AttritionChart from '@/pages/dashboard/components/AttritionChart.vue'
import RecentEmployees from '@/pages/dashboard/components/RecentEmployees.vue'
import RecentLeaves from '@/pages/dashboard/components/RecentLeaves.vue'
import RecentTasks from '@/pages/dashboard/components/RecentTasks.vue'

import { useDashboardStore } from '@/store/dashboardStore';
const store = useDashboardStore();

const payrollMonth = ref(new Date());
const month = payrollMonth.value.getMonth() + 1;
const year = payrollMonth.value.getFullYear();

onMounted(async () => {
  await store.loadAll(month, year);
});

const kpis = computed(() => store.kpis || {});
const empByDept = computed(() => store.empByDept || []);
const perfGroups = computed(() => store.perfGroups || {});
const leaveByType = computed(() => store.leaveByType || {});
const attrition = computed(() => store.attrition || { resigned:0, active:0 });
const lastUpdatedDisplay = computed(() => store.lastUpdated ? new Date(store.lastUpdated).toLocaleString() : '-');
const payrollCount = computed(() => (store.payrolls || []).length || 0);
const payrollLabel = computed(() => `${month}/${year}`);

const recentEmployees = computed(() => (store.employees || []).slice().sort((a,b) => (b.EmpNumber||0) - (a.EmpNumber||0)).slice(0,6));
const recentLeaves = computed(() => (store.leaves || []).slice().sort((a,b) => new Date(b.startDate) - new Date(a.startDate)).slice(0,6));
const recentTasks = computed(() => (store.tasks || []).slice().sort((a,b) => new Date(b.deadline) - new Date(a.deadline)).slice(0,6));
</script>

<style scoped>
:root{
  --card-bg: rgba(255,255,255,0.06);
  --glass-border: rgba(255,255,255,0.12);
  --text-strong: #071028;
  --text-weak: #7b8794;
  --accent-blue: #AEE3FF;
  --accent-mint: #CFFFE8;
  --accent-lav: #E3D9FF;
  --card-shadow: 0 6px 18px rgba(7,16,40,0.04);
}

/* layout */
.df-dashboard { padding: 22px; font-family: Inter, system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial; color:var(--text-strong); }
.df-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:18px; }
.df-header h1 { font-size:20px; margin:0; font-weight:700; color:var(--text-strong); }
.summary-grid { display:flex; flex-wrap:wrap; gap:12px; margin-bottom:20px; }
.summary-grid > * { flex:1 1 calc(33% - 12px); min-width:180px; }

/* charts layout */
.charts-area { display:grid; gap:18px; align-items:flex-start; }
.left-col{ flex:2; }
.right-col{ flex:1; display:flex; flex-direction:column; gap:14px; }

/* === holo-card (clean glass, no colored tint) === */
.holo-card {
  position: relative;
  border-radius: 12px;
  padding: 18px;
  margin-bottom: 5px;

  /* Glassmorphism chuẩn */
  background: rgba(255,255,255,0.10);          /* tăng từ 0.06 → 0.10 để tách lớp rõ */
  border: 1px solid rgba(255,255,255,0.22);     /* rõ hơn nhưng vẫn glass */
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);

  /* tạo chiều sâu */
  box-shadow:
      0 4px 8px rgba(0,0,0,0.02),
      0 2px 4px rgba(0,0,0,0.04);

  overflow: hidden;
}


/* force-hide any old gradient pseudo-element that may exist */
.holo-card::before {
  display: none !important;
  background: none !important;
  content: none !important;
}


/* generic card */
.card{ background:transparent; border:none; padding:5%; border-radius:10px; }

/* row / halves */
.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.card.half{ flex:1; }

/* recent area */
.recent-area {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.recent-area > *{ grid-column:; }

/* quick text */
.info ul{ list-style:none; padding:0; margin:0; font-size:14px; color:var(--text-weak); }
.updated{ font-size:12px; color:var(--text-weak); }

/* headings inside cards */
.holo-card h3{ margin:0 0 12px 0;margin-bottom: 20px; font-size:18px; font-weight:700; color:var(--text-strong); }
.holo-card h4{ margin:0 0 12px 0;margin-bottom: 20px; font-size:16px; font-weight:700; color:var(--text-strong); }
</style>
