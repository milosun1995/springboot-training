import request from '@/utils/request'

export function fetchDicts(params) {
  return request({
    url: '/api/dicts',
    method: 'get',
    params
  })
}

export function createDict(data) {
  return request({
    url: '/api/dicts',
    method: 'post',
    data
  })
}

export function updateDict(id, data) {
  return request({
    url: `/api/dicts/${id}`,
    method: 'put',
    data
  })
}

export function deleteDict(id) {
  return request({
    url: `/api/dicts/${id}`,
    method: 'delete'
  })
}

export function toggleDict(id) {
  return request({
    url: `/api/dicts/${id}/toggle`,
    method: 'post'
  })
}


