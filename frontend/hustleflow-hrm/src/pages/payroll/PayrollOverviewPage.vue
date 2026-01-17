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
        <div class="stat-value text-green-600">
          {{ formatCurrency(stats.paid) }}
        </div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">Pending</div>
        <div class="stat-value text-yellow-600">
          {{ formatCurrency(stats.pending) }}
        </div>
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
            <option v-for="y in 3" :key="y" :value="currentYear - 1 + y">
              {{ currentYear - 1 + y }}
            </option>
          </select>
        </div>

        <!-- NEW: Department Selection -->
        <div class="control-group">
          <span class="label">Dept:</span>
          <select v-model="filterDept">
            <option :value="null">All Departments</option>
            <option
              v-for="d in departments"
              :key="d.id"
              :value="d.departmentName"
            >
              {{ d.departmentName }}
            </option>
          </select>
        </div>
      </div>

      <!-- Status Pills -->
      <div class="status-pills">
        <button
          class="filter-pill"
          :class="{ active: filterStatus === 'All' }"
          @click="filterStatus = 'All'"
        >
          All
        </button>
        <button
          class="filter-pill"
          :class="{ active: filterStatus === 'PAID' }"
          @click="filterStatus = 'PAID'"
        >
          Paid
        </button>
        <button
          class="filter-pill"
          :class="{ active: filterStatus === 'UNPAID' }"
          @click="filterStatus = 'UNPAID'"
        >
          Unpaid
        </button>
      </div>

      <!-- Actions Right -->
      <div class="actions-right">
        <button class="btn-secondary" @click="openGenerateModal">
          Bulk Generate
        </button>
        <button class="btn-primary" @click="openCreateModal">
          Create Payroll
        </button>
      </div>
    </div>

    <!-- 4. Payroll Table -->
    <div class="table-wrapper">
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
            <div class="info-text">
              <div class="info-card-name">
                {{ getEmployee(p.employeeId)?.name || "Unknown" }}
              </div>
              <div class="info-card-meta">ID: #{{ p.employeeId }}</div>
            </div>
          </div>

          <!-- Dept -->
          <div class="info-pill-stack">
            <span
              v-for="token in deptTokens(p)"
              :key="token"
              class="info-pill is-muted"
            >
              {{ token }}
            </span>
          </div>

          <!-- Breakdown Colors -->
          <div class="breakdown-col">
            <div class="bd-item base" title="Base">
              <span class="symbol">B</span> {{ formatK(p.baseSalary) }}
            </div>
            <div v-if="p.bonus > 0" class="bd-item bonus" title="Bonus">
              <span class="symbol">+</span> {{ formatK(p.bonus) }}
            </div>
            <div v-if="p.deduction > 0" class="bd-item ded" title="Deduction">
              <span class="symbol">-</span> {{ formatK(p.deduction) }}
            </div>
          </div>

          <!-- Net Salary -->
          <div class="info-stack text-right">
            <span class="info-label">Net Salary</span>
            <span class="info-value">{{ formatCurrency(p.netSalary) }}</span>
          </div>

          <!-- Status -->
          <div>
            <span class="status-badge" :class="p.status">{{ p.status }}</span>
          </div>

          <!-- Actions -->
          <div class="actions-group">
            <button class="icon-btn edit" @click="openEditModal(p)">
              <Pencil :size="16" />
            </button>
          </div>
        </div>
      </FloatingTable>
    </div>

    <!-- 5. Edit Modal (Sửa lương thủ công) -->
    <BaseModal
      :isOpen="showEditModal"
      title="Adjust Salary"
      @close="showEditModal = false"
    >
      <form @submit.prevent="handleUpdate" class="modal-form">
        <div class="info-header mb-4">
          <div>
            Adjusting payroll for:
            <b>{{ getEmployee(editFormData.employeeId)?.name }}</b>
          </div>
          <div class="text-sm text-gray-500">
            Period: {{ editFormData.month }}/{{ editFormData.year }}
          </div>
        </div>

        <div class="form-group">
          <label>Base Salary (Read-only)</label>
          <input
            :value="formatCurrency(editFormData.baseSalary)"
            disabled
            class="input-disabled"
          />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Bonus</label>
            <input
              v-model.number="editFormData.bonus"
              type="number"
              min="0"
              step="100000"
            />
          </div>
          <div class="form-group">
            <label>Deduction</label>
            <input
              v-model.number="editFormData.deduction"
              type="number"
              min="0"
              step="50000"
            />
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
          Net Pay:
          <span class="val">{{
            formatCurrency(
              Number(editFormData.baseSalary) +
                Number(editFormData.bonus) -
                Number(editFormData.deduction)
            )
          }}</span>
        </div>

        <div class="form-actions">
          <button
            type="button"
            class="btn-cancel"
            @click="showEditModal = false"
          >
            Cancel
          </button>
          <button type="submit" class="btn-submit">Save Adjustments</button>
        </div>
      </form>
    </BaseModal>

    <!-- 6. Payslip View Modal (Xem chi tiết) -->
    <!-- 7. Create Payroll Modal -->
    <BaseModal
      :isOpen="showCreateModal"
      title="Create Payroll"
      @close="showCreateModal = false"
    >
      <form @submit.prevent="handleCreate" class="modal-form">
        <div class="form-row">
          <div class="form-group">
            <label>Employee</label>
            <select v-model.number="createFormData.employeeId" required>
              <option :value="null" disabled>Select employee</option>
              <option v-for="e in employees" :key="e.id" :value="e.id">
                {{ e.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Department Code</label>
            <select v-model="createFormData.departmentCode" required>
              <option :value="''" disabled>Select department</option>
              <option v-for="d in departments" :key="d.id" :value="d.code">
                {{ d.code }} - {{ d.departmentName }}
              </option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Month</label>
            <select v-model.number="createFormData.month" required>
              <option v-for="m in 12" :key="m" :value="m">{{ m }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>Year</label>
            <select v-model.number="createFormData.year" required>
              <option v-for="y in 3" :key="y" :value="currentYear - 1 + y">
                {{ currentYear - 1 + y }}
              </option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Base Salary</label>
            <input
              v-model.number="createFormData.baseSalary"
              type="number"
              min="0"
              step="100000"
              required
            />
          </div>
          <div class="form-group">
            <label>Bonus</label>
            <input
              v-model.number="createFormData.bonus"
              type="number"
              min="0"
              step="100000"
            />
          </div>
          <div class="form-group">
            <label>Deduction</label>
            <input
              v-model.number="createFormData.deduction"
              type="number"
              min="0"
              step="50000"
            />
          </div>
        </div>

        <div class="form-group">
          <label>Status</label>
          <select v-model="createFormData.status">
            <option value="UNPAID">Unpaid</option>
            <option value="PAID">Paid</option>
          </select>
        </div>

        <div class="preview-box">
          Net Pay:
          <span class="val">{{
            formatCurrency(
              Number(createFormData.baseSalary || 0) +
                Number(createFormData.bonus || 0) -
                Number(createFormData.deduction || 0)
            )
          }}</span>
        </div>

        <div class="form-actions">
          <button
            type="button"
            class="btn-cancel"
            @click="showCreateModal = false"
          >
            Cancel
          </button>
          <button type="submit" class="btn-submit">Create</button>
        </div>
      </form>
    </BaseModal>

    <!-- 8. Bulk Generate Modal -->
    <BaseModal
      :isOpen="showGenerateModal"
      title="Bulk Generate Payrolls"
      @close="showGenerateModal = false"
    >
      <form @submit.prevent="handleGenerate" class="modal-form">
        <div class="form-section-label">Time Period</div>
        <div class="form-row">
          <div class="form-group flex-1">
            <label>Month</label>
            <div class="input-wrapper">
              <select v-model.number="generateFormData.month" required>
                <option v-for="m in 12" :key="m" :value="m">
                  Month {{ m }}
                </option>
              </select>
            </div>
          </div>
          <div class="form-group flex-1">
            <label>Year</label>
            <div class="input-wrapper">
              <select v-model.number="generateFormData.year" required>
                <option v-for="y in 3" :key="y" :value="currentYear - 1 + y">
                  {{ currentYear - 1 + y }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <hr class="divider" />

        <div class="form-group">
          <label class="apply-all-toggle">
            <span>Apply to all departments</span>
            <input
              type="checkbox"
              class="accent-[#5fd1c5]"
              v-model="generateFormData.applyToAllDepartments"
            />
          </label>

          <div
            class="custom-select-container"
            :class="{ disabled: generateFormData.applyToAllDepartments }"
          >
            <div
              class="select-trigger"
              @click="
                !generateFormData.applyToAllDepartments &&
                  (isDeptDropdownOpen = !isDeptDropdownOpen)
              "
            >
              <span v-if="generateFormData.applyToAllDepartments"
                >All Departments Selected</span
              >
              <span
                v-else-if="generateFormData.departmentCodes.length === 0"
                class="placeholder"
                >Select departments...</span
              >
              <span v-else
                >{{ generateFormData.departmentCodes.length }} departments
                selected</span
              >
              <span class="chevron">▼</span>
            </div>

            <div
              v-if="
                isDeptDropdownOpen && !generateFormData.applyToAllDepartments
              "
              class="select-dropdown"
            >
              <div
                v-for="d in departments"
                :key="d.id"
                class="dropdown-item"
                @click="toggleDepartment(d.code)"
              >
                <input
                  type="checkbox"
                  :checked="generateFormData.departmentCodes.includes(d.code)"
                  class="accent-[#5fd1c5]"
                  @click.stop
                />
                <span>{{ d.code }} - {{ d.departmentName }}</span>
              </div>
            </div>
          </div>

          <div
            v-if="
              !generateFormData.applyToAllDepartments &&
              generateFormData.departmentCodes.length > 0
            "
            class="selected-tags"
          >
            <span
              v-for="code in generateFormData.departmentCodes"
              :key="code"
              class="tag"
            >
              {{ code }}
              <button type="button" @click="toggleDepartment(code)">×</button>
            </span>
          </div>
        </div>

        <hr class="divider" />

        <div class="form-section-label">Default Values (Optional)</div>
        <div class="financial-grid">
          <div class="form-group">
            <label>Base Salary</label>
            <div class="input-wrapper">
              <input
                v-model.number="generateFormData.baseSalary"
                type="number"
                min="0"
                step="100000"
                placeholder="0"
              />
              <span class="currency-suffix">VND</span>
            </div>
          </div>
          <div class="form-group">
            <label>Bonus</label>
            <div class="input-wrapper success-border">
              <input
                v-model.number="generateFormData.bonus"
                type="number"
                min="0"
                step="100000"
                placeholder="0"
              />
              <span class="currency-suffix text-green-600">+</span>
            </div>
          </div>
          <div class="form-group">
            <label>Deduction</label>
            <div class="input-wrapper danger-border">
              <input
                v-model.number="generateFormData.deduction"
                type="number"
                min="0"
                step="50000"
                placeholder="0"
              />
              <span class="currency-suffix text-red-600">-</span>
            </div>
          </div>
        </div>

        <div class="form-actions pt-4">
          <button
            type="button"
            class="btn-cancel"
            @click="showGenerateModal = false"
          >
            Cancel
          </button>
          <button type="submit" class="btn-submit w-full sm:w-auto">
            Generate Payrolls
          </button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from "vue";
import { Pencil } from "lucide-vue-next"; // Icon
// Import Services
import payrollService from "@/services/payrollService";
import employeeService from "@/services/employeeService";
import departmentService from "@/services/departmentService"; // Thêm để lấy list phòng ban
import { createPayroll } from "@/services/payrollService";
import { USE_MOCK_API } from "@/config/appConfig";

// Components
import PeoplePageHeader from "@/components/common/PeoplePageHeader.vue";
import FloatingTable from "@/components/common/FloatingTable.vue";
import BaseAvatar from "@/components/common/BaseAvatar.vue";
import BaseModal from "@/components/common/BaseModal.vue";

// --- STATE ---
const loading = ref(true);
const payrolls = ref([]);
const employees = ref([]);
const departments = ref([]); // List departments cho filter

const currentYear = new Date().getFullYear();
const filterMonth = ref(new Date().getMonth() + 1);
const filterYear = ref(currentYear);
const filterStatus = ref("All");
const filterDept = ref(null); // Filter Department Value
const searchQuery = ref("");

// Modal state
const showEditModal = ref(false);
const isDeptDropdownOpen = ref(false);
const editFormData = reactive({});
const showCreateModal = ref(false);
const showGenerateModal = ref(false);
const createFormData = reactive({
  employeeId: null,
  departmentCode: "",
  month: new Date().getMonth() + 1,
  year: currentYear,
  baseSalary: 0,
  bonus: 0,
  deduction: 0,
  status: "UNPAID",
});
const generateFormData = reactive({
  month: new Date().getMonth() + 1,
  year: currentYear,
  departmentCodes: [],
  applyToAllDepartments: true,
  baseSalary: null,
  bonus: null,
  deduction: null,
});
const toggleDepartment = (code) => {
  const index = generateFormData.departmentCodes.indexOf(code);
  if (index === -1) {
    generateFormData.departmentCodes.push(code);
  } else {
    generateFormData.departmentCodes.splice(index, 1);
  }
};

// Auto-fill departmentCode when selecting an employee in Create form
watch(
  () => createFormData.employeeId,
  (newEmpId) => {
    if (!newEmpId) {
      createFormData.departmentCode = "";
      return;
    }
    const emp = employees.value.find((e) => e.id === newEmpId);
    if (!emp) {
      createFormData.departmentCode = "";
      return;
    }
    // Prefer direct code on employee if exists; otherwise map by department name
    const codeFromEmp = emp.empDepartmentCode || emp.departmentCode;
    if (codeFromEmp) {
      createFormData.departmentCode = codeFromEmp;
      return;
    }
    const deptObj = departments.value.find(
      (d) =>
        d.id === emp.empDepartmentId || d.departmentName === emp.empDepartment
    );
    createFormData.departmentCode = deptObj?.code || "";
  }
);

// --- FETCH DATA ---
const fetchData = async () => {
  loading.value = true;
  // Load Payroll theo Tháng/Năm trước để giảm tải
  // Load song song Employee và Department để lấy thông tin map
  try {
    // Build params; do not send status when 'All' to conform with backend API
    const params = {
      month: filterMonth.value,
      year: filterYear.value,
    };

    if (filterStatus.value !== "All") params.status = filterStatus.value;

    const payRes = await payrollService
      .getPayrolls(params)
      .catch(() => ({ data: [] }));
    const empRes = await employeeService
      .getEmployees()
      .catch(() => ({ data: [] }));
    const deptRes = await departmentService
      .getDepartments()
      .catch(() => ({ data: [] }));

    payrolls.value = payRes?.data || [];
    employees.value = empRes?.data || [];
    departments.value = deptRes?.data || [];
  } catch (error) {
    console.error("Error loading payroll:", error);
  } finally {
    loading.value = false;
  }
};

// Ensure reference lists (employees, departments) are loaded (used by modals)
const ensureReferenceData = async () => {
  const needsEmp = employees.value.length === 0;
  const needsDept = departments.value.length === 0;
  if (!needsEmp && !needsDept) return;
  try {
    const [empRes, deptRes] = await Promise.allSettled([
      needsEmp
        ? employeeService.getEmployees()
        : Promise.resolve({
            status: "fulfilled",
            value: { data: employees.value },
          }),
      needsDept
        ? departmentService.getDepartments()
        : Promise.resolve({
            status: "fulfilled",
            value: { data: departments.value },
          }),
    ]);
    if (needsEmp && empRes.status === "fulfilled") {
      employees.value = empRes.value.data || [];
    }
    if (needsDept && deptRes.status === "fulfilled") {
      departments.value = deptRes.value.data || [];
    }
  } catch (e) {
    // Silent fail; modals will still open, selects may be empty
  }
};

// Watchers: Reload data khi đổi thời gian hoặc status (Status có thể filter client hoặc server tùy API, ở đây mình call lại API cho chuẩn)
watch([filterMonth, filterYear, filterStatus], fetchData);

// --- HELPERS ---
const getEmployee = (id) => employees.value.find((e) => e.id === id);
const formatCurrency = (v) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    v || 0
  );
const formatK = (v) => (v / 1000).toLocaleString("vi-VN") + "k";

// --- CLIENT-SIDE FILTERING (Dept & Search) ---
const filteredPayrolls = computed(() => {
  let data = payrolls.value;
  if (filterDept.value) {
    data = data.filter(
      (p) => getEmployee(p.employeeId)?.empDepartment === filterDept.value
    );
  }
  if (filterStatus.value !== "All") {
    data = data.filter((p) => p.status === filterStatus.value);
  }
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    data = data.filter((p) =>
      getEmployee(p.employeeId)?.name?.toLowerCase().includes(q)
    );
  }
  return data;
});

const deptTokens = (p) => {
  const raw = getEmployee(p.employeeId)?.empDepartment || "—";
  return raw.split(/\s+/).filter(Boolean);
};

// Stats Computed dựa trên list đã filter
const stats = computed(() => {
  const source = filteredPayrolls.value;
  return {
    total: source.reduce((acc, p) => acc + p.netSalary, 0),
    paid: source
      .filter((p) => p.status === "PAID")
      .reduce((acc, p) => acc + p.netSalary, 0),
    pending: source
      .filter((p) => p.status === "UNPAID")
      .reduce((acc, p) => acc + p.netSalary, 0),
  };
});

const openEditModal = (p) => {
  Object.assign(editFormData, JSON.parse(JSON.stringify(p))); // Deep clone
  showEditModal.value = true;
};

const handleUpdate = async () => {
  const res = await payrollService.updatePayroll(editFormData.id, editFormData);
  if (res.data) {
    // Update local data mà không cần reload trang (Tối ưu trải nghiệm)
    const idx = payrolls.value.findIndex((p) => p.id === editFormData.id);
    if (idx !== -1) {
      payrolls.value[idx] = res.data;
    }
    showEditModal.value = false;
  } else {
    alert("Error updating payroll");
  }
};

const openCreateModal = async () => {
  await ensureReferenceData();
  Object.assign(createFormData, {
    employeeId: null,
    departmentCode: "",
    month: filterMonth.value,
    year: filterYear.value,
    baseSalary: 0,
    bonus: 0,
    deduction: 0,
    status: "UNPAID",
  });
  showCreateModal.value = true;
};

const handleCreate = async () => {
  try {
    if (USE_MOCK_API) {
      const net =
        Number(createFormData.baseSalary || 0) +
        Number(createFormData.bonus || 0) -
        Number(createFormData.deduction || 0);
      const local = {
        id: Date.now(),
        generatedAt: new Date().toISOString(),
        netSalary: net,
        ...createFormData,
      };
      payrolls.value.unshift(local);
      showCreateModal.value = false;
      return;
    }
    const res = await createPayroll(createFormData);
    if (res?.data) payrolls.value.unshift(res.data);
    showCreateModal.value = false;
  } catch (e) {
    console.error(e);
    alert("Failed to create payroll");
  }
};

const openGenerateModal = async () => {
  await ensureReferenceData();
  Object.assign(generateFormData, {
    month: filterMonth.value,
    year: filterYear.value,
    departmentCodes: [],
    applyToAllDepartments: true,
    baseSalary: null,
    bonus: null,
    deduction: null,
  });
  showGenerateModal.value = true;
};

const handleGenerate = async () => {
  try {
    const payload = {
      month: generateFormData.month,
      year: generateFormData.year,
      departmentCodes: generateFormData.applyToAllDepartments
        ? []
        : generateFormData.departmentCodes,
      applyToAllDepartments: !!generateFormData.applyToAllDepartments,
    };
    if (
      generateFormData.baseSalary !== null &&
      generateFormData.baseSalary !== undefined
    ) {
      payload.baseSalary = generateFormData.baseSalary;
    }
    if (
      generateFormData.bonus !== null &&
      generateFormData.bonus !== undefined
    ) {
      payload.bonus = generateFormData.bonus;
    }
    if (
      generateFormData.deduction !== null &&
      generateFormData.deduction !== undefined
    ) {
      payload.deduction = generateFormData.deduction;
    }
    const res = await payrollService.generatePayroll(payload);
    // Với API thật: res.data là list payrolls; Với mock: chỉ message -> gọi fetch lại
    await fetchData();
    showGenerateModal.value = false;
  } catch (e) {
    console.error(e);
    alert("Failed to generate payrolls");
  }
};

onMounted(fetchData);
</script>

<style scoped>
.page-container {
  padding: 24px;
  background-color: #f3f7f9;
  min-height: 100vh;
  font-family: "Inter", sans-serif;
  color: #0b2433;
}

/* Stats */
.stats-grid {
  display: grid;
  gap: 16px;
  margin-bottom: 24px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}
.glass-card {
  background: rgba(255, 255, 255, 0.92);
  border: 0.5px solid rgba(15, 118, 110, 0.12);
  box-shadow: 0 6px 18px rgba(10, 20, 36, 0.06);
  border-radius: 18px;
  padding: 20px 24px;
  min-width: 200px;
  flex: 1;
}
.stat-label {
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  color: #94a3b8;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #0b2433;
}

/* Filters Area */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}
.left-filters {
  display: flex;
  align-items: center;
  gap: 16px;
}
.control-group {
  display: flex;
  align-items: center;
  gap: 8px;
}
.label {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
}
.control-group select {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #334155;
  font-size: 14px;
  outline: none;
  cursor: pointer;
  transition: all 0.2s;
}
.control-group select:hover {
  border-color: #cbd5e1;
}

.status-pills {
  display: flex;
  gap: 8px;
}
.filter-pill {
  border: none;
  background: transparent;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}
.filter-pill.active {
  background: #fff;
  color: #0b2433;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.actions-right {
  display: flex;
  gap: 8px;
  margin-left: auto;
}
.btn-primary {
  padding: 8px 14px;
  border-radius: 10px;
  border: none;
  background: #5fd1c5;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}
.btn-secondary {
  padding: 8px 14px;
  border-radius: 10px;
  border: 1px solid #cbd5e1;
  background: #fff;
  color: #0b2433;
  font-weight: 600;
  cursor: pointer;
}

/* Table Styles */
.row-card {
  display: grid;
  align-items: center;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 2px 6px rgba(10, 20, 36, 0.04);
  margin-bottom: 10px;
  transition: transform 0.2s;
}
.col-info {
  display: flex;
  align-items: center;
}

/* Breakdown Colors */
.breakdown-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  padding-right: 16px;
  gap: 2px;
}
.bd-item.base {
  color: #0284c7;
}
.bd-item.bonus {
  color: #16a34a;
}
.bd-item.ded {
  color: #dc2626;
}
.symbol {
  opacity: 0.6;
  display: inline-block;
  width: 12px;
  text-align: center;
}

/* Status */
.status-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}
.status-badge.PAID {
  background: #dcfce7;
  color: #16a34a;
}
.status-badge.UNPAID {
  background: #fee2e2;
  color: #ef4444;
}

