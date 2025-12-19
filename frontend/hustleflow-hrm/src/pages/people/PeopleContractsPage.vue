<template>
  <div class="page-container">
    <!-- Header -->
    <PeoplePageHeader 
      title="Contracts"
      subtitle="Manage employee contracts & agreements"
      btnText="New Contract"
      v-model="searchQuery"
      @add="openAddModal"
    />

    <!-- Stats -->
    <div class="stats-grid">
      <div class="glass-card stat-card">
        <div class="stat-label">Active Contracts</div>
        <div class="stat-value">{{ contracts.filter(c => c.status === 'ACTIVE').length }}</div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">Total Salary Fund</div>
        <div class="stat-value text-accent">{{ formatCurrency(totalSalary) }}</div>
      </div>
    </div>

    <!-- Table -->
    <!-- Columns: Employee (2.5fr), Type (1.5fr), Duration (2fr), Salary (1.5fr), Status (1fr), Actions (100px) -->
    <FloatingTable gridColumns="2.5fr 1.5fr 2fr 1.5fr 1fr 100px">
      <template #header>
        <div>Employee</div>
        <div>Contract Type</div>
        <div>Duration</div>
        <div class="text-right pr-4">Base Salary</div>
        <div>Status</div>
        <div class="text-right">Actions</div>
      </template>

      <div v-if="loading" class="state-msg">Loading contracts...</div>
      <div v-else-if="filteredContracts.length === 0" class="state-msg">No contracts found.</div>

      <div 
        v-else
        v-for="contract in filteredContracts" 
        :key="contract.id" 
        class="row-card"
        :style="{ gridTemplateColumns: '2.5fr 1.5fr 2fr 1.5fr 1fr 100px' }"
      >
        <!-- Employee Info (Mapped) -->
        <div class="col-info">
          <div v-if="getEmployee(contract.employeeId)" class="flex items-center gap-3">
            <BaseAvatar :name="getEmployee(contract.employeeId).name" :size="36" />
            <div class="truncate">
              <div class="font-semibold text-[#0b2433]">{{ getEmployee(contract.employeeId).name }}</div>
              <div class="text-xs text-gray-400">ID: {{ contract.employeeId }}</div>
            </div>
          </div>
          <span v-else class="text-gray-400 italic">Unknown Employee</span>
        </div>

        <!-- Type & File Link -->
        <div class="flex items-center gap-2">
          <span class="text-sm font-medium text-gray-700">{{ formatType(contract.contractType) }}</span>
          <a 
            v-if="contract.fileUrl" 
            :href="contract.fileUrl" 
            target="_blank" 
            class="file-link"
            title="View Contract File"
          >
            <FileText :size="14" />
          </a>
        </div>

        <!-- Duration -->
        <div class="text-sm text-gray-600">
          <div>{{ formatDate(contract.startDate) }}</div>
          <div class="text-xs text-gray-400">to {{ formatDate(contract.endDate) }}</div>
        </div>

        <!-- Salary (Right Aligned) -->
        <div class="text-right pr-4 font-mono font-medium text-[#0b2433]">
          {{ formatCurrency(contract.baseSalary) }}
        </div>

        <!-- Status -->
        <div>
          <span class="status-badge" :class="getStatusClass(contract.status)">
            {{ contract.status }}
          </span>
        </div>

        <!-- Actions -->
        <div class="actions-group">
          <button class="icon-btn edit" @click.stop="openEditModal(contract)">
            <Pencil :size="16" />
          </button>
          <button class="icon-btn delete" @click.stop="handleDelete(contract.id)">
            <Trash2 :size="16" />
          </button>
        </div>
      </div>
    </FloatingTable>

    <!-- Modal Form -->
    <BaseModal 
      :isOpen="showModal" 
      :title="isEditing ? 'Edit Contract' : 'New Contract'" 
      @close="closeModal"
    >
      <form @submit.prevent="handleSave" class="modal-form">
        <!-- Employee Select -->
        <div class="form-group">
          <label>Employee</label>
          <select v-model="formData.employeeId" required :disabled="isEditing">
            <option :value="null">-- Select Employee --</option>
            <option v-for="emp in employees" :key="emp.id" :value="emp.id">
              {{ emp.name }} - {{ emp.empJobRole }}
            </option>
          </select>
        </div>

        <!-- Contract Type & Status -->
        <div class="form-row">
          <div class="form-group">
            <label>Type</label>
            <select v-model="formData.contractType">
              <option value="FULL_TIME">Full Time</option>
              <option value="PART_TIME">Part Time</option>
              <option value="INTERN">Internship</option>
              <option value="FREELANCE">Freelance</option>
            </select>
          </div>
          <div class="form-group">
            <label>Status</label>
            <select v-model="formData.status">
              <option value="ACTIVE">Active</option>
              <option value="PENDING">Pending</option>
              <option value="EXPIRED">Expired</option>
              <option value="TERMINATED">Terminated</option>
            </select>
          </div>
        </div>

        <!-- Dates -->
        <div class="form-row">
          <div class="form-group">
            <label>Start Date</label>
            <input v-model="formData.startDate" type="date" required />
          </div>
          <div class="form-group">
            <label>End Date</label>
            <input v-model="formData.endDate" type="date" />
          </div>
        </div>

        <!-- Salary -->
        <div class="form-group">
          <label>Base Salary (VND)</label>
          <input v-model.number="formData.baseSalary" type="number" min="0" step="100000" placeholder="e.g. 20000000" />
        </div>

        <!-- File URL (Simulation) -->
        <div class="form-group">
          <label>File URL (Link)</label>
          <input v-model="formData.fileUrl" type="text" placeholder="/docs/contracts/..." />
        </div>

        <!-- Footer -->
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeModal">Cancel</button>
          <button type="submit" class="btn-submit">
            {{ isEditing ? 'Update Contract' : 'Create Contract' }}
          </button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Pencil, Trash2, FileText } from 'lucide-vue-next'; // Icon FileText cho hợp đồng
