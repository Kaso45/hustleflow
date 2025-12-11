// src/router/routes.auth.js
export const authRoutes = {
  path: '/auth',
  component: () => import('@/pages/authentication/AuthLayout.vue'),
  children: [
    {
      path: 'login',
      name: 'Login',
      component: () => import('@/pages/authentication/LoginPage.vue'),
      meta: { guestOnly: true }
    },
    {
      path: 'register',
      name: 'Register',
      component: () => import('@/pages/authentication/RegisterPage.vue'),
      meta: { guestOnly: true }
    }
  ]
}
