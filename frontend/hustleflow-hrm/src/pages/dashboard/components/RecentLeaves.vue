<template>
  <div class="holo-card card">
    <h4>Recent Leave Requests</h4>
    <ul>
      <li v-for="l in leaves" :key="l.id" class="recent-item">
        <div class="left">
          <div class="name">{{ l.employeeName || l.EmpName || '—' }}</div>
          <div class="meta">{{ l.leaveType }} • {{ l.status }}</div>
        </div>
        <div class="right">{{ formatRange(l.startDate, l.endDate) }}</div>
      </li>
      <li v-if="!leaves || leaves.length === 0" class="placeholder">No recent leaves</li>
    </ul>
  </div>
</template>

<script setup>
const props = defineProps({ leaves: { type: Array, default: () => [] } });

function formatRange(s, e) {
  const from = s ? new Date(s).toLocaleDateString() : '';
  const to = e ? new Date(e).toLocaleDateString() : '';
  return from && to ? `${from} → ${to}` : from || to || '';
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
.name { font-weight:600; }
.meta { font-size:12px; color:#777; }
.right { font-size:12px; color:#777; }
.placeholder { color:#999; font-size:13px; }
</style>
