<template>
  <div class="page-container">
    <!-- Header -->
    <PeoplePageHeader 
      title="Leave Requests"
      subtitle="Manage employee time-off requests"
      btnText="New Request"
      v-model="searchQuery"
      @add="openAddModal"
    />

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="glass-card stat-card">
        <div class="stat-label">Pending Requests</div>
        <div class="stat-value text-yellow-500">{{ leaves.filter(l => l.status === 'PENDING').length }}</div>
      </div>
      <div class="glass-card stat-card">
        <div class="stat-label">Approved This Month</div>
        <div class="stat-value text-green-500">{{ leaves.filter(l => l.status === 'APPROVED').length }}</div>
      </div>
    </div>

    <!-- Filter Pills -->
    <div class="filter-section">
      <button class="filter-pill" :class="{ active: filterStatus === 'ALL' }" @click="filterStatus = 'ALL'">All</button>
      <button class="filter-pill" :class="{ active: filterStatus === 'PENDING' }" @click="filterStatus = 'PENDING'">Pending</button>
      <button class="filter-pill" :class="{ active: filterStatus === 'APPROVED' }" @click="filterStatus = 'APPROVED'">Approved</button>
      <button class="filter-pill" :class="{ active: filterStatus === 'REJECTED' }" @click="filterStatus = 'REJECTED'">Rejected</button>
    </div>

    <!-- Table Wrapper (Scroll ngang) -->
    <div class="table-scroll-wrapper">
      <!-- 
         FIX ALIGNMENT HEADER & BODY:
         - Thêm class 'fixed-min-width' vào FloatingTable.
         - Class này sẽ ép cả Header và Body phải rộng tối thiểu 900px.
         - Như vậy khi scroll, cả tiêu đề và nội dung sẽ chạy cùng nhau.
      -->
      <FloatingTable 
        :gridColumns="gridLayout" 
        class="fixed-min-width"
      >
        <template #header>
          <div>Employee</div>
          <div>Type</div>
          <div>Duration</div>
          <div>Reason</div>
          <div>Status</div>
          <div class="text-right">Actions</div>
        </template>

        <div v-if="loading" class="state-msg">Loading requests...</div>
        <div v-else-if="filteredLeaves.length === 0" class="state-msg">No requests found.</div>

        <div 
          v-else
          v-for="req in filteredLeaves" 
          :key="req.id" 
          class="row-card"
          :style="{ gridTemplateColumns: gridLayout }"
        >
          <!-- Col 1: Employee Info -->
          <div class="col-info">
            <BaseAvatar 
              v-if="getEmployee(req.employeeId)" 
              :name="getEmployee(req.employeeId).name" 
              :size="38" 
              class="flex-shrink-0"
            />
            <div v-else class="w-[38px] h-[38px] bg-gray-100 rounded-full flex-shrink-0"></div>

            <div class="info-text">
              <div class="emp-name" v-if="getEmployee(req.employeeId)" :title="getEmployee(req.employeeId).name">
                {{ getEmployee(req.employeeId).name }}
              </div>
              <div class="emp-name text-gray-400" v-else>Unknown</div>
              
              <div class="emp-meta">
                {{ getEmployee(req.employeeId)?.empDepartment || '--' }}
                <span class="mx-1">•</span>
                #{{ req.employeeId }}
              </div>
            </div>
          </div>

          <!-- Col 2: Type -->
          <div>
            <span class="type-badge">{{ humanizeLeaveType(req.leaveType) }}</span>
          </div>

          <!-- Col 3: Duration -->
          <div class="duration-text text-gray-600">
            <div class="font-medium text-[#0b2433] whitespace-nowrap">{{ formatDate(req.startDate) }}</div>
            <div class="text-gray-400 mt-0.5 whitespace-nowrap">to {{ formatDate(req.endDate) }}</div>
          </div>

          <!-- Col 4: Reason -->
          <div class="reason-text text-gray-500 truncate-2-lines" :title="req.reason">
            {{ req.reason || '--' }}
          </div>

          <!-- Col 5: Status -->
          <div>
            <span class="status-badge" :class="getStatusClass(req.status)">
              {{ req.status }}
            </span>
          </div>

          <!-- Col 6: Actions -->
          <div class="actions-group">
            <template v-if="req.status === 'PENDING'">
              <button class="action-btn approve" @click.stop="handleStatusChange(req.id, 'APPROVED')" title="Approve">
                <Check class="icon-scale" /> <span class="btn-text">Approve</span>
              </button>
              <button class="action-btn reject" @click.stop="handleStatusChange(req.id, 'REJECTED')" title="Reject">
                <X class="icon-scale" /> <span class="btn-text">Reject</span>
              </button>
            </template>
            
            <span v-else class="processed-text text-gray-400 italic font-medium">
              Processed
            </span>
          </div>
        </div>
      </FloatingTable>
    </div>

    <!-- Modal Form (Giữ nguyên) -->
    <BaseModal :isOpen="showModal" title="New Leave Request" @close="closeModal">
      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label>Employee</label>
          <select v-model="formData.employeeId" required>
            <option :value="null">-- Select Employee --</option>
            <option v-for="emp in employees" :key="emp.id" :value="emp.id">
              {{ emp.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>Leave Type</label>
          <select v-model="formData.leaveType" required>
            <option value="ANNUAL">Annual Leave</option>
            <option value="SICK">Sick Leave</option>
            <option value="UNPAID">Unpaid Leave</option>
          </select>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Start Date</label>
            <input v-model="formData.startDate" type="date" required />
          </div>
          <div class="form-group">
            <label>End Date</label>
            <input v-model="formData.endDate" type="date" required />
          </div>
        </div>
        <div class="form-group">
          <label>Reason</label>
          <textarea v-model="formData.reason" rows="3" placeholder="Explain reason..."></textarea>
        </div>
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeModal">Cancel</button>
          <button type="submit" class="btn-submit">Submit Request</button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Check, X } from 'lucide-vue-next'; 
import leaveService from '@/services/leaveService';
import employeeService from '@/services/employeeService';
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import FloatingTable from '@/components/common/FloatingTable.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';

// Config Grid Layout
const gridLayout = "minmax(160px, 2.5fr) 0.8fr 1.4fr 2fr 1fr 1.2fr";

// State
const leaves = ref([]);
const employees = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const filterStatus = ref('ALL');
const showModal = ref(false);
const formData = reactive({ employeeId: null, leaveType: 'ANNUAL', startDate: '', endDate: '', reason: '' });

// Fetch Logic
const fetchData = async () => {
  loading.value = true;
  try {
    const [lRes, eRes] = await Promise.all([leaveService.getLeaves(), employeeService.getEmployees()]);
    leaves.value = lRes.data || [];
    employees.value = eRes.data || [];
  } catch (e) { console.error(e); } finally { loading.value = false; }
};

const getEmployee = (id) => employees.value.find(e => e.id === id);

const filteredLeaves = computed(() => {
  let result = leaves.value;
  if (filterStatus.value !== 'ALL') result = result.filter(l => l.status === filterStatus.value);
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(l => {
      const emp = getEmployee(l.employeeId);
      return emp && emp.name.toLowerCase().includes(q);
    });
  }
  return result;
});

