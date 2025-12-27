<template>
  <div class="page-container">
    <!-- Header và Grid Projects-->
    <PeoplePageHeader 
      title="Projects"
      subtitle="Gallery view of company initiatives"
      btnText="New Project"
      v-model="searchQuery"
      @add="openAddModal"
    />

    <div class="filter-section">
      <button class="filter-pill" :class="{ active: filterStatus === 'ALL' }" @click="filterStatus = 'ALL'">All</button>
      <button class="filter-pill" :class="{ active: filterStatus === 'ACTIVE' }" @click="filterStatus = 'ACTIVE'">In Progress</button>
      <button class="filter-pill" :class="{ active: filterStatus === 'PENDING' }" @click="filterStatus = 'PENDING'">Planning</button>
      <button class="filter-pill" :class="{ active: filterStatus === 'COMPLETED' }" @click="filterStatus = 'COMPLETED'">Completed</button>
    </div>

    <div v-if="loading" class="state-msg">Loading projects gallery...</div>
    <div v-else class="project-grid">
      <div 
        v-for="proj in filteredProjects" 
        :key="proj.id" 
        class="project-card"
        @click="openEditModal(proj)"
      >
        <div class="card-cover" :style="{ background: getCoverGradient(proj.id) }"></div>
        <div class="card-body">
          <div class="header-separate">
            <div class="icon-box-small"><Folder :size="20" stroke-width="2" /></div>
            <span class="status-badge" :class="getStatusClass(proj.status)">{{ formatStatus(proj.status) }}</span>
          </div>
          <h3 class="proj-name" :title="proj.projectName">{{ proj.projectName }}</h3>
          <div class="category-text">{{ proj.category || 'General' }}</div>
          <div class="progress-section">
            <div class="progress-labels">
              <span class="font-bold text-[#0b2433]">{{ calculateProgress(proj.id) }}%</span>
              <span class="text-xs text-gray-400 font-medium">{{ getCompletedCount(proj.id) }}/{{ getTotalCount(proj.id) }} Tasks</span>
            </div>
            <div class="progress-track">
              <div class="progress-fill" :style="{ width: calculateProgress(proj.id) + '%', background: getProgressColor(proj.status) }"></div>
            </div>
          </div>
          <div class="card-footer">
            <div class="date-range"><Calendar :size="14" class="text-gray-400" /><span>{{ formatDate(proj.startDate) }}</span><span class="arrow">→</span><span>{{ formatDate(proj.endDate) }}</span></div>
            <div class="manager-avatar"><BaseAvatar v-if="getEmployee(proj.managerId)" :name="getEmployee(proj.managerId).name" :size="24" /></div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="notion-modal-backdrop" @click.self="closeModal">
      <div class="notion-modal-content">
        <!-- Cover -->
        <div class="modal-cover" :style="{ background: getCoverGradient(formData.id || 0) }">
          <button class="close-btn" @click="closeModal"><X :size="18"/></button>
        </div>

        <div class="modal-body-scroll">
          <div class="icon-modal-wrapper">
             <div class="icon-large"><Folder :size="32" /></div>
          </div>
      
          <div class="project-header">
            <input v-model="formData.projectName" class="title-input" placeholder="Untitled Project" />
            <div class="properties-grid">
              <div class="prop-row">
                <div class="prop-label"><User :size="16"/> Manager</div>
                <div class="prop-value">
                   <div class="prop-select-wrapper">
                      <select v-model="formData.managerId" class="prop-select">
                        <option :value="null">Unassigned</option>
                        <option v-for="emp in employees" :key="emp.id" :value="emp.id">{{ emp.name }}</option>
                      </select>
                   </div>
                </div>
              </div>
              <div class="prop-row">
                <div class="prop-label"><Calendar :size="16"/> Timeline</div>
                <div class="prop-value date-group">
                  <input v-model="formData.startDate" type="date" class="prop-date"/>
                  <span class="text-gray-400">→</span>
                  <input v-model="formData.endDate" type="date" class="prop-date"/>
                </div>
              </div>
            </div>
          </div>

          <div class="divider"></div>
          <div class="tasks-section">
            <div class="section-top">
              <div class="section-title">Tasks</div>
              <div class="section-subtitle">Manage project tasks & progress</div>
            </div>

            <div class="notion-table">
              <div class="nt-header">
                <div class="nth-status">Status</div>
                <div class="nth-name">Task Name</div>
                <div class="nth-assignee">Assigned to</div>
                <div class="nth-priority">Priority</div>
                <div class="nth-deadline">Deadline</div>
                <div class="nth-action"></div>
              </div>

              <div v-for="task in projectTasks" :key="task.id" class="nt-row group">
                <div class="nt-cell cell-status">
                  <select v-model="task.status" @change="updateTask(task)" class="status-select" :class="task.status">
                    <option value="TODO">To Do</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="DONE">Done</option>
                  </select>
                </div>
                <div class="nt-cell cell-name">
                  <input v-model="task.title" class="nt-input" placeholder="Type a name..." @blur="updateTask(task)"/>
                </div>
                <div class="nt-cell cell-assignee">
                   <div class="assignee-display">
                      <BaseAvatar v-if="getEmployee(task.assigneeId)" :name="getEmployee(task.assigneeId).name" :size="22" />
                      <select v-model="task.assigneeId" @change="updateTask(task)" class="assignee-select-full">
                        <option :value="null">Unassigned</option>
                        <option v-for="emp in employees" :key="emp.id" :value="emp.id">{{ emp.name }}</option>
                      </select>
                      <span class="assignee-text">{{ getEmployee(task.assigneeId)?.name || 'Empty' }}</span>
                   </div>
                </div>
                <div class="nt-cell cell-priority">
                   <select v-model="task.priority" @change="updateTask(task)" class="priority-badge" :class="task.priority">
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                  </select>
                </div>
                <div class="nt-cell cell-deadline">
                  <input v-model="task.deadline" type="date" class="date-input-mini" @change="updateTask(task)"/>
                </div>
                <div class="nt-cell cell-action">
                   <button class="delete-mini" @click="deleteTask(task.id)"><Trash2 :size="14"/></button>
                </div>
              </div>

              <!-- Add New -->
              <div class="nt-new-row" @click="addNewTask">
                <Plus :size="14" /> New task
              </div>
            </div>
          </div>
          
          <div class="modal-footer">
            <button class="save-btn" @click="handleSaveProject">Save Project</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Folder, Calendar, Trash2, X, User, Activity, Check, Plus } from 'lucide-vue-next';
