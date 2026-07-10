import request from '../utils/request'

export const getUserPage = (params) => request.get('/user/page', { params })
export const createUser = (data) => request.post('/user', data)
export const updateUser = (id, data) => request.put(`/user/${id}`, data)
export const deleteUser = (id) => request.delete(`/user/${id}`)
export const updateUserRole = (id, data) => request.put(`/user/${id}/role`, data)
export const updateUserStatus = (id, data) => request.put(`/user/${id}/status`, data)

// Batch operations
export const batchDeleteUsers = (ids) => request.post('/user/batch-delete', ids)
export const batchCreateUsers = (users) => request.post('/user/batch-create', users)
export const batchUpdateUserRole = (ids, roleId) => request.put('/user/batch-role', { ids, roleId })
export const batchUpdateUserStatus = (ids, status) => request.put('/user/batch-status', { ids, status })
export const getAllRoles = () => request.get('/user/roles')

// Excel import
export const importExcel = (formData) => request.post('/user/import-excel', formData)
export const importConfirm = (users) => request.post('/user/import-confirm', users)
