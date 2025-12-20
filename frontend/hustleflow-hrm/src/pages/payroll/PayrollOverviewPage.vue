<template>
  <div class="page-container">
    <!-- 1. Header (Bỏ nút Run Payroll theo yêu cầu) -->
    <PeoplePageHeader 
      title="Payroll Overview" 
      subtitle="Monthly salary breakdown & department analytics" 
      btnText="" 
      v-model="searchQuery"
    />

    <!-- 2. Stats Grid -->
    <div class="stats-grid">
      <div class="glass-card stat-card">
        <div class="stat-label">Total Net Pay</div>
        <div class="stat-value">{{ formatCurrency(stats.total) }}</div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">Paid</div>
        <div class="stat-value text-green-600">{{ formatCurrency(stats.paid) }}</div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">Pending</div>
        <div class="stat-value text-yellow-600">{{ formatCurrency(stats.pending) }}</div>
      </div>
    </div>

    <!-- 3. Advanced Filters -->
    <div class="filter-section">
      <div class="left-filters">
        <!-- Date Selection -->
        <div class="control-group">
          <span class="label">Period:</span>
          <select v-model="filterMonth">
            <option v-for="m in 12" :key="m" :value="m">Month {{ m }}</option>
          </select>
          <select v-model="filterYear">
            <option v-for="y in 3" :key="y" :value="currentYear - 1 + y">{{ currentYear - 1 + y }}</option>
          </select>
        </div>

        <!-- NEW: Department Selection -->
        <div class="control-group">
          <span class="label">Dept:</span>
          <select v-model="filterDept">
            <option :value="null">All Departments</option>
            <option v-for="d in departments" :key="d.id" :value="d.departmentName">
              {{ d.departmentName }}
            </option>
          </select>
        </div>
      </div>

      <!-- Status Pills -->
      <div class="status-pills">
        <button class="filter-pill" :class="{ active: filterStatus === 'All' }" @click="filterStatus = 'All'">All</button>
        <button class="filter-pill" :class="{ active: filterStatus === 'PAID' }" @click="filterStatus = 'PAID'">Paid</button>
        <button class="filter-pill" :class="{ active: filterStatus === 'UNPAID' }" @click="filterStatus = 'UNPAID'">Unpaid</button>
      </div>
    </div>

    <!-- 4. Payroll Table -->
    <FloatingTable gridColumns="3fr 1.5fr 2fr 1.5fr 1fr 100px">
      <template #header>
        <div>Employee</div>
        <div>Department</div>
        <div class="text-right pr-4">Breakdown</div>
        <div class="text-right pr-4">Net Salary</div>
        <div>Status</div>
        <div class="text-right">Actions</div>
      </template>

      <div v-if="loading" class="state-msg">Loading records...</div>
      <div v-else-if="filteredPayrolls.length === 0" class="state-msg">
        No payroll records found for this criteria.
      </div>

      <div 
        v-else 
        v-for="p in filteredPayrolls" 
        :key="p.id" 
        class="row-card"
        :style="{ gridTemplateColumns: '3fr 1.5fr 2fr 1.5fr 1fr 100px' }"
      >
        <!-- Info -->
        <div class="col-info">
          <BaseAvatar 
            v-if="getEmployee(p.employeeId)" 
            :name="getEmployee(p.employeeId).name" 
            :size="36" 
          />
          <div class="ml-3">
            <div class="font-bold text-[#0b2433]">{{ getEmployee(p.employeeId)?.name || 'Unknown' }}</div>
            <div class="text-xs text-gray-400">#{{ p.employeeId }}</div>
          </div>
        </div>

        <!-- Dept -->
        <div class="text-sm font-medium text-gray-600">
          {{ getEmployee(p.employeeId)?.empDepartment }}
        </div>

        <!-- Breakdown Colors -->
        <div class="breakdown-col">
          <div class="bd-item base" title="Base"><span class="symbol">B</span> {{ formatK(p.baseSalary) }}</div>
          <div v-if="p.bonus > 0" class="bd-item bonus" title="Bonus"><span class="symbol">+</span> {{ formatK(p.bonus) }}</div>
          <div v-if="p.deduction > 0" class="bd-item ded" title="Deduction"><span class="symbol">-</span> {{ formatK(p.deduction) }}</div>
        </div>

        <!-- Net Salary -->
        <div class="text-right pr-4 font-bold text-[#0b2433] text-sm font-mono">
          {{ formatCurrency(p.netSalary) }}
        </div>

        <!-- Status -->
        <div>
          <span class="status-badge" :class="p.status">{{ p.status }}</span>
        </div>

        <!-- Actions -->
        <div class="actions-group">
          <button class="icon-btn edit" @click="openEditModal(p)">
            <Pencil :size="16"/>
          </button>
          <button class="icon-btn view" @click="openSlipModal(p)">
            <ReceiptText :size="16"/>
          </button>
        </div>
      </div>
    </FloatingTable>

    <!-- 5. Edit Modal (Sửa lương thủ công) -->
    <BaseModal :isOpen="showEditModal" title="Adjust Salary" @close="showEditModal = false">
      <form @submit.prevent="handleUpdate" class="modal-form">
        <div class="info-header mb-4">
          <div>Adjusting payroll for: <b>{{ getEmployee(editFormData.employeeId)?.name }}</b></div>
          <div class="text-sm text-gray-500">Period: {{ editFormData.month }}/{{ editFormData.year }}</div>
        </div>

        <div class="form-group">
          <label>Base Salary (Read-only)</label>
          <input :value="formatCurrency(editFormData.baseSalary)" disabled class="input-disabled"/>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Bonus</label>
            <input v-model.number="editFormData.bonus" type="number" min="0" step="100000" />
          </div>
          <div class="form-group">
            <label>Deduction</label>
            <input v-model.number="editFormData.deduction" type="number" min="0" step="50000" />
          </div>
        </div>

        <div class="form-group">
          <label>Payment Status</label>
          <select v-model="editFormData.status">
            <option value="UNPAID">Unpaid</option>
            <option value="PAID">Paid</option>
          </select>
        </div>

        <!-- Auto Calc Net Preview -->
        <div class="preview-box">
          Net Pay: <span class="val">{{ formatCurrency(Number(editFormData.baseSalary) + Number(editFormData.bonus) - Number(editFormData.deduction)) }}</span>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="showEditModal = false">Cancel</button>
          <button type="submit" class="btn-submit">Save Adjustments</button>
        </div>
      </form>
    </BaseModal>

    <!-- 6. Payslip View Modal (Xem chi tiết) -->
    <PayslipModal 
      :isOpen="!!selectedPayroll" 
      :data="selectedPayroll" 
      :employeeName="getEmployee(selectedPayroll?.employeeId)?.name" 
      @close="selectedPayroll = null" 
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { Pencil, ReceiptText } from 'lucide-vue-next'; // Icon
// Import Services
import payrollService from '@/services/payrollService';
import employeeService from '@/services/employeeService';
import departmentService from '@/services/departmentService'; // Thêm để lấy list phòng ban

