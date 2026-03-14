import { createRouter, createWebHistory } from 'vue-router'

// 懒加载路由组件
const Home = () => import('../views/Home.vue')
const User = () => import('../views/User.vue')
const About = () => import('../views/About.vue')
const Environment = () => import('../views/Environment.vue')
const Video = () => import('../views/Video.vue')
const Alert = () => import('../views/Alert.vue')
const Device = () => import('../views/Device.vue')
const Login = () => import('../views/Login.vue')
const Profile = () => import('../views/Profile.vue')
const Report = () => import('../views/Report.vue')

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
    path: '/alert-rule',
    name: 'AlertRule',
    component: () => import('../views/AlertRule.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
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
    path: '/help',
    name: 'Help',
    component: () => import('../views/Help.vue'),
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
