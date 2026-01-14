<template>
  <div class="page-container">
    <!-- Header -->
    <PeoplePageHeader 
      title="Global Tasks" 
      subtitle="HR view: Monitor company-wide workload & status" 
      btnText="New Task" 
      v-model="searchQuery" 
      @add="openAddModal" 
    />
    
    <!-- Filter Bar (Dàn ngang có scroll) -->
    <div class="filter-bar">
      <div class="filter-label">Filter by Employee:</div>
      <div class="filter-scroll">
        <button class="filter-pill" :class="{ active: !selectedEmpId }" @click="selectedEmpId = null">
          All Employees
        </button>
        <button 
          v-for="emp in employees" :key="emp.id" 
          class="filter-pill" :class="{ active: selectedEmpId === emp.id }" 
          @click="selectedEmpId = emp.id"
        >
          <BaseAvatar :name="emp.name" :size="20" /> 
          <span class="ml-2">{{ emp.name }}</span>
        </button>
      </div>
    </div>

    <!-- Table -->
    <FloatingTable gridColumns="2fr 1.2fr 1.2fr 1fr 1fr 90px">
      <template #header>
        <div>Task Title</div>
        <div>Assigned To</div>
        <div>Project</div>
        <div>Deadline</div>
        <div>Status</div>
        <div style="text-align: right; padding-right: 10px;">Actions</div>
      </template>

      <div v-if="loading" class="state-msg">
        <div class="loader"></div>
        <p>Syncing...</p>
      </div>
      <div v-else-if="filteredTasks.length === 0" class="state-msg">No tasks found.</div>

      <div 
        v-else 
        v-for="task in filteredTasks" :key="task.id" 
        class="row-card"
        @click="openEditModal(task)"
      >
        <div class="task-title-cell">
          <Activity :size="16" class="task-icon" />
          <span class="task-title-text">{{ task.title }}</span>
        </div>
        <div class="col-info">
          <div class="mini-assignee">
            <BaseAvatar :name="getEmployee(task.assigneeId)?.name || '?'" :size="24" />
            <span class="assignee-name">{{ getEmployee(task.assigneeId)?.name || 'Unassigned' }}</span>
          </div>
        </div>
        <div class="project-tag-wrapper">
          <span class="project-tag">{{ getProject(task.projectId)?.projectName || 'General' }}</span>
        </div>
        <div class="date-cell">
          <Calendar :size="14" class="mr-2" />
          {{ formatDate(task.deadline) }}
        </div>
        <div class="status-cell">
           <span class="task-status-pill" :class="task.status">{{ task.status }}</span>
        </div>
        <div class="action-cell">
          <button class="row-delete-btn" @click.stop="handleDeleteTask(task.id)">
            <Trash2 :size="16" />
          </button>
          <ChevronRight :size="18" class="chevron-icon" />
        </div>
      </div>
    </FloatingTable>

    <!-- Notion Modal -->
    <div v-if="showModal" class="notion-modal-backdrop" @click.self="closeModal">
      <div class="notion-modal-content">
        <div class="modal-cover">
          <button class="close-btn" @click="closeModal"><X :size="20"/></button>
        </div>
        <div class="icon-modal-fixed">
          <div class="icon-large"><Activity :size="32" /></div>
        </div>
        <div class="modal-body-scroll">
          <input v-model="modalData.title" class="title-input" placeholder="Untitled Task" :readonly="isEditing" />
          <div class="properties-grid">
            <div class="prop-row">
              <div class="prop-label"><Folder :size="15"/> Project</div>
              <div class="prop-value">
                <select v-model="modalData.projectId" class="prop-select" :disabled="isEditing">
                  <option v-for="p in projects" :key="p.id" :value="p.id">{{ p.projectName }}</option>
                </select>
              </div>
            </div>
            <div class="prop-row">
              <div class="prop-label"><User :size="15"/> Assignee</div>
              <div class="prop-value">
                <select v-model="modalData.assigneeId" class="prop-select" :disabled="isEditing">
                  <option v-for="e in employees" :key="e.id" :value="e.id">{{ e.name }}</option>
                </select>
              </div>
            </div>
            <div class="prop-row">
              <div class="prop-label"><Activity :size="15"/> Status</div>
              <div class="prop-value">
                 <select v-model="modalData.status" class="prop-select-status" :class="modalData.status">
                   <option value="TODO">To Do</option>
                   <option value="COMPLETED">Completed</option>
                   <option value="CANCELLED">Cancelled</option>
                 </select>
              </div>
            </div>
          </div>
          <div class="note-section">
            <h4 class="section-title">Completion Note</h4>
            <textarea v-model="modalData.completionNote" class="note-textarea" placeholder="Remarks..."></textarea>
          </div>
          <div class="modal-footer-sticky">
            <button class="save-btn" @click="handleSave">
              {{ isEditing ? 'Update Status' : 'Create Task' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Folder, Calendar, User, Activity, X, ChevronRight, Trash2 } from 'lucide-vue-next';
import taskService from '@/services/taskService';
import employeeService from '@/services/employeeService';
import projectService from '@/services/projectService';
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import FloatingTable from '@/components/common/FloatingTable.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';

const tasks = ref([]);
const employees = ref([]);
const projects = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const selectedEmpId = ref(null);
const showModal = ref(false);
const isEditing = ref(false);

const hiddenTasks = ref(JSON.parse(localStorage.getItem('hidden_tasks') || '[]'));

const modalData = reactive({ id: null, projectId: null, assigneeId: null, title: '', deadline: '', status: 'TODO', completionNote: '' });

const fetchData = async () => {
  loading.value = true;
  try {
    const [tRes, eRes, pRes] = await Promise.all([taskService.getTasks(), employeeService.getEmployees(), projectService.getProjects()]);
    tasks.value = tRes.data || [];
    employees.value = eRes.data || [];
    projects.value = pRes.data || [];
  } catch (err) { console.error(err); }
  loading.value = false;
};

const handleDeleteTask = (id) => {
  if (!confirm("Delete this task?")) return;
  hiddenTasks.value.push(id);
  localStorage.setItem('hidden_tasks', JSON.stringify(hiddenTasks.value));
  // Call backend delete if exists
  try { taskService.deleteTask(id); } catch(e) {}
};

const handleSave = async () => {
  try {
    if (isEditing.value) {
      const res = await taskService.updateTask(modalData.id, { status: modalData.status, completionNote: modalData.completionNote });
      const idx = tasks.value.findIndex(t => t.id === modalData.id);
      if (idx !== -1) tasks.value[idx] = res.data;
    } else {
      const res = await taskService.createTask({ ...modalData, priority: 'MEDIUM' });
      tasks.value.unshift(res.data);
    }
    showModal.value = false;
  } catch (e) { alert("Error"); }
};

const openAddModal = () => {
  isEditing.value = false;
  Object.assign(modalData, { id: null, projectId: projects.value[0]?.id, assigneeId: employees.value[0]?.id, title: '', status: 'TODO' });
  showModal.value = true;
};
const openEditModal = (task) => { isEditing.value = true; Object.assign(modalData, task); showModal.value = true; };
const closeModal = () => showModal.value = false;
const getEmployee = (id) => employees.value.find(e => e.id === id);
const getProject = (id) => projects.value.find(p => p.id === id);
const formatDate = (d) => d ? new Date(d).toLocaleDateString('en-GB', {day:'2-digit', month:'short'}) : '--';

const filteredTasks = computed(() => {
  let res = tasks.value.filter(t => !hiddenTasks.value.includes(t.id));
  if (selectedEmpId.value) res = res.filter(t => t.assigneeId === selectedEmpId.value);
  if (searchQuery.value) res = res.filter(t => t.title.toLowerCase().includes(searchQuery.value.toLowerCase()));
  return res;
});

onMounted(fetchData);
</script>


<style scoped>
.page-container { padding: 32px 48px; background-color: #f9fbfd; min-height: 100vh; font-family: 'Inter', sans-serif; }


.filter-bar { display: flex; align-items: center; gap: 16px; margin-bottom: 32px; overflow: hidden; }
.filter-label { font-size: 13px; font-weight: 600; color: #94a3b8; white-space: nowrap; }
.filter-scroll { display: flex; gap: 10px; overflow-x: auto; padding-bottom: 5px; scrollbar-width: none; }
.filter-scroll::-webkit-scrollbar { display: none; }

.filter-pill { 
  display: flex; align-items: center; background: #fff; padding: 8px 16px; border-radius: 12px; 
  font-size: 13px; font-weight: 500; cursor: pointer; border: 1px solid #e2e8f0; color: #64748b; transition: 0.2s; white-space: nowrap;
}
.filter-pill.active { background: #2d3436; color: #fff; border-color: #2d3436; }

/* Table styles */
.row-card {
  display: grid; align-items: center; background: #fff; border-radius: 16px; padding: 16px 24px; margin-bottom: 12px; 
  transition: 0.3s; border: 1px solid #f1f5f9; grid-template-columns: 2fr 1.2fr 1.2fr 1fr 1fr 90px; gap: 16px; cursor: pointer;
}
.row-card:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(0,0,0,0.04); border-color: #e2e8f0; }

.task-title-text { font-weight: 600; color: #2d3436; font-size: 14px; }
.assignee-name { font-size: 13px; color: #636e72; }

/* Status Pills */
.task-status-pill { padding: 4px 10px; border-radius: 6px; font-size: 10px; font-weight: 700; text-transform: uppercase; }
.TODO { background: #f1f2f6; color: #636e72; }
.COMPLETED, .Done, .ACTIVE { background: #e8f5e9; color: #2e7d32; }
.CANCELLED { background: #ffebee; color: #c62828; }

/* Notion Modal Design theo ảnh */
.notion-modal-backdrop { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.2); backdrop-filter: blur(4px); z-index: 9999; display: flex; justify-content: center; align-items: center; }
.notion-modal-content { background: #fff; width: 90%; max-width: 750px; border-radius: 20px; box-shadow: 0 20px 50px rgba(0,0,0,0.15); position: relative; overflow: hidden; }

.modal-cover { height: 160px; background: #c2e9fb; position: relative; } /* Màu xanh nhạt theo ảnh */
.close-btn { position: absolute; top: 16px; right: 16px; background: #fff; border: none; width: 32px; height: 32px; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }

.icon-modal-fixed { position: absolute; top: 125px; left: 45px; z-index: 10; }
.icon-large { background: #fff; width: 70px; height: 70px; border-radius: 18px; display: flex; align-items: center; justify-content: center; box-shadow: 0 8px 20px rgba(0,0,0,0.08); border: 1px solid #f1f5f9; }

.modal-body-scroll { padding: 60px 45px 30px; max-height: 75vh; overflow-y: auto; }
.title-input { font-size: 36px; font-weight: 800; border: none; outline: none; width: 100%; margin-bottom: 30px; color: #2d3436; }

.prop-row { display: flex; align-items: center; margin-bottom: 15px; }
.prop-label { width: 120px; color: #94a3b8; font-size: 14px; display: flex; align-items: center; gap: 8px; }
.prop-value { flex: 1; font-size: 14px; }
.prop-select, .prop-date { border: none; background: #f8fafc; padding: 6px 12px; border-radius: 6px; width: 200px; outline: none; color: #475569; }
.static-value { display: flex; align-items: center; gap: 8px; color: #2d3436; font-weight: 500; }

.section-title { font-size: 12px; font-weight: 700; color: #cbd5e1; text-transform: uppercase; margin: 40px 0 15px; letter-spacing: 0.5px; }
.note-textarea { width: 100%; height: 120px; padding: 15px; border-radius: 12px; border: 1px solid #f1f5f9; background: #fcfcfc; font-family: inherit; outline: none; font-size: 14px; line-height: 1.6; }

.modal-footer-sticky { display: flex; justify-content: flex-end; gap: 12px; margin-top: 40px; }
.btn-cancel-notion { padding: 10px 20px; border-radius: 8px; border: 1px solid #e2e8f0; background: #fff; font-weight: 600; cursor: pointer; color: #64748b; }
.save-btn { background: #2d3436; color: #fff; border: none; padding: 10px 25px; border-radius: 8px; font-weight: 600; cursor: pointer; }

.action-cell { display: flex; align-items: center; justify-content: flex-end; gap: 12px; }
.row-delete-btn { background: transparent; border: none; color: #cbd5e1; padding: 6px; cursor: pointer; transition: 0.2s; }
.row-delete-btn:hover { color: #ef4444; background: #fef2f2; border-radius: 6px; }

.project-tag { background: #f1f5f9; color: #475569; padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 600; }
</style>