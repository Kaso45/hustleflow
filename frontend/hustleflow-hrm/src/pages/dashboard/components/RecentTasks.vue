<template>
  <div class="holo-card card">
    <h4>Recent Tasks</h4>
    <ul>
      <li v-for="t in tasks" :key="t.id" class="recent-item">
        <div class="left">
          <div class="name">{{ t.title || t.taskName || '—' }}</div>
          <div class="meta">{{ t.assigneeName || t.owner || '' }} • {{ t.status }}</div>
        </div>
        <div class="right">{{ formatDate(t.deadline) }}</div>
      </li>
      <li v-if="!tasks || tasks.length === 0" class="placeholder">No recent tasks</li>
    </ul>
  </div>
</template>

<script setup>
const props = defineProps({ tasks: { type: Array, default: () => [] } });

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
.name { font-weight:600; }
.meta { font-size:12px; color:#777; }
.right { font-size:12px; color:#777; }
.placeholder { color:#999; font-size:13px; }
</style>
