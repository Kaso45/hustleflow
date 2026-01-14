<template>
  <!-- GIỮ NGUYÊN TOÀN BỘ PHẦN TEMPLATE CỦA BẠN -->
  <div class="page-container">
    <PeoplePageHeader 
      title="Departments"
      subtitle="Manage company departments structure"
      btnText="New Department"
      v-model="searchQuery"
      @add="openAddModal"
    />

    <div class="stats-grid">
      <div class="glass-card stat-card">
        <div class="stat-label">Total Departments</div>
        <div class="stat-value">{{ departments.length }}</div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">With Manager</div>
        <div class="stat-value">{{ departments.filter(d => d.managerId).length }}</div>
      </div>
    </div>

    <FloatingTable gridColumns="2fr 1fr 2fr 3fr 100px">
      <template #header>
        <div>Department Name</div>
        <div>Code</div>
        <div>Manager</div>
        <div>Description</div>
        <div class="text-right">Actions</div>
      </template>

      <div v-if="loading" class="state-msg">Loading departments...</div>
      <div v-else-if="filteredDepartments.length === 0" class="state-msg">No departments found.</div>

      <div 
        v-else
        v-for="dept in filteredDepartments" 
        :key="dept.id" 
        class="row-card"
        :style="{ gridTemplateColumns: '2fr 1fr 2fr 3fr 100px' }"
      >
        <div class="font-bold text-[#0b2433]">{{ dept.departmentName }}</div>
        <div><span class="code-badge">{{ dept.code }}</span></div>

        <div class="col-manager">
          <div v-if="getManager(dept.managerId)" class="flex items-center gap-3">
            <BaseAvatar :name="getManager(dept.managerId).name" :size="32" />
            <div class="text-sm font-medium text-gray-700 truncate">
              {{ getManager(dept.managerId).name }}
            </div>
          </div>
          <span v-else class="text-xs text-gray-400 italic">Unassigned</span>
        </div>

        <div class="text-sm text-gray-500 truncate pr-4" :title="dept.description">
          {{ dept.description || '--' }}
        </div>

        <div class="actions-group">
          <button class="icon-btn edit" @click.stop="openEditModal(dept)" title="Edit">
            <Pencil :size="16" />
          </button>
          <button class="icon-btn delete" @click.stop="handleDelete(dept.id)" title="Delete">
            <Trash2 :size="16" />
          </button>
        </div>
      </div>
    </FloatingTable>

    <BaseModal 
      :isOpen="showModal" 
      :title="isEditing ? 'Edit Department' : 'New Department'" 
      @close="closeModal"
    >
      <form @submit.prevent="handleSave" class="modal-form">
        <div class="form-row">
          <div class="form-group flex-[2]">
            <label>Department Name</label>
            <input v-model="formData.departmentName" type="text" placeholder="e.g. Human Resources" required />
          </div>
          <div class="form-group flex-[1]">
            <label>Code</label>
            <input v-model="formData.code" type="text" placeholder="e.g. HR01" required />
          </div>
        </div>

        <div class="form-group">
          <label>Manager (Required)</label>
          <select v-model="formData.managerId" required>
            <option :value="null" disabled>-- Select Manager --</option>
            <option v-for="emp in employees" :key="emp.id" :value="emp.id">
              {{ emp.name }} ({{ emp.empJobRole }})
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Description</label>
          <textarea v-model="formData.description" rows="3" placeholder="Enter department description..."></textarea>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeModal">Cancel</button>
          <button type="submit" class="btn-submit">{{ isEditing ? 'Update' : 'Create' }}</button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Pencil, Trash2 } from 'lucide-vue-next';
import departmentService from '@/services/departmentService';
import employeeService from '@/services/employeeService';
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import FloatingTable from '@/components/common/FloatingTable.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';

// State
const departments = ref([]);
const employees = ref([]);
const loading = ref(true);
const searchQuery = ref('');

// Modal State
const showModal = ref(false);
const isEditing = ref(false);
const formData = reactive({
  id: null,
  departmentName: '',
  code: '',
  description: '',
  managerId: null
});

// --- LOGIC FETCH DATA (Đã sửa để khớp với API thật) ---
const fetchData = async () => {
  loading.value = true;
  try {
    const [deptRes, empRes] = await Promise.all([
      departmentService.getDepartments(),
      employeeService.getEmployees()
    ]);
    
    const rawDepts = deptRes.data || deptRes || [];
    
    // XỬ LÝ QUAN TRỌNG: 
    // Nếu là API thật, BE trả về object { manager: { id: ... } }
    // Nếu là Mock, nó trả về managerId
    departments.value = rawDepts.map(dept => {
      let mId = dept.managerId; // Ưu tiên managerId (dành cho Mock)
      if (dept.manager && dept.manager.id) {
        mId = dept.manager.id; // Nếu có object manager (dành cho API thật)
      }
      return { ...dept, managerId: mId };
    });
    
    employees.value = empRes.data || empRes || [];
  } catch (error) {
    console.error("Failed to fetch data", error);
  } finally {
    loading.value = false;
  }
};

// --- HELPERS ---
const getManager = (managerId) => {
  if (!managerId) return null;
  return employees.value.find(e => e.id === managerId);
};

