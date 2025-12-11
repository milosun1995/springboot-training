import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  
  // 登录页面
  if (to.path === '/login') {
    if (token) {
      // 已登录，重定向到首页
      next('/home')
    } else {
      next()
    }
  } 
  // 需要认证的页面
  else if (to.meta?.requiresAuth) {
    if (token) {
      next()
    } else {
      // 未登录，重定向到登录页
      next('/login')
    }
  } 
  // 其他页面
  else {
    next()
  }
})

export default router