/* Action Icons */
.actions-group {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.icon-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s;
}
.icon-btn.edit:hover {
  background: #fef9c3;
  color: #b45309;
}
.table-wrapper {
  width: 100%;
  overflow-x: auto;
}

/* Modal Form Custom */
.info-header {
  padding: 12px;
  background: #f1f5f9;
  border-radius: 8px;
  border-left: 4px solid #0b2433;
}
.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-row {
  display: flex;
  gap: 16px;
}
.form-group label {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
}
.form-group input,
.form-group select {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  width: 100%;
  font-size: 14px;
  background: #fff;
}
.input-disabled {
  background: #f8fafc;
  color: #64748b;
}
.preview-box {
  text-align: right;
  margin-top: 4px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}
.preview-box .val {
  color: #0f766e;
  font-size: 16px;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}
.btn-cancel {
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 600;
  color: #64748b;
  background: transparent;
  cursor: pointer;
  border: 1px solid transparent;
}
.btn-submit {
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 600;
  color: white;
  background: #5fd1c5;
  border: none;
  cursor: pointer;
}
.state-msg {
  text-align: center;
  padding: 40px;
  color: #94a3b8;
  font-style: italic;
}
/* --- New Modern Form Styles --- */
.divider {
  border: 0;
  border-top: 1px solid #e2e8f0;
  margin: 8px 0;
}

