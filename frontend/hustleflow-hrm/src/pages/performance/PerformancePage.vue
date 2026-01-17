<template>
  <div class="page-container">
    <PeoplePageHeader
      title="Performance Review"
      subtitle="Run AI-backed reviews and view saved results"
      btnText=""
      placeholder="Search employees..."
      v-model="searchQuery"
    />

    <div
      class="filter-section"
      style="margin-bottom: 12px; display: flex; gap: 8px"
    >
      <button
        class="filter-pill"
        :class="{ active: activeTab === 'employees' }"
        @click="switchTab('employees')"
      >
        Current employees
      </button>
      <button
        class="filter-pill"
        :class="{ active: activeTab === 'results' }"
        @click="switchTab('results')"
      >
        Review results
      </button>
    </div>

    <div v-if="activeTab === 'employees'">
      <div v-if="loadingEmployees" class="state-msg">Loading employees...</div>
      <div v-else>
        <FloatingTable gridColumns="3fr 1.5fr 1.5fr 120px">
          <template #header>
            <div>Employee</div>
            <div>Department</div>
            <div>Job Role</div>
            <div class="text-right">Action</div>
          </template>

          <div v-if="filteredEmployees.length === 0" class="state-msg">
            No employees found.
          </div>

          <template v-else>
            <div
              v-for="emp in filteredEmployees"
              :key="emp.id"
              class="row-card"
              :style="{ gridTemplateColumns: '3fr 1.5fr 1.5fr 120px' }"
            >
              <div class="col-info">
                <BaseAvatar :name="emp.name" :size="40" />
                <div class="info-text">
                  <div class="info-card-name">{{ emp.name }}</div>
                  <div class="info-card-meta">
                    {{ emp.empJobRole || "—" }} • Level
                    {{ emp.empJobLevel ?? "—" }}
                  </div>
                </div>
              </div>
              <div class="info-pill-stack">
                <span
                  v-for="token in deptTokens(emp)"
                  :key="token"
                  class="info-pill is-muted"
                >
                  {{ token }}
                </span>
              </div>
              <div class="info-stack">
                <span class="info-label">Job role</span>
                <span class="info-value">{{
                  emp.empJobRole || "Not set"
                }}</span>
              </div>
              <div class="actions-group">
                <button
                  class="btn-primary btn-small"
                  :disabled="!!reviewLoading[emp.id]"
                  @click.stop="handleReview(emp)"
                  :title="
                    reviewLoading[emp.id]
                      ? 'Running review'
                      : 'Run AI-backed review for this employee'
                  "
                >
                  <template v-if="reviewLoading[emp.id]">
                    <span class="btn-spinner" aria-hidden></span>
                    <span>Running...</span>
                  </template>
                  <template v-else>
                    <Sparkles :size="14" />
                    <span>Run review</span>
                  </template>
                </button>
              </div>
            </div>
            <div style="display: none"></div>
          </template>
        </FloatingTable>
      </div>
    </div>

    <div v-else>
      <div class="results-filter-bar">
        <div class="results-filter-label">Filter by employee</div>
        <select
          class="results-select"
          v-model="selectedResultsEmpId"
          @change="resetAndLoadResults()"
        >
          <option :value="null">All employees</option>
          <option v-for="e in employees" :key="e.id" :value="e.id">
            {{ e.name }}
          </option>
        </select>
      </div>

      <div class="results-grid">
        <div v-for="item in resultsItems" :key="item.id" class="result-card">
          <div class="rc-header">
            <div class="rc-emp">
              <BaseAvatar :name="employeeName(item.employeeId)" :size="36" />
              <div class="rc-name">{{ employeeName(item.employeeId) }}</div>
            </div>
            <div
              :class="[
                'score-badge',
                'rc-score-badge',
                scoreClass(item.performanceScore),
              ]"
            >
              <Sparkles :size="14" />
              <span>{{ item.performanceScore ?? "—" }}</span>
            </div>
          </div>

          <div class="rc-section">
            <div class="rc-section-title">
              <MessageSquare :size="14" />
              <span>Comments</span>
            </div>
            <div class="rc-comments">{{ item.comments || "—" }}</div>
          </div>

          <div class="rc-footer">
            <div class="rc-date">
              <Calendar :size="14" />
              <span>{{ formatDate(item.reviewDate) }}</span>
            </div>
          </div>
        </div>

        <div ref="resultsSentinel" style="height: 1px"></div>
        <div v-if="loadingResults" class="state-msg">Loading results...</div>
        <div
          v-else-if="!loadingResults && resultsItems.length === 0"
          class="state-msg"
        >
          No results found.
        </div>
        <div v-else-if="!resultsHasMore" class="state-msg">
          No more results.
        </div>
      </div>
    </div>
    <BaseModal
      :isOpen="showResultModal"
      title="ML Prediction Result"
      @close="closeReview"
    >
      <div style="display: flex; flex-direction: column; gap: 12px">
        <div>
          <div style="font-size: 12px; color: #64748b">Employee ID</div>
          <div style="font-weight: 700">{{ resultModalData?.employeeId }}</div>
        </div>
        <div>
          <div style="font-size: 12px; color: #64748b">Performance Score</div>
          <div style="font-weight: 700; text-transform: capitalize">
            {{ resultModalData?.performanceScore }}
          </div>
        </div>
        <div>
          <div style="font-size: 12px; color: #64748b">Review Date</div>
          <div style="font-weight: 700">
            {{ formatDate(resultModalData?.reviewDate) }}
          </div>
        </div>
        <div>
          <div style="font-size: 12px; color: #64748b">Comments</div>
          <div>{{ resultModalData?.comments }}</div>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import {
  ref,
  computed,
  onMounted,
  onBeforeUnmount,
  watch,
  nextTick,
} from "vue";
import employeeService from "@/services/employeeService";
import performanceReviewService from "@/services/performanceReviewService";
import PeoplePageHeader from "@/components/common/PeoplePageHeader.vue";
import BaseAvatar from "@/components/common/BaseAvatar.vue";
import FloatingTable from "@/components/common/FloatingTable.vue";
import BaseModal from "@/components/common/BaseModal.vue";
import { Sparkles, MessageSquare, Calendar } from "lucide-vue-next";