import projectService from '@/services/projectService';
import taskService from '@/services/taskService';
import employeeService from '@/services/employeeService';
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';

// State
const projects = ref([]);
const employees = ref([]);
const allTasks = ref([]);
const projectTasks = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const filterStatus = ref('ALL');
const showModal = ref(false);
const isEditing = ref(false);
const formData = reactive({ id: null, projectName: '', category: 'General', startDate: '', endDate: '', status: 'ACTIVE', managerId: null });

const fetchData = async () => {
  loading.value = true;
  const [pRes, eRes, tRes] = await Promise.all([
    projectService.getProjects(),
    employeeService.getEmployees(),
    taskService.getTasks()
  ]);
  projects.value = pRes.data || [];
  employees.value = eRes.data || [];
  allTasks.value = tRes.data || [];
  loading.value = false;
};

const getEmployee = (id) => employees.value.find(e => e.id === id);
const formatDate = (d) => d ? new Date(d).toLocaleDateString('en-GB', {day:'numeric', month:'short'}) : '--';
const formatStatus = (s) => s === 'ACTIVE' ? 'In Progress' : s.charAt(0) + s.slice(1).toLowerCase();
const getStatusClass = (s) => (s === 'ACTIVE' ? 'st-active' : s === 'COMPLETED' ? 'st-completed' : 'st-gray');
const getProgressColor = (s) => s === 'COMPLETED' ? '#3b82f6' : '#5fd1c5';
const getCoverGradient = (id) => {
  const gradients = ['linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%)', 'linear-gradient(120deg, #a18cd1 0%, #fbc2eb 100%)', 'linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%)', 'linear-gradient(120deg, #fccb90 0%, #d57eeb 100%)', 'linear-gradient(120deg, #e0c3fc 0%, #8ec5fc 100%)'];
  return gradients[(id || 0) % gradients.length];
};

