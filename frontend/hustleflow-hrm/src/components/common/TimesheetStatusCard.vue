<template>
  <div class="ts-card">
    <div class="top">
      <BaseAvatar :name="employee?.name" :size="56" />
      <div>
        <h3>{{ employee?.name || "No employee selected" }}</h3>
        <p>{{ nowTime }}</p>
      </div>
    </div>

    <div class="stats">
      <div class="stat">
        <label>Clock In</label>
        <div>{{ today?.checkIn ?? "--" }}</div>
      </div>

      <div class="stat">
        <label>Clock Out</label>
        <div>{{ today?.checkOut ?? "--" }}</div>
      </div>

      <div class="stat">
        <label>Total Hours</label>
        <div>{{ today?.totalHours ?? "--" }}</div>
      </div>

      <div class="stat">
        <label>Status</label>
        <div class="badge" :class="today?.status">
          {{ today?.status || "--" }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import BaseAvatar from "./BaseAvatar.vue";

const props = defineProps({
  employee: Object,
  today: Object,
});

const nowTime = ref("");

function updateClock() {
  const d = new Date();
  nowTime.value = d.toLocaleTimeString();
}

onMounted(() => {
  updateClock();
  setInterval(updateClock, 1000);
});
</script>

<style scoped>
.ts-card {
  background: linear-gradient(135deg, var(--accent-blue), var(--accent-mint));
  padding: 20px;
  border-radius: 14px;
  box-shadow: var(--card-shadow);
  color: var(--text-strong);
}

.top {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.stat label {
  font-size: 12px;
  color: var(--text-weak);
}

.stat div {
  font-size: 16px;
  font-weight: 600;
}

.badge {
  padding: 4px 10px;
  border-radius: 10px;
  font-size: 12px;
  text-align: center;
}

.badge.ON_TIME {
  background: var(--accent-mint);
  color: #0f8a5f;
}
.badge.LATE {
  background: #ffe0e4;
  color: #c44562;
}
.badge.ABSENT {
  background: #ffe0e4;
  color: #c44562;
}

@media (max-width: 768px) {
  .ts-card {
    padding: 16px;
  }
  .stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
}
</style>
