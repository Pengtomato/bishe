// 缓存服务
class CacheService {
  constructor() {
    this.cache = new Map()
    this.defaultTTL = 30000 // 默认缓存30秒
  }

  // 设置缓存
  set(key, value, ttl = this.defaultTTL) {
    const item = {
      value,
      expiry: Date.now() + ttl
    }
    this.cache.set(key, item)
  }

  // 获取缓存
  get(key) {
    const item = this.cache.get(key)
    if (!item) return null

    // 检查是否过期
    if (Date.now() > item.expiry) {
      this.cache.delete(key)
      return null
    }

    return item.value
  }

  // 清除缓存
  clear(key) {
    if (key) {
      this.cache.delete(key)
    } else {
      this.cache.clear()
    }
  }

  // 生成缓存键
  generateKey(url, params = {}) {
    const paramsStr = Object.keys(params)
      .sort()
      .map(key => `${key}=${params[key]}`)
      .join('&')
    return `${url}${paramsStr ? '?' + paramsStr : ''}`
  }
}

// 创建缓存实例
const cacheService = new CacheService()

// 缓存装饰器
function cached(ttl) {
  return function(target, property, descriptor) {
    const originalMethod = descriptor.value
    
    descriptor.value = async function(...args) {
      // 生成缓存键
      const url = args[0] || ''
      const params = args[1]?.params || {}
      const cacheKey = cacheService.generateKey(url, params)
      
      // 检查缓存
      const cachedData = cacheService.get(cacheKey)
      if (cachedData) {
        return cachedData
      }
      
      // 调用原始方法
      const result = await originalMethod.apply(this, args)
      
      // 设置缓存
      cacheService.set(cacheKey, result, ttl)
      
      return result
    }
    
    return descriptor
  }
}

export default cacheService

export { cached }