const getProjectTasks = (pid) => allTasks.value.filter(t => t.projectId === pid);
const getTotalCount = (pid) => getProjectTasks(pid).length;
const getCompletedCount = (pid) => getProjectTasks(pid).filter(t => t.status === 'DONE').length;
const calculateProgress = (pid) => {
  const total = getTotalCount(pid);
  if (total === 0) return 0;
  return Math.round((getCompletedCount(pid) / total) * 100);
};

const openAddModal = () => {
  isEditing.value = false;
  Object.assign(formData, { id: null, projectName: '', category: 'General', startDate: '', endDate: '', status: 'ACTIVE', managerId: null });
  projectTasks.value = [];
  showModal.value = true;
};
const openEditModal = async (proj) => {
  isEditing.value = true;
  Object.assign(formData, { ...proj });
  const res = await taskService.getTasks({ projectId: proj.id });
  projectTasks.value = res.data || [];
  showModal.value = true;
};
const closeModal = () => showModal.value = false;
const handleSaveProject = async () => {
  if (isEditing.value) {
    await projectService.updateProject(formData.id, formData);
    const idx = projects.value.findIndex(p => p.id === formData.id);
    if (idx !== -1) projects.value[idx] = { ...formData };
  } else {
    const res = await projectService.createProject(formData);
    projects.value.unshift(res.data);
  }
  closeModal();
};

const updateTask = async (task) => {
  await taskService.updateTask(task.id, {
    status: task.status, title: task.title, assigneeId: task.assigneeId, priority: task.priority, deadline: task.deadline
  });
  // Sync global
  const idx = allTasks.value.findIndex(t => t.id === task.id);
  if (idx !== -1) allTasks.value[idx] = { ...task };
};

const addNewTask = async () => {
  if (!formData.id && !isEditing.value) { alert("Please save the project first."); return; }
  const newTask = {
    projectId: formData.id,
    title: "", status: 'TODO', priority: 'MEDIUM', deadline: new Date().toISOString().split('T')[0], assigneeId: null, progress: 0
  };
  const res = await taskService.createTask(newTask);
  projectTasks.value.push(res.data);
  allTasks.value.push(res.data);
};

const deleteTask = async (id) => {
  if (!confirm("Delete task?")) return;
  await taskService.deleteTask(id);
  projectTasks.value = projectTasks.value.filter(t => t.id !== id);
  allTasks.value = allTasks.value.filter(t => t.id !== id);
};

const filteredProjects = computed(() => {
  let result = projects.value;
  if (filterStatus.value !== 'ALL') result = result.filter(p => p.status === filterStatus.value);
  if (searchQuery.value) result = result.filter(p => p.projectName.toLowerCase().includes(searchQuery.value.toLowerCase()));
  return result;
});
onMounted(() => fetchData());
</script>

<style scoped>



.notion-modal-backdrop { 
  position: fixed; 
  inset: 0; 
  width: 100vw;
  height: 100vh;
  background: rgba(15, 23, 42, 0.5); 
  backdrop-filter: blur(4px); 
  z-index: 9999; /* Ensure it is ABOVE Sidebar */
  display: flex; 
  justify-content: center; 
  align-items: center; 
}

