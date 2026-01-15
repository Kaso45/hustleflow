<template>
  <div class="page-container">
    <PeoplePageHeader 
      title="Projects"
      subtitle="Gallery view of company initiatives"
      btnText="New Project"
      v-model="searchQuery"
      @add="openAddModal"
    />

    <div class="filter-section">
      <button class="filter-pill" :class="{ active: filterStatus === 'ALL' }" @click="filterStatus = 'ALL'">All Projects</button>
      <button class="filter-pill st-active-btn" :class="{ active: filterStatus === 'ACTIVE' }" @click="filterStatus = 'ACTIVE'">Active</button>
      <button class="filter-pill st-pending-btn" :class="{ active: filterStatus === 'PENDING' }" @click="filterStatus = 'PENDING'">Pending</button>
      <button class="filter-pill st-completed-btn" :class="{ active: filterStatus === 'COMPLETED' }" @click="filterStatus = 'COMPLETED'">Completed</button>
    </div>

    <div v-if="loading" class="state-msg">
      <div class="loader"></div>
      <p>Gathering projects...</p>
    </div>
    
    <div v-else class="project-grid">
      <div v-for="proj in filteredProjects" :key="proj.id" class="project-card" @click="openEditModal(proj)">
        <div class="card-cover" :style="{ background: getCoverGradient(proj.id) }"></div>
        <div class="card-body">
          <div class="header-separate">
            <div class="icon-box-small"><Folder :size="18" stroke-width="2.5" /></div>
            <span class="status-badge" :class="getStatusClass(proj.status)">{{ proj.status }}</span>
          </div>
          
          <h3 class="proj-name">{{ proj.projectName }}</h3>
          <p class="category-text">Internal Initiative • Design Team</p>
          
          <div class="progress-section">
            <div class="progress-labels">
              <span class="progress-percent">{{ calculateProgress(proj.id) }}%</span>
              <span class="progress-count">{{ getCompletedCount(proj.id) }}/{{ getTotalCount(proj.id) }} Tasks</span>
            </div>
            <div class="progress-track">
              <div class="progress-fill" :style="{ width: calculateProgress(proj.id) + '%', background: getProgressColor(proj.status) }"></div>
            </div>
          </div>

          <div class="card-footer">
            <div class="date-range">
              <Calendar :size="14" />
              <span>{{ formatDate(proj.startDate) }} — {{ formatDate(proj.endDate) }}</span>
            </div>
            <div class="manager-avatar">
              <BaseAvatar v-if="getEmployee(proj.managerId)" :name="getEmployee(proj.managerId).name" :size="28" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="notion-modal-backdrop" @click.self="closeModal">
      <div class="notion-modal-content">
        <div class="modal-cover" :style="{ background: getCoverGradient(formData.id || 0) }">
      <button class="close-btn" @click="closeModal" aria-label="Close Modal" title="Close">
        <X :size="20"/>
      </button>
    </div>
<div class="icon-modal-fixed">
      <div class="icon-large" title="Project Icon">
        <Folder :size="36" stroke-width="2.5" />
      </div>
    </div>
        <div class="modal-body-scroll">
      <div class="project-header">
        <input v-model="formData.projectName" class="title-input" placeholder="Untitled Project" aria-label="Project Title" />
        
        <div class="properties-grid">
          <div class="prop-row">
            <div class="prop-label"><User :size="15"/> Manager</div>
            <div class="prop-value">
              <div class="mini-assignee">
                <BaseAvatar v-if="getEmployee(formData.managerId)" :name="getEmployee(formData.managerId).name" :size="22" />
                <select v-model="formData.managerId" class="prop-select" aria-label="Select Manager">
                  <option :value="null">Unassigned</option>
                  <option v-for="emp in employees" :key="emp.id" :value="emp.id">{{ emp.name }}</option>
                </select>
              </div>
            </div>
          </div>
              
              <div class="prop-row">
                <div class="prop-label"><Calendar :size="15"/> Timeline</div>
                <div class="prop-value date-group">
                  <input v-model="formData.startDate" type="date" class="prop-date"/>
                  <span class="sep">/</span>
                  <input v-model="formData.endDate" type="date" class="prop-date"/>
                </div>
              </div>

              <div class="prop-row">
                <div class="prop-label"><Activity :size="15"/> Status</div>
                <div class="prop-value">
                   <select v-model="formData.status" class="prop-select-status" :class="formData.status">
                     <option value="ACTIVE">Active</option>
                     <option value="PENDING">Pending</option>
                     <option value="COMPLETED">Completed</option>
                   </select>
                </div>
              </div>
            </div>
          </div>

          <div class="tasks-section">
            <h4 class="section-title">Project Tasks</h4>
            <div class="notion-table">
              <div class="nt-header">
                <div>Status</div>
                <div>Task Name</div>
                <div>Assignee</div>
                <div>Priority</div>
                <div>Deadline</div>
                <div style="text-align: right;"></div>
              </div>

              <div v-for="task in projectTasks" :key="task.id" class="nt-row">
                <div class="nt-cell">
                  <select v-model="task.status" @change="updateTask(task)" class="task-status-pill" :class="task.status">
                    <option value="TODO">To Do</option>
                    <option value="COMPLETED">Done</option>
                    <option value="CANCELLED">Cancel</option>
                  </select>
                </div>
                <div class="nt-cell font-medium">{{ task.title }}</div>
                <div class="nt-cell">
                   <div class="mini-assignee">
                      <BaseAvatar v-if="getEmployee(task.assigneeId)" :name="getEmployee(task.assigneeId).name" :size="20" />
                      <span>{{ getEmployee(task.assigneeId)?.name || 'N/A' }}</span>
                   </div>
                </div>
                <div class="nt-cell">
                  <span class="prio-tag" :class="task.priority">{{ task.priority }}</span>
                </div>
                <div class="nt-cell date-cell">{{ formatDate(task.deadline) }}</div>
                <div class="nt-cell" style="justify-content: flex-end;">
                  <button class="delete-mini" @click="deleteTask(task.id)"><Trash2 :size="14"/></button>
                </div>
              </div>
              <div class="nt-add-btn" @click="addNewTask">
                <Plus :size="16" /> Add a task
              </div>
            </div>
          </div>
          
          <div class="modal-footer-sticky">
            <button class="save-btn" @click="handleSaveProject">Update Project Information</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Folder, Calendar, Trash2, X, User, Plus, Activity } from 'lucide-vue-next';
