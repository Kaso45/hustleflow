// src/router/guards.js
import { useAuthStore } from '@/store/authStore'

export function authGuard(to, from, next) {
  const authStore = useAuthStore()
  const loggedIn = authStore.isAuthenticated

  if (to.meta.requiresAuth && !loggedIn) {
    return next({ name: 'Login' })
  }

  if (to.meta.guestOnly && loggedIn) {
    return next({ name: 'Dashboard' })
  }

  return next()
}