const formatDate = (d) => d ? new Date(d).toLocaleDateString('en-GB') : '';
const humanizeLeaveType = (t) => t.charAt(0) + t.slice(1).toLowerCase();
const getStatusClass = (s) => {
  if (s === 'APPROVED') return 'st-approved';
  if (s === 'REJECTED') return 'st-rejected';
  return 'st-pending';
};

// Actions
const openAddModal = () => { Object.assign(formData, { employeeId: null, leaveType: 'ANNUAL', startDate: '', endDate: '', reason: '' }); showModal.value = true; };
const closeModal = () => showModal.value = false;
const handleSubmit = async () => {
  try {
    const res = await leaveService.createLeave(formData);
    leaves.value.unshift(res.data);
    closeModal();
  } catch (e) { alert("Failed"); }
};
const handleStatusChange = async (id, newStatus) => {
  const idx = leaves.value.findIndex(l => l.id === id);
  if (idx === -1) return;
  const oldStatus = leaves.value[idx].status;
  leaves.value[idx] = { ...leaves.value[idx], status: newStatus };
  try { await leaveService.updateLeaveStatus(id, newStatus); } 
  catch (e) { console.error("Update failed", e); leaves.value[idx] = { ...leaves.value[idx], status: oldStatus }; alert("Failed to update status."); }
};
onMounted(() => fetchData());
</script>

<style scoped>
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; color: #0b2433; }

