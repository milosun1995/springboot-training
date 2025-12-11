import request from '@/utils/request'

export function fetchUsers(params) {
  return request({
    url: '/api/users',
    method: 'get',
    params
  })
}

export function createUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

export function updateUser(id, data) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

export function toggleUser(id) {
  return request({
    url: `/api/users/${id}/toggle`,
    method: 'post'
  })
}

