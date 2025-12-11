import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login as loginApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    username: '',
    nickname: ''
  }),
  
  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      this.token = res.data.token
      this.username = res.data.username
      this.nickname = res.data.nickname
      setToken(res.data.token)
      return res
    },
    
    logout() {
      this.token = ''
      this.username = ''
      this.nickname = ''
      removeToken()
    }
  }
})