/* Custom Input Wrappers */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper input,
.input-wrapper select {
  padding-right: 40px; /* Space for suffix */
}

.currency-suffix {
  position: absolute;
  right: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  pointer-events: none;
}

/* Grid for Financials */
.financial-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
}

/* Custom Select / Multi-Select */
.custom-select-container {
  position: relative;
  font-size: 14px;
}

.custom-select-container.disabled {
  opacity: 0.6;
  pointer-events: none;
}

.select-trigger {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 10px 12px;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  min-height: 42px;
}

.select-trigger .placeholder {
  color: #94a3b8;
}

.select-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-height: 200px;
  overflow-y: auto;
  z-index: 50;
  margin-top: 4px;
}

.dropdown-item {
  padding: 10px 12px;
  display: grid;
  grid-template-columns: auto 1fr;
  align-items: center;
  column-gap: 10px;
  cursor: pointer;
  transition: background 0.2s;
  width: 100%;
}

.dropdown-item span {
  font-weight: 600;
  color: #0b2433;
  text-align: left;
}

.dropdown-item input {
  justify-self: start;
  margin: 0;
}

.apply-all-toggle {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #0b2433;
  padding-bottom: 8px;
}

.apply-all-toggle span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.apply-all-toggle input {
  width: 16px;
  height: 16px;
}

.dropdown-item:hover {
  background: #f1f5f9;
}

/* Tags for selected items */
.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.tag {
  background: #e0f2fe;
  color: #0284c7;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag button {
  border: none;
  background: none;
  color: #0284c7;
  font-size: 16px;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

/* Semantic Borders for Financials */
.success-border input:focus {
  border-color: #22c55e !important;
  box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.1);
}

.danger-border input:focus {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.1);
}

@media (max-width: 1024px) {
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions-right {
    width: 100%;
    justify-content: flex-start;
  }

  .left-filters {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  }

  .left-filters,
  .status-pills {
    width: 100%;
    justify-content: space-between;
  }

  .control-group {
    width: 100%;
    justify-content: space-between;
  }

  .control-group select {
    flex: 1;
  }

  .row-card {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .breakdown-col {
    flex-direction: row;
    justify-content: flex-start;
    padding-right: 0;
  }

  .actions-group {
    justify-content: flex-start;
  }
}

@media (max-width: 480px) {
  .actions-right {
    flex-direction: column;
    width: 100%;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    text-align: center;
  }
}
</style>
