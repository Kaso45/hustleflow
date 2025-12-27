<template>
  <div class="page-container">
    <PeoplePageHeader 
      title="Performance Review" 
      subtitle="AI-driven assessment and prediction models"
      btnText=""
      v-model="searchQuery"
    />

    <!-- 1. Employee Selector -->
    <div class="selector-wrapper">
      <div class="selector-label">Reviewing:</div>
      <div class="selector-scroll">
        <button 
          v-for="emp in filteredEmployees" 
          :key="emp.id"
          class="emp-pill"
          :class="{ active: selectedEmpId === emp.id }"
          @click="selectEmployee(emp)"
        >
          <BaseAvatar :name="emp.name" :size="20" />
          <span class="truncate">{{ emp.name }}</span>
        </button>
      </div>
    </div>

    <!-- MAIN CONTENT -->
    <div v-if="loadingEmployees" class="state-msg">Loading profiles...</div>
    <div v-else-if="!currentData && !loadingAi" class="state-msg">
      Select an employee to view details.
    </div>

    <div v-else class="dashboard-grid">
      <!-- 2. CARD 1: OVERALL SCORE & KPI (Giữ nguyên design cũ của bạn) -->
      <div class="card glass-card">
        <div class="card-header-row">
          <h3 class="card-title">Overall Performance</h3>
          <span class="ranking-badge">{{ currentData?.ranking }}</span>
        </div>

        <div class="score-content">
          <!-- Chart -->
          <div class="chart-wrapper">
            <svg viewBox="0 0 36 36" class="circular-chart">
              <path class="circle-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
              <path class="circle" 
                :stroke-dasharray="`${currentData?.overallScore || 0}, 100`"
                :class="getScoreColorClass(currentData?.overallScore)"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
              />
            </svg>
            <div class="score-num">
              {{ currentData?.overallScore }}
              <span class="score-label">Score</span>
            </div>
          </div>

          <!-- KPI List -->
          <div class="kpi-list">
            <div v-for="kpi in currentData?.kpis" :key="kpi.name" class="kpi-row">
              <div class="kpi-info">
                <span class="kpi-name">{{ kpi.name }}</span>
                <span class="kpi-val">{{ kpi.value }}%</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill" :style="{ width: kpi.value + '%', backgroundColor: getKPIColor(kpi.value) }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 3. CARD 2: AI INSIGHTS (NEW DESIGN: Dark Indigo Theme) -->
      <div class="card ai-card">
        <!-- Background Animation Layer -->
        <div class="ai-bg-animate"></div>
        
        <!-- Content Layer (Z-index cao hơn) -->
        <div class="ai-content-layer">
          <div class="ai-header">
            <Sparkles :size="14" class="text-indigo-300" />
            <span>Predictive Analysis</span>
          </div>

          <!-- Metrics Grid -->
          <div class="ai-grid">
            <!-- Metric 1: Risk -->
            <div class="ai-metric">
              <div class="aim-label">Retention Risk</div>
              <div class="aim-value">
                {{ getRiskLabel(currentData.prediction.churnProbability) }}
                <!-- Badge hiển thị % rủi ro -->
                <span class="aim-badge" :style="getRiskBadgeStyle(currentData.prediction.churnProbability)">
                  {{ currentData.prediction.churnProbability }}%
                </span>
              </div>
            </div>

            <!-- Metric 2: Growth -->
            <div class="ai-metric">
              <div class="aim-label">Growth Potential</div>
              <div class="aim-value text-indigo-100">
                {{ currentData.prediction.growthPotential }}
              </div>
            </div>
          </div>

          <!-- Keywords (Adapted to Dark Theme) -->
          <div class="ai-keywords">
            <span v-for="kw in currentData.prediction.keywords" :key="kw" class="ai-pill">
              {{ kw }}
            </span>
          </div>

          <!-- Summary Box -->
          <div class="ai-summary">
             "{{ currentData.prediction.summary }}"
          </div>
        </div>
      </div>

      <!-- 4. CARD 3: HISTORY (Giữ nguyên) -->
      <div class="card glass-card history-card">
        <div class="card-header-row mb-4">
          <h3 class="card-title">Assessment History</h3>
        </div>
        
        <div class="table-wrapper">
          <div class="h-row h-header">
            <div class="col-period">Period</div>
            <div class="col-reviewer">Reviewer</div>
            <div class="col-score text-right">Score</div>
            <div class="col-status text-right">Status</div>
          </div>
          <div v-for="h in currentData?.history" :key="h.period" class="h-row h-item">
            <div class="col-period font-semibold text-[#0b2433]">{{ h.period }}</div>
            <div class="col-reviewer text-gray-500">{{ h.reviewer }}</div>
            <div class="col-score text-right">
              <span class="score-badge" :class="getScoreColorClass(h.score)">{{ h.score }}</span>
            </div>
            <div class="col-status text-right">
              <span class="status-pill">Finalized</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Sparkles } from 'lucide-vue-next';
