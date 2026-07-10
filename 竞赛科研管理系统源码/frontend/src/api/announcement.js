import request from '../utils/request'

export const getAnnouncementPage = (params) => request.get('/announcement/page', { params })
export const getAnnouncementDetail = (id) => request.get(`/announcement/${id}`)
export const getLatestAnnouncement = () => request.get('/announcement/latest')
export const createAnnouncement = (data) => request.post('/announcement', data)
export const updateAnnouncement = (id, data) => request.put(`/announcement/${id}`, data)
export const deleteAnnouncement = (id) => request.delete(`/announcement/${id}`)
