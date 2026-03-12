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
import { ref, onMounted } from 'vue'
import { environmentApi, videoApi, alertApi, deviceApi } from '../api'
import { DataAnalysis, VideoCamera, Warning, Monitor } from '@element-plus/icons-vue'

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
    
    // 加载统计数据
    const loadStats = async () => {
      try {
        // 环境数据统计
        const environmentData = await environmentApi.getAll()
        environmentCount.value = environmentData.length
        
        // 视频监控统计
        const videoData = await videoApi.getAll()
        videoCount.value = videoData.length
        
        // 预警统计
        const alertData = await alertApi.getAll()
        alertCount.value = alertData.length
        
        // 设备统计
        const deviceData = await deviceApi.getAll()
        deviceCount.value = deviceData.length
        
        // 最新环境数据
        const latestData = await environmentApi.getLatestData()
        if (latestData && latestData.length > 0) {
          latestEnvironment.value = latestData[0]
        }
        
        // 未处理预警
        const unprocessedData = await alertApi.getUnprocessed()
        unprocessedAlerts.value = unprocessedData
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (datetime) => {
      if (!datetime) return ''
      return new Date(datetime).toLocaleString()
    }
    
    onMounted(() => {
      loadStats()
    })
    
    return {
      environmentCount,
      videoCount,
      alertCount,
      deviceCount,
      latestEnvironment,
      unprocessedAlerts,
      formatDateTime
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
</style>
