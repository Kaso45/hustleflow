<template>
  <div class="people-header">
    <div>
      <h1 class="page-title">{{ title }}</h1>
      <p class="page-subtitle">{{ subtitle }}</p>
    </div>

    <div class="actions-wrapper">
      <!-- Search Input -->
      <div class="search-box">
        <!-- Dùng Component Search Icon thay vì text -->
        <Search class="search-icon" :size="16" />
        <input
          :value="modelValue"
          @input="$emit('update:modelValue', $event.target.value)"
          type="text"
          :placeholder="placeholder"
          class="custom-input"
        />
      </div>

      <!-- Add Button -->
      <button v-if="btnText" class="btn-primary" @click="$emit('add')">
        <!-- Dùng Component Plus Icon -->
        <Plus :size="18" stroke-width="2.5" />
        {{ btnText }}
      </button>
    </div>
  </div>
</template>

<script setup>
// Import Icon từ thư viện lucide-vue-next
import { Search, Plus } from "lucide-vue-next";

defineProps({
  title: String,
  subtitle: String,
  btnText: { type: String, default: "Add New" },
  placeholder: { type: String, default: "Search..." },
  modelValue: String, // Cho v-model search
});

defineEmits(["update:modelValue", "add"]);
</script>

<style scoped>
.people-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
  font-family: "Inter", sans-serif;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #0b2433;
  margin: 0;
  letter-spacing: -0.5px;
}

.page-subtitle {
  color: #64748b;
  font-size: 14px;
  margin-top: 4px;
}

.actions-wrapper {
  display: flex;
  gap: 16px;
}

/* --- Search Box Styles --- */
.search-box {
  position: relative;
  width: 260px;
}

/* Canh chỉnh icon Search nằm giữa theo chiều dọc */
.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8; /* Màu xám nhạt chuyên nghiệp */
  pointer-events: none; /* Click xuyên qua icon vào input */
  z-index: 1;
}

.custom-input {
  width: 100%;
  padding: 10px 12px 10px 38px; /* Padding trái lớn hơn để né icon */
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
  color: #0b2433;
}

.custom-input::placeholder {
  color: #94a3b8;
}

.custom-input:focus {
  background: #fff;
  border-color: #5fd1c5;
  box-shadow: 0 0 0 3px rgba(95, 209, 197, 0.2);
}

/* --- Button Styles --- */
.btn-primary {
  background-color: #5fd1c5;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(95, 209, 197, 0.4);
  display: flex;
  align-items: center;
  gap: 8px; /* Khoảng cách giữa icon cộng và chữ */
  transition: transform 0.1s;
  white-space: nowrap;
}

.btn-primary:hover {
  background-color: #4bc2b6;
}

.btn-primary:active {
  transform: scale(0.98);
}
</style>
