<template>
  <div>
    <AppPageHeader
      title="Leave Requests"
      subtitle="Submit and review leave requests"
    />

    <v-row class="mb-4">
      <!-- NEW LEAVE FORM -->
      <v-col cols="12" md="4">
        <v-card class="pa-4 leave-card">

          <div class="text-subtitle-1 mb-2">New Leave Request</div>
          <p class="text-body-2">Skeleton form for new leave request.</p>
          <v-text-field
  v-model="newLeave.leaveType"
  label="Leave Type"
  variant="outlined"
  density="compact"
/>

<v-text-field
  v-model="newLeave.startDate"
  label="Start Date"
  variant="outlined"
  density="compact"
/>

<v-text-field
  v-model="newLeave.endDate"
  label="End Date"
  variant="outlined"
  density="compact"
/>

<v-textarea
  v-model="newLeave.reason"
  label="Reason"
  variant="outlined"
  density="compact"
/>

<v-btn
  class="mt-3 submit-btn"
  @click="submitNewLeave"
>
  Submit
</v-btn>

    
        </v-card>
      </v-col>

      <!-- LIST -->
      <v-col cols="12" md="8">
        <v-card class="pa-4 leave-card">


          <div class="d-flex justify-space-between align-center mb-3">
            <div class="text-subtitle-1">Requests List</div>
            <v-select
              v-model="filterStatus"
              :items="['All','PENDING','APPROVED','REJECTED']"
              density="compact"
              hide-details
              style="max-width:180px"
            />
          </div>

          <PastelTable>
            <template #thead>
              <tr>
                <th>Employee</th>
                <th>Type</th>
                <th>From</th>
                <th>To</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </template>

            <!-- DATA ROW -->
            <tr v-for="req in filteredLeaves" :key="req.id">
              <td>
                <div style="display:flex; align-items:center; gap:12px">
                  <BaseAvatar :name="req.employeeName" :size="40" />
                  <div style="display:flex; flex-direction:column">
                    <div style="font-weight:600">{{ req.employeeName }}</div>
                    <div style="font-size:12px; color:#6b7a82">#{{ req.employeeId }}</div>
                  </div>
                </div>
              </td>

              <td>{{ humanizeLeaveType(req.leaveType) }}</td>
              <td>{{ formatDate(req.startDate) }}</td>
              <td>{{ formatDate(req.endDate) }}</td>
              <td><StatusBadge :status="req.status" /></td>

              <td>
                <div style="display:flex; gap:8px">
                  <v-btn
  size="x-small"
  variant="text"
  class="action-approve"
  @click="changeLeaveStatus(req,'APPROVED')"
>
  Approve
</v-btn>

<v-btn
  size="x-small"
  variant="text"
  class="action-reject"
  @click="changeLeaveStatus(req,'REJECTED')"
>
  Reject
</v-btn>

                </div>
              </td>
            </tr>

            <tr v-if="loading">
              <td colspan="6" style="text-align:center; padding:18px">Loading...</td>
            </tr>

            <tr v-if="!loading && filteredLeaves.length === 0">
              <td colspan="6" style="text-align:center; padding:18px">No leave requests.</td>
            </tr>
          </PastelTable>

        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import AppPageHeader from '@/components/common/AppPageHeader.vue';
import PastelTable from '@/components/common/PastelTable.vue';
import BaseAvatar from '@/components/common/BaseAvatar.vue';
import StatusBadge from '@/components/common/StatusBadge.vue';

import dashboardService from '@/services/dashboard/dashboardService';
import apiClient from '@/services/apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

const leaveRequests = ref([]);
const loading = ref(false);
const filterStatus = ref("All");

const newLeave = ref({
  leaveType: "",
  startDate: "",
  endDate: "",
  reason: ""
});

// Helpers
function humanizeLeaveType(t) {
  if (!t) return "";
  const map = { ANNUAL: "Annual", SICK: "Sick", UNPAID: "Unpaid" };
  return map[t.toUpperCase()] || t;
}

function formatDate(d) {
  if (!d) return "-";
  return new Date(d).toLocaleDateString(undefined, {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

const filteredLeaves = computed(() => {
  if (filterStatus.value === "All") return leaveRequests.value;
  return leaveRequests.value.filter(
    r => r.status?.toUpperCase() === filterStatus.value
  );
});

// Load Leaves
async function loadLeaves() {
  loading.value = true;
  try {
    const data = await dashboardService.getLeaves();
    leaveRequests.value = data.map(x => ({
      id: x.id,
      employeeId: x.employeeId,
      employeeName: x.employeeName ?? `Employee ${x.employeeId}`,
      leaveType: x.leaveType,
      startDate: x.startDate,
      endDate: x.endDate,
      reason: x.reason,
      status: (x.status ?? "PENDING").toUpperCase(),
    }));
  } finally {
    loading.value = false;
  }
}

// Change status
async function changeLeaveStatus(req, newStatus) {
  const old = req.status;
  req.status = newStatus;

  try {
    if (!USE_MOCK_API)
      await apiClient.put(`/leaves/${req.id}/status`, { status: newStatus });
  } catch (e) {
    req.status = old;
  }
}

// Submit new leave
async function submitNewLeave() {
  const payload = {
    employeeId: 1,
    employeeName: "You",
    ...newLeave.value,
    status: "PENDING",
  };

  if (USE_MOCK_API) {
    const id = Math.max(0, ...leaveRequests.value.map(x => x.id || 0)) + 1;
    leaveRequests.value.unshift({ ...payload, id });
  } else {
    await apiClient.post("/leaves", payload);
    await loadLeaves();
  }

  newLeave.value = { leaveType: "", startDate:"", endDate:"", reason:"" };
}

onMounted(loadLeaves);
</script>
<style scoped>
.leave-card {
  border-radius: 14px;
  box-shadow: 0 4px 14px rgba(10,20,36,0.04);
}
.action-approve {
  color: #1a7f37;
  font-weight: 600;
}

.action-reject {
  color: #b42318;
  font-weight: 600;
}
.submit-btn {
  background: #e8ffe8;
  color: #1a7f37;
  font-weight: 600;
  text-transform: none;
}
.pending {
  background: #fff6e5;
  color: #b26a00;
}

.approved {
  background: #eaffea;
  color: #1a7f37;
}

.rejected {
  background: #ffeaea;
  color: #b42318;
}

</style>
