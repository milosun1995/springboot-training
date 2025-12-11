import request from '@/utils/request'

export function fetchRoles(params) {
  return request({
    url: '/api/roles',
    method: 'get',
    params
  })
}

export function createRole(data) {
  return request({
    url: '/api/roles',
    method: 'post',
    data
  })
}

export function updateRole(id, data) {
  return request({
    url: `/api/roles/${id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/api/roles/${id}`,
    method: 'delete'
  })
}

export function toggleRole(id) {
  return request({
    url: `/api/roles/${id}/toggle`,
    method: 'post'
  })
}

export function fetchRolePermissions(id) {
  return request({
    url: `/api/roles/${id}/permissions`,
    method: 'get'
  })
}

export function saveRolePermissions(id, data) {
  return request({
    url: `/api/roles/${id}/permissions`,
    method: 'post',
    data
  })
}

export function fetchRoleMenus(id) {
  return request({
    url: `/api/roles/${id}/menus`,
    method: 'get'
  })
}

export function saveRoleMenus(id, data) {
  return request({
    url: `/api/roles/${id}/menus`,
    method: 'post',
    data
  })
}


