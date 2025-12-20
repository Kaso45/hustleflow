<template>
  <div class="page-container">
    <!-- Header -->
    <PeoplePageHeader 
      title="Employees"
      subtitle="Manage employee profiles"
      btnText="New Employee"
      v-model="searchQuery"
      @add="openAddModal"
    />

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div class="glass-card stat-card">
        <div class="stat-label">Total Staff</div>
        <div class="stat-value">{{ employees.length }}</div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">Departments</div>
        <div class="stat-value">{{ departments.length }}</div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">Avg Performance</div>
        <div class="stat-value">82<span class="text-small">/100</span></div>
      </div>
    </div>

    <!-- Filter Pills -->
    <div class="filter-section">
      <button class="filter-pill" :class="{ active: !selectedDept }" @click="selectedDept = null">All</button>
      <button 
        v-for="dept in departments" :key="dept.id"
        class="filter-pill" :class="{ active: selectedDept === dept.departmentName }"
        @click="selectedDept = dept.departmentName"
      >
        {{ dept.departmentName }}
      </button>
    </div>

    <!-- Table -->
    <FloatingTable gridColumns="3fr 1.5fr 1.5fr 1.5fr 100px">
      <template #header>
        <div>Employee Info</div>
        <div>Department</div>
        <div>Job Role</div>
        <div>Performance</div>
        <div class="text-right">Actions</div>
      </template>

      <div v-if="loading" class="state-msg">Loading employees...</div>
      <div v-else-if="filteredEmployees.length === 0" class="state-msg">No employees found.</div>

      <div 
        v-else
        v-for="emp in filteredEmployees" 
        :key="emp.id" 
        class="row-card"
        :style="{ gridTemplateColumns: '3fr 1.5fr 1.5fr 1.5fr 100px' }"
      >
        <!-- Columns -->
        <div class="col-info">
          <BaseAvatar :name="emp.name" :size="40" />
          <div class="info-text">
            <div class="emp-name">{{ emp.name }}</div>
            <div class="emp-email">{{ generateEmail(emp.name) }}</div>
          </div>
        </div>

        <div><span class="dept-badge" :class="getDeptClass(emp.empDepartment)">{{ emp.empDepartment }}</span></div>
        
        <div>
          <div class="role-title">{{ emp.empJobRole }}</div>
          <div class="role-level">Level {{ emp.empJobLevel }}</div>
        </div>

        <div class="pr-4">
          <div class="flex items-center gap-2 mb-1">
            <span class="text-xs font-bold" :style="{ color: getScoreColor(emp.performanceScore) }">{{ emp.performanceScore || 0 }}</span>
          </div>
          <div class="w-full bg-gray-100 rounded-full h-1.5">
            <div class="h-1.5 rounded-full" :style="{ width: (emp.performanceScore || 0) + '%', backgroundColor: getScoreColor(emp.performanceScore) }"></div>
          </div>
        </div>

        <!-- ACTIONS: Dùng Lucide Icons -->
        <div class="actions-group">
          <button class="icon-btn edit" @click.stop="openEditModal(emp)" title="Edit">
            <Pencil :size="16" />
          </button>
          <button class="icon-btn delete" @click.stop="handleDelete(emp.id)" title="Delete">
            <Trash2 :size="16" />
          </button>
        </div>
      </div>
    </FloatingTable>

    <!-- FORM MODAL (Create/Edit) -->
    <BaseModal 
      :isOpen="showModal" 
      :title="isEditing ? 'Edit Employee' : 'New Employee'" 
      @close="closeModal"
    >
      <form @submit.prevent="handleSave" class="modal-form">
        <!-- Name -->
        <div class="form-group">
          <label>Full Name</label>
          <input v-model="formData.name" type="text" placeholder="e.g. Nguyen Van A" required />
        </div>

        <!-- Department & Role Row -->
        <div class="form-row">
          <div class="form-group">
            <label>Department</label>
            <select v-model="formData.empDepartment" required>
              <option disabled value="">Select Dept</option>
              <option v-for="dept in departments" :key="dept.id" :value="dept.departmentName">
                {{ dept.departmentName }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Job Role</label>
            <input v-model="formData.empJobRole" type="text" placeholder="e.g. Developer" required />
          </div>
        </div>

        <!-- Level & Performance Row -->
        <div class="form-row">
          <div class="form-group">
            <label>Level (1-5)</label>
            <input v-model.number="formData.empJobLevel" type="number" min="1" max="5" />
          </div>
          <div class="form-group">
            <label>Score (0-100)</label>
            <input v-model.number="formData.performanceScore" type="number" min="0" max="100" />
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeModal">Cancel</button>
          <button type="submit" class="btn-submit">
            {{ isEditing ? 'Update Changes' : 'Create Employee' }}
          </button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Pencil, Trash2 } from 'lucide-vue-next'; // Import Icon chuẩn
