import request from '../utils/request'

export const getNotificationPage = (params) => request.get('/notification/page', { params })
export const getUnreadCount = () => request.get('/notification/unread-count')
export const markRead = (id) => request.put(`/notification/${id}/read`)
export const markAllRead = () => request.put('/notification/read-all')
export const deleteNotification = (id) => request.delete(`/notification/${id}`)
export const deleteAllNotifications = () => request.delete('/notification/all')
