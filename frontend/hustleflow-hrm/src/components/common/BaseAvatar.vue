<template>
  <div :style="avatarStyle" :class="['base-avatar', sizeClass]" :title="name">
    <span class="avatar-text">{{ initials }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  name: { type: String, default: '' },
  size: { type: Number, default: 36 }
});

// initials (2 letters)
const initials = computed(() => {
  if (!props.name) return '';
  const parts = props.name.trim().split(/\s+/);
  const first = parts[0]?.[0] ?? '';
  const last = parts.length > 1 ? parts[parts.length - 1][0] : (parts[0]?.[1] ?? '');
  return (first + last).toUpperCase();
});

// stable hue from string
function nameToHue(name) {
  let h = 0;
  for (let i = 0; i < name.length; i++) {
    h = (h * 31 + name.charCodeAt(i)) % 360;
  }
  return h;
}

// pastel cold-tone gradient
const avatarStyle = computed(() => {
  const h = nameToHue(props.name || 'anon');
  const coolHue = ((h % 100) + 180) % 360;
  const size = props.size;
  return {
    width: `${size}px`,
    height: `${size}px`,
    borderRadius: "50%",
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    background: `linear-gradient(135deg, hsl(${coolHue} 70% 90%), hsl(${(coolHue + 25) % 360} 70% 86%))`,
    boxShadow: "0 2px 6px rgba(8,16,30,0.06)",
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
  font-weight: 600;
  color: #0b2433;
  user-select: none;
}

.avatar-text {
  font-size: 12px;
  line-height: 1;
}

.base-avatar.md .avatar-text { font-size: 12px; }
.base-avatar.sm .avatar-text { font-size: 11px; }
.base-avatar.lg .avatar-text { font-size: 14px; }
</style>
