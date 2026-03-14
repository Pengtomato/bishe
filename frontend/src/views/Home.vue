<template>
  <div class="home">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>平台概览</span>
        </div>
      </template>
      
      <div class="dashboard-stats">
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
import { DataAnalysis, VideoCamera, Warning, Monitor } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

export default {
  name: 'Home',
  components: {
    DataAnalysis,
    VideoCamera,
    Warning,
    Monitor
  },
  setup() {
    const environmentCount = ref(0)
    const videoCount = ref(0)
    const alertCount = ref(0)
    const deviceCount = ref(0)
    const latestEnvironment = ref(null)
    const unprocessedAlerts = ref([])
    
    // 图表引用
    const environmentChartRef = ref(null)
    const deviceStatusChartRef = ref(null)
    const alertLevelChartRef = ref(null)
    const videoStatusChartRef = ref(null)
    
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
          console.log('跳转到环境数据页面')
          // 这里可以使用路由跳转，例如：router.push('/environment')
          break
        case 'device':
          // 跳转到设备管理页面
          console.log('跳转到设备管理页面')
          // router.push('/device')
          break
        case 'alert':
          // 跳转到预警管理页面
          console.log('跳转到预警管理页面')
          // router.push('/alert')
          break
        case 'video':
          // 跳转到视频监控页面
          console.log('跳转到视频监控页面')
          // router.push('/video')
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
      window.addEventListener('resize', handleResize)
    })
    
    onUnmounted(() => {
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
}

.environment-data {
  margin-top: 10px;
}

.alert-list {
  margin-top: 10px;
  max-height: 200px;
  overflow-y: auto;
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
