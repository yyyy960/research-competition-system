import request from '../utils/request'

export const uploadFile = (formData) => request.post('/file/upload', formData)
export const deleteFile = (id) => request.delete(`/file/${id}`)
export const getFileUrl = (id) => `/api/file/${id}`
export const getPreviewUrl = (id) => `/api/file/preview/${id}`
