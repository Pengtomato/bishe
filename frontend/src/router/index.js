import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import User from '../views/User.vue'
import About from '../views/About.vue'
import Environment from '../views/Environment.vue'
import Video from '../views/Video.vue'
import Alert from '../views/Alert.vue'
import Device from '../views/Device.vue'
import Login from '../views/Login.vue'
import Profile from '../views/Profile.vue'
import Report from '../views/Report.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/user',
    name: 'User',
    component: User,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/environment',
    name: 'Environment',
    component: Environment,
    meta: { requiresAuth: true }
  },
  {
    path: '/video',
    name: 'Video',
    component: Video,
    meta: { requiresAuth: true }
  },
  {
    path: '/alert',
    name: 'Alert',
    component: Alert,
    meta: { requiresAuth: true }
  },
  {
    path: '/device',
    name: 'Device',
    component: Device,
    meta: { requiresAuth: true }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/report',
    name: 'Report',
    component: Report,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)
  
  const user = JSON.parse(localStorage.getItem('user'))
  const token = localStorage.getItem('token')
  
  if (requiresAuth && !token) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if (requiresAdmin && (!user || user.role !== 'admin')) {
    // 需要管理员权限但不是管理员，跳转到首页
    next('/')
  } else {
    next()
  }
})

export default router
