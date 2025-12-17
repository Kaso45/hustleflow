<template>
  <table class="timesheet-table">
    <thead>
      <tr>
        <th>Employee</th>
        <th>Clock In</th>
        <th>Clock Out</th>
        <th>Date</th>
        <th>Status</th>
      </tr>
    </thead>

    <tbody>
      <tr v-for="row in items" :key="row.id">
        <td>{{ row.employeeName }}</td>
        <td>{{ row.checkIn || '-' }}</td>
        <td>{{ row.checkOut || '-' }}</td>
        <td>{{ formatDate(row.date) }}</td>
        <td>
          <span
            class="status-pill"
            :class="row.status === 'ON_TIME' ? 'on' : 'late'"
          >
            {{ row.status === 'ON_TIME' ? 'On Time' : 'Late' }}
          </span>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
defineProps({ items: Array })

function formatDate(d) {
  return new Date(d).toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  })
}
</script>

<style scoped>
.timesheet-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 10px;
}

th {
  text-align: left;
  padding: 12px;
  color: #6b7280;
}

td {
  padding: 14px;
  background: #fff;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.status-pill {
  padding: 6px 12px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 13px;
  display: inline-flex;
  align-items: center;      
  justify-content: center;  
  gap: 6px;
  width: 100px;
}


.status-pill.on {
  background: #eaffea;
  color: #1a7f37;
}

.status-pill.late {
  background: #ffeaea;
  color: #b42318;
}
</style>
