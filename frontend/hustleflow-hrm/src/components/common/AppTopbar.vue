<template>
  <v-app-bar
    app
    flat
    density="comfortable"
    class="hf-topbar"
    :style="topbarStyle"
  >
    <v-btn
      icon
      variant="text"
      @click="$emit('toggle-sidebar')"
      aria-label="Toggle navigation"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        fill="currentColor"
        viewBox="0 0 16 16"
      >
        <path
          d="M0 3a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2zm5-1v12h9a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1zM4 2H2a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h2z"
        />
      </svg>
    </v-btn>

    <v-toolbar-title class="text-subtitle-2 text-uppercase text-grey-darken-1">
      Workspace
    </v-toolbar-title>

    <v-spacer />

    <div class="topbar-actions">
      <v-btn
        color="primary"
        variant="elevated"
        class="logout-btn"
        @click="handleLogout"
      >
        Logout
      </v-btn>
    </div>
  </v-app-bar>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/authStore";

const props = defineProps({
  contentOffset: {
    type: Number,
    default: 0,
  },
});

defineEmits(["toggle-sidebar"]);

const router = useRouter();
const authStore = useAuthStore();

const handleLogout = () => {
  authStore.logout();
  router.push({ name: "Login" });
};

const basePadding = 16;

const topbarStyle = computed(() => {
  if (!props.contentOffset) {
    return { paddingLeft: `${basePadding}px` };
  }

  return {
    paddingLeft: `${props.contentOffset + basePadding}px`,
  };
});
</script>

<style scoped>
.hf-topbar {
  border-bottom: 1px solid rgba(148, 163, 184, 0.3);
  background-color: rgba(255, 255, 255, 0.92) !important;
  backdrop-filter: blur(18px);
  z-index: 3;
}

.topbar-actions {
  display: inline-flex;
  align-items: center;
}

.logout-btn {
  text-transform: none;
  font-weight: 600;
  letter-spacing: 0.01em;
  padding-inline: 20px;
  border-radius: 999px;
  box-shadow: 0 10px 30px rgba(13, 148, 136, 0.25);
  background: linear-gradient(135deg, #14b8a6, #0ea5e9) !important;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  min-width: auto;
}

.logout-btn :deep(.v-btn__content) {
  display: inline-flex;
  align-items: center;
  color: #f8fafc;
  font-weight: 600;
}
</style>
