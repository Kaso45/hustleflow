<template>
  <div class="page-container">
    <!-- Header -->
    <PeoplePageHeader 
      title="Timesheets"
      subtitle="Track attendance & working hours"
      btnText="Clock In/Out"
      v-model="searchQuery"
      @add="openClockModal"
    />

    <!-- Filter Bar: DEPARTMENT -->
    <div class="filter-bar">
      <div class="filter-label">Filter by Department:</div>
      <div class="filter-scroll">
        <!-- Nút All -->
        <button 
          class="filter-pill" 
          :class="{ active: !selectedDeptName }"
          @click="selectedDeptName = null"
        >
          All Departments
        </button>

        <!-- Nút Từng Phòng Ban -->
        <button 
          v-for="dept in departments" 
          :key="dept.id"
          class="filter-pill"
          :class="{ active: selectedDeptName === dept.departmentName }"
          @click="selectedDeptName = dept.departmentName"
        >
          {{ dept.departmentName }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <FloatingTable gridColumns="2.5fr 1.5fr 1fr 1fr 1fr 1fr">
      <template #header>
        <div>Employee</div>
        <div>Date</div>
        <div>Clock In</div>
        <div>Clock Out</div>
        <div>Total Hrs</div>
        <div>Status</div>
      </template>

      <div v-if="loading" class="state-msg">Loading timesheets...</div>
      <div v-else-if="filteredTimesheets.length === 0" class="state-msg">No records found.</div>

      <div 
        v-else
        v-for="ts in filteredTimesheets" 
        :key="ts.id" 
        class="row-card"
        :style="{ gridTemplateColumns: '2.5fr 1.5fr 1fr 1fr 1fr 1fr' }"
      >
        <!-- Employee Info -->
        <div class="col-info">
          <div v-if="getEmployee(ts.employeeId)" class="flex items-center gap-3">
            <BaseAvatar :name="getEmployee(ts.employeeId).name" :size="36" />
            <div>
              <div class="font-semibold text-[#0b2433]">{{ getEmployee(ts.employeeId).name }}</div>
              <!-- Hiển thị tên phòng ban nhỏ bên dưới -->
              <div class="dept-tag">{{ getEmployee(ts.employeeId).empDepartment }}</div>
            </div>
          </div>
          <span v-else class="text-gray-400 italic">Unknown</span>
        </div>

        <!-- Date -->
        <div class="text-sm font-medium text-gray-600">
          {{ formatDate(ts.date) }}
        </div>

        <!-- Time -->
        <div class="time-text text-green-600">{{ ts.clockIn }}</div>
        <div class="time-text text-gray-500">{{ ts.clockOut || '--:--' }}</div>
        
        <!-- Hours -->
        <div class="font-mono text-sm font-bold text-[#0b2433]">
          {{ ts.totalHours }}h
        </div>

        <!-- Status -->
        <div>
          <span class="status-badge" :class="getStatusClass(ts.status)">
            {{ ts.status }}
          </span>
        </div>
      </div>
    </FloatingTable>

    <!-- Modal Clock In/Out (Giữ nguyên logic cũ) -->
    <BaseModal 
      :isOpen="showModal" 
      title="Manual Attendance" 
      @close="closeModal"
    >
      <form @submit.prevent="handleClockAction" class="modal-form">
        <div class="form-group">
          <label>Select Employee</label>
          <select v-model="clockData.employeeId" required>
            <option :value="null">-- Choose Employee --</option>
            <option v-for="emp in employees" :key="emp.id" :value="emp.id">
              {{ emp.name }}
            </option>
          </select>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Date</label>
            <input v-model="clockData.date" type="date" required />
          </div>
          <div class="form-group">
            <label>Time</label>
            <input v-model="clockData.time" type="time" required />
          </div>
        </div>
        <div class="form-group">
          <label>Action Type</label>
          <div class="radio-group">
            <label class="radio-label">
              <input type="radio" v-model="clockData.type" value="IN" />
              <span class="radio-custom in">Clock In</span>
            </label>
            <label class="radio-label">
              <input type="radio" v-model="clockData.type" value="OUT" />
              <span class="radio-custom out">Clock Out</span>
            </label>
          </div>
        </div>
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeModal">Cancel</button>
          <button type="submit" class="btn-submit">Save Record</button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import timesheetService from '@/services/timesheetService';
import employeeService from '@/services/employeeService';
import departmentService from '@/services/departmentService'; // Import thêm Department Service
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import FloatingTable from '@/components/common/FloatingTable.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';

// State
const timesheets = ref([]);
const employees = ref([]);
const departments = ref([]); // State chứa list phòng ban
const loading = ref(true);
const searchQuery = ref('');
const selectedDeptName = ref(null); // Filter theo Tên phòng ban

// Modal State
const showModal = ref(false);
const clockData = reactive({
  employeeId: null,
  date: new Date().toISOString().split('T')[0],
  time: '08:00',
  type: 'IN'
});

// --- DATA FETCHING ---
const fetchData = async () => {
  loading.value = true;
  try {
    // Gọi song song 3 API: Timesheet, Employee, Department
    const [tsRes, empRes, deptRes] = await Promise.all([
      timesheetService.getTimesheets(),
      employeeService.getEmployees(),
      departmentService.getDepartments()
    ]);
    
    timesheets.value = tsRes.data || [];
    employees.value = empRes.data || [];
    departments.value = deptRes.data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

// --- HELPERS ---
const getEmployee = (id) => employees.value.find(e => e.id === id);

// --- COMPUTED: FILTER LOGIC ---
const filteredTimesheets = computed(() => {
  let result = timesheets.value;
  
  // 1. Filter by Department (Logic mới)
  if (selectedDeptName.value) {
    // Lấy ra danh sách ID nhân viên thuộc phòng ban đang chọn
    const empIdsInDept = employees.value
      .filter(e => e.empDepartment === selectedDeptName.value)
      .map(e => e.id);
    
    // Chỉ lấy timesheet của những nhân viên này
    result = result.filter(t => empIdsInDept.includes(t.employeeId));
  }

  // 2. Search Text (Name)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(t => {
      const emp = getEmployee(t.employeeId);
      return emp && emp.name.toLowerCase().includes(q);
    });
  }

  return result;
});

