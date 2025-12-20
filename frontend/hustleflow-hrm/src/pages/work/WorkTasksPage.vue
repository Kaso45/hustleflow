<template>
  <div class="page-container">
    <PeoplePageHeader 
      title="All Tasks"
      subtitle="HR view: Monitor workload across employees"
      btnText="" 
      v-model="searchQuery"
    />

    <!-- Filter by Employee -->
    <div class="filter-bar">
      <div class="filter-label">Filter by Employee:</div>
      <div class="filter-scroll">
        <button class="filter-pill" :class="{ active: !selectedEmpId }" @click="selectedEmpId = null">All Employees</button>
        <button 
          v-for="emp in employees" :key="emp.id"
          class="filter-pill" :class="{ active: selectedEmpId === emp.id }"
          @click="selectedEmpId = emp.id"
        >
          <BaseAvatar :name="emp.name" :size="20" class="mr-2"/> {{ emp.name }}
        </button>
      </div>
    </div>

    <FloatingTable gridColumns="2fr 1.5fr 1.5fr 1fr 1fr">
      <template #header>
        <div>Task Title</div>
        <div>Assigned To</div>
        <div>Project</div>
        <div>Deadline</div>
        <div>Status</div>
      </template>

      <div v-if="loading" class="state-msg">Loading all tasks...</div>
      <div v-else-if="filteredTasks.length === 0" class="state-msg">No tasks found.</div>

      <div 
        v-else
        v-for="task in filteredTasks" 
        :key="task.id" 
        class="row-card"
        :style="{ gridTemplateColumns: '2fr 1.5fr 1.5fr 1fr 1fr' }"
      >
        <!-- Title -->
        <div class="font-medium text-[#0b2433]">{{ task.title }}</div>

        <!-- Assignee -->
        <div class="col-info">
          <BaseAvatar v-if="getEmployee(task.assigneeId)" :name="getEmployee(task.assigneeId).name" :size="32" />
          <div class="text-sm ml-3">
            <div class="font-semibold">{{ getEmployee(task.assigneeId)?.name || 'Unassigned' }}</div>
            <div class="text-xs text-gray-400">{{ getEmployee(task.assigneeId)?.empDepartment }}</div>
          </div>
        </div>

        <!-- Project Context -->
        <div class="text-sm text-blue-600 font-medium truncate">
          {{ getProject(task.projectId)?.projectName || 'Unknown Project' }}
        </div>

        <!-- Deadline -->
        <div class="text-sm text-gray-600 font-mono">{{ formatDate(task.deadline) }}</div>

        <!-- Status & Priority -->
        <div class="flex items-center gap-2">
          <span class="status-badge" :class="task.status === 'DONE' ? 'st-completed' : 'st-pending'">
            {{ task.status === 'DONE' ? 'Done' : task.status === 'IN_PROGRESS' ? 'In Prog' : 'Todo' }}
          </span>
          <span v-if="task.priority === 'HIGH'" class="w-2 h-2 rounded-full bg-red-500" title="High Priority"></span>
        </div>
      </div>
    </FloatingTable>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
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

const fetchData = async () => {
  loading.value = true;
  const [tRes, eRes, pRes] = await Promise.all([
    taskService.getTasks(),
    employeeService.getEmployees(),
    projectService.getProjects()
  ]);
  tasks.value = tRes.data || [];
  employees.value = eRes.data || [];
  projects.value = pRes.data || [];
  loading.value = false;
};

const getEmployee = (id) => employees.value.find(e => e.id === id);
const getProject = (id) => projects.value.find(p => p.id === id);
const formatDate = (d) => d ? new Date(d).toLocaleDateString('en-GB') : '--';

const filteredTasks = computed(() => {
  let result = tasks.value;
  if (selectedEmpId.value) result = result.filter(t => t.assigneeId === selectedEmpId.value);
  if (searchQuery.value) result = result.filter(t => t.title.toLowerCase().includes(searchQuery.value.toLowerCase()));
  return result;
});

onMounted(() => fetchData());
</script>

<style scoped>
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; }
.filter-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
.filter-label { font-size: 13px; font-weight: 600; color: #64748b; }
.filter-scroll { display: flex; gap: 8px; overflow-x: auto; padding-bottom: 4px; }
.filter-pill { border: none; background: #fff; padding: 6px 14px; border-radius: 20px; font-size: 13px; font-weight: 600; color: #64748b; cursor: pointer; box-shadow: 0 2px 4px rgba(0,0,0,0.02); transition: all 0.2s; white-space: nowrap; }
.filter-pill:hover { background: #f1f5f9; color: #0f766e; }
.filter-pill.active { background: #0b2433; color: #fff; }
.row-card { display: grid; align-items: center; background: #ffffff; border-radius: 12px; padding: 16px 20px; box-shadow: 0 2px 6px rgba(10, 20, 36, 0.04); margin-bottom: 10px; }
.col-info { display: flex; align-items: center; }
.status-badge { padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
.st-completed { background: #dcfce7; color: #16a34a; }
.st-pending { background: #fef9c3; color: #ca8a04; }
.state-msg { text-align: center; padding: 40px; color: #94a3b8; font-style: italic; }
</style>