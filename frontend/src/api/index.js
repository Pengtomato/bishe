import axios from 'axios'
import cacheService, { cached } from '../utils/cache'

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
    // 处理网络错误
    if (!error.response) {
      console.error('网络错误，请检查网络连接')
      return Promise.reject(new Error('网络错误，请检查网络连接'))
    }
    
    // 处理HTTP错误
    const status = error.response.status
    const message = error.response.data?.message || error.message
    
    switch (status) {
      case 400:
        console.error('请求参数错误:', message)
        break
      case 401:
        console.error('未授权，请重新登录')
        // 清除本地存储，跳转到登录页
        localStorage.removeItem('user')
        localStorage.removeItem('token')
        window.location.href = '/login'
        break
      case 403:
        console.error('无权限访问')
        break
      case 404:
        console.error('请求的资源不存在')
        break
      case 500:
        console.error('服务器内部错误')
        break
      default:
        console.error(`请求失败: ${status}`, message)
    }
    
    return Promise.reject(error)
  }
)

// 带缓存的get请求
const cachedGet = async (url, config = {}, ttl = 30000) => {
  const cacheKey = cacheService.generateKey(url, config.params || {})
  const cachedData = cacheService.get(cacheKey)
  
  if (cachedData) {
    return cachedData
  }
  
  const result = await api.get(url, config)
  cacheService.set(cacheKey, result, ttl)
  return result
}

export const userApi = {
  getAll: () => cachedGet('/users', {}, 60000),
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
  getLatestData: () => cachedGet('/environment/latest', {}, 10000),
  delete: (id) => api.delete(`/environment/${id}`)
}

export const videoApi = {
  getAll: () => cachedGet('/video', {}, 60000),
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
  getUnprocessed: () => cachedGet('/alerts/unprocessed', {}, 15000),
  process: (id, status) => api.put(`/alerts/${id}/process`, {}, { params: { status } }),
  delete: (id) => api.delete(`/alerts/${id}`)
}

export const deviceApi = {
  getAll: () => cachedGet('/devices', {}, 60000),
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
