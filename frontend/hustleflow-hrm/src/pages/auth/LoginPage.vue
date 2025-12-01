<template>
  <v-app>
    <v-main class="d-flex align-center justify-center">
      <v-card width="400">
        <v-card-title class="text-h5">
          Login
        </v-card-title>
        <v-card-text>
          <v-form @submit.prevent="onSubmit">
            <v-text-field
              v-model="username"
              label="Username"
              required
            />
            <v-text-field
              v-model="password"
              label="Password"
              :type="showPassword ? 'text' : 'password'"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showPassword = !showPassword"
              required
            />
            <v-btn
              type="submit"
              block
              class="mt-4"
              :loading="loading"
            >
              Login
            </v-btn>

            <div v-if="error" class="mt-2" style="color: red;">
              {{ error }}
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../../services/authService'
import { setToken } from '../../store/authStore'

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')
const router = useRouter()

async function onSubmit() {
  try {
    loading.value = true
    error.value = ''

    const data = await login(username.value, password.value)
    // Backend trả về { token, expiresIn }
    setToken(data.token)

    router.push({ name: 'Dashboard' })
  } catch (e) {
    error.value = 'Invalid username or password'
  } finally {
    loading.value = false
  }
}
</script>
