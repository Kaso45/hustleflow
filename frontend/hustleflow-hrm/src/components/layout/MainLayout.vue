<template>
  <v-app>
    <v-navigation-drawer app permanent>
      <AppSidebar />
    </v-navigation-drawer>

    <v-main>
      <AppTopbar />
      <v-container fluid class="pa-4">
        <!-- Nơi render các page con: dashboard, people, attendance,... -->
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import AppSidebar from './AppSidebar.vue'
import AppTopbar from '../common/AppTopbar.vue'
</script>
