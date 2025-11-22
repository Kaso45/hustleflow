<template>
  <v-list nav density="compact">
    <v-list-item
      v-for="item in items"
      :key="item.to"
      :to="item.to"
      link
      :title="item.label"
    />
  </v-list>
</template>

<script setup>
// Sidebar menu; dùng router-link thông qua prop "to"
const items = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/people/employees', label: 'Employees' },
  { to: '/people/departments', label: 'Departments' },
  { to: '/people/contracts', label: 'Contracts' },
  { to: '/attendance/timesheets', label: 'Timesheets' },
  { to: '/attendance/leaves', label: 'Leave Requests' },
  { to: '/work/projects', label: 'Projects' },
  { to: '/work/tasks', label: 'Tasks' },
  { to: '/payroll', label: 'Payroll' },
  { to: '/performance', label: 'Performance' },
]
</script>