// Components
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import FloatingTable from '@/components/common/FloatingTable.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import PayslipModal from '@/components/payroll/PayslipModal.vue';

// --- STATE ---
const loading = ref(true);
const payrolls = ref([]);
const employees = ref([]);
const departments = ref([]); // List departments cho filter

const currentYear = new Date().getFullYear();
const filterMonth = ref(new Date().getMonth() + 1);
const filterYear = ref(currentYear);
const filterStatus = ref('All');
const filterDept = ref(null); // Filter Department Value
const searchQuery = ref('');

// Modal state
const showEditModal = ref(false);
const selectedPayroll = ref(null); // Cho payslip
const editFormData = reactive({});

// --- FETCH DATA ---
const fetchData = async () => {
  loading.value = true;
  // Load Payroll theo Tháng/Năm trước để giảm tải
  // Load song song Employee và Department để lấy thông tin map
  try {
    const [payRes, empRes, deptRes] = await Promise.all([
      payrollService.getPayrolls({ 
        month: filterMonth.value, 
        year: filterYear.value, 
        status: filterStatus.value 
      }),
      employeeService.getEmployees(),
      departmentService.getDepartments()
    ]);

    payrolls.value = payRes.data || [];
    employees.value = empRes.data || [];
    departments.value = deptRes.data || [];
  } catch (error) {
    console.error("Error loading payroll:", error);
  } finally {
    loading.value = false;
  }
};

// Watchers: Reload data khi đổi thời gian hoặc status (Status có thể filter client hoặc server tùy API, ở đây mình call lại API cho chuẩn)
watch([filterMonth, filterYear, filterStatus], fetchData);

// --- HELPERS ---
const getEmployee = (id) => employees.value.find(e => e.id === id);
const formatCurrency = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v || 0);
const formatK = (v) => (v / 1000).toLocaleString('vi-VN') + 'k';

// --- CLIENT-SIDE FILTERING (Dept & Search) ---
const filteredPayrolls = computed(() => {
  let result = payrolls.value;

  // 1. Filter Department (Phải map qua Employee trước vì bảng Payroll ko có department)
  if (filterDept.value) {
    result = result.filter(p => {
      const emp = getEmployee(p.employeeId);
      return emp && emp.empDepartment === filterDept.value;
    });
  }

  // 2. Filter Search Name
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(p => {
      const emp = getEmployee(p.employeeId);
      return emp && emp.name.toLowerCase().includes(q);
    });
  }

  return result;
});

