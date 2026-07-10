import request from '../utils/request'

export const checkDuplicate = (data) => request.post('/check/duplicate', data)
export const validateFields = (data) => request.post('/check/validate', data)
