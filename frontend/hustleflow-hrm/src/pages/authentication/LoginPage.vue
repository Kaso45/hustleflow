<template>
  <div class="form-box">
    <h2>Welcome back</h2>
    <p class="desc">Login to continue managing your workspace</p>

    <form @submit.prevent="onSubmit">

      <div class="input-box">
        <label>Username</label>
        <input v-model="username" type="text" required />
      </div>

      <div class="input-box">
        <label>Password</label>
        <input v-model="password" type="password" required />
      </div>

      <p v-if="error" class="error">{{ error }}</p>

      <button class="primary-btn" :disabled="loading">
        {{ loading ? "Logging in..." : "Login" }}
      </button>
    </form>

    <p class="sub-text">
      Don’t have an account?
      <RouterLink class="link" to="/auth/register">Create one</RouterLink>
    </p>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useAuthStore } from '../../store/authStore';
import { useRouter } from 'vue-router';

const router = useRouter()

const authStore = useAuthStore();
const username = ref('');
const password = ref('');

const loading = computed(() => authStore.loading);
const error = computed(() => authStore.error);

async function onSubmit() {
  if (!username.value || !password.value) return;

  const success = await authStore.login({
    username: username.value.trim(),
    password: password.value,
  });

  if (success) {
    router.push({ name: 'Dashboard' });   // ⭐ Redirect ngay lập tức
  }
}
</script>

<style scoped>
.form-box {
  width: 100%;
  max-width: 380px;
}

h2 {
  font-size: 32px;
  margin-bottom: 6px;
}

.desc {
  color: #888;
  margin-bottom: 28px;
}

.input-box {
  display: flex;
  flex-direction: column;
  margin-bottom: 18px;
}

label {
  font-size: 14px;
  margin-bottom: 4px;
  color: #444;
}

input {
  padding: 12px;
  border-radius: 10px;
  border: 1px solid #ddd;
  outline: none;
  font-size: 14px;
}

.primary-btn {
  width: 100%;
  margin-top: 10px;
  padding: 14px 0;
  background: #6c5ce7;
  color: white;
  font-weight: 600;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  transition: 0.2s;
}

.primary-btn:hover {
  background: #574bd6;
}

.error {
  color: red;
  margin: -5px 0 10px;
}

.sub-text {
  margin-top: 16px;
  font-size: 14px;
}

.link {
  color: #6c5ce7;
  font-weight: 600;
}
</style>
