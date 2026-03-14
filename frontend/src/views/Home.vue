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
        <div class="refresh-controls fade-in">
          <el-button type="primary" size="small" @click="handleManualRefresh" :loading="refreshing" class="btn-hover">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
          <span class="last-update-time">最后更新: {{ lastUpdateTime }}</span>
        </div>
        <el-row :gutter="20">
          <el-col :span="6" :xs="12" :sm="6">
            <el-card shadow="hover" class="stats-card card-hover fade-in delay-100">
              <div class="stats-item">
                <div class="stats-icon environment-icon pulse">
                  <el-icon><DataAnalysis /></el-icon>
                </div>
                <div class="stats-content">
                  <div class="stats-value">{{ environmentCount }}</div>
                  <div class="stats-label">环境数据</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6" :xs="12" :sm="6">
            <el-card shadow="hover" class="stats-card card-hover fade-in delay-200">
              <div class="stats-item">
                <div class="stats-icon video-icon pulse">
                  <el-icon><VideoCamera /></el-icon>
                </div>
                <div class="stats-content">
                  <div class="stats-value">{{ videoCount }}</div>
                  <div class="stats-label">视频监控</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6" :xs="12" :sm="6">
            <el-card shadow="hover" class="stats-card card-hover fade-in delay-300">
              <div class="stats-item">
                <div class="stats-icon alert-icon pulse">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="stats-content">
                  <div class="stats-value">{{ alertCount }}</div>
                  <div class="stats-label">预警信息</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6" :xs="12" :sm="6">
            <el-card shadow="hover" class="stats-card card-hover fade-in delay-400">
              <div class="stats-item">
                <div class="stats-icon device-icon pulse">
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
          <el-col :span="12" :xs="24" :sm="12">
            <el-card shadow="hover" class="chart-card card-hover fade-in delay-100" @click="navigateTo('environment')">
              <template #header>
                <div class="card-header-small">
                  <span>环境数据趋势</span>
                </div>
              </template>
              <div ref="environmentChartRef" class="chart"></div>
            </el-card>
          </el-col>
          <el-col :span="12" :xs="24" :sm="12">
            <el-card shadow="hover" class="chart-card card-hover fade-in delay-200" @click="navigateTo('device')">
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
          <el-col :span="12" :xs="24" :sm="12">
            <el-card shadow="hover" class="chart-card card-hover fade-in delay-300" @click="navigateTo('alert')">
              <template #header>
                <div class="card-header-small">
                  <span>预警级别分布</span>
                </div>
              </template>
              <div ref="alertLevelChartRef" class="chart"></div>
            </el-card>
          </el-col>
          <el-col :span="12" :xs="24" :sm="12">
            <el-card shadow="hover" class="chart-card card-hover fade-in delay-400" @click="navigateTo('video')">
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
          <el-col :span="12" :xs="24" :sm="12">
            <el-card shadow="hover" class="status-card card-hover fade-in delay-100">
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
          <el-col :span="12" :xs="24" :sm="12">
            <el-card shadow="hover" class="status-card card-hover fade-in delay-200">
              <template #header>
                <div class="card-header-small">
                  <span>未处理预警</span>
                </div>
              </template>
              <div v-if="unprocessedAlerts.length > 0" class="alert-list">
                <el-list>
                  <el-list-item v-for="(alert, index) in unprocessedAlerts.slice(0, 5)" :key="alert.id" class="fade-in" :style="{ animationDelay: `${0.1 * index}s` }">
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

<script src="./Home.js"></script>

<style scoped src="./Home.css"></style>
