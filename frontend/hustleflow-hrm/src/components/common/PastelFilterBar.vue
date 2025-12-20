<template>
  <div class="filter-bar">
    <v-select
      v-model="month"
      :items="months"
      label="Month"
      density="compact"
      class="w"
      clearable
    />

    <v-select
      v-model="year"
      :items="years"
      label="Year"
      density="compact"
      class="w"
      clearable
    />

    <v-select
      v-model="status"
      :items="statusList"
      label="Status"
      density="compact"
      class="w"
      clearable
    />

    <v-btn class="filter-btn" @click="apply">
      Filter
    </v-btn>

    
    <v-btn
      icon
      variant="text"
      class="clear-btn"
      @click="clearAll"
    >
      <v-icon size="18">mdi-close</v-icon>
    </v-btn>
  </div>
</template>


<script setup>
const emit = defineEmits(["filter", "clear"]);

const months = Array.from({ length: 12 }, (_, i) => i + 1);
const years = Array.from({ length: 20 }, (_, i) => 2020 + i);
const statusList = ["All", "ON_TIME", "LATE", "ABSENT"];

const month = defineModel("month");
const year = defineModel("year");
const status = defineModel("status");

function apply() {
  emit("filter");
}

function clearAll() {
  month.value = null;
  year.value = null;
  status.value = null;
  emit("clear");
}
</script>


<style scoped>
.filter-bar {
  padding: 14px;
  border-radius: 14px;
  background: rgba(255,255,255,0.55);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(16px);
  display: flex;
  gap: 12px;
  align-items: center;
  box-shadow: var(--card-shadow);
}

.w {
  width: 150px;
}
.clear-btn {
  margin-left: auto;
  opacity: 0.6;
}

.clear-btn:hover {
  opacity: 1;
}

.filter-btn {
  background: linear-gradient(135deg, var(--accent-blue), var(--accent-mint));
  color: var(--text-strong);
  font-weight: 600;
  border-radius: 10px;
}
</style>
