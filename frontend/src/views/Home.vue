<template>
  <div class="home">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>平台概览</span>
          <div class="user-info">
            <el-dropdown>
              <span class="user-dropdown">
                <el-avatar :size="32" :src="userAvatar"></el-avatar>
                <span class="user-name">{{ user?.nickname || '游客' }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="navigateTo('profile')">
                    <el-icon><UserFilled /></el-icon>
                    <span>个人信息</span>
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>
      
      <div class="dashboard-stats">
        <div class="refresh-controls">
          <el-button type="primary" size="small" @click="handleManualRefresh" :loading="refreshing">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
          <span class="last-update-time">最后更新: {{ lastUpdateTime }}</span>
        </div>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="stats-card">
              <div class="stats-item">
                <div class="stats-icon environment-icon">
                  <el-icon><DataAnalysis /></el-icon>
                </div>
                <div class="stats-content">
                  <div class="stats-value">{{ environmentCount }}</div>
                  <div class="stats-label">环境数据</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stats-card">
              <div class="stats-item">
                <div class="stats-icon video-icon">
                  <el-icon><VideoCamera /></el-icon>
                </div>
                <div class="stats-content">
                  <div class="stats-value">{{ videoCount }}</div>
                  <div class="stats-label">视频监控</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stats-card">
              <div class="stats-item">
                <div class="stats-icon alert-icon">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="stats-content">
                  <div class="stats-value">{{ alertCount }}</div>
                  <div class="stats-label">预警信息</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stats-card">
              <div class="stats-item">
                <div class="stats-icon device-icon">
                  <el-icon><Monitor /></el-icon>
                </div>
                <div class="stats-content">
                  <div class="stats-value">{{ deviceCount }}</div>
                  <div class="stats-label">设备总数</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>数据图表</span>
        </div>
      </template>
      
      <div class="charts-container">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover" class="chart-card" @click="navigateTo('environment')">
              <template #header>
                <div class="card-header-small">
                  <span>环境数据趋势</span>
                </div>
              </template>
              <div ref="environmentChartRef" class="chart"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="chart-card" @click="navigateTo('device')">
              <template #header>
                <div class="card-header-small">
                  <span>设备状态分布</span>
                </div>
              </template>
              <div ref="deviceStatusChartRef" class="chart"></div>
            </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <el-card shadow="hover" class="chart-card" @click="navigateTo('alert')">
              <template #header>
                <div class="card-header-small">
                  <span>预警级别分布</span>
                </div>
              </template>
              <div ref="alertLevelChartRef" class="chart"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="chart-card" @click="navigateTo('video')">
              <template #header>
                <div class="card-header-small">
                  <span>视频监控状态</span>
                </div>
              </template>
              <div ref="videoStatusChartRef" class="chart"></div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>系统状态</span>
        </div>
      </template>
      
      <div class="system-status">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover" class="status-card">
              <template #header>
                <div class="card-header-small">
                  <span>最新环境数据</span>
                </div>
              </template>
              <div v-if="latestEnvironment" class="environment-data">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="设备ID">{{ latestEnvironment.deviceId }}</el-descriptions-item>
                  <el-descriptions-item label="温度">{{ latestEnvironment.temperature }}℃</el-descriptions-item>
                  <el-descriptions-item label="湿度">{{ latestEnvironment.humidity }}%</el-descriptions-item>
                  <el-descriptions-item label="CO2">{{ latestEnvironment.co2 }}ppm</el-descriptions-item>
                  <el-descriptions-item label="氨气">{{ latestEnvironment.ammonia }}ppm</el-descriptions-item>
                  <el-descriptions-item label="光照">{{ latestEnvironment.lightIntensity }}lux</el-descriptions-item>
                </el-descriptions>
              </div>
              <div v-else class="empty-data">
                暂无环境数据
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="status-card">
              <template #header>
                <div class="card-header-small">
                  <span>未处理预警</span>
                </div>
              </template>
              <div v-if="unprocessedAlerts.length > 0" class="alert-list">
                <el-list>
                  <el-list-item v-for="alert in unprocessedAlerts.slice(0, 5)" :key="alert.id">
                    <template #prefix>
                      <el-tag type="danger" size="small">
                        {{ alert.level }}
                      </el-tag>
                    </template>
                    <div class="alert-item">
                      <div class="alert-type">{{ alert.type }}</div>
                      <div class="alert-message">{{ alert.message }}</div>
                      <div class="alert-time">{{ formatDateTime(alert.timestamp) }}</div>
                    </div>
                  </el-list-item>
                </el-list>
              </div>
              <div v-else class="empty-data">
                暂无未处理预警
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
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
        const environmentData = await environmentApi.getAll() || []
        const videoData = await videoApi.getAll() || []
        const alertData = await alertApi.getAll() || []
        const deviceData = await deviceApi.getAll() || []
        
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
      } catch (error) {
        console.error('加载统计数据失败:', error)
        // 显示错误信息给用户
        ElMessage.error('加载数据失败，请检查后端服务是否运行')
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
</script>

<style scoped>
.home {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.el-avatar {
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
}

.user-dropdown:hover .el-avatar {
  border-color: #409EFF;
  transform: scale(1.05);
}

.card-header-small {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.mb-4 {
  margin-bottom: 16px;
}

.dashboard-stats {
  margin-top: 20px;
}

.refresh-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.refresh-controls .el-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

.last-update-time {
  font-size: 12px;
  color: #909399;
}

.stats-card {
  height: 120px;
}

.stats-item {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
  margin-right: 20px;
}

.environment-icon {
  background-color: #ecf5ff;
  color: #409EFF;
}

.video-icon {
  background-color: #f0f9eb;
  color: #67C23A;
}

.alert-icon {
  background-color: #fef0f0;
  color: #F56C6C;
}

.device-icon {
  background-color: #fdf6ec;
  color: #E6A23C;
}

.stats-content {
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.system-status {
  margin-top: 20px;
}

.status-card {
  height: 280px;
  display: flex;
  flex-direction: column;
}

.environment-data {
  margin-top: 10px;
  flex: 1;
  overflow-y: auto;
}

.alert-list {
  margin-top: 10px;
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

.alert-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.alert-item:last-child {
  border-bottom: none;
}

.alert-type {
  font-weight: bold;
  margin-bottom: 4px;
}

.alert-message {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
  line-height: 1.4;
}

.alert-time {
  font-size: 12px;
  color: #909399;
}

.empty-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #909399;
}

.charts-container {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
  height: 300px;
}

.chart {
  width: 100%;
  height: calc(100% - 40px);
}
</style>