import employeeService from '@/services/employeeService';
import assessmentService from '@/services/assessmentService';
import PeoplePageHeader from '@/components/common/PeoplePageHeader.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';

// State
const employees = ref([]);
const currentData = ref(null);
const selectedEmpId = ref(null);
const loadingEmployees = ref(true);
const loadingAi = ref(false);
const searchQuery = ref('');

// Init
const initData = async () => {
  loadingEmployees.value = true;
  try {
    const res = await employeeService.getEmployees();
    employees.value = res.data || [];
    if (employees.value.length > 0) selectEmployee(employees.value[0]);
  } finally {
    loadingEmployees.value = false;
  }
};

const selectEmployee = async (emp) => {
  if (selectedEmpId.value === emp.id) return;
  selectedEmpId.value = emp.id;
  currentData.value = null; 
  loadingAi.value = true;
  try {
    const res = await assessmentService.getAssessmentByEmployee(emp.id, emp);
    currentData.value = res.data;
  } catch (e) { console.error(e); } 
  finally { loadingAi.value = false; }
};

const filteredEmployees = computed(() => {
  if (!searchQuery.value) return employees.value;
  return employees.value.filter(e => e.name.toLowerCase().includes(searchQuery.value.toLowerCase()));
});

// Stylings Helpers
const getScoreColorClass = (s) => s >= 85 ? 'text-teal' : (s >= 65 ? 'text-gold' : 'text-red');
const getKPIColor = (s) => s >= 85 ? '#5fd1c5' : (s >= 65 ? '#fcd34d' : '#f87171');

const getRiskLabel = (prob) => prob > 70 ? 'High' : (prob > 40 ? 'Medium' : 'Low');

// Hàm style mới cho badge trong thẻ tối màu (AI Card)
const getRiskBadgeStyle = (prob) => {
  if (prob > 70) return { background: 'rgba(239, 68, 68, 0.2)', color: '#fca5a5', border: '1px solid rgba(239, 68, 68, 0.4)' }; // Red
  if (prob > 40) return { background: 'rgba(245, 158, 11, 0.2)', color: '#fcd34d', border: '1px solid rgba(245, 158, 11, 0.4)' }; // Yellow
  return { background: 'rgba(16, 185, 129, 0.2)', color: '#6ee7b7', border: '1px solid rgba(16, 185, 129, 0.4)' }; // Green
};

onMounted(initData);
</script>