const activeTab = ref("employees");
const searchQuery = ref("");

// Employees
const employees = ref([]);
const loadingEmployees = ref(true);

const fetchEmployees = async () => {
  loadingEmployees.value = true;
  try {
    const res = await employeeService.getEmployees();
    employees.value = res.data || [];
  } finally {
    loadingEmployees.value = false;
  }
};

const filteredEmployees = computed(() => {
  if (!searchQuery.value) return employees.value;
  return employees.value.filter((e) =>
    e.name?.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const deptTokens = (emp) => {
  const src = emp.empDepartment?.departmentName || emp.empDepartment || "—";
  return src.split(/\s+/).filter(Boolean);
};

// Review action per employee
const reviewLoading = ref({});
const showResultModal = ref(false);
const resultModalData = ref(null);

const handleReview = async (emp) => {
  if (reviewLoading.value[emp.id]) return;
  reviewLoading.value = { ...reviewLoading.value, [emp.id]: true };
  try {
    const res = await performanceReviewService.createReview(emp.id);
    const data = res.data;
    resultModalData.value = data;
    showResultModal.value = true;
    // Optimistically add to results list if user switches to results
    resultsItems.value = [data, ...resultsItems.value];
  } catch (e) {
    console.error(e);
    alert("Prediction failed. Please try again.");
  } finally {
    reviewLoading.value = { ...reviewLoading.value, [emp.id]: false };
  }
};

const closeReview = () => {
  showResultModal.value = false;
  resultModalData.value = null;
};

// Results (infinite scroll)
const selectedResultsEmpId = ref(null);
const resultsItems = ref([]);
const resultsPage = ref(0);
const resultsSize = 20;
const resultsHasMore = ref(true);
const loadingResults = ref(false);
const resultsSentinel = ref(null);
let observer;

const employeeName = (id) =>
  employees.value.find((e) => e.id === id)?.name || `#${id}`;

const fetchResultsNextPage = async () => {
  if (loadingResults.value || !resultsHasMore.value) return;
  loadingResults.value = true;
  try {
    const res = await performanceReviewService.getReviews({
      employeeId: selectedResultsEmpId.value,
      page: resultsPage.value,
      size: resultsSize,
    });
    const pageData = res.data || {};
    const content = pageData.content || [];
    resultsItems.value = [...resultsItems.value, ...content];
    const totalPages = pageData.totalPages ?? 0;
    const number = pageData.number ?? 0;
    resultsHasMore.value = number + 1 < totalPages;
    resultsPage.value = number + 1;
  } catch (e) {
    console.error(e);
  } finally {
    loadingResults.value = false;
  }
};

const resetAndLoadResults = async () => {
  resultsItems.value = [];
  resultsPage.value = 0;
  resultsHasMore.value = true;
  await fetchResultsNextPage();
};

const setupObserver = () => {
  if (observer) observer.disconnect();
  observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting && activeTab.value === "results") {
        fetchResultsNextPage();
      }
    });
  });
  if (resultsSentinel.value) observer.observe(resultsSentinel.value);
};

