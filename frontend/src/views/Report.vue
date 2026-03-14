<template>
  <div class="report">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>数据统计报表</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="报表类型">
          <el-select v-model="searchForm.reportType" placeholder="请选择报表类型" @change="handleReportTypeChange">
            <el-option label="日报" value="daily" />
            <el-option label="周报" value="weekly" />
            <el-option label="月报" value="monthly" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :disabled-date="disabledDate"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generateReport">生成报表</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="success" @click="exportReport" :disabled="!reportData.length">导出报表</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card shadow="hover" class="mb-4" v-if="reportData.length > 0">
      <template #header>
        <div class="card-header">
          <span>{{ reportTitle }}</span>
        </div>
      </template>
      
      <div class="report-summary">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">环境数据总数</div>
              <div class="summary-value">{{ reportSummary.environmentCount }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">预警总数</div>
              <div class="summary-value">{{ reportSummary.alertCount }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">设备总数</div>
              <div class="summary-value">{{ reportSummary.deviceCount }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">视频监控数</div>
              <div class="summary-value">{{ reportSummary.videoCount }}</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <el-card shadow="hover" class="mb-4" v-if="reportData.length > 0">
      <template #header>
        <div class="card-header">
          <span>数据趋势</span>
        </div>
      </template>
      
      <div ref="trendChartRef" class="chart"></div>
    </el-card>
    
    <el-card shadow="hover" v-if="reportData.length > 0">
      <template #header>
        <div class="card-header">
          <span>详细数据</span>
        </div>
      </template>
      
      <el-table :data="reportData" style="width: 100%" stripe>
        <el-table-column prop="date" label="日期" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.date) }}
          </template>
        </el-table-column>
        <el-table-column prop="environmentCount" label="环境数据" width="120" />
        <el-table-column prop="alertCount" label="预警数量" width="120" />
        <el-table-column prop="deviceCount" label="设备数量" width="120" />
        <el-table-column prop="videoCount" label="视频监控" width="120" />
        <el-table-column prop="avgTemperature" label="平均温度" width="120">
          <template #default="scope">
            {{ scope.row.avgTemperature ? scope.row.avgTemperature.toFixed(1) : '-' }}℃
          </template>
        </el-table-column>
        <el-table-column prop="avgHumidity" label="平均湿度" width="120">
          <template #default="scope">
            {{ scope.row.avgHumidity ? scope.row.avgHumidity.toFixed(1) : '-' }}%
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { environmentApi, alertApi, deviceApi, videoApi } from '../api'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

