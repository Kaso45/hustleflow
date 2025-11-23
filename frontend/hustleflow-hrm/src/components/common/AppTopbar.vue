<template>
  <v-app-bar flat>
    <v-toolbar-title>HustleFlow HRM</v-toolbar-title>
    <v-spacer />
    <v-btn variant="text" @click="handleLogout">
      Logout
    </v-btn>
  </v-app-bar>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { clearToken } from '../../store/authStore'

const router = useRouter()

function handleLogout() {
  clearToken()
  router.push({ name: 'Login' })
}
</script>