import projectService from '@/services/projectService';
import taskService from '@/services/taskService';
import employeeService from '@/services/employeeService';
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';

const projects = ref([]);
const employees = ref([]);
const allTasks = ref([]);
const projectTasks = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const filterStatus = ref('ALL');
const showModal = ref(false);
const isEditing = ref(false);

const hiddenTasks = ref(JSON.parse(localStorage.getItem('hidden_tasks') || '[]'));
const hiddenProjects = ref(JSON.parse(localStorage.getItem('hidden_projects') || '[]'));

const formData = reactive({
  id: null,
  projectName: '',
  description: '',
  startDate: '',
  endDate: '',
  status: 'ACTIVE',
  managerId: null
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [pRes, eRes, tRes] = await Promise.all([
      projectService.getProjects(),
      employeeService.getEmployees(),
      taskService.getTasks()
    ]);
    projects.value = pRes.data || [];
    employees.value = eRes.data || [];
    allTasks.value = (tRes.data || []).filter(t => !hiddenTasks.value.includes(t.id));
  } catch (e) {}
  loading.value = false;
};

const getProjectTasksList = (pid) =>
  allTasks.value.filter(t => Number(t.projectId) === Number(pid));

const getTotalCount = (pid) => getProjectTasksList(pid).length;

const getCompletedCount = (pid) =>
  getProjectTasksList(pid).filter(t => t.status === 'COMPLETED').length;

const calculateProgress = (pid) => {
  const total = getTotalCount(pid);
  return total === 0 ? 0 : Math.round((getCompletedCount(pid) / total) * 100);
};

const handleSaveProject = async () => {
  try {
    const payload = {
      projectName: formData.projectName,
      description: formData.description || '',
      startDate: formData.startDate,
      endDate: formData.endDate,
      status: formData.status,
      managerId: Number(formData.managerId)
    };

    if (isEditing.value) {
      const res = await projectService.updateProject(formData.id, payload);
      const idx = projects.value.findIndex(p => p.id === formData.id);
      if (idx !== -1) projects.value[idx] = res.data;
    } else {
      const res = await projectService.createProject(payload);
      projects.value.unshift(res.data);
      formData.id = res.data.id;
      isEditing.value = true;
    }
  } catch (e) {}
};

const updateTask = async (task) => {
  try {
    const res = await taskService.updateTask(task.id, {
      status: task.status,
      completionNote: ''
    });
    const idx = allTasks.value.findIndex(t => t.id === task.id);
    if (idx !== -1) allTasks.value[idx].status = res.data.status;
  } catch (e) {}
};

const addNewTask = async () => {
  if (!formData.id) return;
  try {
    const res = await taskService.createTask({
      projectId: Number(formData.id),
      assigneeId: Number(formData.managerId || employees.value[0]?.id),
      title: 'New Task',
      description: '',
      deadline: new Date().toISOString(),
      priority: 'MEDIUM',
      status: 'TODO'
    });
    projectTasks.value.push(res.data);
    allTasks.value.push(res.data);
  } catch (e) {}
};

