<template>
  <v-app>
    <v-main class="d-flex align-center justify-center">
      <v-card width="400">
        <v-card-title class="text-h5">
          Register
        </v-card-title>
        <v-card-text>
          <p class="text-body-2 mb-2">
            (Optional) Simple skeleton – bạn có thể bỏ qua nếu không cần đăng ký qua UI.
          </p>
          <v-form @submit.prevent="onSubmit">
            <v-text-field
              v-model="username"
              label="Username"
              required
            />
            <v-text-field
              v-model="email"
              label="Email"
              type="email"
              required
            />
            <v-text-field
              v-model="password"
              label="Password"
              type="password"
              required
            />
            <v-btn
              type="submit"
              block
              class="mt-4"
              :loading="loading"
            >
              Register
            </v-btn>

            <div v-if="message" class="mt-2">
              {{ message }}
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'
import { register } from '../../services/authService'

const username = ref('')
const email = ref('')
const password = ref('')
const loading = ref(false)
const message = ref('')

async function onSubmit() {
  try {
    loading.value = true
    message.value = ''

    await register({
      username: username.value,
      email: email.value,
      password: password.value,
    })

    message.value = 'User registered successfully (mock message).'
  } finally {
    loading.value = false
  }
}
</script>
