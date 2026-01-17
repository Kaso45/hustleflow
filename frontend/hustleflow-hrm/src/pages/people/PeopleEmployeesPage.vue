<!-- A:\Software Design Project Newest\hustleflow\frontend\hustleflow-hrm\src\pages\people\PeopleEmployeesPage.vue -->
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
    </div>

    <!-- Filter Pills -->
    <div class="filter-section">
      <button
        class="filter-pill"
        :class="{ active: !selectedDept }"
        @click="selectedDept = null"
      >
        All
      </button>
      <button
        v-for="dept in departments"
        :key="dept.id"
        class="filter-pill"
        :class="{ active: selectedDept === dept.departmentName }"
        @click="selectedDept = dept.departmentName"
      >
        {{ dept.departmentName }}
      </button>
    </div>

    <!-- Table -->
    <FloatingTable gridColumns="3fr 1.5fr 1.5fr 1.2fr 1fr 100px">
      <template #header>
        <div>Employee Info</div>
        <div>Department</div>
        <div>Job Role</div>
        <div>Rating</div>
        <div>Overtime</div>
        <div class="text-right">Actions</div>
      </template>

      <div v-if="loading" class="state-msg">Loading employees...</div>
      <div v-else-if="filteredEmployees.length === 0" class="state-msg">
        No employees found.
      </div>

      <div
        v-else
        v-for="emp in filteredEmployees"
        :key="emp.id"
        class="row-card is-clickable"
        :style="{ gridTemplateColumns: '3fr 1.5fr 1.5fr 1.2fr 1fr 100px' }"
      >
        <!-- Columns -->
        <div class="col-info">
          <BaseAvatar :name="emp.name" :size="40" />
          <div class="info-text">
            <div class="info-card-name">{{ emp.name }}</div>
            <div class="info-card-meta">{{ generateEmail(emp.name) }}</div>
          </div>
        </div>

        <div class="info-pill-stack">
          <span
            v-for="(token, index) in tokenizeDepartment(emp.empDepartment)"
            :key="index"
            class="info-pill is-muted"
          >
            {{ token }}
          </span>
        </div>

        <div class="info-stack">
          <span class="info-label">Role</span>
          <span class="info-value">{{ emp.empJobRole }}</span>
          <span class="info-card-meta">Level {{ emp.empJobLevel }}</span>
        </div>

        <div class="info-stack">
          <span class="info-label">Rating</span>
          <span
            class="info-value"
            :class="ratingToneClass(emp.performanceRating)"
          >
            {{ ratingLabel(emp.performanceRating) }}
          </span>
        </div>

        <div class="info-stack">
          <span class="info-label">Overtime</span>
          <span class="info-value" :class="{ 'info-accent': emp.overTime }">
            {{ emp.overTime ? "Yes" : "No" }}
          </span>
        </div>

        <!-- ACTIONS: Dùng Lucide Icons -->
        <div class="actions-group">
          <button
            class="icon-btn edit"
            @click.stop="openEditModal(emp)"
            title="Edit"
          >
            <Pencil :size="16" />
          </button>
          <button
            class="icon-btn delete"
            @click.stop="handleDelete(emp.id)"
            title="Delete"
          >
            <Trash2 :size="16" />
          </button>
        </div>
      </div>
    </FloatingTable>

    <BaseModal
      :isOpen="showModal"
      :title="isEditing ? 'Edit Employee' : 'New Employee'"
      @close="closeModal"
    >
      <form @submit.prevent="handleSave" class="modal-form">
        <div class="form-group">
          <label>Full Name</label>
          <input
            v-model="formData.name"
            type="text"
            placeholder="e.g. Nguyen Van A"
            required
          />
        </div>

        <!-- Mở rộng: Thêm dòng Tuổi và Giới tính để tránh lỗi DB -->
        <div class="form-row">
          <div class="form-group">
            <label>Age</label>
            <input v-model.number="formData.age" type="number" required />
          </div>
          <div class="form-group">
            <label>Gender</label>
            <select v-model="formData.gender" required>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Department</label>
            <select v-model="formData.empDepartmentId" required>
              <option :value="''">Select Dept</option>
              <option
                v-for="dept in departments"
                :key="dept.id"
                :value="dept.id"
              >
                {{ dept.departmentName }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Job Role</label>
            <input v-model="formData.empJobRole" type="text" required />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Level (1-5)</label>
            <input
              v-model.number="formData.empJobLevel"
              type="number"
              min="1"
              max="5"
            />
          </div>
        </div>

        <!-- Additional fields to align with backend schema -->
        <div class="form-row">
          <div class="form-group">
            <label>Education Background</label>
            <input v-model="formData.educationBackground" type="text" />
          </div>
          <div class="form-group">
            <label>Overtime</label>
            <select v-model="formData.overTime">
              <option :value="false">No</option>
              <option :value="true">Yes</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Num Companies Worked</label>
            <input v-model.number="formData.numCompaniesWorked" type="number" />
          </div>
          <div class="form-group">
            <label>Job Involvement (1-5)</label>
            <input
              v-model.number="formData.empJobInvolvement"
              type="number"
              min="1"
              max="5"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Hourly Rate</label>
            <input
              v-model.number="formData.empHourlyRate"
              type="number"
              step="0.1"
            />
          </div>
          <div class="form-group">
            <label>Job Satisfaction (1-5)</label>
            <input
              v-model.number="formData.empJobSatisfaction"
              type="number"
              min="1"
              max="5"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Environment Satisfaction (1-5)</label>
            <input
              v-model.number="formData.empEnvironmentSatisfaction"
              type="number"
              min="1"
              max="5"
            />
          </div>
          <div class="form-group">
            <label>Marital Status</label>
            <select v-model="formData.maritalStatus">
              <option :value="''">Select</option>
              <option value="SINGLE">Single</option>
              <option value="MARRIED">Married</option>
              <option value="DIVORCED">Divorced</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Business Travel Frequency</label>
            <select v-model="formData.businessTravelFrequency">
              <option :value="''">Select</option>
              <option value="NONE_TRAVEL">None</option>
              <option value="TRAVEL_RARELY">Rarely</option>
              <option value="TRAVEL_FREQUENTLY">Frequently</option>
            </select>
          </div>
          <div class="form-group">
            <label>Distance From Home</label>
            <input v-model.number="formData.distanceFromHome" type="number" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Education Level (1-5)</label>
            <input
              v-model.number="formData.empEducationLevel"
              type="number"
              min="1"
              max="5"
            />
          </div>
          <div class="form-group">
            <label>Last Salary Hike %</label>
            <input
              v-model.number="formData.empLastSalaryHikePercent"
              type="number"
              min="0"
              max="100"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Relationship Satisfaction (1-5)</label>
            <input
              v-model.number="formData.empRelationshipSatisfaction"
              type="number"
              min="1"
              max="5"
            />
          </div>
          <div class="form-group">
            <label>Total Work Experience (years)</label>
            <input
              v-model.number="formData.totalWorkExperienceInYears"
              type="number"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Training Times Last Year</label>
            <input
              v-model.number="formData.trainingTimesLastYear"
              type="number"
            />
          </div>
          <div class="form-group">
            <label>Work-Life Balance (1-5)</label>
            <input
              v-model.number="formData.empWorkLifeBalance"
              type="number"
              min="1"
              max="5"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Years at Company</label>
            <input
              v-model.number="formData.experienceYearsAtThisCompany"
              type="number"
            />
          </div>
          <div class="form-group">
            <label>Years in Current Role</label>
            <input
              v-model.number="formData.experienceYearsInCurrentRole"
              type="number"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Years Since Last Promotion</label>
            <input
              v-model.number="formData.yearsSinceLastPromotion"
              type="number"
            />
          </div>
          <div class="form-group">
            <label>Years With Current Manager</label>
            <input
              v-model.number="formData.yearsWithCurrManager"
              type="number"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Attrition</label>
            <select v-model="formData.attrition">
              <option :value="false">No</option>
              <option :value="true">Yes</option>
            </select>
          </div>
          <div class="form-group">
            <label>Performance Rating</label>
            <select v-model="formData.performanceRating">
              <option :value="''">Select</option>
              <option :value="0">Low</option>
              <option :value="1">Medium</option>
              <option :value="2">High</option>
            </select>
          </div>
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
import { ref, reactive, computed, onMounted, watch } from "vue";
import { Pencil, Trash2 } from "lucide-vue-next";
import dashboardService from "@/services/dashboard/dashboardService";
import employeeService from "@/services/employeeService";
import BaseAvatar from "@/components/common/BaseAvatar.vue";
import PeoplePageHeader from "@/components/common/PeoplePageHeader.vue";
import FloatingTable from "@/components/common/FloatingTable.vue";
import BaseModal from "@/components/common/BaseModal.vue";
import { toEmployeeApiPayload } from "@/models/employeePayload";

// State
const employees = ref([]);
const departments = ref([]);
const loading = ref(true);
const searchQuery = ref("");
const selectedDept = ref(null);

// Modal & Form State
const showModal = ref(false);
const isEditing = ref(false);
const formData = reactive({
  id: null,
  name: "",
  age: 0,
  gender: "",
  empDepartmentId: null,
  empDepartment: "",
  empJobRole: "",
  empJobLevel: 1,
  educationBackground: "",
  overTime: false,
  numCompaniesWorked: 0,
  empJobInvolvement: 1,
  empHourlyRate: 0,
  empJobSatisfaction: 1,
  empEnvironmentSatisfaction: 1,
  maritalStatus: "",
  businessTravelFrequency: "",
  distanceFromHome: 0,
  empEducationLevel: 1,
  empLastSalaryHikePercent: 0,
  empRelationshipSatisfaction: 1,
  totalWorkExperienceInYears: 0,
  trainingTimesLastYear: 0,
  empWorkLifeBalance: 1,
  experienceYearsAtThisCompany: 0,
  experienceYearsInCurrentRole: 0,
  yearsSinceLastPromotion: 0,
  yearsWithCurrManager: 0,
  attrition: false,
  performanceRating: "",
});

// --- MODAL ACTIONS ---

const openAddModal = () => {
  isEditing.value = false;
  // Reset Form
  Object.assign(formData, {
    id: null,
    name: "",
    age: 0,
    gender: "",
    empDepartmentId: null,
    empDepartment: "",
    empJobRole: "",
    empJobLevel: 1,
    educationBackground: "",
    overTime: false,
    numCompaniesWorked: 0,
    empJobInvolvement: 1,
    empHourlyRate: 0,
    empJobSatisfaction: 1,
    empEnvironmentSatisfaction: 1,
    maritalStatus: "",
    businessTravelFrequency: "",
    distanceFromHome: 0,
    empEducationLevel: 1,
    empLastSalaryHikePercent: 0,
    empRelationshipSatisfaction: 1,
    totalWorkExperienceInYears: 0,
    trainingTimesLastYear: 0,
    empWorkLifeBalance: 1,
    experienceYearsAtThisCompany: 0,
    experienceYearsInCurrentRole: 0,
    yearsSinceLastPromotion: 0,
    yearsWithCurrManager: 0,
    attrition: false,
    performanceRating: "",
  });
  showModal.value = true;
};

const openEditModal = (emp) => {
  isEditing.value = true;
  Object.assign(formData, { ...emp });
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const handleSave = async () => {
  try {
    const payload = toEmployeeApiPayload(formData);
    if (isEditing.value) {
      // UPDATE Logic
      await employeeService.updateEmployee(formData.id, payload);
      const index = employees.value.findIndex((e) => e.id === formData.id);
      if (index !== -1) employees.value[index] = { ...formData };
      alert("Updated successfully!");
    } else {
      // CREATE Logic
      const res = await employeeService.createEmployee(payload);
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
  if (!confirm("Are you sure you want to delete this employee?")) return;
  try {
    await employeeService.deleteEmployee(id);
    employees.value = employees.value.filter((e) => e.id !== id);
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
      dashboardService.getDepartments(),
    ]);
    employees.value = empRes || [];
    departments.value = deptRes || [];
  } catch (error) {
    console.error("Failed to fetch data", error);
  } finally {
    loading.value = false;
  }
};

// Keep department name in sync when department id changes
watch(
  () => formData.empDepartmentId,
  (newId) => {
    const dept = departments.value.find((d) => String(d.id) === String(newId));
    formData.empDepartment = dept ? dept.departmentName : "";
  }
);

// --- HELPERS ---
const filteredEmployees = computed(() => {
  let result = employees.value;
  if (selectedDept.value)
    result = result.filter((e) => e.empDepartment === selectedDept.value);
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (e) =>
        e.name?.toLowerCase().includes(query) ||
        e.empJobRole?.toLowerCase().includes(query)
    );
  }
  return result;
});

const generateEmail = (name) =>
  name ? `${name.toLowerCase().replace(/\s/g, ".")}@hustleflow.com` : "";

const tokenizeDepartment = (deptName) =>
  (deptName || "").split(/\s+/).filter(Boolean);

const ratingLabel = (val) => {
  if (val === null || val === undefined || val === "") return "—";
  const n = Number(val);
  switch (n) {
    case 0:
      return "Low";
    case 1:
      return "Medium";
    case 2:
      return "High";
    default:
      return String(val);
  }
};

const ratingToneClass = (val) => {
  const label = ratingLabel(val);
  if (label === "High") return "rating-high";
  if (label === "Medium") return "rating-medium";
  if (label === "Low") return "rating-low";
  return "rating-neutral";
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.rating-high {
  color: #0f766e;
}

.rating-medium {
  color: #eab308;
}

.rating-low {
  color: #e11d48;
}

.rating-neutral {
  color: #94a3b8;
}

/* Actions */
.modal-form {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 8px; /* space for scrollbar */
}
.btn-submit:hover {
  background: #4bc2b6;
}
</style>
