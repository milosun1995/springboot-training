import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login as loginApi, profile as profileApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    username: '',
    nickname: '',
    roles: [],
    permissions: [],
    menus: []
  }),
  
  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      this.token = res.data.token
      this.username = res.data.username
      this.nickname = res.data.nickname
      setToken(res.data.token)
      await this.loadProfile()
      return res
    },

    async loadProfile() {
      if (!this.token) return
      const res = await profileApi()
      this.roles = res.data.roles || []
      this.permissions = res.data.permissions || []
      this.menus = res.data.menus || []
      this.username = res.data.username || this.username
      this.nickname = res.data.nickname || this.nickname
      return res
    },
    
    logout() {
      this.token = ''
      this.username = ''
      this.nickname = ''
      this.roles = []
      this.permissions = []
      this.menus = []
      removeToken()
    }
  }
})

