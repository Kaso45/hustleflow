<template>
  <v-navigation-drawer
    v-model="open"
    location="right"
    width="380"
    class="employee-picker"
  >
    <div class="header">
      <h3>Select Employee</h3>
      <v-btn icon variant="text" @click="open = false">
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </div>

    <v-text-field
      v-model="search"
      density="compact"
      placeholder="Search employee..."
      prepend-inner-icon="mdi-magnify"
      class="mb-4"
    />

    <div class="grid">
      <div
        v-for="emp in filtered"
        :key="emp.id"
        class="emp-card"
        @click="select(emp)"
      >
        <BaseAvatar :name="emp.employeeName" :size="44" />
        <div class="info">
          <div class="name">{{ emp.employeeName }}</div>
          <div class="sub">{{ emp.empDepartment ?? emp.department }}</div>
        </div>
      </div>
    </div>
  </v-navigation-drawer>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import BaseAvatar from "./BaseAvatar.vue";

const props = defineProps({
  modelValue: Boolean,
  employees: Array,
});

const emit = defineEmits(["update:modelValue", "select"]);

const open = computed({
  get: () => props.modelValue,
  set: v => emit("update:modelValue", v)
});

const search = ref("");

const filtered = computed(() => {
  if (!search.value) return props.employees;
  return props.employees.filter(e =>
    e.employeeName.toLowerCase().includes(search.value.toLowerCase())
  );
});

function select(emp) {
  emit("select", emp);
  open.value = false;
}
</script>

<style scoped>
.employee-picker {
  padding: 16px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(14px);
  border-left: 1px solid var(--glass-border);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.emp-card {
  display: flex;
  gap: 10px;
  padding: 12px;
  background: rgba(255,255,255,0.55);
  border-radius: 12px;
  cursor: pointer;
  transition: 0.15s;
  border: 1px solid rgba(255,255,255,0.3);
}

.emp-card:hover {
  background: var(--accent-blue);
  transform: translateY(-2px);
}

.name {
  font-weight: 600;
  color: var(--text-strong);
}

.sub {
  font-size: 12px;
  color: var(--text-weak);
}
</style>