const filteredDepartments = computed(() => {
  if (!searchQuery.value) return departments.value;
  const query = searchQuery.value.toLowerCase();
  return departments.value.filter(d => 
    d.departmentName.toLowerCase().includes(query) || 
    d.code.toLowerCase().includes(query)
  );
});

// --- ACTIONS ---
const openAddModal = () => {
  isEditing.value = false;
  Object.assign(formData, { id: null, departmentName: '', code: '', description: '', managerId: null });
  showModal.value = true;
};

const openEditModal = (dept) => {
  isEditing.value = true;
  // Chỉ lấy các trường cần thiết để đưa vào form
  Object.assign(formData, {
    id: dept.id,
    departmentName: dept.departmentName,
    code: dept.code,
    description: dept.description,
    managerId: dept.managerId
  });
  showModal.value = true;
};

const closeModal = () => showModal.value = false;

// --- LOGIC SAVE (Đã sửa để tránh lỗi 500) ---
const handleSave = async () => {
  // 1. Kiểm tra: BE bắt buộc phải có managerId
  if (!formData.managerId) {
    alert("Please select a Manager. This is required by the backend.");
    return;
  }

  // 2. Tạo Payload sạch (Chỉ gửi 4 trường BE DTO yêu cầu)
  const payload = {
    departmentName: formData.departmentName,
    code: formData.code,
    description: formData.description,
    managerId: Number(formData.managerId)
  };

  try {
    if (isEditing.value) {
      await departmentService.updateDepartment(formData.id, payload);
    } else {
      await departmentService.createDepartment(payload);
    }
    
    // 3. Sau khi save, fetch lại để cập nhật danh sách chuẩn từ DB
    await fetchData(); 
    closeModal();
  } catch (error) {
    console.error("Save failed", error);
    // Hiển thị lỗi từ server trả về (nếu có)
    const errorMsg = error.response?.data || "Check if Name/Code is unique.";
    alert("Error: " + errorMsg);
  }
};

const handleDelete = async (id) => {
  if (!confirm('Delete this department?')) return;
  try {
    await departmentService.deleteDepartment(id);
    await fetchData();
  } catch (error) {
    console.error("Delete failed", error);
    alert(error.response?.data || "Cannot delete. Ensure no manager is linked to this department.");
  }
};

onMounted(() => {
  fetchData();
});
</script>
<style scoped>
/* GIỮ NGUYÊN TOÀN BỘ CSS CỦA BẠN */
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; color: #0b2433; }
.stats-grid { display: flex; gap: 16px; margin-bottom: 32px; }
.glass-card { background: rgba(255, 255, 255, 0.92); border: 0.5px solid rgba(15, 118, 110, 0.12); box-shadow: 0 6px 18px rgba(10, 20, 36, 0.06); border-radius: 18px; padding: 20px 24px; min-width: 200px; }
.stat-label { font-size: 12px; font-weight: 700; text-transform: uppercase; color: #94a3b8; margin-bottom: 8px; }
.stat-value { font-size: 32px; font-weight: 700; color: #0b2433; }
.row-card { display: grid; align-items: center; background: #ffffff; border-radius: 12px; padding: 16px 20px; box-shadow: 0 2px 6px rgba(10, 20, 36, 0.04); transition: all 0.2s ease; border: 1px solid transparent; gap: 10px; }
.row-card:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(10, 20, 36, 0.08); border-color: rgba(95, 209, 197, 0.3); }
.code-badge { background: #f1f5f9; color: #475569; padding: 4px 8px; border-radius: 6px; font-family: monospace; font-size: 12px; font-weight: 600; border: 1px solid #e2e8f0; }
.col-manager { display: flex; align-items: center; }
.actions-group { display: flex; justify-content: flex-end; gap: 8px; }
.icon-btn { width: 32px; height: 32px; min-width: 32px; flex-shrink: 0; border-radius: 8px; border: none; background: transparent; color: #94a3b8; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s; }
.icon-btn.edit:hover { background: #e0f2fe; color: #0284c7; }
.icon-btn.delete:hover { background: #fee2e2; color: #ef4444; }
.text-right { text-align: right; }
.state-msg { text-align: center; padding: 40px; color: #94a3b8; font-style: italic; }
.modal-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: flex; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; flex: 1; }
.form-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; }
.form-group input, .form-group select, .form-group textarea { padding: 10px 12px; border-radius: 10px; border: 1px solid #e2e8f0; font-size: 14px; color: #0f172a; transition: all 0.2s; background: #f8fafc; width: 100%; }
.form-group input:focus, .form-group select:focus, .form-group textarea:focus { background: #fff; border-color: #5fd1c5; outline: none; box-shadow: 0 0 0 3px rgba(95,209,197,0.15); }
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
.btn-cancel { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: #64748b; background: transparent; cursor: pointer; border: 1px solid transparent;}
.btn-cancel:hover { background: #f1f5f9; }
.btn-submit { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: white; background: #5fd1c5; border: none; cursor: pointer; box-shadow: 0 4px 12px rgba(95, 209, 197, 0.3); }
.btn-submit:hover { background: #4bc2b6; }
</style>