import contractService from '@/services/contractService';
import employeeService from '@/services/employeeService';
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import FloatingTable from '@/components/common/FloatingTable.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';

// State
const contracts = ref([]);
const employees = ref([]);
const loading = ref(true);
const searchQuery = ref('');

// Modal
const showModal = ref(false);
const isEditing = ref(false);
const formData = reactive({
  id: null,
  employeeId: null,
  contractType: 'FULL_TIME',
  startDate: '',
  endDate: '',
  baseSalary: 0,
  status: 'ACTIVE',
  fileUrl: ''
});

// Fetch Data
const fetchData = async () => {
  loading.value = true;
  try {
    const [conRes, empRes] = await Promise.all([
      contractService.getContracts(),
      employeeService.getEmployees()
    ]);
    contracts.value = conRes.data || [];
    employees.value = empRes.data || [];
  } catch (err) {
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// Computed Helpers
const filteredContracts = computed(() => {
  if (!searchQuery.value) return contracts.value;
  const q = searchQuery.value.toLowerCase();
  return contracts.value.filter(c => {
    const emp = getEmployee(c.employeeId);
    const name = emp ? emp.name.toLowerCase() : '';
    return name.includes(q) || c.contractType.toLowerCase().includes(q);
  });
});

const totalSalary = computed(() => {
  return contracts.value
    .filter(c => c.status === 'ACTIVE')
    .reduce((sum, c) => sum + Number(c.baseSalary || 0), 0);
});

// Utils
const getEmployee = (id) => employees.value.find(e => e.id === id);

const formatCurrency = (val) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

const formatDate = (dateStr) => {
  if (!dateStr) return '--';
  return new Date(dateStr).toLocaleDateString('vi-VN');
};

const formatType = (type) => type.replace('_', ' ');

const getStatusClass = (status) => {
  const map = {
    'ACTIVE': 'st-active',
    'PENDING': 'st-pending',
    'EXPIRED': 'st-expired',
    'TERMINATED': 'st-terminated'
  };
  return map[status] || 'st-gray';
};

// Actions
const openAddModal = () => {
  isEditing.value = false;
  Object.assign(formData, { id: null, employeeId: null, contractType: 'FULL_TIME', startDate: '', endDate: '', baseSalary: 0, status: 'ACTIVE', fileUrl: '' });
  showModal.value = true;
};

const openEditModal = (c) => {
  isEditing.value = true;
  Object.assign(formData, { ...c });
  showModal.value = true;
};

const closeModal = () => showModal.value = false;

const handleSave = async () => {
  try {
    if (isEditing.value) {
      await contractService.updateContract(formData.id, formData);
      const idx = contracts.value.findIndex(c => c.id === formData.id);
      if (idx !== -1) contracts.value[idx] = { ...formData };
    } else {
      const res = await contractService.createContract(formData);
      const newC = res.data || { ...formData, id: Date.now() };
      contracts.value.unshift(newC);
    }
    closeModal();
  } catch (e) {
    alert("Error saving contract");
  }
};

const handleDelete = async (id) => {
  if (!confirm('Delete contract?')) return;
  try {
    await contractService.deleteContract(id);
    contracts.value = contracts.value.filter(c => c.id !== id);
  } catch (e) {
    alert("Error deleting");
  }
};

onMounted(() => fetchData());
</script>

<style scoped>
/* Common Page Styles */
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; color: #0b2433; }

/* Stats */
.stats-grid { display: flex; gap: 16px; margin-bottom: 32px; }
.glass-card { background: rgba(255, 255, 255, 0.92); border: 0.5px solid rgba(15, 118, 110, 0.12); box-shadow: 0 6px 18px rgba(10, 20, 36, 0.06); border-radius: 18px; padding: 20px 24px; min-width: 220px; }
.stat-label { font-size: 12px; font-weight: 700; text-transform: uppercase; color: #94a3b8; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: 700; color: #0b2433; }
.text-accent { color: #5fd1c5; }

/* Table Row */
.row-card {
  display: grid; align-items: center; background: #ffffff; border-radius: 12px;
  padding: 16px 20px; box-shadow: 0 2px 6px rgba(10, 20, 36, 0.04); transition: all 0.2s ease;
  border: 1px solid transparent; gap: 10px;
}
.row-card:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(10, 20, 36, 0.08); border-color: rgba(95, 209, 197, 0.3); }

/* File Link */
.file-link {
  display: flex; align-items: center; justify-content: center;
  width: 24px; height: 24px; background: #e0f2fe; color: #0284c7;
  border-radius: 6px; transition: background 0.2s;
}
.file-link:hover { background: #bae6fd; }

/* Status Badges */
.status-badge { padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
.st-active { background: #dcfce7; color: #16a34a; }
.st-pending { background: #fef9c3; color: #ca8a04; }
.st-expired { background: #f1f5f9; color: #64748b; }
.st-terminated { background: #fee2e2; color: #ef4444; }

/* Actions & Form */
.actions-group { display: flex; justify-content: flex-end; gap: 8px; }
.icon-btn {
  width: 32px; height: 32px; min-width: 32px; flex-shrink: 0;
  border-radius: 8px; border: none; background: transparent; color: #94a3b8;
  cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s;
}
.icon-btn.edit:hover { background: #e0f2fe; color: #0284c7; }
.icon-btn.delete:hover { background: #fee2e2; color: #ef4444; }
.text-right { text-align: right; }
.state-msg { text-align: center; padding: 40px; color: #94a3b8; font-style: italic; }

/* Modal Form Styles (Reused) */
.modal-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: flex; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; flex: 1; }
.form-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; }
.form-group input, .form-group select {
  padding: 10px 12px; border-radius: 10px; border: 1px solid #e2e8f0; width: 100%;
  font-size: 14px; color: #0f172a; transition: all 0.2s; background: #f8fafc;
}
.form-group input:focus, .form-group select:focus {
  background: #fff; border-color: #5fd1c5; outline: none; box-shadow: 0 0 0 3px rgba(95,209,197,0.15);
}
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
.btn-cancel { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: #64748b; background: transparent; cursor: pointer; border: 1px solid transparent;}
.btn-cancel:hover { background: #f1f5f9; }
.btn-submit { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: white; background: #5fd1c5; border: none; cursor: pointer; box-shadow: 0 4px 12px rgba(95, 209, 197, 0.3); }
.btn-submit:hover { background: #4bc2b6; }
</style>