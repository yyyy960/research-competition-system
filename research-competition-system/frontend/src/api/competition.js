import request from '../utils/request'

export function getCompetitionPage(params) {
  return request.get('/competition/page', { params })
}

export function getCompetitionDetail(id) {
  return request.get(`/competition/${id}`)
}

export function createCompetition(data) {
  return request.post('/competition', data)
}

export function updateCompetition(id, data) {
  return request.put(`/competition/${id}`, data)
}

export function deleteCompetition(id) {
  return request.delete(`/competition/${id}`)
}

export function withdrawCompetition(id) {
  return request.put(`/competition/${id}/withdraw`)
}
