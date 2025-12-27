<template>
  <div class="auth-wrapper">

    <!-- LEFT PANEL -->
    <div 
      class="left-panel" 
      :class="{ moveToRight: isRegister }"
    >
      <HologramGradient />
    </div>

    <!-- RIGHT PANEL -->
    <div 
      class="right-panel"
      :class="{ moveToLeft: isRegister }"
    >
      <transition name="fade">
        <router-view />
      </transition>
    </div>

  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import HologramGradient from '../../components/common/HologramGradient.vue'

const route = useRoute()

// Khi route là Register → true
const isRegister = computed(() => route.name === 'Register')
</script>

<style scoped>
.auth-wrapper {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  display: flex;
}

/* ---------------- LEFT PANEL ---------------- */
.left-panel {
  position: absolute;
  left: 0;
  width: 50%;
  height: 100%;
  transition: transform 0.8s cubic-bezier(.6,.1,.3,1.0);
}

/* Khi sang Register → hologram trượt sang phải */
.moveToRight {
  transform: translateX(100%);
}

/* ---------------- RIGHT PANEL ---------------- */
.right-panel {
  position: absolute;
  right: 0;
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.8s cubic-bezier(.6,.1,.3,1.0);
  background: white;
  z-index: 10;
}

/* Khi sang Register → form trượt sang trái */
.moveToLeft {
  transform: translateX(-100%);
}

/* ---------------- ANIMATION ---------------- */
.fade-enter-active,
.fade-leave-active {
  transition: opacity .3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
