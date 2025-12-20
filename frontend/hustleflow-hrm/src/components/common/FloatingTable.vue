<template>
  <div class="floating-table-container">
    <!-- Header Row -->
    <div class="table-header-row" :style="{ gridTemplateColumns: gridColumns }">
      <slot name="header"></slot>
    </div>

    <!-- Body Rows -->
    <div class="table-body">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  gridColumns: { type: String, required: true } 
});
</script>

<style scoped>
.floating-table-container { 
  display: flex; 
  flex-direction: column; 
  gap: 10px; /* Khoảng cách dọc giữa các hàng */
}

.table-header-row {
  display: grid; 
  align-items: center; /* Căn giữa theo chiều dọc cho header */
  padding: 0 20px; 
  margin-bottom: 4px;
  gap: 10px; 
  

  font-size: 11px; 
  font-weight: 700; 
  color: #94a3b8;
  text-transform: uppercase; 
  letter-spacing: 0.8px;
  font-family: 'Inter', sans-serif;
}

.table-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>