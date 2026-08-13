import request from '../utils/request'

export function getInnovationPage(params) {
  return request.get('/innovation/page', { params })
}

export function getInnovationDetail(id) {
  return request.get(`/innovation/${id}`)
}

export function createInnovation(data) {
  return request.post('/innovation', data)
}

export function updateInnovation(id, data) {
  return request.put(`/innovation/${id}`, data)
}

export function deleteInnovation(id) {
  return request.delete(`/innovation/${id}`)
}

export function withdrawInnovation(id) {
  return request.put(`/innovation/${id}/withdraw`)
}
