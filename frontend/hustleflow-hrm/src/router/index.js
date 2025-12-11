// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { authRoutes } from './routes.auth'
import { appRoutes } from './routes.app'
import NotFoundPage from '@/pages/NotFoundPage.vue'
import { authGuard } from './guards'

const routes = [
  authRoutes,
  appRoutes,
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFoundPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Global guard
router.beforeEach(authGuard)

export default router
