import request from '../utils/request'

export const getCopyrightPage = (params) => request.get('/copyright/page', { params })
export const getCopyrightDetail = (id) => request.get(`/copyright/${id}`)
export const createCopyright = (data) => request.post('/copyright', data)
export const updateCopyright = (id, data) => request.put(`/copyright/${id}`, data)
export const deleteCopyright = (id) => request.delete(`/copyright/${id}`)
export const withdrawCopyright = (id) => request.put(`/copyright/${id}/withdraw`)
