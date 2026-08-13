import request from '../utils/request'

export const getPersonalOverview = () => request.get('/personal/overview')
export const getPersonalAchievements = (params) => request.get('/personal/achievements', { params })
export const togglePin = (type, id) => request.put(`/personal/pin/${type}/${id}`)
export const exportReport = () => request.get('/personal/export', { responseType: 'blob' })