const deleteTask = (id) => {
  if (!confirm('Delete this task?')) return;

  projectTasks.value = projectTasks.value.filter(t => t.id !== id);
  allTasks.value = allTasks.value.filter(t => t.id !== id);

  if (!hiddenTasks.value.includes(id)) {
    hiddenTasks.value.push(id);
    localStorage.setItem('hidden_tasks', JSON.stringify(hiddenTasks.value));
  }

  try { taskService.deleteTask(id); } catch (e) {}
};

const deleteProject = (id) => {
  if (!confirm('Delete project?')) return;
  if (!hiddenProjects.value.includes(id)) {
    hiddenProjects.value.push(id);
    localStorage.setItem('hidden_projects', JSON.stringify(hiddenProjects.value));
  }
};

const openAddModal = () => {
  isEditing.value = false;
  Object.assign(formData, {
    id: null,
    projectName: '',
    description: '',
    startDate: '',
    endDate: '',
    status: 'ACTIVE',
    managerId: null
  });
  projectTasks.value = [];
  showModal.value = true;
};

const openEditModal = async (proj) => {
  isEditing.value = true;
  Object.assign(formData, proj);
  try {
    const res = await taskService.getTasks({ projectId: proj.id });
    projectTasks.value = (res.data || []).filter(
      t => !hiddenTasks.value.includes(t.id)
    );
  } catch (e) {}
  showModal.value = true;
};

const closeModal = () => showModal.value = false;

const getEmployee = (id) => employees.value.find(e => e.id === id);

const formatDate = (d) =>
  d ? new Date(d).toLocaleDateString('en-GB', { day: 'numeric', month: 'short' }) : '--';

const getStatusClass = (s) =>
  s === 'ACTIVE' ? 'st-active' : s === 'COMPLETED' ? 'st-completed' : 'st-gray';

const getProgressColor = (s) =>
  s === 'COMPLETED' ? '#B5EAD7' : '#A0D2EB';

const getCoverGradient = (id) => {
  const gradients = [
    'linear-gradient(135deg, #E0C3FC 0%, #8EC5FC 100%)',
    'linear-gradient(135deg, #FBC2EB 0%, #A6C1EE 100%)',
    'linear-gradient(135deg, #84FAB0 0%, #8FD3F4 100%)',
    'linear-gradient(135deg, #A1C4FD 0%, #C2E9FB 100%)',
    'linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%)'
  ];
  return gradients[(id || 0) % gradients.length];
};

const filteredProjects = computed(() => {
  let result = projects.value.filter(p => !hiddenProjects.value.includes(p.id));
  if (filterStatus.value !== 'ALL')
    result = result.filter(p => p.status === filterStatus.value);
  if (searchQuery.value)
    result = result.filter(p =>
      p.projectName.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  return result;
});

onMounted(fetchData);
</script>


<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.page-container {
  padding: 32px;
  background-color: #f9fbfd;
  min-height: 100vh;
  font-family: 'Inter', system-ui, sans-serif;
  color: #2d3436;
  letter-spacing: -0.01em;
}

/* Filters */
.filter-section {
  display: flex;
  gap: 12px;
  margin: 24px 0 32px;
}

.filter-pill {
  border: 1px solid transparent;
  background: #fff;
  padding: 8px 20px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
  color: #636e72;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.filter-pill:hover {
  background: #f1f2f6;
  transform: translateY(-1px);
}

.filter-pill.active {
  background: #2d3436;
  color: #fff;
  box-shadow: 0 4px 12px rgba(45, 52, 54, 0.15);
}

/* Project Grid */
.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 28px;
}

.project-card {
  background: #ffffff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0,0,0,0.03);
  cursor: pointer;
  transition: all 0.4s ease;
  border: 1px solid rgba(0,0,0,0.04);
  display: flex;
  flex-direction: column;
}

.project-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0,0,0,0.07);
}

.card-cover {
  height: 100px;
  opacity: 0.85;
}

.card-body {
  padding: 24px;
  flex: 1;
}