import dashboardService from '@/services/dashboard/dashboardService';
import employeeService from '@/services/employeeService'; 
import BaseAvatar from '@/components/common/BaseAvatar.vue';
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import FloatingTable from '@/components/common/FloatingTable.vue';
import BaseModal from '@/components/common/BaseModal.vue'; // Import Modal mới

// State
const employees = ref([]);
const departments = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const selectedDept = ref(null);

// Modal & Form State
const showModal = ref(false);
const isEditing = ref(false);
const formData = reactive({
  id: null,
  name: '',
  empDepartment: '',
  empJobRole: '',
  empJobLevel: 1,
  performanceScore: 80
});

// --- MODAL ACTIONS ---

const openAddModal = () => {
  isEditing.value = false;
  // Reset Form
  Object.assign(formData, { id: null, name: '', empDepartment: '', empJobRole: '', empJobLevel: 1, performanceScore: 80 });
  showModal.value = true;
};

const openEditModal = (emp) => {
  isEditing.value = true;
  // Clone data vào form
  Object.assign(formData, { ...emp });
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const handleSave = async () => {
  try {
    if (isEditing.value) {
      // UPDATE Logic
      await employeeService.updateEmployee(formData.id, formData);
      // Cập nhật UI local (Optimistic update)
      const index = employees.value.findIndex(e => e.id === formData.id);
      if (index !== -1) employees.value[index] = { ...formData };
      alert("Updated successfully!");
    } else {
      // CREATE Logic
      const res = await employeeService.createEmployee(formData);
      // Giả sử API trả về object vừa tạo (hoặc mock trả về)
      const newEmp = res.data || { ...formData, id: Date.now() }; 
      employees.value.unshift(newEmp);
      alert("Created successfully!");
    }
    closeModal();
  } catch (error) {
    console.error(error);
    alert("Failed to save.");
  }
};

const handleDelete = async (id) => {
  if (!confirm('Are you sure you want to delete this employee?')) return;
  try {
    await employeeService.deleteEmployee(id);
    employees.value = employees.value.filter(e => e.id !== id);
  } catch (error) {
    console.error(error);
  }
};

// --- DATA FETCHING ---
const fetchData = async () => {
  loading.value = true;
  try {
    const [empRes, deptRes] = await Promise.all([
      dashboardService.getEmployees(),
      dashboardService.getDepartments()
    ]);
    employees.value = empRes || [];
    departments.value = deptRes || [];
  } catch (error) {
    console.error("Failed to fetch data", error);
  } finally {
    loading.value = false;
  }
};

// --- HELPERS ---
const filteredEmployees = computed(() => {
  let result = employees.value;
  if (selectedDept.value) result = result.filter(e => e.empDepartment === selectedDept.value);
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(e => e.name?.toLowerCase().includes(query) || e.empJobRole?.toLowerCase().includes(query));
  }
  return result;
});
const generateEmail = (name) => name ? `${name.toLowerCase().replace(/\s/g, '.')}@hustleflow.com` : '';
const getDeptClass = (deptName) => {
  const map = { 'Sales': 'badge-blue', 'HR': 'badge-purple', 'Engineering': 'badge-green', 'Finance': 'badge-orange', 'Marketing': 'badge-pink' };
  return map[deptName] || 'badge-gray';
};
const getScoreColor = (score) => score >= 80 ? '#5fd1c5' : score >= 60 ? '#fcd34d' : '#f87171';

