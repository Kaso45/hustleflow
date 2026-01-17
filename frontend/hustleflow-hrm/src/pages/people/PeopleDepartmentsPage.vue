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
        <div class="stat-value">
          {{ departments.filter((d) => d.managerId).length }}
        </div>
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
      <div v-else-if="filteredDepartments.length === 0" class="state-msg">
        No departments found.
      </div>

      <div
        v-else
        v-for="dept in filteredDepartments"
        :key="dept.id"
        class="row-card"
        :style="{ gridTemplateColumns: '2fr 1fr 2fr 3fr 100px' }"
      >
        <div class="info-stack">
          <span class="info-label">Department</span>
          <span class="info-card-name">{{ dept.departmentName }}</span>
          <span class="info-card-meta">ID: #{{ dept.id }}</span>
        </div>

        <div class="info-pill-stack">
          <span class="info-pill code-pill">{{ dept.code }}</span>
        </div>

        <div class="col-info manager-cell">
          <template v-if="getManager(dept.managerId)">
            <BaseAvatar :name="getManager(dept.managerId).name" :size="32" />
            <div class="info-text">
              <span class="info-card-name">{{
                getManager(dept.managerId).name
              }}</span>
              <span class="info-card-meta">Manager</span>
            </div>
          </template>
          <span v-else class="info-card-meta italic text-gray-400"
            >Unassigned</span
          >
        </div>

        <div class="info-stack description-cell" :title="dept.description">
          <span class="info-label">Description</span>
          <span class="info-value truncate">{{
            dept.description || "--"
          }}</span>
        </div>

        <div class="actions-group">
          <button
            class="icon-btn edit"
            @click.stop="openEditModal(dept)"
            title="Edit"
          >
            <Pencil :size="16" />
          </button>
          <button
            class="icon-btn delete"
            @click.stop="handleDelete(dept.id)"
            title="Delete"
          >
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
            <input
              v-model="formData.departmentName"
              type="text"
              placeholder="e.g. Human Resources"
              required
            />
          </div>
          <div class="form-group flex-[1]">
            <label>Code</label>
            <input
              v-model="formData.code"
              type="text"
              placeholder="e.g. HR01"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label>Manager</label>
          <select v-model="formData.managerId">
            <option :value="null">-- No Manager --</option>
            <option v-for="emp in employees" :key="emp.id" :value="emp.id">
              {{ emp.name }} ({{ emp.empJobRole }})
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Description</label>
          <textarea
            v-model="formData.description"
            rows="3"
            placeholder="Enter department description..."
          ></textarea>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeModal">
            Cancel
          </button>
          <button type="submit" class="btn-submit">
            {{ isEditing ? "Update" : "Create" }}
          </button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { Pencil, Trash2 } from "lucide-vue-next";
import departmentService from "@/services/departmentService";
import employeeService from "@/services/employeeService";
import PeoplePageHeader from "@/components/common/PeoplePageHeader.vue";
import FloatingTable from "@/components/common/FloatingTable.vue";
import BaseModal from "@/components/common/BaseModal.vue";
import BaseAvatar from "@/components/common/BaseAvatar.vue";

// State
const departments = ref([]);
const employees = ref([]);
const loading = ref(true);
const searchQuery = ref("");

// Modal State
const showModal = ref(false);
const isEditing = ref(false);
const formData = reactive({
  id: null,
  departmentName: "",
  code: "",
  description: "",
  managerId: null,
});

// --- LOGIC FETCH DATA (Đã sửa để khớp với API thật) ---
const fetchData = async () => {
  loading.value = true;
  try {
    const [deptRes, empRes] = await Promise.all([
      departmentService.getDepartments(),
      employeeService.getEmployees(),
    ]);

    const rawDepts = deptRes.data || deptRes || [];

    // XỬ LÝ QUAN TRỌNG:
    // Nếu là API thật, BE trả về object { manager: { id: ... } }
    // Nếu là Mock, nó trả về managerId
    departments.value = rawDepts.map((dept) => {
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
  return employees.value.find((e) => e.id === managerId);
};

const filteredDepartments = computed(() => {
  if (!searchQuery.value) return departments.value;
  const query = searchQuery.value.toLowerCase();
  return departments.value.filter(
    (d) =>
      d.departmentName.toLowerCase().includes(query) ||
      d.code.toLowerCase().includes(query)
  );
});

// --- ACTIONS ---
const openAddModal = () => {
  isEditing.value = false;
  Object.assign(formData, {
    id: null,
    departmentName: "",
    code: "",
    description: "",
    managerId: null,
  });
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
    managerId: dept.managerId,
  });
  showModal.value = true;
};

const closeModal = () => (showModal.value = false);

// --- LOGIC SAVE (Đã sửa để tránh lỗi 500) ---
const handleSave = async () => {
  // Tạo Payload sạch (Chỉ gửi 4 trường BE DTO yêu cầu)
  const payload = {
    departmentName: formData.departmentName,
    code: formData.code,
    description: formData.description,
    managerId: formData.managerId ? Number(formData.managerId) : null,
  };

  try {
    if (isEditing.value) {
      await departmentService.updateDepartment(formData.id, payload);
    } else {
      await departmentService.createDepartment(payload);
    }

    // Sau khi save, fetch lại để cập nhật danh sách chuẩn từ DB
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
  if (!confirm("Delete this department?")) return;
  try {
    await departmentService.deleteDepartment(id);
    await fetchData();
  } catch (error) {
    console.error("Delete failed", error);
    alert(
      error.response?.data ||
        "Cannot delete. Ensure no manager is linked to this department."
    );
  }
};

onMounted(() => {
  fetchData();
});
</script>
<style scoped>
.code-pill {
  font-family: "Space Mono", "Roboto Mono", monospace;
  letter-spacing: 0.08em;
}

.manager-cell {
  min-width: 0;
}

.description-cell {
  min-width: 0;
}

.description-cell .truncate {
  display: block;
}
</style>
