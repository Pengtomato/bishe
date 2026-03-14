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
      <div class="nav-brand">规模化养殖环境监控平台</div>
      <div class="nav-links">
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/environment">环境数据</RouterLink>
        <RouterLink to="/video">视频监控</RouterLink>
        <RouterLink to="/alert">预警管理</RouterLink>
        <RouterLink to="/device">设备控制</RouterLink>
        <RouterLink to="/report">数据报表</RouterLink>
        <RouterLink to="/user" v-if="user && user.role === 'admin'">用户管理</RouterLink>
        <RouterLink to="/about">关于</RouterLink>
      </div>
    </nav>
    <main class="main-content">
      <RouterView />
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
</style>
