<template>
  <div class="holo-card card">
    <h4>Recent Employees Added</h4>
    <ul>
      <li v-for="emp in employees" :key="emp.EmpNumber" class="recent-item">
        <div class="left">
          <div class="name">{{ emp.fullName || emp.EmpName || '—' }}</div>
          <div class="meta">{{ emp.EmpNumber ? 'ID: ' + emp.EmpNumber : '' }} • {{ emp.EmpDepartment || '—' }}</div>
        </div>
        <div class="right">{{ formatDate(emp.createdAt || emp.Hire_Date) }}</div>
      </li>
      <li v-if="!employees || employees.length === 0" class="placeholder">No recent employees</li>
    </ul>
  </div>
</template>

<script setup>
const props = defineProps({ employees: { type: Array, default: () => [] } });

function formatDate(d) {
  if (!d) return '';
  const dt = new Date(d);
  if (isNaN(dt)) return d;
  return dt.toLocaleDateString();
}
</script>

<style scoped>
.card {
  padding: 16px;
  border-radius: 16px;
  border: none;
  background: transparent;
}
.recent-item { display:flex; justify-content:space-between; padding:8px 0; border-bottom:1px solid #f3f3f3; }
.recent-item:last-child { border-bottom:none; }
.name { font-weight:600; }
.meta { font-size:12px; color:#777; }
.right { font-size:12px; color:#777; }
.placeholder { color:#999; font-size:13px; }
</style>
