<template>
  <div class="sidebar">
    <v-list nav density="compact" class="sidebar-list">
      <v-list-item
        v-for="item in items"
        :key="item.to"
        :to="item.to"
        link
        class="sidebar-item"
        :title="item.label"
      />
    </v-list>
  </div>
</template>

<script setup>
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
];
</script>

<style scoped>
.sidebar {
  padding: 14px;
  width: full;
  height: 100%;
  background: rgba(255, 255, 255, 0.866);
  backdrop-filter: blur(18px);
  
  position: relative;
}


.sidebar::after {
  content:"";
  position:absolute;
  inset: 0;
  mix-blend-mode:overlay;
  background:url('@/assets/noise.jpg');
  opacity:1;
  scale: fit;
  pointer-events:none;

}

.sidebar-item {
  border-radius: 1px;
  position: relative;
  overflow: visible;
  transition: .2s;
  background-position-x: -50%;

}

.sidebar-item:hover {
  background: rgba(187, 204, 255, 0.849);
  backdrop-filter: blur(12px);
  box-shadow: 0 8px 24px rgb(177, 194, 255);
  sidebar-item {
    cursor: pointer;
  }
  
}

.sidebar-item:hover::before {
  content:"";
  position:absolute;
  inset:-2px;
  border-radius:inherit;
  background: linear-gradient(135deg, #ff90cd, #043d76);
  filter: gaussian(1px);
  opacity:0.5;
  z-index:-1;
}
/* --- ACTIVE ITEM (đang đứng trong route) --- */
.sidebar-item.v-list-item--active {
  background: rgba(140, 186, 255, 0.9);     /* nền đẹp hơn rõ ràng hơn */
  backdrop-filter: blur(14px);
  color: #0e1a3a !important;
  font-weight: 600;
  position: relative;
  box-shadow: 0 0 18px rgba(187, 204, 255, 0.8);
 background-blend-mode: soft-light;


}




</style>
