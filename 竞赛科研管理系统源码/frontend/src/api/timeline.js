import request from '../utils/request'

export const getTimeline = (type, id) => request.get(`/timeline/${type}/${id}`)
