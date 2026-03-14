import { ref, onMounted, onUnmounted } from 'vue'
import { environmentApi, videoApi, alertApi, deviceApi } from '../api'
import { DataAnalysis, VideoCamera, Warning, Monitor, ArrowDown, UserFilled, SwitchButton, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import router from '../router'

export default {
  name: 'Home',
  components: {
    DataAnalysis,
    VideoCamera,
    Warning,
    Monitor,
    ArrowDown,
    UserFilled,
    SwitchButton,
    Refresh
  },
  setup() {
    const environmentCount = ref(0)
    const videoCount = ref(0)
    const alertCount = ref(0)
    const deviceCount = ref(0)
    const latestEnvironment = ref(null)
    const unprocessedAlerts = ref([])
    const user = ref(JSON.parse(localStorage.getItem('user')))
    const userAvatar = ref('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20user%20avatar%20portrait&image_size=square')
    const refreshing = ref(false)
    const lastUpdateTime = ref('')
    let refreshTimer = null
    
    // 图表引用
    const environmentChartRef = ref(null)
    const deviceStatusChartRef = ref(null)
    const alertLevelChartRef = ref(null)
    const videoStatusChartRef = ref(null)
    
    // 登出函数
    const handleLogout = () => {
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      user.value = null
      ElMessage.success('登出成功')
      router.push('/login')
    }
    
    // 图表实例
    let environmentChart = null
    let deviceStatusChart = null
    let alertLevelChart = null
    let videoStatusChart = null
    
    // 真实数据处理函数
    const processRealData = (data, defaultData = []) => {
      return data && data.length > 0 ? data : defaultData
    }

    // 加载统计数据
    const loadStats = async () => {
      try {
        refreshing.value = true
        // 从API获取真实的数据库数据
        const [environmentData, videoData, alertData, deviceData] = await Promise.all([
          environmentApi.getAll().catch(() => []),
          videoApi.getAll().catch(() => []),
          alertApi.getAll().catch(() => []),
          deviceApi.getAll().catch(() => [])
        ])
        
        // 更新统计数据
        environmentCount.value = environmentData.length
        videoCount.value = videoData.length
        alertCount.value = alertData.length
        deviceCount.value = deviceData.length
        
        // 更新时间
        lastUpdateTime.value = new Date().toLocaleString()
        
        // 最新环境数据
        if (environmentData && environmentData.length > 0) {
          latestEnvironment.value = environmentData[environmentData.length - 1]
        }
        
        // 未处理预警
        unprocessedAlerts.value = alertData.filter(alert => alert.status === '未处理')
        
        // 初始化图表
        initCharts(environmentData, deviceData, alertData, videoData)
        
        // 显示成功提示
        if (environmentData.length > 0 || videoData.length > 0 || alertData.length > 0 || deviceData.length > 0) {
          ElMessage.success('数据加载成功')
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
        // 显示错误信息给用户
        ElMessage.error(`加载数据失败: ${error.message || '未知错误'}`)
      } finally {
        refreshing.value = false
      }
    }
    
    // 手动刷新
    const handleManualRefresh = () => {
      loadStats()
    }
    
    // 启动自动刷新
    const startAutoRefresh = () => {
      // 每30秒自动刷新一次数据
      refreshTimer = setInterval(() => {
        loadStats()
      }, 30000)
    }
    
    // 停止自动刷新
    const stopAutoRefresh = () => {
      if (refreshTimer) {
        clearInterval(refreshTimer)
        refreshTimer = null
      }
    }
    
    // 初始化图表
    const initCharts = (environmentData, deviceData, alertData, videoData) => {
      // 环境数据趋势图
      if (environmentChartRef.value) {
        environmentChart = echarts.init(environmentChartRef.value)
        
        // 准备数据
        const timeData = environmentData.slice(-20).map(item => {
          return new Date(item.timestamp).toLocaleTimeString()
        })
        const temperatureData = environmentData.slice(-20).map(item => item.temperature)
        const humidityData = environmentData.slice(-20).map(item => item.humidity)
        
        const environmentOption = {
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['温度', '湿度']
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: timeData
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value}'
            }
          },
          series: [
            {
              name: '温度',
              type: 'line',
              data: temperatureData,
              smooth: true,
              itemStyle: {
                color: '#409EFF'
              }
            },
            {
              name: '湿度',
              type: 'line',
              data: humidityData,
              smooth: true,
              itemStyle: {
                color: '#67C23A'
              }
            }
          ]
        }
        
        environmentChart.setOption(environmentOption)
      }
      
      // 设备状态分布图
      if (deviceStatusChartRef.value) {
        deviceStatusChart = echarts.init(deviceStatusChartRef.value)
        
        // 统计设备状态
        const statusCount = {}
        deviceData.forEach(device => {
          const status = device.status || '未知'
          statusCount[status] = (statusCount[status] || 0) + 1
        })
        
        const statusData = Object.keys(statusCount).map(key => ({
          name: key,
          value: statusCount[key]
        }))
        
        const deviceStatusOption = {
          tooltip: {
            trigger: 'item'
          },
          legend: {
            orient: 'vertical',
            left: 'left'
          },
          series: [
            {
              name: '设备状态',
              type: 'pie',
              radius: '60%',
              data: statusData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        }
        
        deviceStatusChart.setOption(deviceStatusOption)
      }
      
      // 预警级别分布图
      if (alertLevelChartRef.value) {
        alertLevelChart = echarts.init(alertLevelChartRef.value)
        
        // 统计预警级别
        const levelCount = {}
        alertData.forEach(alert => {
          const level = alert.level || '未知'
          levelCount[level] = (levelCount[level] || 0) + 1
        })
        
        const levelData = Object.keys(levelCount).map(key => ({
          name: key,
          value: levelCount[key]
        }))
        
        const alertLevelOption = {
          tooltip: {
            trigger: 'item'
          },
          legend: {
            orient: 'vertical',
            left: 'left'
          },
          series: [
            {
              name: '预警级别',
              type: 'pie',
              radius: '60%',
              data: levelData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        }
        
        alertLevelChart.setOption(alertLevelOption)
      }
      
      // 视频监控状态图
      if (videoStatusChartRef.value) {
        videoStatusChart = echarts.init(videoStatusChartRef.value)
        
        // 统计视频监控状态
        const videoStatusCount = {}
        const processedVideoData = processRealData(videoData, [])
        processedVideoData.forEach(video => {
          const status = video.status || '未知'
          videoStatusCount[status] = (videoStatusCount[status] || 0) + 1
        })
        
        const videoStatusData = Object.keys(videoStatusCount).map(key => ({
          name: key,
          value: videoStatusCount[key]
        }))
        
        const videoStatusOption = {
          tooltip: {
            trigger: 'item'
          },
          legend: {
            orient: 'vertical',
            left: 'left'
          },
          series: [
            {
              name: '视频监控状态',
              type: 'pie',
              radius: '60%',
              data: videoStatusData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        }
        
        videoStatusChart.setOption(videoStatusOption)
      }
    }
    
    // 导航函数
    const navigateTo = (path) => {
      // 根据不同的路径跳转到对应的页面
      switch (path) {
        case 'environment':
          // 跳转到环境数据页面
          router.push('/environment')
          break
        case 'device':
          // 跳转到设备管理页面
          router.push('/device')
          break
        case 'alert':
          // 跳转到预警管理页面
          router.push('/alert')
          break
        case 'video':
          // 跳转到视频监控页面
          router.push('/video')
          break
        case 'profile':
          // 跳转到个人信息页面
          router.push('/profile')
          break
        default:
          break
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (datetime) => {
      if (!datetime) return ''
      return new Date(datetime).toLocaleString()
    }
    
    // 响应式调整图表大小
    const handleResize = () => {
      environmentChart?.resize()
      deviceStatusChart?.resize()
      alertLevelChart?.resize()
    }
    
    onMounted(() => {
      loadStats()
      startAutoRefresh()
      window.addEventListener('resize', handleResize)
    })
    
    onUnmounted(() => {
      stopAutoRefresh()
      window.removeEventListener('resize', handleResize)
      environmentChart?.dispose()
      deviceStatusChart?.dispose()
      alertLevelChart?.dispose()
    })
    
    return {
      environmentCount,
      videoCount,
      alertCount,
      deviceCount,
      latestEnvironment,
      unprocessedAlerts,
      user,
      userAvatar,
      refreshing,
      lastUpdateTime,
      handleLogout,
      handleManualRefresh,
      formatDateTime,
      navigateTo,
      environmentChartRef,
      deviceStatusChartRef,
      alertLevelChartRef,
      videoStatusChartRef
    }
  }
}