<template>
  <div :style="avatarStyle" :class="['base-avatar', sizeClass]" :title="name">
    <span class="avatar-text">{{ initials }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  name: { type: String, default: '' },
  size: { type: Number, default: 40 } // Mặc định to hơn chút (40px) cho giống design
});

const initials = computed(() => {
  if (!props.name) return '??';
  const parts = props.name.trim().split(/\s+/);
  const first = parts[0]?.[0] ?? '';
  const last = parts.length > 1 ? parts[parts.length - 1][0] : (parts[0]?.[1] ?? '');
  return (first + last).toUpperCase();
});

// Hàm tạo màu pastel đẹp hơn (Teal/Cyan base)
function getAvatarColor(name) {
  const colors = [
    { bg: '#c7e8f3', text: '#0e7490' }, // Cyan
    { bg: '#e3f6f5', text: '#0f766e' }, // Teal
    { bg: '#fee2e2', text: '#b91c1c' }, // Red
    { bg: '#ffedd5', text: '#c2410c' }, // Orange
    { bg: '#f3e8ff', text: '#7e22ce' }, // Purple
    { bg: '#dcfce7', text: '#15803d' }, // Green
    { bg: '#e0f2fe', text: '#0369a1' }, // Sky
  ];
  const index = name ? name.length % colors.length : 0;
  return colors[index];
}

const avatarStyle = computed(() => {
  const colorSet = getAvatarColor(props.name || 'User');
  const size = props.size;
  
  return {
    width: `${size}px`,
    height: `${size}px`,
    // Design System: Squircle radius (approx 25-30% of size)
    borderRadius: `${size * 0.28}px`, 
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colorSet.bg,
    color: colorSet.text,
    boxShadow: "0 2px 5px rgba(0,0,0,0.03)",
    flexShrink: 0, // Quan trọng: Chống bị méo khi nằm trong flex
  };
});

const sizeClass = computed(() => {
  if (props.size <= 28) return "sm";
  if (props.size >= 48) return "lg";
  return "md";
});
</script>

<style scoped>
.base-avatar {
  font-family: 'Inter', sans-serif;
  font-weight: 700;
  user-select: none;
  transition: transform 0.2s;
}

/* Hover effect nhẹ */
.base-avatar:hover {
  transform: scale(1.05);
}

.avatar-text {
  line-height: 1;
}

.base-avatar.sm .avatar-text { font-size: 10px; }
.base-avatar.md .avatar-text { font-size: 14px; }
.base-avatar.lg .avatar-text { font-size: 18px; }
</style>