const switchTab = async (tab) => {
  activeTab.value = tab;
  if (tab === "results") {
    await resetAndLoadResults();
    await nextTick();
    setupObserver();
  }
};

watch(selectedResultsEmpId, () => {
  if (activeTab.value === "results") resetAndLoadResults();
});

onMounted(async () => {
  await fetchEmployees();
  setupObserver();
});

onBeforeUnmount(() => {
  if (observer) observer.disconnect();
});

// Utilities
const formatDate = (val) => {
  if (!val) return "";
  try {
    const d = new Date(val);
    return d.toLocaleString();
  } catch (e) {
    return String(val);
  }
};

// Return a CSS class for score coloring
const scoreClass = (score) => {
  const s = Number(score);
  if (!Number.isFinite(s)) return "score-unknown";
  if (s >= 75) return "score-high";
  if (s >= 50) return "score-medium";
  return "score-low";
};
</script>

<style scoped>
.page-container {
  padding: 24px;
  background-color: #f3f7f9;
  min-height: 100vh;
  font-family: "Inter", sans-serif;
  color: #0b2433;
}

/* Filters (tabs) */
.filter-pill {
  border: none;
  background: #ffffff;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
  transition: all 0.2s;
  white-space: nowrap;
}
.filter-pill:hover {
  background: #f1f5f9;
  color: #0f766e;
}
.filter-pill.active {
  background: #0b2433;
  color: #ffffff;
}

/* Selector Scroll */
.selector-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}
.selector-label {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
}
.selector-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
  flex: 1;
}
.emp-pill {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px 6px 6px;
  background: #fff;
  border-radius: 30px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
  min-width: max-content;
}
.emp-pill:hover {
  background: #f8fafc;
  border-color: #e2e8f0;
}
.emp-pill.active {
  background: #fff;
  color: #0b2433;
  border-color: #cbd5e1;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.06);
  font-weight: 600;
}

/* Row actions */
.actions-group {
  display: flex;
  justify-content: flex-end;
}
.text-right {
  text-align: right;
}

/* Primary button used in row actions */
.btn-primary {
  background-color: #5fd1c5;
  color: #ffffff;
  border: none;
  padding: 10px 16px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(95, 209, 197, 0.35);
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: transform 0.1s, background-color 0.2s;
}
.btn-primary:hover {
  background-color: #4bc2b6;
}
.btn-primary:active {
  transform: translateY(1px);
}

/* LAYOUT GRID */
.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.card {
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease;
}

/* --- CARD 1: GLASS CARD (Giữ nguyên) --- */
.glass-card {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.03);
  box-shadow: 0 6px 18px rgba(10, 20, 36, 0.04);
  padding: 24px;
  display: flex;
  flex-direction: column;
}
.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #0b2433;
  letter-spacing: -0.01em;
  margin: 0;
}
.ranking-badge {
  background: #f1f5f9;
  color: #475569;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}
.score-content {
  display: flex;
  align-items: center;
  gap: 32px;
  flex: 1;
}
.chart-wrapper {
  position: relative;
  width: 130px;
  height: 130px;
  flex-shrink: 0;
}
.circular-chart {
  display: block;
  margin: 0 auto;
  max-width: 100%;
  max-height: 100%;
}
.circle-bg {
  fill: none;
  stroke: #f1f5f9;
  stroke-width: 2;
}
.circle {
  fill: none;
  stroke-width: 2.5;
  stroke-linecap: round;
  animation: progress 1s ease-out forwards;
}
.score-num {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 32px;
  font-weight: 700;
  color: #0b2433;
  line-height: 1;
}
.score-label {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
  text-transform: uppercase;
  margin-top: 2px;
}
.text-teal {
  stroke: #5fd1c5;
  color: #5fd1c5;
}
.text-gold {
  stroke: #fcd34d;
  color: #fbbf24;
}
.text-red {
  stroke: #fca5a5;
  color: #f87171;
}
.kpi-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.kpi-row {
  font-size: 13px;
}
.kpi-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-weight: 500;
  color: #475569;
}
.kpi-val {
  color: #0b2433;
  font-weight: 600;
}
.progress-track {
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  border-radius: 3px;
  width: 0;
  animation: slideRight 1s ease-out forwards;
}

