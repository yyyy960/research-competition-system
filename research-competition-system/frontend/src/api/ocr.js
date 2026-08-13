import request from '../utils/request'

export const recognizeCertificate = (formData) => request.post('/ocr/certificate', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
export const getOcrStatus = () => request.get('/ocr/status')
