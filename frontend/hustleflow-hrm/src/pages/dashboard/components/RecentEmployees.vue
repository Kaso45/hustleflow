<template>
  <div class="holo-card card">
    <h4>Recent Employees Added</h4>
    <ul>
      <!-- API trả về 'id' làm key, không phải EmpNumber -->
      <li v-for="emp in employees" :key="emp.id" class="recent-item">
        <div class="left">
          <!-- API field: name -->
          <div class="name">{{ emp.name || '—' }}</div>
          
          <!-- API fields: id, empDepartment -->
          <div class="meta">
            {{ emp.id ? 'ID: ' + emp.id : '' }} • {{ emp.empDepartment || '—' }}
          </div>
        </div>

        <div class="right">
          <!-- LƯU Ý QUAN TRỌNG:
               API EmployeeResponse hiện KHÔNG có trường ngày (createdAt/Hire_Date).
               Tạm thời hiển thị JobRole hoặc phải yêu cầu Backend thêm trường hireDate.
               Dưới đây mình fallback sang JobRole để không bị lỗi.
          -->
          <span class="job-role">{{ emp.empJobRole }}</span>
        </div>
      </li>

      <li v-if="!employees || employees.length === 0" class="placeholder">
        No recent employees
      </li>
    </ul>
  </div>
</template>

<script setup>
defineProps({ 
  employees: { type: Array, default: () => [] } 
});
// Đã xóa hàm formatDate vì API chưa có field ngày để format
</script>

<style scoped>
/* Giữ nguyên style cũ của bạn */
.card {
  padding: 16px;
  border-radius: 16px; /* Nếu parent đã bo góc thì cái này optional */
  border: none;
  background: transparent;
  height: 100%; /* Đảm bảo stretch nếu parent dùng grid */
}
.recent-item { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; /* Căn giữa dọc */
  padding: 10px 0; 
  border-bottom: 1px solid rgba(255,255,255,0.1); /* Dùng border mờ cho hợp theme dark */
}
.recent-item:last-child { border-bottom: none; }

.name { 
  font-weight: 600; 
  font-size: 14px;
  color: var(--text-strong, #071028); 
}

.meta { 
  font-size: 12px; 
  color: var(--text-weak, #777); 
  margin-top: 2px;
}

.job-role {
  font-size: 12px;
  background: rgba(0,0,0,0.05);
  padding: 2px 8px;
  border-radius: 10px;
  color: #555;
}

.placeholder { color: #999; font-size: 13px; font-style: italic; margin-top:10px;}
</style>