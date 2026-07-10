import request from '../utils/request'

export const getPaperPage = (params) => request.get('/paper/page', { params })
export const getPaperDetail = (id) => request.get(`/paper/${id}`)
export const createPaper = (data) => request.post('/paper', data)
export const updatePaper = (id, data) => request.put(`/paper/${id}`, data)
export const deletePaper = (id) => request.delete(`/paper/${id}`)
export const withdrawPaper = (id) => request.put(`/paper/${id}/withdraw`)
