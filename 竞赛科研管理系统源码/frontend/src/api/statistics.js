import request from '../utils/request'

export const getOverview = () => request.get('/statistics/overview')
export const getCompetitionStats = (year) => request.get('/statistics/competition', { params: { year } })
export const getInnovationStats = (year) => request.get('/statistics/innovation', { params: { year } })
export const getCopyrightStats = (year) => request.get('/statistics/copyright', { params: { year } })
export const getPaperStats = (year) => request.get('/statistics/paper', { params: { year } })
