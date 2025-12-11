import request from '@/utils/request'

export function fetchMenuTree(params) {
  return request({
    url: '/api/menus/tree',
    method: 'get',
    params
  })
}

export function createMenu(data) {
  return request({
    url: '/api/menus',
    method: 'post',
    data
  })
}

export function updateMenu(id, data) {
  return request({
    url: `/api/menus/${id}`,
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/api/menus/${id}`,
    method: 'delete'
  })
}

export function toggleMenu(id) {
  return request({
    url: `/api/menus/${id}/toggle`,
    method: 'post'
  })
}


