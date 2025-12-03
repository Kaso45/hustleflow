<template>
  <v-app>
    <v-main class="register-main">
      <v-container class="fill-height" fluid>
        <v-row class="fill-height" no-gutters>
          <!-- Left -->
          <v-col
            cols="12"
            md="6"
            class="register-left d-none d-md-flex"
          >
            <div class="register-left-content">
              <h1 class="register-title">
                Create your HustleFlow account
              </h1>
              <p class="register-subtitle">
                Set up an admin account to configure your HRM system.
              </p>
              <ul class="register-list">
                <li>Centralize employee and department management.</li>
                <li>Track attendance, leave, and payroll clearly.</li>
                <li>Monitor projects, tasks and employee performance.</li>
              </ul>
            </div>
          </v-col>

          <!-- Right: form -->
          <v-col
            cols="12"
            md="6"
            class="d-flex align-center justify-center"
          >
            <v-card class="register-card" elevation="8">
              <div class="register-card-header">
                <span class="register-card-title">Sign up</span>
                <span class="register-card-subtitle">
                  Create a new account to get started
                </span>
              </div>

              <v-card-text class="pt-6">
                <v-form @submit.prevent="onSubmit">
                  <v-text-field
                    v-model="username"
                    label="Username"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
                    required
                  />
                  <v-text-field
                    v-model="email"
                    label="Email"
                    type="email"
                    variant="outlined"
                    density="comfortable"
                    class="mb-3"
                    required
                  />
                  <v-text-field
                    v-model="password"
                    label="Password"
                    :type="showPassword ? 'text' : 'password'"
                    variant="outlined"
                    density="comfortable"
                    :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                    @click:append-inner="showPassword = !showPassword"
                    class="mb-1"
                    required
                  />

                  <p class="register-hint text-body-2 mb-4">
                    Password should be at least 8 characters with upper, lower case and numbers.
                  </p>

                  <v-btn
                    type="submit"
                    block
                    class="register-button"
                    :loading="loading"
                  >
                    Register
                  </v-btn>

                  <div v-if="message" class="mt-3 register-message">
                    {{ message }}
                  </div>

                  <div class="mt-6 text-center">
                    <span class="register-text-muted">
                      Already have an account?
                    </span>
                    <RouterLink
                      class="register-link"
                      :to="{ name: 'Login' }"
                    >
                      Login
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
import { RouterLink } from 'vue-router'
import { register } from '../../services/authService'

const username = ref('')
const email = ref('')
const password = ref('')
const loading = ref(false)
const message = ref('')
const showPassword = ref(false)

async function onSubmit() {
  try {
    loading.value = true
    message.value = ''

    await register({
      username: username.value,
      email: email.value,
      password: password.value,
    })

    message.value = 'User registered successfully.'
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

.register-main {
  background: linear-gradient(
    135deg,
    var(--hf-blue) 0%,
    var(--hf-gray) 40%,
    var(--hf-yellow) 90%
  );
}

.register-left {
  background: rgba(128, 128, 128, 0.92);
  color: #ffffff;
}

.register-left-content {
  padding: 48px;
  max-width: 440px;
}

.register-title {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 12px;
}

.register-subtitle {
  font-size: 14px;
  line-height: 1.5;
  opacity: 0.95;
  margin-bottom: 18px;
}

.register-list {
  padding-left: 20px;
  font-size: 13px;
  line-height: 1.6;
}

.register-card {
  width: 420px;
  max-width: 90%;
  border-radius: 16px;
  background-color: #ffffff;
}

.register-card-header {
  background-color: var(--hf-light-gray);
  padding: 20px 24px 16px;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  border-bottom: 3px solid var(--hf-yellow);
}

.register-card-title {
  display: block;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #000000;
}

.register-card-subtitle {
  font-size: 13px;
  color: var(--hf-gray);
}

.register-button {
  background-color: var(--hf-yellow) !important;
  color: #000000 !important;
  text-transform: none;
  font-weight: 600;
}

.register-button:hover {
  filter: brightness(1.05);
}

.register-message {
  font-size: 13px;
  color: var(--hf-blue);
}

.register-text-muted {
  font-size: 13px;
  color: var(--hf-gray);
}

.register-link {
  margin-left: 4px;
  color: var(--hf-blue);
  font-weight: 500;
  text-decoration: none;
}

.register-link:hover {
  text-decoration: underline;
}

.register-hint {
  color: var(--hf-gray);
}

@media (max-width: 960px) {
  .register-main {
    background: var(--hf-light-gray);
  }

  .register-card {
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
  }
}
</style>
