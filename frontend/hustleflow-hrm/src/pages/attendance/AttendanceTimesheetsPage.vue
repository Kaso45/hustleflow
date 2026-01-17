<template>
  <div class="page-container">
    <!-- 1. Header & Search -->
    <PeoplePageHeader
      title="Attendance Records"
      subtitle="Management and monthly tracking"
      btnText="Clock In / Out"
      v-model="searchQuery"
      @add="openClockModal"
    />

    <!-- 2. Filter Bar (Pastel Style) -->
    <div class="filter-section card-glass">
      <div class="filter-row">
        <div class="filter-group">
          <label><i class="pi pi-calendar"></i> Select Date</label>
          <input type="date" v-model="selectedDate" class="input-pastel" />
        </div>

        <div class="filter-group">
          <label><i class="pi pi-briefcase"></i> Department</label>
          <div class="pill-group">
            <button
              class="pill-item"
              :class="{ active: !selectedDeptName }"
              @click="selectedDeptName = null"
            >
              All
            </button>
            <button
              v-for="dept in departments"
              :key="dept.id"
              class="pill-item"
              :class="{ active: selectedDeptName === dept.departmentName }"
              @click="selectedDeptName = dept.departmentName"
            >
              {{ dept.departmentName }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 3. Daily Attendance Table (Main View) -->
    <div class="section-title">
      Daily Overview - {{ formatDate(selectedDate) }}
    </div>
    <FloatingTable gridColumns="2fr 1.2fr 1fr 1fr 1fr 1.2fr">
      <template #header>
        <div>Employee</div>
        <div>Date</div>
        <div>Clock In</div>
        <div>Clock Out</div>
        <div>Work Hrs</div>
        <div>Status</div>
      </template>

      <div v-if="loading" class="state-msg">Fetching data...</div>
      <div v-else-if="filteredTimesheets.length === 0" class="state-msg">
        No records today.
      </div>

      <div
        v-else
        v-for="ts in filteredTimesheets"
        :key="ts.employeeId + ts.date"
        class="row-card"
      >
        <div class="col-info">
          <BaseAvatar :name="getEmployee(ts.employeeId)?.name" :size="36" />
          <div>
            <div class="emp-name">{{ getEmployee(ts.employeeId)?.name }}</div>
            <div class="dept-tag">
              {{ getEmployee(ts.employeeId)?.empDepartment }}
            </div>
          </div>
        </div>
        <div class="text-sm font-medium">{{ formatDate(ts.date) }}</div>
        <div class="time-box in">{{ formatTime(ts.checkIn) }}</div>
        <div class="time-box out">{{ formatTime(ts.checkOut) }}</div>
        <div class="hours-text">{{ ts.totalHours?.toFixed(2) || "0.00" }}h</div>
        <div>
          <span class="status-badge" :class="getStatusClass(ts.status)">{{
            ts.status
          }}</span>
        </div>
      </div>
    </FloatingTable>

    <!-- 4. Monthly Individual Report (PHẦN BỔ SUNG MỚI) -->
    <div class="monthly-report-section card-glass">
      <div class="report-header">
        <h3 class="section-title">Monthly Detail Report</h3>
        <div class="report-controls">
          <select v-model="reportEmployeeId" class="input-pastel select-emp">
            <option :value="null">-- Select Employee to View Month --</option>
            <option v-for="emp in employees" :key="emp.id" :value="emp.id">
              {{ emp.name }}
            </option>
          </select>
        </div>
      </div>

      <div v-if="!reportEmployeeId" class="state-msg-small">
        Select an employee to view their monthly performance.
      </div>
      <table v-else class="monthly-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Clock In</th>
            <th>Clock Out</th>
            <th>Late (Min)</th>
            <th>Total Hours</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="day in monthlyData" :key="day.date">
            <td>{{ day.dateLabel }}</td>
            <td :class="{ 'text-red-500': day.isLate }">
              {{ formatTime(day.checkIn) }}
            </td>
            <td>{{ formatTime(day.checkOut) }}</td>
            <td class="font-bold">
              {{ day.lateMinutes > 0 ? day.lateMinutes + " min" : "-" }}
            </td>
            <td>
              {{ day.totalHours ? day.totalHours.toFixed(2) + "h" : "-" }}
            </td>
            <td>
              <span
                class="status-dot"
                :class="getStatusClass(day.status)"
              ></span>
              {{ day.status || "N/A" }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 5. Modal: Clock In/Out (Pastel UI) -->
    <BaseModal
      :isOpen="showModal"
      title="Manual Attendance"
      @close="closeModal"
    >
      <form @submit.prevent="handleClockAction" class="modal-form">
        <div class="form-group">
          <label>Employee</label>
          <select v-model="clockData.employeeId" class="input-pastel" required>
            <option :value="null" disabled>Choose employee...</option>
            <option v-for="emp in employees" :key="emp.id" :value="emp.id">
              {{ emp.name }}
            </option>
          </select>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Date</label>
            <input
              v-model="clockData.date"
              type="date"
              class="input-pastel"
              required
            />
          </div>
          <div class="form-group">
            <label>Time</label>
            <input
              v-model="clockData.time"
              type="time"
              step="1"
              class="input-pastel"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label>Action Type</label>
          <div class="action-selector">
            <button
              type="button"
              class="action-btn btn-in"
              :class="{ active: clockData.type === 'IN' }"
              @click="clockData.type = 'IN'"
            >
              Clock In
            </button>
            <button
              type="button"
              class="action-btn btn-out"
              :class="{ active: clockData.type === 'OUT' }"
              @click="clockData.type = 'OUT'"
            >
              Clock Out
            </button>
          </div>
          <p v-if="clockData.type === 'OUT'" class="hint-text">
            Requires an existing entry for this day.
          </p>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel-flat" @click="closeModal">
            Cancel
          </button>
          <button
            type="submit"
            class="btn-confirm-pastel"
            :disabled="submitting"
          >
            {{ submitting ? "Processing..." : "Confirm Action" }}
          </button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import timesheetService from "@/services/timesheetService";
import employeeService from "@/services/employeeService";
import departmentService from "@/services/departmentService";
import PeoplePageHeader from "@/components/common/PeoplePageHeader.vue";
import FloatingTable from "@/components/common/FloatingTable.vue";
import BaseModal from "@/components/common/BaseModal.vue";
import BaseAvatar from "@/components/common/BaseAvatar.vue";

const toLocalDateInputValue = (d = new Date()) => {
  const tzOffset = d.getTimezoneOffset() * 60000;
  const localISO = new Date(d.getTime() - tzOffset).toISOString();
  return localISO.slice(0, 10);
};

// --- STATE ---
const timesheets = ref([]);
const employees = ref([]);
const departments = ref([]);
const loading = ref(true);
const submitting = ref(false);
const searchQuery = ref("");
const selectedDeptName = ref(null);
const selectedDate = ref(toLocalDateInputValue());
const showModal = ref(false);

// State cho Monthly Report
const reportEmployeeId = ref(null);

const clockData = reactive({
  employeeId: null,
  date: toLocalDateInputValue(),
  time: new Date().toLocaleTimeString("it-IT"),
  type: "IN",
});

// --- LOGIC TÍNH TOÁN BÁO CÁO THÁNG ---
const monthlyData = computed(() => {
  if (!reportEmployeeId.value) return [];

  // Lấy dữ liệu của nhân viên được chọn trong tháng của selectedDate
  const targetDate = new Date(selectedDate.value);
  const year = targetDate.getFullYear();
  const month = targetDate.getMonth();

  // Tạo danh sách tất cả các ngày trong tháng (Giống kiểu Google Calendar)
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const report = [];

  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, "0")}-${String(
      d
    ).padStart(2, "0")}`;
    const record = timesheets.value.find(
      (t) => t.employeeId === reportEmployeeId.value && t.date === dateStr
    );

    // Tính phút đi muộn (Deadline 9:00 AM như trong Java Service của bạn)
    let lateMinutes = 0;
    if (record && record.checkIn) {
      const [h, m] = record.checkIn.split(":").map(Number);
      const checkInMinutes = h * 60 + m;
      const deadlineMinutes = 9 * 60;
      if (checkInMinutes > deadlineMinutes)
        lateMinutes = checkInMinutes - deadlineMinutes;
    }

    report.push({
      date: dateStr,
      dateLabel: `${d} ${targetDate.toLocaleString("default", {
        month: "short",
      })}`,
      checkIn: record?.checkIn || null,
      checkOut: record?.checkOut || null,
      totalHours: record?.totalHours || 0,
      status:
        record?.status || (new Date(dateStr) < new Date() ? "ABSENT" : "-"),
      lateMinutes,
      isLate: lateMinutes > 0,
    });
  }
  return report;
});

// --- FETCH & FILTER ---
const fetchData = async () => {
  loading.value = true;
  try {
    const [empRes, deptRes] = await Promise.all([
      employeeService.getEmployees(),
      departmentService.getDepartments(),
    ]);
    employees.value = empRes.data || [];
    departments.value = deptRes.data || [];
    await refreshTimesheets();
  } finally {
    loading.value = false;
  }
};

const refreshTimesheets = async () => {
  if (!employees.value.length) return;
  const d = new Date(selectedDate.value);
  const results = await Promise.all(
    employees.value.map((e) =>
      timesheetService.getTimesheets(
        e.id,
        (d.getMonth() + 1).toString(),
        d.getFullYear().toString()
      )
    )
  );
  timesheets.value = results.flatMap((r) => r.data || []);
};

watch(selectedDate, refreshTimesheets);

const filteredTimesheets = computed(() => {
  return timesheets.value.filter((ts) => {
    const emp = getEmployee(ts.employeeId);
    if (!emp) return false;
    const matchDate = ts.date === selectedDate.value;
    const matchDept =
      !selectedDeptName.value || emp.empDepartment === selectedDeptName.value;
    const matchSearch =
      !searchQuery.value ||
      emp.name.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchDate && matchDept && matchSearch;
  });
});

const getEmployee = (id) => employees.value.find((e) => e.id === id);
const formatTime = (t) => (t && t !== "null" ? t.substring(0, 5) : "--:--");
const formatDate = (d) => {
  if (!d) return "";
  const [y, m, day] = d.split("-").map(Number);
  const date = new Date(y, m - 1, day);
  return date.toLocaleDateString("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
};

const getStatusClass = (s) => {
  if (s === "ON_TIME") return "st-on-time";
  if (s === "LATE") return "st-late";
  if (s === "ABSENT") return "st-absent";
  return "st-empty";
};

const openClockModal = () => {
  showModal.value = true;
};
const closeModal = () => {
  showModal.value = false;
};

const handleClockAction = async () => {
  submitting.value = true;
  try {
    const payload = {
      employeeId: clockData.employeeId,
      timestamp: `${clockData.date}T${clockData.time}`,
    };
    if (clockData.type === "IN") await timesheetService.clockIn(payload);
    else await timesheetService.clockOut(payload);
    alert("Success!");
    closeModal();
    refreshTimesheets();
  } catch (e) {
    alert(e.response?.data?.message || "Error occurred");
  } finally {
    submitting.value = false;
  }
};

onMounted(fetchData);
</script>

<style scoped>
.page-container {
  padding: 30px;
  background: #f0f4f8;
  min-height: 100vh;
  font-family: "Plus Jakarta Sans", sans-serif;
}

/* Glassmorphism Cards */
.card-glass {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.05);
  margin-bottom: 25px;
}

.section-title {
  font-size: 18px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 15px;
  margin-top: 10px;
}

/* Filter UI */
.filter-row {
  display: flex;
  gap: 40px;
  align-items: center;
}
.pill-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.pill-item {
  padding: 8px 18px;
  border-radius: 99px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.3s;
}
.pill-item.active {
  background: #0b2433;
  color: white;
  border-color: #0b2433;
  transform: scale(1.05);
}

/* Input Pastel */
.input-pastel {
  padding: 10px 15px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  font-weight: 600;
  font-size: 14px;
  outline: none;
  transition: 0.2s;
}
.input-pastel:focus {
  border-color: #5fd1c5;
  box-shadow: 0 0 0 3px rgba(95, 209, 197, 0.2);
}

.emp-name {
  font-weight: 700;
  color: #0b2433;
}
.dept-tag {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
}

.time-box {
  font-family: "Monaco", monospace;
  font-weight: 700;
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 6px;
  width: fit-content;
}
.time-box.in {
  color: #10b981;
  background: #ecfdf5;
}
.time-box.out {
  color: #f59e0b;
  background: #fffbeb;
}

/* Status Badges */
.status-badge {
  padding: 5px 12px;
  border-radius: 99px;
  font-size: 11px;
  font-weight: 800;
}
.st-on-time {
  background: #dcfce7;
  color: #16a34a;
}
.st-late {
  background: #fee2e2;
  color: #ef4444;
}
.st-absent {
  background: #f1f5f9;
  color: #94a3b8;
}

/* Monthly Table */
.monthly-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  margin-top: 15px;
}
.monthly-table th {
  text-align: left;
  padding: 10px;
  color: #64748b;
  font-size: 12px;
  text-transform: uppercase;
}
.monthly-table td {
  padding: 12px 10px;
  background: rgba(255, 255, 255, 0.5);
  font-size: 14px;
}
.monthly-table tr td:first-child {
  border-radius: 12px 0 0 12px;
  font-weight: 600;
}
.monthly-table tr td:last-child {
  border-radius: 0 12px 12px 0;
}
.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 5px;
}

/* Modal Styles */
.action-selector {
  display: flex;
  gap: 10px;
}
.action-btn {
  flex: 1;
  padding: 12px;
  border-radius: 15px;
  border: 2px solid #f1f5f9;
  background: #f8fafc;
  font-weight: 700;
  cursor: pointer;
  transition: 0.3s;
}
.btn-in.active {
  background: #dcfce7;
  color: #16a34a;
  border-color: #16a34a;
}
.btn-out.active {
  background: #fff7ed;
  color: #f59e0b;
  border-color: #f59e0b;
}

.btn-confirm-pastel {
  background: #5fd1c5;
  color: white;
  padding: 12px 25px;
  border-radius: 99px;
  border: none;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(95, 209, 197, 0.4);
}
.btn-cancel-flat {
  background: transparent;
  color: #94a3b8;
  font-weight: 700;
  cursor: pointer;
  border: none;
}

/* ------- Responsive Enhancements ------- */
/* Define grid columns for the daily overview and apply to body rows */
.page-container {
  --grid-columns: 2fr 1.2fr 1fr 1fr 1fr 1.2fr;
}

/* Ensure header inside FloatingTable uses same columns (when visible) */
:deep(.floating-table-container .table-header-row) {
  grid-template-columns: var(--grid-columns);
}

/* Align row cards with the header grid on larger screens */
.row-card {
  grid-template-columns: var(--grid-columns);
  gap: 10px;
}

/* Tablet adjustments */
@media (max-width: 1024px) {
  .page-container {
    padding: 20px;
  }
  .filter-row {
    gap: 20px;
  }
}

/* Mobile layout */
@media (max-width: 768px) {
  /* Compact page padding */
  .page-container {
    padding: 16px;
  }

  /* Wrap filter controls */
  .filter-row {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  /* Hide table header on small screens */
  :deep(.floating-table-container .table-header-row) {
    display: none;
  }

  /* Stack each row item with inline labels */
  .row-card {
    grid-template-columns: 1fr;
    gap: 8px;
    padding: 12px 14px;
  }
  .row-card > div {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .row-card > .col-info {
    justify-content: flex-start;
    gap: 10px;
  }
  .row-card > div:nth-child(2)::before {
    content: "Date";
    color: #64748b;
    font-size: 12px;
    font-weight: 600;
  }
  .row-card > div:nth-child(3)::before {
    content: "Clock In";
    color: #64748b;
    font-size: 12px;
    font-weight: 600;
  }
  .row-card > div:nth-child(4)::before {
    content: "Clock Out";
    color: #64748b;
    font-size: 12px;
    font-weight: 600;
  }
  .row-card > div:nth-child(5)::before {
    content: "Work Hrs";
    color: #64748b;
    font-size: 12px;
    font-weight: 600;
  }
  .row-card > div:nth-child(6)::before {
    content: "Status";
    color: #64748b;
    font-size: 12px;
    font-weight: 600;
  }
  .row-card > div:nth-child(6) {
    justify-content: space-between;
  }
}
</style>