const formatDate = (d) => {
  if (!d) return '';
  const date = new Date(d);
  return date.toLocaleDateString('en-GB', { weekday: 'short', day: '2-digit', month: 'short' });
};

const getStatusClass = (status) => {
  if (status === 'PRESENT') return 'st-present';
  if (status === 'LATE') return 'st-late';
  if (status === 'ABSENT') return 'st-absent';
  return 'st-gray';
};

// Actions
const openClockModal = () => {
  clockData.employeeId = null;
  showModal.value = true;
};
const closeModal = () => showModal.value = false;

const handleClockAction = async () => {
  try {
    const payload = {
      employeeId: clockData.employeeId,
      date: clockData.date,
      timestamp: `${clockData.date}T${clockData.time}:00`
    };

    if (clockData.type === 'IN') {
      const res = await timesheetService.clockIn(payload);
      timesheets.value.unshift(res.data);
    } else {
      await timesheetService.clockOut(payload);
      alert("Clock Out simulated.");
    }
    closeModal();
  } catch (e) {
    alert("Action failed");
  }
};

onMounted(() => fetchData());
</script>

<style scoped>
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; color: #0b2433; }

/* Filter Bar */
.filter-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
.filter-label { font-size: 13px; font-weight: 600; color: #64748b; }
.filter-scroll { display: flex; gap: 8px; overflow-x: auto; padding-bottom: 4px; max-width: 100%; }
.filter-pill {
  display: flex; align-items: center; white-space: nowrap;
  border: none; background: #fff; padding: 6px 14px; border-radius: 20px;
  font-size: 13px; font-weight: 600; color: #64748b; cursor: pointer;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02); transition: all 0.2s;
}
.filter-pill:hover { background: #f1f5f9; color: #0f766e; }
.filter-pill.active { background: #0b2433; color: #fff; box-shadow: 0 4px 10px rgba(11, 36, 51, 0.2); }

/* Table Elements */
.row-card {
  display: grid; align-items: center; background: #ffffff; border-radius: 12px;
  padding: 16px 20px; box-shadow: 0 2px 6px rgba(10, 20, 36, 0.04); transition: all 0.2s ease;
  border: 1px solid transparent; gap: 10px;
}
.row-card:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(10, 20, 36, 0.08); border-color: rgba(95, 209, 197, 0.3); }

.dept-tag { font-size: 11px; color: #94a3b8; font-weight: 500; margin-top: 2px; }

.status-badge { padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
.st-present { background: #dcfce7; color: #16a34a; }
.st-late { background: #fee2e2; color: #ef4444; }
.st-absent { background: #f1f5f9; color: #94a3b8; }
.st-gray { background: #f1f5f9; color: #64748b; }

.time-text { font-family: monospace; font-size: 14px; font-weight: 600; }
.state-msg { text-align: center; padding: 40px; color: #94a3b8; font-style: italic; }

/* Radio & Modal Styles (Reuse) */
.radio-group { display: flex; gap: 12px; }
.radio-label { cursor: pointer; }
.radio-label input { display: none; }
.radio-custom { display: inline-block; padding: 10px 20px; border-radius: 10px; font-weight: 600; font-size: 13px; border: 1px solid #e2e8f0; color: #64748b; }
.radio-label input:checked + .radio-custom.in { background: #dcfce7; color: #16a34a; border-color: #16a34a; }
.radio-label input:checked + .radio-custom.out { background: #fee2e2; color: #ef4444; border-color: #ef4444; }

.modal-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: flex; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; flex: 1; }
.form-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; }
.form-group input, .form-group select { padding: 10px 12px; border-radius: 10px; border: 1px solid #e2e8f0; width: 100%; font-size: 14px; color: #0f172a; background: #f8fafc; }
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
.btn-cancel { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: #64748b; background: transparent; cursor: pointer; border: 1px solid transparent;}
.btn-submit { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: white; background: #5fd1c5; border: none; cursor: pointer; box-shadow: 0 4px 12px rgba(95, 209, 197, 0.3); }
</style>