<style scoped>
.page-container { padding: 24px; background-color: #F3F7F9; min-height: 100vh; font-family: 'Inter', sans-serif; color: #0b2433; }

/* Selector Scroll */
.selector-wrapper { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
.selector-label { font-size: 13px; font-weight: 600; color: #64748b; }
.selector-scroll { display: flex; gap: 8px; overflow-x: auto; padding-bottom: 4px; flex: 1; }
.emp-pill {
  display: flex; align-items: center; gap: 8px; padding: 6px 14px 6px 6px;
  background: #fff; border-radius: 30px; border: 1px solid transparent;
  cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0,0,0,0.02);
  color: #64748b; font-size: 13px; font-weight: 500; min-width: max-content;
}
.emp-pill:hover { background: #f8fafc; border-color: #e2e8f0; }
.emp-pill.active { background: #fff; color: #0b2433; border-color: #cbd5e1; box-shadow: 0 4px 8px rgba(0,0,0,0.06); font-weight: 600; }

/* LAYOUT GRID */
.dashboard-grid { 
  display: grid; 
  grid-template-columns: 1fr 1fr; 
  gap: 20px; 
}
.card { border-radius: 16px; overflow: hidden; position: relative; transition: all 0.3s ease; }

/* --- CARD 1: GLASS CARD (Giữ nguyên) --- */
.glass-card { 
  background: #ffffff; 
  border: 1px solid rgba(0,0,0,0.03); 
  box-shadow: 0 6px 18px rgba(10, 20, 36, 0.04); 
  padding: 24px; 
  display: flex; flex-direction: column;
}
.card-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.card-title { font-size: 16px; font-weight: 700; color: #0b2433; letter-spacing: -0.01em; margin: 0; }
.ranking-badge { background: #f1f5f9; color: #475569; padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 600; text-transform: uppercase; }
.score-content { display: flex; align-items: center; gap: 32px; flex: 1; }
.chart-wrapper { position: relative; width: 130px; height: 130px; flex-shrink: 0; }
.circular-chart { display: block; margin: 0 auto; max-width: 100%; max-height: 100%; }
.circle-bg { fill: none; stroke: #f1f5f9; stroke-width: 2; }
.circle { fill: none; stroke-width: 2.5; stroke-linecap: round; animation: progress 1s ease-out forwards; }
.score-num { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); display: flex; flex-direction: column; align-items: center; font-size: 32px; font-weight: 700; color: #0b2433; line-height: 1; }
.score-label { font-size: 11px; color: #94a3b8; font-weight: 500; text-transform: uppercase; margin-top: 2px; }
.text-teal { stroke: #5fd1c5; color: #5fd1c5; }
.text-gold { stroke: #fcd34d; color: #fbbf24; }
.text-red { stroke: #fca5a5; color: #f87171; }
.kpi-list { flex: 1; display: flex; flex-direction: column; gap: 10px; }
.kpi-row { font-size: 13px; }
.kpi-info { display: flex; justify-content: space-between; margin-bottom: 4px; font-weight: 500; color: #475569; }
.kpi-val { color: #0b2433; font-weight: 600; }
.progress-track { height: 6px; background: #f1f5f9; border-radius: 3px; overflow: hidden; }
.progress-fill { height: 100%; border-radius: 3px; width: 0; animation: slideRight 1s ease-out forwards; }

/* --- CARD 2: AI INSIGHTS (NEW DESIGN) --- */
.ai-card { 
  background: #01444e; /* Dark Indigo */
  color: white; 
  padding: 28px; 
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 30px rgba(79, 70, 229, 0.25);
  display: flex; flex-direction: column;
}
/* Animation nền */
.ai-bg-animate {
  position: absolute; top: -50%; left: -50%; width: 200%; height: 200%;
  background: radial-gradient(circle at 50% 50%, rgba(129, 140, 248, 0.25), transparent 60%);
  animation: pulse 6s infinite ease-in-out;
  z-index: 0;
  pointer-events: none;
}
/* Wrapper nội dung để nổi lên trên nền */
.ai-content-layer {
  position: relative;
  z-index: 1;
  display: flex; flex-direction: column; height: 100%;
}
.ai-header { 
  display: flex; align-items: center; gap: 8px; 
  font-weight: 700; color: #ffffff; text-transform: uppercase; 
  font-size: 12px; margin-bottom: 24px; letter-spacing: 0.5px;
}
.ai-grid { display: flex; gap: 40px; margin-bottom: 24px; }
.ai-metric { display: flex; flex-direction: column; gap: 4px; }
.aim-label { font-size: 12px; color: #c7d2fe; opacity: 0.8; font-weight: 500; text-transform: uppercase; }
.aim-value { font-size: 28px; font-weight: 700; display: flex; align-items: center; gap: 10px; color: white; letter-spacing: -0.5px; }
.aim-badge { 
  font-size: 12px; padding: 2px 8px; border-radius: 6px; 
  font-weight: 600; vertical-align: middle; line-height: 1.4;
}
.ai-keywords { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px; }
.ai-pill { 
  background: rgba(255,255,255,0.1); border: 1px solid rgba(255,255,255,0.15); 
  padding: 4px 12px; border-radius: 20px; font-size: 12px; color: #e0e7ff; 
  backdrop-filter: blur(4px); transition: all 0.2s;
}
.ai-pill:hover { background: rgba(255,255,255,0.2); }
.ai-summary { 
  background: rgba(255,255,255,0.08); padding: 16px; border-radius: 12px; 
  font-style: italic; font-size: 14px; color: #e0e7ff; line-height: 1.6; 
  backdrop-filter: blur(10px); border-left: 3px solid #43ffe0; margin-top: auto;
}

/* --- CARD 3: HISTORY (Giữ nguyên) --- */
.history-card { grid-column: span 2; padding: 0; }
.history-card .card-header-row { padding: 20px 24px 0 24px; margin-bottom: 12px; }
.table-wrapper { width: 100%; font-size: 13px; }
.h-row { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; padding: 12px 24px; border-bottom: 1px solid #f1f5f9; align-items: center; }
.h-header { background: #f8fafc; font-weight: 600; color: #94a3b8; font-size: 11px; text-transform: uppercase; border-top: 1px solid #f1f5f9; }
.h-item:last-child { border-bottom: none; }
.h-item:hover { background-color: #fcfcfc; }
.score-badge { font-weight: 700; font-family: 'Inter', monospace; }
.status-pill { background: #ecfdf5; color: #006b4d; padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 600; }
.state-msg { text-align: center; padding: 60px; color: #94a3b8; font-style: italic; grid-column: span 2; }

/* Animations */
@keyframes progress { from { stroke-dasharray: 0 100; } }
@keyframes slideRight { from { width: 0; } }
@keyframes pulse { 0% { transform: scale(1); opacity: 0.5; } 50% { transform: scale(1.1); opacity: 0.7; } 100% { transform: scale(1); opacity: 0.5; } }
</style>