// Stats Computed dựa trên list đã filter
const stats = computed(() => {
  const source = filteredPayrolls.value;
  return {
    total: source.reduce((acc, p) => acc + p.netSalary, 0),
    paid: source.filter(p => p.status === 'PAID').reduce((acc, p) => acc + p.netSalary, 0),
    pending: source.filter(p => p.status === 'UNPAID').reduce((acc, p) => acc + p.netSalary, 0),
  }
});

// --- ACTIONS ---
const openSlipModal = (p) => selectedPayroll.value = p;

const openEditModal = (p) => {
  Object.assign(editFormData, JSON.parse(JSON.stringify(p))); // Deep clone
  showEditModal.value = true;
};

const handleUpdate = async () => {
  const res = await payrollService.updatePayroll(editFormData.id, editFormData);
  if (res.data) {
    // Update local data mà không cần reload trang (Tối ưu trải nghiệm)
    const idx = payrolls.value.findIndex(p => p.id === editFormData.id);
    if (idx !== -1) {
      payrolls.value[idx] = res.data; 
    }
    showEditModal.value = false;
  } else {
    alert("Error updating payroll");
  }
};

onMounted(fetchData);
</script>

<style scoped>
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; color: #0b2433; }

/* Stats */
.stats-grid { display: flex; gap: 16px; margin-bottom: 24px; }
.glass-card { background: rgba(255, 255, 255, 0.92); border: 0.5px solid rgba(15, 118, 110, 0.12); box-shadow: 0 6px 18px rgba(10, 20, 36, 0.06); border-radius: 18px; padding: 20px 24px; min-width: 200px; flex: 1; }
.stat-label { font-size: 12px; font-weight: 700; text-transform: uppercase; color: #94a3b8; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: 700; color: #0b2433; }

/* Filters Area */
.filter-section { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.left-filters { display: flex; align-items: center; gap: 16px; }
.control-group { display: flex; align-items: center; gap: 8px; }
.label { font-size: 13px; font-weight: 600; color: #64748b; }
.control-group select { padding: 6px 12px; border-radius: 8px; border: 1px solid #e2e8f0; background: #fff; color: #334155; font-size: 14px; outline: none; cursor: pointer; transition: all 0.2s; }
.control-group select:hover { border-color: #cbd5e1; }

.status-pills { display: flex; gap: 8px; }
.filter-pill { border: none; background: transparent; padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; color: #64748b; cursor: pointer; transition: all 0.2s; }
.filter-pill.active { background: #fff; color: #0b2433; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }

/* Table Styles */
.row-card { display: grid; align-items: center; background: #ffffff; border-radius: 12px; padding: 16px 20px; box-shadow: 0 2px 6px rgba(10, 20, 36, 0.04); margin-bottom: 10px; transition: transform 0.2s; }
.col-info { display: flex; align-items: center; }

/* Breakdown Colors */
.breakdown-col { display: flex; flex-direction: column; align-items: flex-end; justify-content: center; font-size: 11px; font-weight: 600; padding-right: 16px; gap: 2px; }
.bd-item.base { color: #0284c7; } 
.bd-item.bonus { color: #16a34a; } 
.bd-item.ded { color: #dc2626; }
.symbol { opacity: 0.6; display: inline-block; width: 12px; text-align: center; }

/* Status */
.status-badge { padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
.status-badge.PAID { background: #dcfce7; color: #16a34a; }
.status-badge.UNPAID { background: #fee2e2; color: #ef4444; }

/* Action Icons */
.actions-group { display: flex; justify-content: flex-end; gap: 8px; }
.icon-btn { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; border: none; background: transparent; color: #94a3b8; cursor: pointer; transition: all 0.2s; }
.icon-btn.edit:hover { background: #fef9c3; color: #b45309; }
.icon-btn.view:hover { background: #e0f2fe; color: #0284c7; }

/* Modal Form Custom */
.info-header { padding: 12px; background: #f1f5f9; border-radius: 8px; border-left: 4px solid #0b2433; }
.modal-form { display: flex; flex-direction: column; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-row { display: flex; gap: 16px; }
.form-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; }
.form-group input, .form-group select { padding: 10px 12px; border-radius: 8px; border: 1px solid #e2e8f0; width: 100%; font-size: 14px; background: #fff; }
.input-disabled { background: #f8fafc; color: #64748b; }
.preview-box { text-align: right; margin-top: 4px; font-size: 14px; font-weight: 600; color: #334155; }
.preview-box .val { color: #0f766e; font-size: 16px; }
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 16px; }
.btn-cancel { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: #64748b; background: transparent; cursor: pointer; border: 1px solid transparent;}
.btn-submit { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: white; background: #5fd1c5; border: none; cursor: pointer; }
.state-msg { text-align: center; padding: 40px; color: #94a3b8; font-style: italic; }
</style>