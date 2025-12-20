<template>
  <div class="df-summary-card" :style="{ '--accent-gradient': color || defaultGradient }">
    <div class="icon-wrap" :style="{ backgroundImage: color || defaultGradient }">
      <component :is="icon" :size="20" stroke-width="1.8" />
    </div>

    <div class="info">
      <div class="title">{{ title }}</div>
      <div class="value">{{ value }}</div>
      <div v-if="subtitle" class="subtitle">{{ subtitle }}</div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  icon: { type: [Object, Function], default: null },
  title: { type: String, default: '' },
  value: { type: [String, Number], default: 0 },
  subtitle: { type: String, default: '' },
  color: { type: String, default: '' }
});

const defaultGradient = 'linear-gradient(135deg,#BFFFE0,#E8FBFF)'; // Aqua Frost
</script>

<style scoped>
.df-summary-card{
  position: relative;
  display:flex;
  align-items:center;
  gap:16px;
  padding: 14px 18px;
  border-radius: 2cm; /* vuông vức nhẹ */
  background: rgba(255,255,255,0.04);
  border: 0.5px solid rgba(255,255,255,0.06); /* glass-like */

  cursor: default;
}

/* noise */
.df-summary-card::after {
  content:"";
  position:absolute;
  inset:0;
  background:url('@/assets/noise.png');
  mix-blend-mode: overlay;
  opacity: 0.08;
  border-radius: inherit;
  pointer-events:none;
}

/* icon */
.icon-wrap{
  width:52px;
  height:52px;
  border-radius:8px;
  display:flex;
  align-items:center;
  justify-content:center;
  color: #004554;
  position:relative;
  background-size:cover;
  box-shadow: inset 0 0 10px rgba(255,255,255,0.06);
}

/* subtle neutral glow under icon — use neutral white so whole card không tinted */
.icon-wrap::before {
  content: "";
  position: absolute;
  inset: -6px;
  border-radius: inherit;
  /* neutral soft glow — NOT inherit gradient */
  background: radial-gradient(circle at center, rgba(255,255,255,0.12), rgba(255,255,255,0.02) 45%, transparent 60%);
  filter: blur(10px);
  opacity: 0.28;
  z-index: -1;
  pointer-events: none;
}

.info .title{
  font-size:13px;
  color: #8a98a7;
  margin-bottom:4px;
}

.info .value{
  font-size:20px;
  font-weight:700;
  color: #071028;
}

.info .subtitle{
  font-size:12px;
  color: #9aa6b2;
  margin-top:4px;
}
</style>
