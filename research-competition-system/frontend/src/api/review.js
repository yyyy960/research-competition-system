import request from '../utils/request'

export const getReviewTodo = (params) => request.get('/review/todo', { params })
export const approveReview = (data) => request.post('/review/approve', data)
export const rejectReview = (data) => request.post('/review/reject', data)
