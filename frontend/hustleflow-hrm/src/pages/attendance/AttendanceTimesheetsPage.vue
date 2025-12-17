<script setup>
import { ref, computed, onMounted, watch } from 'vue'

/* ===================== COMPONENTS ===================== */
import AppPageHeader from '@/components/common/AppPageHeader.vue'
import ManualAttendancePanel from '@/components/attendance/ManualAttendancePanel.vue'
import PastelTimesheetTable from '@/components/common/PastelTimesheetTable.vue'

/* ===================== SERVICES ===================== */
import dashboardService from '@/services/dashboard/dashboardService'
import timesheetMock from '@/services/mocks/timesheet.mock'

/* ===================== UTILS ===================== */
import { mapTimesheetsWithEmployeeName } from '@/utils/attendance/mapTimesheets'
import { fakeEmployeeNames } from '@/services/mocks/fakeEmployeeNames'

/* ===================== STATE ===================== */
const employees = ref([])
const selectedEmployee = ref(null)

const timesheetsAll = ref([])

/* Pagination */
const page = ref(1)
const PER_PAGE = 10

/**
 * FEATURE FLAG – SYSTEM SAFETY FIRST
 * false = frontend UI only, backend is source of truth
 * true  = backend supports manual status override
 */
const ALLOW_STATUS_OVERRIDE = false

/* ===================== INIT ===================== */
onMounted(async () => {
  const rawEmployees = await dashboardService.getEmployees()

  /**
   * Attach display name ONLY for UI
   * Does NOT change backend schema
   */
  employees.value = rawEmployees.map(e => ({
    ...e,
    employeeName: fakeEmployeeNames[e.EmpNumber] || `Employee ${e.EmpNumber}`
  }))

  const rawTimesheets = await timesheetMock.GET_timesheets()
  timesheetsAll.value = mapTimesheetsWithEmployeeName(
    rawTimesheets,
    employees.value
  )
})

/* Reset pagination when filter changes */
watch(selectedEmployee, (val) => {
  // luôn reset pagination
  page.value = 1

  // khi user bấm ❌ (clear search)
  if (!val) {
    // UX feedback: quay về đầu trang
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }
})

/* ===================== METHODS ===================== */
async function handleManualSave(payload) {
  if (!selectedEmployee.value) return

  const { date, clockInTime, clockOutTime } = payload

  /**
   * Clock-in
   */
  if (clockInTime) {
    await timesheetMock.POST_clockIn({
      employeeId: selectedEmployee.value.EmpNumber,
      employeeName: selectedEmployee.value.employeeName,
      timestamp: `${date}T${clockInTime}:00`
    })
  }

  /**
   * Clock-out
   */
  if (clockOutTime) {
    await timesheetMock.PATCH_clockOut({
      employeeId: selectedEmployee.value.EmpNumber,
      timestamp: `${date}T${clockOutTime}:00`
    })
  }

  /**
   * Reload timesheets – backend is source of truth
   */
  const raw = await timesheetMock.GET_timesheets()
  timesheetsAll.value = mapTimesheetsWithEmployeeName(
    raw,
    employees.value
  )
}

/* ===================== COMPUTED ===================== */
const filteredTimesheets = computed(() => {
  if (!selectedEmployee.value) return timesheetsAll.value
  return timesheetsAll.value.filter(
    t => t.employeeId === selectedEmployee.value.EmpNumber
  )
})

const pagedTimesheets = computed(() => {
  const start = (page.value - 1) * PER_PAGE
  return filteredTimesheets.value.slice(start, start + PER_PAGE)
})

const pageCount = computed(() =>
  Math.ceil(filteredTimesheets.value.length / PER_PAGE)
)

function reloadPage() {
  window.location.reload()
}

</script>

<template>
  <div class="ts-page">
    <!-- ===================== HEADER ===================== -->
    <AppPageHeader
  title="Time Sheets"
  subtitle="Shift tracking and attendance history"
  class="clickable-header"
  @click="reloadPage"
/>


    <!-- ===================== SELECT EMPLOYEE ===================== -->
    <section class="select-employee">
      <div class="search-row">
        <h3>Select Employee</h3>

       <div class="search-wrapper">
  <v-autocomplete
    v-model="selectedEmployee"
    :items="employees"
    item-title="employeeName"
    item-value="EmpNumber"
    return-object
    placeholder="Search employee"
    prepend-inner-icon="mdi-magnify"
    variant="outlined"
    hide-details
  />

  <!-- ICON X RELOAD -->
  <v-icon
    v-if="selectedEmployee"
    class="clear-icon"
    @click="reloadPage"
  >
    mdi-close
  </v-icon>
</div>

      </div>
      
    </section>

    <!-- ===================== MANUAL ATTENDANCE ===================== -->
    <section v-if="selectedEmployee" class="manual-section">
      <ManualAttendancePanel
        :employee="selectedEmployee"
        :allowOverride="ALLOW_STATUS_OVERRIDE"
        @save="handleManualSave"
      />
    </section>

    <!-- ===================== TIMESHEET TABLE ===================== -->
    <section class="timesheets">
      <PastelTimesheetTable :items="pagedTimesheets" />

      <v-pagination
        v-model="page"
        :length="pageCount"
        class="mt-4"
      />
    </section>
  </div>
</template>

<style scoped>
.ts-page {
  padding: 24px;
}

/* ---------- SELECT EMPLOYEE ---------- */
.select-employee {
  margin-bottom: 24px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* ---------- MANUAL ATTENDANCE ---------- */
.manual-section {
  margin-bottom: 32px;
}

/* ---------- TABLE ---------- */
.timesheets {
  margin-top: 24px;
}

.clickable-header {
  cursor: pointer;
}
.search-wrapper {
  position: relative;
  flex: 1;
}

.clear-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  opacity: 0.6;
}

.clear-icon:hover {
  opacity: 1;
}

</style>