.header-separate {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.icon-box-small {
  background: #f1f2f6;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #636e72;
}

.proj-name {
  font-size: 18px;
  font-weight: 700;
  color: #2d3436;
  line-height: 1.4;
  margin-bottom: 4px;
}

.category-text {
  font-size: 13px;
  color: #b2bec3;
  margin-bottom: 24px;
}

/* Progress Section */
.progress-section {
  margin-bottom: 24px;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 8px;
}

.progress-percent {
  font-size: 16px;
  font-weight: 700;
  color: #2d3436;
}

.progress-count {
  font-size: 11px;
  font-weight: 600;
  color: #b2bec3;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.progress-track {
  background: #f1f2f6;
  height: 8px;
  border-radius: 10px;
}

.progress-fill {
  height: 100%;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

/* Card Footer */
.card-footer {
  padding-top: 16px;
  border-top: 1px solid #f1f2f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #636e72;
}

/* Notion Modal UI */
.notion-modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(45, 52, 54, 0.4);
  backdrop-filter: blur(8px);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.notion-modal-content {
  background: #fff;
  width: 95%;
  max-width: 860px;
  height: 85vh;
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  position: relative; /* Bắt buộc để icon absolute theo nó */
  overflow: hidden;
}

.modal-cover {
  height: 160px;
  width: 100%;
  z-index: 1;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: white;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.icon-modal-fixed {
  position: absolute;
  top: 120px;    /* Đẩy icon đè lên giữa đường kẻ của Cover (Cover 160px - Icon 80px/2) */
  left: 60px;    /* Khoảng cách lề trái */
  z-index: 100;  /* Luôn luôn nằm trên cùng */
}
.modal-body-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 60px 60px 40px; /* Top padding 60px để nội dung không dính vào icon absolute */
  z-index: 5;
}
.icon-modal-wrapper {
  position: relative;
  display: inline-block;
  margin-top: -42px;   
  z-index: 50;        
  pointer-events: none; 
}
.icon-large {
  background: #fff;
  width: 82px;
  height: 82px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4px solid #fff; /* Viền trắng tách biệt icon với nền */
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
  color: #2d3436;
}
.title-input {
  font-size: 40px;
  font-weight: 800;
  border: none;
  outline: none;
  width: 100%;
  color: #2d3436;
  letter-spacing: -0.03em;
  margin-bottom: 32px;
}

/* Properties Grid */
.properties-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 40px;
}

.prop-row {
  display: flex;
  align-items: center;
  min-height: 32px;
}

.prop-label {
  width: 140px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #b2bec3;
  font-size: 14px;
  font-weight: 500;
}

.prop-value {
  flex: 1;
}

.prop-select, .prop-date {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none; /* Fix lỗi UI select trên Safari/Chrome Android */
  border: none;
  background: transparent;
  padding: 4px 8px;
  border-radius: 6px;
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  color: #2d3436;
  cursor: pointer;
  
}

.prop-select:hover, .prop-date:hover {
  background: #f1f2f6;
}

.prop-select-status {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  border: none;
  text-transform: uppercase;
}

/* Status Pastel Colors */
.status-badge {
  padding: 5px 12px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.st-active, .ACTIVE { background: #E3F2FD; color: #1976D2; }
.st-completed, .COMPLETED { background: #E8F5E9; color: #2E7D32; }
.st-gray, .PENDING { background: #FFF3E0; color: #EF6C00; }

/* Notion Table */
.section-title {
  font-size: 14px;
  font-weight: 700;
  color: #b2bec3;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f2f6;
}

.notion-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #f1f2f6;
}

.nt-header {
  display: grid;
  grid-template-columns: 120px 1.5fr 1fr 100px 110px 40px;
  padding: 12px 16px;
  background: #fafbfc;
  font-size: 12px;
  font-weight: 700;
  color: #b2bec3;
}

.nt-row {
  display: grid;
  grid-template-columns: 120px 1.5fr 1fr 100px 110px 40px;
  padding: 12px 16px;
  align-items: center;
  border-top: 1px solid #f1f2f6;
  transition: background 0.2s;
}

.nt-row:hover {
  background: #fafbfc;
}

.task-status-pill {
  border: none;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 700;
}

.TODO { background: #f1f2f6; color: #636e72; }
.CANCELLED { background: #ffebee; color: #c62828; }

.prio-tag {
  font-size: 10px;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 4px;
}
.HIGH { color: #d63031; background: #fab1a022; }
.MEDIUM { color: #e67e22; background: #fde6d2; }
.LOW { color: #00b894; background: #55efc422; }

.nt-add-btn {
  padding: 12px 16px;
  color: #b2bec3;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nt-add-btn:hover {
  background: #f1f2f6;
  color: #636e72;
}

.modal-footer-sticky {
  margin-top: 40px;
  display: flex;
  justify-content: flex-end;
}

.save-btn {
  background: #2d3436;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(45, 52, 54, 0.2);
}

.delete-mini {
  background: transparent;
  border: none;
  color: #dfe6e9;
  cursor: pointer;
  transition: color 0.2s;
}

.nt-row:hover .delete-mini {
  color: #ff7675;
}

.mini-assignee {
  display: flex;
  align-items: center;
  gap: 12px; /* Dãn cách 12px chuẩn pastel */
}

/* Đảm bảo chữ assignee không bị dính */
.mini-assignee span {
  font-size: 13px;
  font-weight: 500;
  color: #2d3436;
}
</style>