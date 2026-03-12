import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import User from '../views/User.vue'
import About from '../views/About.vue'
import Environment from '../views/Environment.vue'
import Video from '../views/Video.vue'
import Alert from '../views/Alert.vue'
import Device from '../views/Device.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/user',
    name: 'User',
    component: User
  },
  {
    path: '/environment',
    name: 'Environment',
    component: Environment
  },
  {
    path: '/video',
    name: 'Video',
    component: Video
  },
  {
    path: '/alert',
    name: 'Alert',
    component: Alert
  },
  {
    path: '/device',
    name: 'Device',
    component: Device
  },
  {
    path: '/about',
    name: 'About',
    component: About
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
