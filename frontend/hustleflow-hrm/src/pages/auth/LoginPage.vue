<template>
  <v-app>
    <v-main class="login-main">
      <v-container class="fill-height" fluid>
        <v-row class="fill-height" no-gutters>
          <!-- Left: intro -->
          <v-col
            cols="12"
            md="6"
            class="login-left d-none d-md-flex"
          >
            <div class="login-left-content">
              <h1 class="login-title">HustleFlow HRM</h1>
              <p class="login-subtitle">
                Manage employees, attendance, payroll and projects in one single platform.
              </p>
              <div class="login-highlight">
                <span class="highlight-dot"></span>
                <span>Control your workforce. Boost your productivity.</span>
              </div>
            </div>
          </v-col>

          <!-- Right: form -->
          <v-col
            cols="12"
            md="6"
            class="d-flex align-center justify-center"
          >
            <v-card class="login-card" elevation="8">
              <div class="login-card-header">
                <span class="login-card-title">Welcome back 👋</span>
                <span class="login-card-subtitle">
                  Log in to access your dashboard
                </span>
              </div>

              <v-card-text class="pt-6">
                <v-form @submit.prevent="onSubmit">
                  <v-text-field
                    v-model="username"
                    label="Username"
                    variant="outlined"
                    density="comfortable"
                    color="primary"
                    class="mb-4"
                    required
                  />

                  <v-text-field
                    v-model="password"
                    :type="showPassword ? 'text' : 'password'"
                    label="Password"
                    variant="outlined"
                    density="comfortable"
                    color="primary"
                    :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                    @click:append-inner="showPassword = !showPassword"
                    class="mb-2"
                    required
                  />

                  <div class="d-flex justify-end mb-4">
                    <a class="login-link-small" href="javascript:void(0)">
                      Forgot password?
                    </a>
                  </div>

                  <v-btn
                    type="submit"
                    block
                    class="login-button"
                    :loading="loading"
                  >
                    Login
                  </v-btn>

                  <div
                    v-if="error"
                    class="mt-3 login-error"
                  >
                    Invalid username or password.
                  </div>

                  <div class="mt-6 text-center">
                    <span class="login-text-muted">
                      Don&apos;t have an account?
                    </span>
                    <RouterLink
                      class="login-link"
                      :to="{ name: 'Register' }"
                    >
                      Register now
                    </RouterLink>
                  </div>
                </v-form>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { login } from '../../services/authService'
import { setToken } from '../../store/authStore'

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const error = ref(false)
const router = useRouter()

async function onSubmit() {
  try {
    loading.value = true
    error.value = false

    const data = await login(username.value, password.value)
    // Backend trả về { token, expiresIn }
    setToken(data.token)

    router.push({ name: 'Dashboard' })
  } catch (e) {
    error.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
:root {
  --hf-gray: #808080;
  --hf-yellow: #ffc709;
  --hf-light-gray: #dedede;
  --hf-blue: #1736f5;
}

.login-main {
  background: linear-gradient(
    135deg,
    var(--hf-gray) 0%,
    var(--hf-blue) 55%,
    var(--hf-yellow) 100%
  );
}

.login-left {
  background: rgba(23, 54, 245, 0.9);
  color: #ffffff;
}

.login-left-content {
  padding: 48px;
  max-width: 420px;
}

.login-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
}

.login-subtitle {
  font-size: 14px;
  line-height: 1.5;
  opacity: 0.9;
  margin-bottom: 24px;
}

.login-highlight {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(255, 199, 9, 0.15);
  font-size: 13px;
}

.highlight-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--hf-yellow);
}

.login-card {
  width: 420px;
  max-width: 90%;
  border-radius: 16px;
  background-color: #ffffff;
}

.login-card-header {
  background-color: var(--hf-light-gray);
  padding: 20px 24px 16px;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  border-bottom: 3px solid var(--hf-yellow);
}

.login-card-title {
  display: block;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #000000;
}

.login-card-subtitle {
  font-size: 13px;
  color: var(--hf-gray);
}

.login-button {
  background-color: var(--hf-blue) !important;
  color: #ffffff !important;
  text-transform: none;
  font-weight: 600;
}

.login-button:hover {
  filter: brightness(1.05);
}

.login-link {
  margin-left: 4px;
  color: var(--hf-blue);
  font-weight: 500;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}

.login-link-small {
  font-size: 12px;
  color: var(--hf-gray);
  text-decoration: none;
}

.login-link-small:hover {
  text-decoration: underline;
}

.login-error {
  font-size: 13px;
  color: #ff4d4f;
}

.login-text-muted {
  font-size: 13px;
  color: var(--hf-gray);
}

@media (max-width: 960px) {
  .login-main {
    background: var(--hf-light-gray);
  }

  .login-card {
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
  }
}
</style>
