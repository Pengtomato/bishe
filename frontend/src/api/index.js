import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

api.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export const userApi = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  create: (data) => api.post('/users', data),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`)
}

export const environmentApi = {
  getAll: () => api.get('/environment'),
  getById: (id) => api.get(`/environment/${id}`),
  create: (data) => api.post('/environment', data),
  getByDeviceId: (deviceId) => api.get(`/environment/device/${deviceId}`),
  getLatest: (deviceId) => api.get(`/environment/device/${deviceId}/latest`),
  getByTimeRange: (deviceId, start, end) => api.get(`/environment/device/${deviceId}/range`, { params: { start, end } }),
  getLatestData: () => api.get('/environment/latest'),
  delete: (id) => api.delete(`/environment/${id}`)
}

export const videoApi = {
  getAll: () => api.get('/video'),
  getById: (id) => api.get(`/video/${id}`),
  create: (data) => api.post('/video', data),
  update: (id, data) => api.put(`/video/${id}`, data),
  getByDeviceId: (deviceId) => api.get(`/video/device/${deviceId}`),
  getByStatus: (status) => api.get(`/video/status/${status}`),
  searchByName: (name) => api.get(`/video/search`, { params: { name } }),
  delete: (id) => api.delete(`/video/${id}`)
}

export const alertApi = {
  getAll: () => api.get('/alerts'),
  getById: (id) => api.get(`/alerts/${id}`),
  create: (data) => api.post('/alerts', data),
  getByDeviceId: (deviceId) => api.get(`/alerts/device/${deviceId}`),
  getByStatus: (status) => api.get(`/alerts/status/${status}`),
  getByLevel: (level) => api.get(`/alerts/level/${level}`),
  getByType: (type) => api.get(`/alerts/type/${type}`),
  getByTimeRange: (start, end) => api.get('/alerts/range', { params: { start, end } }),
  getByTimeRangeOrderByLevel: (start, end) => api.get('/alerts/range/level', { params: { start, end } }),
  getUnprocessed: () => api.get('/alerts/unprocessed'),
  process: (id, status) => api.put(`/alerts/${id}/process`, {}, { params: { status } }),
  delete: (id) => api.delete(`/alerts/${id}`)
}

export const deviceApi = {
  getAll: () => api.get('/devices'),
  getById: (id) => api.get(`/devices/${id}`),
  create: (data) => api.post('/devices', data),
  update: (id, data) => api.put(`/devices/${id}`, data),
  getByDeviceId: (deviceId) => api.get(`/devices/device/${deviceId}`),
  getByType: (deviceType) => api.get(`/devices/type/${deviceType}`),
  getByStatus: (status) => api.get(`/devices/status/${status}`),
  searchByName: (name) => api.get(`/devices/search/name`, { params: { name } }),
  searchByLocation: (location) => api.get(`/devices/search/location`, { params: { location } }),
  updateStatus: (id, status) => api.put(`/devices/${id}/status`, {}, { params: { status } }),
  updateParams: (id, controlParams) => api.put(`/devices/${id}/params`, {}, { params: { controlParams } }),
  delete: (id) => api.delete(`/devices/${id}`)
}

export default api
