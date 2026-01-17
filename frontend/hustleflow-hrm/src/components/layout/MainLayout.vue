<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      app
      :temporary="isOverlay"
      :scrim="isOverlay"
      class="hf-drawer"
      :width="drawerWidth"
    >
      <AppSidebar
        :show-close="isOverlay"
        @close="drawer = false"
        @navigate="handleAfterNavigate"
      />
    </v-navigation-drawer>

    <AppTopbar :content-offset="topbarOffset" @toggle-sidebar="toggleDrawer" />

    <v-main>
      <v-container fluid class="pa-4">
        <!-- Nơi render các page con: dashboard, people, attendance,... -->
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from "vue";
import AppSidebar from "./AppSidebar.vue";
import AppTopbar from "../common/AppTopbar.vue";

const getWindowWidth = () =>
  typeof window !== "undefined" ? window.innerWidth : 1920;

const viewportWidth = ref(getWindowWidth());
const drawer = ref(viewportWidth.value > 1024);

const isOverlay = computed(() => viewportWidth.value <= 1024);
let lastOverlay = isOverlay.value;

const drawerWidth = computed(() => {
  if (!isOverlay.value) {
    return 260;
  }
  const available = Math.max(220, viewportWidth.value - 72);
  return Math.min(280, available);
});

const toggleDrawer = () => {
  drawer.value = !drawer.value;
};

const topbarOffset = computed(() => {
  if (isOverlay.value || !drawer.value) {
    return 0;
  }
  return drawerWidth.value;
});

const handleResize = () => {
  viewportWidth.value = getWindowWidth();
  const overlayNow = isOverlay.value;

  if (!overlayNow && !drawer.value) {
    drawer.value = true;
  }

  if (overlayNow && !lastOverlay) {
    drawer.value = false;
  }

  lastOverlay = overlayNow;
};

const handleAfterNavigate = () => {
  if (isOverlay.value) {
    drawer.value = false;
  }
};

onMounted(() => {
  handleResize();
  if (typeof window !== "undefined") {
    window.addEventListener("resize", handleResize, { passive: true });
  }
});

onUnmounted(() => {
  if (typeof window !== "undefined") {
    window.removeEventListener("resize", handleResize);
  }
});
</script>

<style scoped>
.hf-drawer {
  border-right: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.96);
}

@media (max-width: 1366px) {
  .hf-drawer {
    box-shadow: 0 20px 50px rgba(15, 23, 42, 0.2);
  }
}

@media (max-width: 600px) {
  .hf-drawer {
    width: min(90vw, 280px) !important;
  }
}
</style>
