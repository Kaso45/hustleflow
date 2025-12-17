<script setup>
import { ref } from 'vue'

/**
 * SECURITY PRINCIPLE:
 * - UI never computes status
 * - Backend is source of truth
 */

const props = defineProps({
  employee: { type: Object, required: true },
  allowOverride: { type: Boolean, default: false }
})

const emit = defineEmits(['save'])

/* ===================== STATE ===================== */
const clockInTime = ref(null)
const clockOutTime = ref(null)
const selectedDate = ref(new Date())
const manualStatus = ref(null)

/* ===================== FORMATTERS ===================== */
function formatTime(val) {
  if (!val) return null

  // Vuetify time picker object
  if (typeof val === 'object' && 'hours' in val) {
    return `${String(val.hours).padStart(2, '0')}:${String(val.minutes).padStart(2, '0')}`
  }

  if (typeof val === 'string') return val
  return null
}

function formatDate(val) {
  if (!val) return null

  if (val instanceof Date) {
    return val.toISOString().slice(0, 10)
  }

  if (typeof val === 'object' && 'year' in val) {
    const m = String(val.month + 1).padStart(2, '0')
    const d = String(val.day).padStart(2, '0')
    return `${val.year}-${m}-${d}`
  }

  if (typeof val === 'string') return val
  return null
}

/* ===================== SUBMIT ===================== */
function submit() {
  emit('save', {
    date: formatDate(selectedDate.value),
    clockInTime: formatTime(clockInTime.value),
    clockOutTime: formatTime(clockOutTime.value),
    manualStatus: props.allowOverride ? manualStatus.value : null
  })

  clockInTime.value = null
  clockOutTime.value = null
  manualStatus.value = null
}
</script>


<template>
  <div class="manual-card">
    <!-- LEFT -->
    <div class="left-info">
      <div class="emp-name">
        Employee Name: {{ employee.employeeName }}
      </div>
      <div class="emp-meta">
        ID: {{ employee.EmpNumber }} · {{ employee.EmpDepartment }}
      </div>
    </div>

    <!-- RIGHT -->
    <div class="right-actions">
  <div class="action-row">
    <!-- Clock In -->
    <v-menu
  location="bottom"
  attach="body"
  :close-on-content-click="false"
>
  <template #activator="{ props }">
    <v-btn class="pill" v-bind="props">
      {{ clockInTime || 'Clock In' }}
    </v-btn>
  </template>

  <v-time-picker
  v-model="clockInTime"
  format="24hr"
  use-seconds="false"
/>

</v-menu>


    <!-- Clock Out -->
    <v-menu
     location="bottom"
     attach="body"
    :close-on-content-click="false"
>
  <template #activator="{ props }">
    <v-btn class="pill" v-bind="props">
      {{ clockOutTime || 'Clock Out' }}
    </v-btn>
  </template>

  <v-time-picker
  v-model="clockOutTime"
  format="24hr"
  use-seconds="false"
/>

</v-menu>

    <!-- Date -->
    <v-menu
  location="bottom"
  attach="body"
  :close-on-content-click="false"
>
  <template #activator="{ props }">
    <v-btn class="pill soft" v-bind="props">
      {{ selectedDate }}
    </v-btn>
  </template>

  <v-date-picker v-model="selectedDate" />

</v-menu>

  </div>

  <v-btn class="save-btn" @click="submit">
    Save “Clock In” or “Clock Out”
  </v-btn>
</div>

  </div>
</template>

<style scoped>
.manual-card {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 24px;
  padding: 24px;
  border-radius: 16px;
  border: 1px solid #eee;
  background: #fff;
}

.emp-name {
  font-size: 18px;
  font-weight: 700;
}

.emp-meta {
  font-size: 13px;
  color: #6b7280;
  margin-top: 6px;
}


.pill {
  min-width: 120px;
  border-radius: 999px;
  background: #7de1df;
  color: #fff;
}

.pill.soft {
  background: #d6f3f2;
  color: #333;
}
.right-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-row {
  display: flex;
  gap: 12px;
  justify-content: center;
}



.save-btn {
  margin-top: 16px;
  border-radius: 999px;
  background: #d6f3f2;
  font-weight: 600;
  width: calc(120px * 3 + 24px);
  text-align: center;
}
</style>
