import request from '../utils/request'

export const getCcfPage = (params) => request.get('/ccf/page', { params })
export const getCcfAreas = () => request.get('/ccf/areas')
export const getCcfLevels = () => request.get('/ccf/levels')
