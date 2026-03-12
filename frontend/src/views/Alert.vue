<template>
  <div class="alert-container">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>预警管理</span>
          <el-button type="danger" size="small" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="预警类型">
          <el-input v-model="searchForm.type" placeholder="请输入预警类型" style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 120px">
            <el-option label="未处理" value="未处理" />
            <el-option label="已处理" value="已处理" />
            <el-option label="已忽略" value="已忽略" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="searchForm.level" placeholder="请选择级别" style="width: 120px">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="warning" @click="handleUnprocessed">未处理预警</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="alerts" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="预警类型" width="150" />
        <el-table-column prop="level" label="级别" width="80">
          <template #default="scope">
            <el-tag :type="getLevelType(scope.row.level)">
              {{ scope.row.level }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceId" label="设备ID" width="120" />
        <el-table-column prop="message" label="预警信息" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="timestamp" label="预警时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">
              详情
            </el-button>
            <el-button v-if="scope.row.status === '未处理'" type="success" size="small" @click="handleProcess(scope.row.id, '已处理')">
              处理
            </el-button>
            <el-button v-if="scope.row.status === '未处理'" type="warning" size="small" @click="handleProcess(scope.row.id, '已忽略')">
              忽略
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 预警详情对话框 -->
    <el-dialog v-model="viewVisible" title="预警详情" width="500px">
      <div v-if="currentAlert" class="alert-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="预警类型">{{ currentAlert.type }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">{{ currentAlert.level }}</el-descriptions-item>
          <el-descriptions-item label="设备ID">{{ currentAlert.deviceId }}</el-descriptions-item>
          <el-descriptions-item label="预警信息">{{ currentAlert.message }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">{{ currentAlert.status }}</el-descriptions-item>
          <el-descriptions-item label="预警时间">{{ formatDateTime(currentAlert.timestamp) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentAlert.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { alertApi } from '../api'
import { Refresh } from '@element-plus/icons-vue'

export default {
  name: 'Alert',
  components: {
    Refresh
  },
  setup() {
    const alerts = ref([])
    const viewVisible = ref(false)
    const currentAlert = ref(null)
    const searchForm = ref({
      type: '',
      status: '',
      level: ''
    })
    
    // 加载预警数据
    const loadAlerts = async () => {
      try {
        const data = await alertApi.getAll()
        alerts.value = data
      } catch (error) {
        console.error('加载预警数据失败:', error)
      }
    }
    
    // 搜索预警
    const handleSearch = async () => {
      try {
        if (searchForm.value.type) {
          const data = await alertApi.getByType(searchForm.value.type)
          alerts.value = data
        } else if (searchForm.value.status) {
          const data = await alertApi.getByStatus(searchForm.value.status)
          alerts.value = data
        } else if (searchForm.value.level) {
          const data = await alertApi.getByLevel(searchForm.value.level)
          alerts.value = data
        } else {
          await loadAlerts()
        }
      } catch (error) {
        console.error('搜索预警失败:', error)
      }
    }
    
    // 重置表单
    const resetForm = () => {
      searchForm.value.type = ''
      searchForm.value.status = ''
      searchForm.value.level = ''
      loadAlerts()
    }
    
    // 查看未处理预警
    const handleUnprocessed = async () => {
      try {
        const data = await alertApi.getUnprocessed()
        alerts.value = data
      } catch (error) {
        console.error('获取未处理预警失败:', error)
      }
    }
    
    // 刷新预警数据
    const handleRefresh = () => {
      loadAlerts()
    }
    
    // 打开预警详情对话框
    const handleView = (row) => {
      currentAlert.value = row
      viewVisible.value = true
    }
    
    // 处理预警
    const handleProcess = async (id, status) => {
      try {
        await alertApi.process(id, status)
        await loadAlerts()
      } catch (error) {
        console.error('处理预警失败:', error)
      }
    }
    
    // 删除预警
    const handleDelete = async (id) => {
      try {
        await alertApi.delete(id)
        await loadAlerts()
      } catch (error) {
        console.error('删除预警失败:', error)
      }
    }
    
    // 根据预警级别获取标签类型
    const getLevelType = (level) => {
      switch (level) {
        case '高': return 'danger'
        case '中': return 'warning'
        case '低': return 'info'
        default: return 'default'
      }
    }
    
    // 根据预警状态获取标签类型
    const getStatusType = (status) => {
      switch (status) {
        case '未处理': return 'danger'
        case '已处理': return 'success'
        case '已忽略': return 'warning'
        default: return 'default'
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (datetime) => {
      if (!datetime) return ''
      return new Date(datetime).toLocaleString()
    }
    
    onMounted(() => {
      loadAlerts()
    })
    
    return {
      alerts,
      viewVisible,
      currentAlert,
      searchForm,
      handleSearch,
      resetForm,
      handleUnprocessed,
      handleRefresh,
      handleView,
      handleProcess,
      handleDelete,
      getLevelType,
      getStatusType,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.alert-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.alert-detail {
  width: 100%;
}
</style>