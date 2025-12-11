import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/store/auth'

const app = createApp(App)
const pinia = createPinia()

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 权限指令 v-perm
app.directive('perm', {
  mounted(el, binding) {
    const authStore = useAuthStore()
    const need = binding.value
    if (!need) return
    const has = authStore.permissions?.includes(need)
    if (!has) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
})

app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.mount('#app')

