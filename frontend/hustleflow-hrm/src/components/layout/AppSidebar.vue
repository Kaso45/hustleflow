<template>
  <div class="sidebar">
    <div class="sidebar__header">
      <div class="sidebar__brand">
        <div class="logo-mark">HF</div>
        <div class="logo-text">
          <span class="logo-title">HustleFlow HRM</span>
          <span class="logo-subtitle">People OS</span>
        </div>
      </div>
      <button
        v-if="showClose"
        class="sidebar__close"
        type="button"
        aria-label="Close navigation"
        @click="$emit('close')"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </svg>
      </button>
    </div>

    <div class="sidebar-scroll">
      <v-list nav density="compact" class="sidebar-list">
        <v-list-item
          v-for="item in items"
          :key="item.to"
          :to="item.to"
          link
          class="sidebar-item"
          :title="item.label"
          @click="$emit('navigate')"
        />
      </v-list>
    </div>

    <div class="sidebar__footer" />
  </div>
</template>

<script setup>
defineProps({
  showClose: {
    type: Boolean,
    default: false,
  },
});

defineEmits(["navigate", "close"]);

const items = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/people/employees", label: "Employees" },
  { to: "/people/departments", label: "Departments" },
  { to: "/people/contracts", label: "Contracts" },
  { to: "/attendance/timesheets", label: "Timesheets" },
  { to: "/attendance/leaves", label: "Leave Requests" },
  { to: "/work/projects", label: "Projects" },
  { to: "/work/tasks", label: "Tasks" },
  { to: "/payroll", label: "Payroll" },
  { to: "/performance", label: "Performance" },
];
</script>

<style scoped>
.sidebar {
  padding: 20px 16px 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(18px);
  position: relative;
  overflow: hidden;
}

.sidebar::after {
  content: "";
  position: absolute;
  inset: 0;
  background: url("@/assets/noise.jpg");
  opacity: 0.4;
  pointer-events: none;
}

.sidebar__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.25);
}

.sidebar__brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-mark {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #5fd1c5, #2dd4bf);
  color: white;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 1px;
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.sidebar__close {
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: rgba(248, 250, 252, 0.92);
  border-radius: 12px;
  width: 40px;
  height: 40px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #0b2433;
  cursor: pointer;
  transition: all 0.2s ease;
}

.sidebar__close:hover {
  border-color: rgba(15, 118, 110, 0.4);
  background: #ffffff;
  color: #0f766e;
}

.logo-title {
  font-size: 15px;
  font-weight: 700;
  color: #0b2433;
}

.logo-subtitle {
  font-size: 12px;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.sidebar-item {
  border-radius: 1px;
  position: relative;
  overflow: visible;
  transition: 0.2s;
  background-position-x: -50%;
}

.sidebar-item:hover {
  background: rgba(187, 204, 255, 0.25);
  backdrop-filter: blur(12px);
  box-shadow: 0 8px 24px rgba(23, 37, 84, 0.08);
}

.sidebar-item:hover::before {
  content: "";
  position: absolute;
  inset: -2px;
  border-radius: inherit;
  background: linear-gradient(135deg, #ff90cd, #043d76);
  filter: blur(12px);
  opacity: 0.35;
  z-index: -1;
}

.sidebar-item.v-list-item--active {
  background: rgba(140, 186, 255, 0.35);
  backdrop-filter: blur(14px);
  color: #0e1a3a !important;
  font-weight: 600;
  position: relative;
  box-shadow: 0 0 18px rgba(187, 204, 255, 0.4);
}

.sidebar__footer {
  margin-top: auto;
  border-top: 1px solid rgba(148, 163, 184, 0.25);
  padding-top: 16px;
  padding-bottom: 4px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: inherit;
}

.logout-btn {
  justify-content: flex-start;
  text-transform: none;
  font-weight: 600;
}

@media (max-width: 640px) {
  .sidebar {
    padding: 16px 14px 14px;
    overflow: hidden;
  }

  .sidebar-scroll {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    padding-right: 4px;
    padding-bottom: 12px;
  }
}
</style>
