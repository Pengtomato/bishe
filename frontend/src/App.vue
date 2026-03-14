<script setup>
import { RouterView, RouterLink, useRouter, useRoute } from 'vue-router'
import { ref, onMounted, watch } from 'vue'

const user = ref(null)
const router = useRouter()
const route = useRoute()

const loadUser = () => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
  } else {
    user.value = null
  }
}

onMounted(() => {
  loadUser()
  // 监听存储变化，以便在登录/登出后更新用户状态
  window.addEventListener('storage', loadUser)
})

// 监听路由变化，确保用户状态始终最新
watch(() => route.path, () => {
  loadUser()
})
</script>

<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-brand zoom-in">规模化养殖环境监控平台</div>
      <div class="nav-links">
        <RouterLink to="/" class="nav-link btn-hover fade-in delay-100">首页</RouterLink>
        <RouterLink to="/environment" class="nav-link btn-hover fade-in delay-200">环境数据</RouterLink>
        <RouterLink to="/video" class="nav-link btn-hover fade-in delay-300">视频监控</RouterLink>
        <RouterLink to="/alert" class="nav-link btn-hover fade-in delay-400">预警管理</RouterLink>
        <RouterLink to="/alert-rule" v-if="user && user.role === 'admin'" class="nav-link btn-hover fade-in delay-500">预警规则</RouterLink>
        <RouterLink to="/device" class="nav-link btn-hover fade-in delay-600">设备控制</RouterLink>
        <RouterLink to="/report" class="nav-link btn-hover fade-in delay-700">数据报表</RouterLink>
        <RouterLink to="/user" v-if="user && user.role === 'admin'" class="nav-link btn-hover fade-in delay-800">用户管理</RouterLink>
        <RouterLink to="/help" class="nav-link btn-hover fade-in delay-900">帮助</RouterLink>
        <RouterLink to="/about" class="nav-link btn-hover fade-in delay-1000">关于</RouterLink>
      </div>
    </nav>
    <main class="main-content">
      <RouterView v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </RouterView>
    </main>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: #f5f5f5;
}
#app {
  min-height: 100vh;
}
.navbar {
  background: #409EFF;
  color: white;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.nav-brand {
  font-size: 20px;
  font-weight: bold;
}
.nav-links {
  display: flex;
  gap: 20px;
}
.nav-links a {
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background 0.2s;
}
.nav-links a:hover,
.nav-links a.router-link-active {
  background: rgba(255,255,255,0.2);
}
.main-content {
  padding: 20px;
}
/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 导航链接样式 */
.nav-link {
  position: relative;
  overflow: hidden;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: -100%;
  width: 100%;
  height: 2px;
  background: #409EFF;
  transition: left 0.3s ease;
}

.nav-link:hover::after {
  left: 0;
}

/* 响应式导航栏 */
@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    align-items: flex-start;
    padding: 10px;
  }
  
  .nav-links {
    margin-top: 10px;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .nav-link {
    padding: 8px 12px;
  }
}

@media (max-width: 480px) {
  .nav-brand {
    font-size: 1.2rem;
  }
  
  .nav-links {
    flex-direction: column;
    width: 100%;
  }
  
  .nav-link {
    width: 100%;
    text-align: center;
  }
}
</style>
