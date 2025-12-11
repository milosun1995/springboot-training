import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useAuthStore } from '@/store/auth'

const staticRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

// 根据后端菜单生成路由
const viewMap = {
  '/home': () => import('@/views/Home.vue'),
  '/users': () => import('@/views/UserList.vue'),
  '/roles': () => import('@/views/RoleList.vue'),
  '/permissions': () => import('@/views/PermissionList.vue'),
  '/menus': () => import('@/views/MenuList.vue'),
  '/dicts': () => import('@/views/DictList.vue')
}

function buildDynamicRoutes(menus) {
  const routes = []
  const dfs = (nodes) => {
    nodes.forEach((m) => {
      if (m.path && viewMap[m.path]) {
        routes.push({
          path: m.path,
          name: m.code || m.path,
          component: viewMap[m.path]
        })
      }
      if (m.children && m.children.length) dfs(m.children)
    })
  }
  dfs(menus || [])
  return routes
}

let hasBuilt = false

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const token = getToken()
  const authStore = useAuthStore()
  if (to.path === '/login') {
    if (token) {
      return next('/')
    }
    return next()
  }

  if (!token) {
    return next('/login')
  }

  try {
    if (!hasBuilt || !authStore.permissions.length) {
      await authStore.loadProfile()
      const dynamicRoutes = buildDynamicRoutes(authStore.menus)
      // 将动态路由添加到 Layout 的 children 中
      dynamicRoutes.forEach((route) => {
        if (!router.hasRoute(route.name)) {
          router.addRoute('Layout', route)
        }
      })
      // 设置首页重定向
      const firstPath = authStore.menus?.[0]?.children?.[0]?.path || authStore.menus?.[0]?.path
      if (firstPath) {
        const layoutRoute = router.getRoutes().find(r => r.name === 'Layout')
        if (layoutRoute) {
          layoutRoute.redirect = firstPath
        }
      }
      hasBuilt = true
      // 处理首次进入的目标路由
      return next({ ...to, replace: true })
    }
    return next()
  } catch (e) {
    authStore.logout()
    return next('/login')
  }
})

export default router