/* --- CARD 2: AI INSIGHTS (NEW DESIGN) --- */
.ai-card {
  background: #01444e; /* Dark Indigo */
  color: white;
  padding: 28px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 30px rgba(79, 70, 229, 0.25);
  display: flex;
  flex-direction: column;
}
/* Animation nền */
.ai-bg-animate {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle at 50% 50%,
    rgba(129, 140, 248, 0.25),
    transparent 60%
  );
  animation: pulse 6s infinite ease-in-out;
  z-index: 0;
  pointer-events: none;
}
/* Wrapper nội dung để nổi lên trên nền */
.ai-content-layer {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.ai-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: #ffffff;
  text-transform: uppercase;
  font-size: 12px;
  margin-bottom: 24px;
  letter-spacing: 0.5px;
}
.ai-grid {
  display: flex;
  gap: 40px;
  margin-bottom: 24px;
}
.ai-metric {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.aim-label {
  font-size: 12px;
  color: #c7d2fe;
  opacity: 0.8;
  font-weight: 500;
  text-transform: uppercase;
}
.aim-value {
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  letter-spacing: -0.5px;
}
.aim-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
  font-weight: 600;
  vertical-align: middle;
  line-height: 1.4;
}
.ai-keywords {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}
.ai-pill {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  color: #e0e7ff;
  backdrop-filter: blur(4px);
  transition: all 0.2s;
}
.ai-pill:hover {
  background: rgba(255, 255, 255, 0.2);
}
.ai-summary {
  background: rgba(255, 255, 255, 0.08);
  padding: 16px;
  border-radius: 12px;
  font-style: italic;
  font-size: 14px;
  color: #e0e7ff;
  line-height: 1.6;
  backdrop-filter: blur(10px);
  border-left: 3px solid #43ffe0;
  margin-top: auto;
}

/* --- CARD 3: HISTORY (Giữ nguyên) --- */
.history-card {
  grid-column: span 2;
  padding: 0;
}
.history-card .card-header-row {
  padding: 20px 24px 0 24px;
  margin-bottom: 12px;
}
.table-wrapper {
  width: 100%;
  font-size: 13px;
}
.h-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  padding: 12px 24px;
  border-bottom: 1px solid #f1f5f9;
  align-items: center;
}
.h-header {
  background: #f8fafc;
  font-weight: 600;
  color: #94a3b8;
  font-size: 11px;
  text-transform: uppercase;
  border-top: 1px solid #f1f5f9;
}
.h-item:last-child {
  border-bottom: none;
}
.h-item:hover {
  background-color: #fcfcfc;
}
.score-badge {
  font-weight: 700;
  font-family: "Inter", monospace;
}
.status-pill {
  background: #ecfdf5;
  color: #006b4d;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}
.state-msg {
  text-align: center;
  padding: 60px;
  color: #94a3b8;
  font-style: italic;
  grid-column: span 2;
}

/* Buttons */
.btn-small {
  padding: 8px 12px;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border-radius: 10px;
}
.btn-spinner {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-top-color: rgba(255, 255, 255, 0.9);
  animation: spin 0.8s linear infinite;
  display: inline-block;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Results filter bar */
.results-filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: flex-end;
  margin-bottom: 12px;
}
.results-filter-label {
  font-size: 13px;
  color: #64748b;
}
.results-select {
  padding: 8px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #fff;
  color: #0b2433;
}

/* Results cards grid */
.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 14px;
}

/* Results card */
.result-card {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.03);
  box-shadow: 0 6px 18px rgba(10, 20, 36, 0.04);
  padding: 16px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.rc-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.rc-emp {
  display: flex;
  align-items: center;
  gap: 10px;
}
.rc-name {
  font-weight: 700;
  color: #0b2433;
}
.rc-score-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.rc-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.rc-section-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  text-transform: uppercase;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
}
.rc-comments {
  color: #0b2433;
  line-height: 1.6;
}

.rc-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f1f5f9;
}
.rc-date {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
}

.muted-label {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 6px;
}
.muted-txt {
  color: #475569;
  font-size: 13px;
}

.score-badge {
  font-weight: 700;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 13px;
  min-width: 56px;
  text-align: center;
}
.score-high {
  background: #ecfdf5;
  color: #006b4d;
  border: 1px solid rgba(0, 107, 77, 0.08);
}
.score-medium {
  background: #fffbeb;
  color: #92400e;
  border: 1px solid rgba(146, 64, 14, 0.06);
}
.score-low {
  background: #fff1f2;
  color: #9f1239;
  border: 1px solid rgba(159, 18, 57, 0.06);
}
.score-unknown {
  background: #f1f5f9;
  color: #475569;
}

/* Animations */
@keyframes progress {
  from {
    stroke-dasharray: 0 100;
  }
}
@keyframes slideRight {
  from {
    width: 0;
  }
}
@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.7;
  }
  100% {
    transform: scale(1);
    opacity: 0.5;
  }
}
</style>
