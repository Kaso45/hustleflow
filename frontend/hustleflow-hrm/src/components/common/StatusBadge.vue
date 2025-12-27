<template>
  <span :class="['badge', status.toLowerCase()]">
    {{ label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ status: String })

const label = computed(() =>
  props.status === 'ON_TIME'
    ? 'On Time'
    : props.status === 'LATE'
    ? 'Late'
    : props.status
)
</script>

<style scoped>
.badge {
  padding: 6px 12px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 13px;
  width: 50px;
}

.on_time {
  background: #e8ffe8;
  color: #1a7f37;
}

.late {
  background: #ffe8e8;
  color: #b42318;
}
</style>