/* Stats & Filter */
.stats-grid { display: flex; gap: 16px; margin-bottom: 24px; }
.glass-card { background: rgba(255, 255, 255, 0.92); border: 0.5px solid rgba(15, 118, 110, 0.12); box-shadow: 0 6px 18px rgba(10, 20, 36, 0.06); border-radius: 18px; padding: 20px 24px; min-width: 200px; }
.stat-label { font-size: 12px; font-weight: 700; text-transform: uppercase; color: #94a3b8; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: 700; }
.filter-section { display: flex; gap: 10px; margin-bottom: 20px; overflow-x: auto; padding-bottom: 4px; }
.filter-pill { border: none; background: transparent; padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; color: #64748b; cursor: pointer; transition: all 0.2s; }
.filter-pill:hover { background: rgba(95, 209, 197, 0.1); color: #0f766e; }
.filter-pill.active { background: #fff; color: #0b2433; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }

/* --- FIX SCROLL & HEADER ALIGNMENT --- */
.table-scroll-wrapper {
  width: 100%;
  overflow-x: auto;
  padding-bottom: 10px;
}

/* Áp dụng min-width cho TOÀN BỘ FloatingTable (bao gồm Header và Body) */
/* Sử dụng :deep() vì FloatingTable là component con */
:deep(.fixed-min-width) {
  min-width: 900px; /* Con số này ép toàn bộ bảng rộng ra */
}

/* Table Row */
.row-card {
  display: grid; align-items: center; background: #ffffff; border-radius: 12px;
  padding: 12px 16px; 
  box-shadow: 0 2px 6px rgba(10, 20, 36, 0.04); transition: all 0.2s ease;
  border: 1px solid transparent; gap: 10px;
  /* Đã xóa min-width ở đây để nó theo cha */
}
.row-card:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(10, 20, 36, 0.08); border-color: rgba(95, 209, 197, 0.3); }

/* --- CONTENT STYLES --- */
.col-info { display: flex; align-items: center; gap: 10px; overflow: hidden; min-width: 0; }
.info-text { display: flex; flex-direction: column; justify-content: center; min-width: 0; }
.emp-name { font-weight: 600; font-size: clamp(13px, 1vw, 15px); color: #0b2433; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.emp-meta { font-size: clamp(10px, 0.8vw, 12px); color: #94a3b8; margin-top: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-weight: 500; }

.type-badge { font-size: clamp(10px, 0.8vw, 12px); font-weight: 600; background: #f1f5f9; padding: clamp(2px, 0.4vw, 4px) clamp(6px, 0.8vw, 8px); border-radius: 6px; color: #475569; white-space: nowrap;}
.duration-text { font-size: clamp(11px, 0.9vw, 14px); line-height: 1.3; }
.reason-text { font-size: clamp(12px, 0.9vw, 14px); }
.truncate-2-lines { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis; line-height: 1.4; }

.status-badge { padding: clamp(2px, 0.4vw, 4px) clamp(6px, 0.8vw, 10px); border-radius: 6px; font-size: clamp(9px, 0.7vw, 11px); font-weight: 700; text-transform: uppercase; white-space: nowrap;}
.st-approved { background: #dcfce7; color: #16a34a; }
.st-rejected { background: #fee2e2; color: #ef4444; }
.st-pending { background: #fef9c3; color: #ca8a04; }

.actions-group { display: flex; justify-content: flex-end; gap: 6px; flex-wrap: wrap; }
.action-btn { display: flex; align-items: center; justify-content: center; gap: 4px; padding: 6px 10px; border-radius: 8px; border: none; cursor: pointer; transition: all 0.2s; }
.icon-scale { width: 14px; height: 14px; }
.btn-text { font-size: clamp(10px, 0.8vw, 12px); font-weight: 600; display: none; }
@media (min-width: 1024px) { .btn-text { display: inline; } }

.action-btn.approve { background: #dcfce7; color: #16a34a; }
.action-btn.approve:hover { background: #bbf7d0; }
.action-btn.reject { background: #fee2e2; color: #ef4444; }
.action-btn.reject:hover { background: #fecaca; }
.processed-text { font-size: clamp(10px, 0.8vw, 12px); }


.modal-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: flex; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; flex: 1; }
.form-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; }
.form-group input, .form-group select, .form-group textarea { padding: 10px 12px; border-radius: 10px; border: 1px solid #e2e8f0; width: 100%; font-size: 14px; color: #0f172a; background: #f8fafc; }
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
.btn-cancel { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: #64748b; background: transparent; cursor: pointer; border: 1px solid transparent;}
.btn-submit { padding: 10px 20px; border-radius: 12px; font-weight: 600; color: white; background: #5fd1c5; border: none; cursor: pointer; box-shadow: 0 4px 12px rgba(95, 209, 197, 0.3); }
</style>