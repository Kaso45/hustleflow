<template>
  <div class="page-container">
    <!-- Header: GIỮ NGUYÊN -->
    <PeoplePageHeader
      title="Contracts"
      subtitle="Manage employee contracts & agreements"
      btnText="New Contract"
      v-model="searchQuery"
      @add="openAddModal"
    />

    <!-- Stats: GIỮ NGUYÊN -->
    <div class="stats-grid">
      <div class="glass-card stat-card">
        <div class="stat-label">Active Contracts</div>
        <div class="stat-value">
          {{ contracts.filter((c) => c.status === "ACTIVE").length }}
        </div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">Total Salary Fund</div>
        <div class="stat-value text-accent">
          {{ formatCurrency(totalSalary) }}
        </div>
      </div>
    </div>

    <!-- Table: GIỮ NGUYÊN DESIGN -->
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
      <div v-else-if="filteredContracts.length === 0" class="state-msg">
        No contracts found.
      </div>

      <div
        v-else
        v-for="contract in filteredContracts"
        :key="contract.id"
        class="row-card"
        :style="{ gridTemplateColumns: '2.5fr 1.5fr 2fr 1.5fr 1fr 100px' }"
      >
        <div class="col-info">
          <template v-if="getEmployee(contract.employeeId)">
            <BaseAvatar
              :name="getEmployee(contract.employeeId).name"
              :size="36"
            />
            <div class="info-text">
              <div class="info-card-name">
                {{ getEmployee(contract.employeeId).name }}
              </div>
              <div class="info-card-meta">ID: #{{ contract.employeeId }}</div>
            </div>
          </template>
          <template v-else>
            <div class="info-text">
              <div class="info-card-name">Unknown Employee</div>
              <div class="info-card-meta">ID: #{{ contract.employeeId }}</div>
            </div>
          </template>
        </div>

        <div class="info-stack">
          <span class="info-label">Contract</span>
          <div class="contract-type-stack">
            <span class="info-pill">{{
              formatType(contract.contractType)
            }}</span>
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
        </div>

        <div class="info-stack">
          <span class="info-label">Duration</span>
          <span class="info-value">{{ formatDate(contract.startDate) }}</span>
          <span class="info-card-meta"
            >to {{ formatDate(contract.endDate) }}</span
          >
        </div>

        <div class="info-stack text-right">
          <span class="info-label">Base Salary</span>
          <span class="info-value">{{
            formatCurrency(contract.baseSalary)
          }}</span>
        </div>

        <div>
          <span class="status-badge" :class="getStatusClass(contract.status)">
            {{ contract.status }}
          </span>
        </div>

        <div class="actions-group">
          <button class="icon-btn edit" @click.stop="openEditModal(contract)">
            <Pencil :size="16" />
          </button>
          <button
            class="icon-btn delete"
            @click.stop="handleDelete(contract.id)"
          >
            <Trash2 :size="16" />
          </button>
        </div>
      </div>
    </FloatingTable>

    <!-- Modal Form: LOGIC UPDATE -->
    <BaseModal
      :isOpen="showModal"
      :title="isEditing ? 'Edit Contract' : 'New Contract'"
      @close="closeModal"
    >
      <form @submit.prevent="handleSave" class="modal-form">
        <div class="form-group">
          <label>Employee</label>
          <!-- Disable employee selection on Edit because backend doesn't support changing owner of a contract -->
          <select v-model="formData.employeeId" required :disabled="isEditing">
            <option :value="null">-- Select Employee --</option>
            <option v-for="emp in employees" :key="emp.id" :value="emp.id">
              {{ emp.name }} - {{ emp.empJobRole }}
            </option>
          </select>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Type</label>
            <!-- Disable Type on Edit to match Backend Logic -->
            <select v-model="formData.contractType" :disabled="isEditing">
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

        <div class="form-row">
          <div class="form-group">
            <label>Start Date</label>
            <!-- Disable Start Date on Edit to match Backend Logic -->
            <input
              v-model="formData.startDate"
              type="date"
              required
              :disabled="isEditing"
            />
          </div>
          <div class="form-group">
            <label>End Date</label>
            <input v-model="formData.endDate" type="date" required />
          </div>
        </div>

        <div class="form-group">
          <label>Base Salary (VND)</label>
          <input
            v-model.number="formData.baseSalary"
            type="number"
            min="0"
            step="100000"
            placeholder="e.g. 20000000"
          />
        </div>

        <div class="form-group">
          <label>File URL (Link)</label>
          <input
            v-model="formData.fileUrl"
            type="text"
            placeholder="/docs/contracts/..."
          />
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeModal">
            Cancel
          </button>
          <button type="submit" class="btn-submit">
            {{ isEditing ? "Update Contract" : "Create Contract" }}
          </button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { Pencil, Trash2, FileText } from "lucide-vue-next";
