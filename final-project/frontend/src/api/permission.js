import request from '@/utils/request'

export function fetchPermissionTree(params) {
  return request({
    url: '/api/permissions/tree',
    method: 'get',
    params
  })
}

export function createPermission(data) {
  return request({
    url: '/api/permissions',
    method: 'post',
    data
  })
}

export function updatePermission(id, data) {
  return request({
    url: `/api/permissions/${id}`,
    method: 'put',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: `/api/permissions/${id}`,
    method: 'delete'
  })
}

export function togglePermission(id) {
  return request({
    url: `/api/permissions/${id}/toggle`,
    method: 'post'
  })
}