export default {
  name: 'Report',
  setup() {
    const searchForm = reactive({
      reportType: 'daily',
      dateRange: []
    })
    
    const reportData = ref([])
    const reportSummary = ref({
      environmentCount: 0,
      alertCount: 0,
      deviceCount: 0,
      videoCount: 0
    })
    const reportTitle = ref('日报')
    const trendChartRef = ref(null)
    let trendChart = null
    
    // 禁用未来日期
    const disabledDate = (time) => {
      return time.getTime() > Date.now()
    }
    
    // 报表类型变化
    const handleReportTypeChange = (type) => {
      const typeMap = {
        daily: '日报',
        weekly: '周报',
        monthly: '月报'
      }
      reportTitle.value = typeMap[type]
      resetForm()
    }
    
    // 日期变化
    const handleDateChange = () => {
      // 自动生成报表
      if (searchForm.dateRange && searchForm.dateRange.length === 2) {
        generateReport()
      }
    }
    
    // 生成报表
    const generateReport = async () => {
      if (!searchForm.dateRange || searchForm.dateRange.length !== 2) {
        ElMessage.warning('请选择日期范围')
        return
      }
      
      try {
        const [startDate, endDate] = searchForm.dateRange
        const environmentData = await environmentApi.getAll() || []
        const alertData = await alertApi.getAll() || []
        const deviceData = await deviceApi.getAll() || []
        const videoData = await videoApi.getAll() || []
        
        // 根据报表类型生成数据
        const data = generateReportData(startDate, endDate, environmentData, alertData, deviceData, videoData)
        reportData.value = data
        
        // 计算汇总数据
        calculateSummary(data)
        
        // 初始化图表
        initTrendChart(data)
        
        ElMessage.success('报表生成成功')
      } catch (error) {
        console.error('生成报表失败:', error)
        ElMessage.error('生成报表失败，请重试')
      }
    }
    
    // 生成报表数据
    const generateReportData = (startDate, endDate, environmentData, alertData, deviceData, videoData) => {
      const start = new Date(startDate)
      const end = new Date(endDate)
      const data = []
      
      // 根据报表类型确定时间间隔
      let interval = 1 // 天
      if (searchForm.reportType === 'weekly') {
        interval = 7 // 周
      } else if (searchForm.reportType === 'monthly') {
        interval = 30 // 月
      }
      
      // 生成时间范围内的数据点
      const currentDate = new Date(start)
      while (currentDate <= end) {
        const dateStr = currentDate.toISOString().split('T')[0]
        
        // 筛选当天的数据
        const dayEnvironmentData = environmentData.filter(item => 
          item.timestamp && item.timestamp.startsWith(dateStr)
        )
        const dayAlertData = alertData.filter(item => 
          item.timestamp && item.timestamp.startsWith(dateStr)
        )
        
        // 计算统计数据
        const environmentCount = dayEnvironmentData.length
        const alertCount = dayAlertData.length
        const deviceCount = deviceData.length
        const videoCount = videoData.length
        
        // 计算平均值
        const avgTemperature = dayEnvironmentData.length > 0 
          ? dayEnvironmentData.reduce((sum, item) => sum + (item.temperature || 0), 0) / dayEnvironmentData.length 
          : null
        const avgHumidity = dayEnvironmentData.length > 0 
          ? dayEnvironmentData.reduce((sum, item) => sum + (item.humidity || 0), 0) / dayEnvironmentData.length 
          : null
        
        data.push({
          date: dateStr,
          environmentCount,
          alertCount,
          deviceCount,
          videoCount,
          avgTemperature,
          avgHumidity
        })
        
        // 移动到下一个时间点
        currentDate.setDate(currentDate.getDate() + interval)
      }
      
      return data
    }
    
    // 计算汇总数据
    const calculateSummary = (data) => {
      reportSummary.value = {
        environmentCount: data.reduce((sum, item) => sum + item.environmentCount, 0),
        alertCount: data.reduce((sum, item) => sum + item.alertCount, 0),
        deviceCount: data[0]?.deviceCount || 0,
        videoCount: data[0]?.videoCount || 0
      }
    }
    
    // 初始化趋势图表
    const initTrendChart = (data) => {
      if (!trendChartRef.value) return
      
      trendChart = echarts.init(trendChartRef.value)
      
      const dates = data.map(item => item.date)
      const environmentData = data.map(item => item.environmentCount)
      const alertData = data.map(item => item.alertCount)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['环境数据', '预警数量']
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '环境数据',
            type: 'line',
            data: environmentData,
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '预警数量',
            type: 'line',
            data: alertData,
            smooth: true,
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      }
      
      trendChart.setOption(option)
    }
    
    // 重置表单
    const resetForm = () => {
      searchForm.dateRange = []
      reportData.value = []
      reportSummary.value = {
        environmentCount: 0,
        alertCount: 0,
        deviceCount: 0,
        videoCount: 0
      }
      
      if (trendChart) {
        trendChart.dispose()
        trendChart = null
      }
    }
    
    // 导出报表
    const exportReport = () => {
      if (reportData.value.length === 0) {
        ElMessage.warning('请先生成报表')
        return
      }
      
      // 生成CSV格式的报表
      const headers = ['日期', '环境数据', '预警数量', '设备数量', '视频监控', '平均温度', '平均湿度']
      const csvContent = [
        headers.join(','),
        ...reportData.value.map(row => 
          headers.map(header => row[header] || '-').join(',')
        )
      ].join('\n')
      
      // 创建下载链接
      const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.setAttribute('href', url)
      link.setAttribute('download', `${reportTitle.value}_${new Date().toISOString().split('T')[0]}.csv`)
      link.style.visibility = 'hidden'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      ElMessage.success('报表导出成功')
    }
    
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      return new Date(dateStr).toLocaleDateString()
    }
    
    // 响应式调整图表大小
    const handleResize = () => {
      trendChart?.resize()
    }
    
    onMounted(() => {
      window.addEventListener('resize', handleResize)
    })
    
    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      trendChart?.dispose()
    })
    
    return {
      searchForm,
      reportData,
      reportSummary,
      reportTitle,
      trendChartRef,
      disabledDate,
      handleReportTypeChange,
      handleDateChange,
      generateReport,
      resetForm,
      exportReport,
      formatDate
    }
  }
}
</script>

<style scoped>
.report {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-4 {
  margin-bottom: 16px;
}

.search-form {
  padding: 20px;
}

.report-summary {
  padding: 20px;
}

.summary-item {
  text-align: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
}

.summary-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart {
  width: 100%;
  height: 400px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .report {
    padding: 10px;
  }
  
  .search-form {
    padding: 10px;
  }
  
  .summary-item {
    padding: 10px;
  }
  
  .chart {
    height: 300px;
  }
}
</style>