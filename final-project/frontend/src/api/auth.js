import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

export function profile() {
  return request({
    url: '/api/auth/profile',
    method: 'get'
  })
}
