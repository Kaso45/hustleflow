<template>
  <div class="form-box">
    <h2>Create account</h2>
    <p class="desc">Join the workspace today</p>

    <form @submit.prevent="onSubmit">

      <div class="input-box">
        <label>Username</label>
        <input v-model="username" type="text" required />
      </div>

      <div class="input-box">
        <label>Email</label>
        <input v-model="email" type="email" required />
      </div>

      <div class="input-box">
        <label>Password</label>
        <input v-model="password" type="password" required />
      </div>

      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="successMessage" class="success">{{ successMessage }}</p>

      <button class="primary-btn" :disabled="loading">
        {{ loading ? "Registering..." : "Register" }}
      </button>
    </form>

    <p class="sub-text">
      Already have an account?
      <RouterLink class="link" to="/auth/login">Sign in</RouterLink>
    </p>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useAuthStore } from "../../store/authStore";

const authStore = useAuthStore();

const username = ref("");
const email = ref("");
const password = ref("");

const error = computed(() => authStore.error);
const loading = computed(() => authStore.loading);

const successMessage = ref("");

async function onSubmit() {
  successMessage.value = "";
  const { success, message } = await authStore.register({
    username: username.value,
    email: email.value,
    password: password.value
  });

  if (success) {
    successMessage.value = message;
  }
}
</script>

<style scoped>
@import './auth-base.css';
</style>