onMounted(() => fetchData());
</script>

<style scoped>
/* Page Styles */
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; color: #0b2433; }

/* Stats & Filter styles reuse... */
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 32px; }
.glass-card { background: rgba(255, 255, 255, 0.92); border: 0.5px solid rgba(15, 118, 110, 0.12); box-shadow: 0 6px 18px rgba(10, 20, 36, 0.06); border-radius: 18px; padding: 20px 24px; }
.stat-label { font-size: 12px; font-weight: 700; text-transform: uppercase; color: #94a3b8; margin-bottom: 8px; }
.stat-value { font-size: 32px; font-weight: 700; color: #0b2433; }
.text-small { font-size: 14px; color: #94a3b8; font-weight: 500; }

.filter-section { display: flex; gap: 10px; margin-bottom: 20px; overflow-x: auto; padding-bottom: 4px; }
.filter-pill { border: none; background: transparent; padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; color: #64748b; cursor: pointer; transition: all 0.2s; }
.filter-pill:hover { background: rgba(95, 209, 197, 0.1); color: #0f766e; }
.filter-pill.active { background: #fff; color: #0b2433; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }

/* Row Card */
.row-card {
  display: grid; align-items: center; background: #ffffff; border-radius: 12px;
  padding: 16px 20px; box-shadow: 0 2px 6px rgba(10, 20, 36, 0.04); transition: all 0.2s ease;
  border: 1px solid transparent; gap: 10px;
}
.row-card:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(10, 20, 36, 0.08); border-color: rgba(95, 209, 197, 0.3); }

/* Column Helpers */
.col-info { display: flex; align-items: center; gap: 14px; overflow: hidden; }
.info-text { overflow: hidden; }
.emp-name { font-weight: 600; font-size: 15px; color: #0b2433; }
.emp-email { font-size: 12px; color: #94a3b8; }
.dept-badge { padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
.badge-blue { background: #e0f2fe; color: #0284c7; }
.badge-purple { background: #f3e8ff; color: #9333ea; }
.badge-green { background: #dcfce7; color: #16a34a; }
.badge-orange { background: #ffedd5; color: #ea580c; }
.badge-gray { background: #f1f5f9; color: #475569; }
.badge-pink { background: #fce7f3; color: #be185d; }
.role-title { font-size: 14px; font-weight: 500; color: #334155; }
.role-level { font-size: 11px; color: #94a3b8; margin-top: 2px; }

/* Actions */
.actions-group { display: flex; justify-content: flex-end; gap: 8px; }
.icon-btn {
  width: 32px; height: 32px; min-width: 32px; flex-shrink: 0;
  border-radius: 8px; border: none; background: transparent;
  color: #94a3b8; cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.icon-btn.edit:hover { background: #e0f2fe; color: #0284c7; }
.icon-btn.delete:hover { background: #fee2e2; color: #ef4444; }
.text-right { text-align: right; }
.state-msg { text-align: center; padding: 40px; color: #94a3b8; font-style: italic; }

/* FORM STYLES */
.modal-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: flex; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; flex: 1; }
.form-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; }
.form-group input, .form-group select {
  padding: 10px 12px; border-radius: 10px; border: 1px solid #e2e8f0;
  font-size: 14px; color: #0f172a; transition: all 0.2s; background: #f8fafc;
}
.form-group input:focus, .form-group select:focus {
  background: #fff; border-color: #5fd1c5; outline: none; box-shadow: 0 0 0 3px rgba(95,209,197,0.15);
}
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
.btn-cancel {
  padding: 10px 20px; border-radius: 12px; font-weight: 600; color: #64748b;
  background: transparent; border: 1px solid transparent; cursor: pointer;
}
.btn-cancel:hover { background: #f1f5f9; }
.btn-submit {
  padding: 10px 20px; border-radius: 12px; font-weight: 600; color: white;
  background: #5fd1c5; border: none; cursor: pointer;
  box-shadow: 0 4px 12px rgba(95, 209, 197, 0.3);
}
.btn-submit:hover { background: #4bc2b6; }
</style>