import contractService from "@/services/contractService";
import employeeService from "@/services/employeeService";
import PeoplePageHeader from "@/components/common/PeoplePageHeader.vue";
import FloatingTable from "@/components/common/FloatingTable.vue";
import BaseModal from "@/components/common/BaseModal.vue";
import BaseAvatar from "@/components/common/BaseAvatar.vue";

const contracts = ref([]);
const employees = ref([]);
const loading = ref(true);
const searchQuery = ref("");
const showModal = ref(false);
const isEditing = ref(false);

const formData = reactive({
  id: null,
  employeeId: null,
  contractType: "FULL_TIME",
  startDate: "",
  endDate: "",
  baseSalary: 0,
  status: "ACTIVE",
  fileUrl: "",
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [conRes, empRes] = await Promise.all([
      contractService.getContracts(),
      employeeService.getEmployees(),
    ]);
    contracts.value = conRes.data || [];
    employees.value = empRes.data || [];
  } catch (err) {
    console.error("Fetch Error:", err);
  } finally {
    loading.value = false;
  }
};

const filteredContracts = computed(() => {
  if (!searchQuery.value) return contracts.value;
  const q = searchQuery.value.toLowerCase();
  return contracts.value.filter((c) => {
    const emp = getEmployee(c.employeeId);
    const name = emp ? emp.name.toLowerCase() : "";
    const type = c.contractType ? c.contractType.toLowerCase() : "";
    return name.includes(q) || type.includes(q);
  });
});

const totalSalary = computed(() => {
  return contracts.value
    .filter((c) => c.status === "ACTIVE")
    .reduce((sum, c) => sum + Number(c.baseSalary || 0), 0);
});

const getEmployee = (id) => employees.value.find((e) => e.id === id);
const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    val
  );
const formatDate = (dateStr) =>
  dateStr ? new Date(dateStr).toLocaleDateString("vi-VN") : "--";
const formatType = (type) => (type ? type.replace("_", " ") : "");

const getStatusClass = (status) => {
  const map = {
    ACTIVE: "st-active",
    PENDING: "st-pending",
    EXPIRED: "st-expired",
    TERMINATED: "st-terminated",
  };
  return map[status] || "st-gray";
};

const openAddModal = () => {
  isEditing.value = false;
  Object.assign(formData, {
    id: null,
    employeeId: null,
    contractType: "FULL_TIME",
    startDate: "",
    endDate: "",
    baseSalary: 0,
    status: "ACTIVE",
    fileUrl: "",
  });
  showModal.value = true;
};

const openEditModal = (c) => {
  isEditing.value = true;
  Object.assign(formData, { ...c });
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const handleSave = async () => {
  // Logic Validation: Check Dates
  if (
    formData.endDate &&
    new Date(formData.endDate) < new Date(formData.startDate)
  ) {
    alert("Error: End Date cannot be earlier than Start Date");
    return;
  }

  try {
    if (isEditing.value) {
      const res = await contractService.updateContract(formData.id, formData);
      const idx = contracts.value.findIndex((c) => c.id === formData.id);
      if (idx !== -1 && res.data) {
        // Cập nhật bằng dữ liệu trả về từ server để đồng bộ tuyệt đối
        contracts.value[idx] = res.data;
      }
    } else {
      const res = await contractService.createContract(formData);
      if (res.data) {
        contracts.value.unshift(res.data);
      }
    }
    closeModal();
  } catch (e) {
    console.error(e);
    alert(
      e.response?.data?.message || "Error saving contract. Please try again."
    );
  }
};

const handleDelete = async (id) => {
  if (!confirm("Are you sure you want to delete this contract?")) return;
  try {
    await contractService.deleteContract(id);
    contracts.value = contracts.value.filter((c) => c.id !== id);
  } catch (e) {
    alert("Error deleting contract.");
  }
};

onMounted(() => fetchData());
</script>

<style scoped>
.text-accent {
  color: #5fd1c5;
}
.contract-type-stack {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.file-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: #e0f2fe;
  color: #0284c7;
  border-radius: 6px;
  transition: background 0.2s;
}
.file-link:hover {
  background: #bae6fd;
}
.status-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}
.st-active {
  background: #dcfce7;
  color: #16a34a;
}
.st-pending {
  background: #fef9c3;
  color: #ca8a04;
}
.st-expired {
  background: #f1f5f9;
  color: #64748b;
}
.st-terminated {
  background: #fee2e2;
  color: #ef4444;
}
/* Style cho các input bị disabled để user biết là không sửa được */
.form-group input:disabled,
.form-group select:disabled {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}
</style>