.notion-modal-content { 
  background: #fff; 
  width: 90%; 
  max-width: 900px; 
  height: 90vh; 
  border-radius: 12px; 
  box-shadow: 0 20px 50px rgba(0,0,0,0.3); 
  overflow: hidden; 
  display: flex; 
  flex-direction: column; 
  position: relative;
  /* Reset margin just in case */
  margin: 0; 
}


.modal-cover { height: 130px; width: 100%; position: relative; z-index: 1; flex-shrink: 0;}

.icon-modal-wrapper { 
  position: relative;
  z-index: 10; /* Important: On Top of Cover */
  margin-top: 10px; /* Push up into Cover */
  margin-left: 2px;
  margin-bottom: 24px;
}

.icon-large { 
  background: #fff; width: 72px; height: 72px; border-radius: 12px; 
  display: flex; align-items: center; justify-content: center; 
  box-shadow: 0 4px 12px rgba(0,0,0,0.12); 
  color: #333;
}

/* 3. SCROLL AREA FIX */
.modal-body-scroll { 
  flex: 1; 
  overflow-y: auto; 
  padding: 0 40px 40px 40px; 
  background: #fff;
  /* Add z-index to content background to prevent transparency issues */
  position: relative;
  z-index: 2; 
}

/* Close Button positioning relative to modal content */
.close-btn { 
  position: absolute; top: 12px; right: 12px; 
  background: rgba(255,255,255,0.4); 
  border: none; padding: 6px; border-radius: 6px; cursor: pointer; z-index: 20; 
}
.close-btn:hover { background: #fff; }

/* ------ REST OF STYLES (Tables, Fonts...) same as before ------ */
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; color: #0b2433; }
.filter-section { display: flex; gap: 10px; margin-bottom: 24px; }
.filter-pill { border: none; background: transparent; padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; color: #64748b; cursor: pointer; }
.filter-pill:hover { background: rgba(95, 209, 197, 0.1); color: #0f766e; }
.filter-pill.active { background: #fff; color: #0b2433; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
.state-msg { text-align: center; padding: 40px; color: #94a3b8; font-style: italic; }

/* Grid Cards */
.project-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 24px; }
.project-card { background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 12px rgba(10, 20, 36, 0.04); cursor: pointer; transition: all 0.3s; border: 1px solid rgba(0,0,0,0.03); display: flex; flex-direction: column; }
.project-card:hover { transform: translateY(-4px); box-shadow: 0 12px 24px rgba(10, 20, 36, 0.08); }
.card-cover { height: 90px; width: 100%; opacity: 0.9; }
.card-body { padding: 16px 20px 20px; display: flex; flex-direction: column; gap: 8px; flex: 1; }
.header-separate { display: flex; justify-content: space-between; align-items: flex-start; margin-top: 4px; margin-bottom: 8px; }
.icon-box-small { background: #f1f5f9; color: #0b2433; width: 40px; height: 40px; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
.proj-name { font-size: 16px; font-weight: 700; color: #0b2433; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.category-text { font-size: 11px; color: #64748b; font-weight: 500; background: #f8fafc; padding: 2px 8px; border-radius: 4px; display: inline-block; align-self: flex-start; }
.status-badge { padding: 4px 8px; border-radius: 6px; font-size: 10px; font-weight: 700; text-transform: uppercase; background: #fff; border: 1px solid #f1f5f9; }
.st-active { color: #0284c7; background: #e0f2fe; border-color: #bae6fd; } 
.st-completed { color: #16a34a; background: #dcfce7; border-color: #bbf7d0; } 
.st-gray { color: #64748b; }
.progress-section { margin-top: auto; padding-top: 12px; }
.progress-labels { display: flex; justify-content: space-between; margin-bottom: 4px; align-items: flex-end; }
.progress-track { background: #f1f5f9; height: 6px; border-radius: 10px; overflow: hidden; }
.progress-fill { height: 100%; border-radius: 10px; transition: width 0.5s ease; }
.card-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 14px; border-top: 1px solid #f1f5f9; margin-top: 12px; }
.date-range { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #64748b; font-weight: 500; }

/* Task Table Inside Modal */
.project-header { margin-bottom: 32px; }
.title-input { font-size: 36px; font-weight: 700; color: #0b2433; border: none; width: 100%; outline: none; margin-bottom: 24px; background: transparent; line-height: 1.2; }
.title-input::placeholder { color: #e2e8f0; }
.properties-grid { display: grid; gap: 8px; max-width: 600px; }
.prop-row { display: grid; grid-template-columns: 140px 1fr; align-items: center; min-height: 34px; }
.prop-label { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #64748b; }
.prop-select-wrapper select, .prop-date { padding: 4px 8px; border-radius: 4px; background: transparent; border: 1px solid transparent; font-size: 14px; cursor: pointer; color: #334155; }
.prop-select-wrapper select:hover, .prop-date:hover { background: #f1f5f9; }
.divider { height: 1px; background: #e2e8f0; margin: 32px 0; }
.section-top { margin-bottom: 16px; }
.section-title { font-size: 20px; font-weight: 700; color: #0b2433; }
.section-subtitle { font-size: 13px; color: #64748b; margin-top: 4px; }
.notion-table { border: 1px solid #e2e8f0; border-radius: 6px; overflow: visible; }
.nt-header { display: grid; grid-template-columns: 130px 2fr 1.5fr 100px 140px 40px; background: #f8fafc; border-bottom: 1px solid #e2e8f0; font-size: 12px; font-weight: 600; color: #64748b; }
.nth-status, .nth-name, .nth-assignee, .nth-priority, .nth-deadline, .nth-action { padding: 10px 12px; display: flex; align-items: center; border-right: 1px solid #f1f5f9; }
.nt-row { display: grid; grid-template-columns: 130px 2fr 1.5fr 100px 140px 40px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
.nt-row:hover { background: #fbfcff; }
.nt-cell { padding: 6px 12px; display: flex; align-items: center; border-right: 1px solid transparent; position: relative; }
.nt-row:hover .nt-cell { border-right-color: #f1f5f9; }
.status-select { width: 100%; border-radius: 4px; padding: 4px 8px; font-size: 12px; font-weight: 600; border: none; cursor: pointer; appearance: none; text-align: center; }
.status-select.TODO { background: #fef9c3; color: #854d0e; }
.status-select.IN_PROGRESS { background: #e0f2fe; color: #0369a1; }
.status-select.DONE { background: #dcfce7; color: #15803d; }
.nt-input { width: 100%; border: none; background: transparent; outline: none; font-size: 14px; }
.assignee-display { display: flex; align-items: center; gap: 8px; width: 100%; position: relative; }
.assignee-select-full { position: absolute; inset: 0; opacity: 0; cursor: pointer; width: 100%; height: 100%; }
.assignee-text { font-size: 13px; color: #334155; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.priority-badge { border: none; border-radius: 4px; padding: 2px 6px; font-size: 12px; font-weight: 500; width: 100%; cursor: pointer; }
.priority-badge.HIGH { background: #fee2e2; color: #991b1b; }
.priority-badge.MEDIUM { background: #ffedd5; color: #9a3412; }
.priority-badge.LOW { background: #f3f4f6; color: #374151; }
.date-input-mini { border: none; background: transparent; font-size: 13px; color: #475569; width: 100%; }
.delete-mini { border: none; background: transparent; color: #cbd5e1; cursor: pointer; display: flex; align-items: center; justify-content: center; width: 100%; }
.delete-mini:hover { color: #ef4444; }
.nt-new-row { padding: 10px 12px; display: flex; align-items: center; gap: 6px; color: #64748b; font-size: 13px; cursor: pointer; }
.nt-new-row:hover { background: #f8fafc; color: #334155; }
.modal-footer { margin-top: 32px; display: flex; justify-content: flex-end; }
.save-btn { background: #0b2433; color: white; border: none; padding: 8px 24px; border-radius: 8px; font-weight: 600; cursor: pointer; }
</style>