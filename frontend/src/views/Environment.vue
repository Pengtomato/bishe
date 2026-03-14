<template>
  <div class="environment-container">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>环境数据管理</span>
          <div class="header-actions">
            <el-button type="primary" size="small" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加数据
            </el-button>
            <el-button type="success" size="small" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="设备ID">
          <el-input v-model="searchForm.deviceId" placeholder="请输入设备ID" style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="environmentData" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="deviceId" label="设备ID" width="120" />
        <el-table-column prop="temperature" label="温度(℃)" width="100" />
        <el-table-column prop="humidity" label="湿度(%)" width="100" />
        <el-table-column prop="co2" label="CO2(ppm)" width="100" />
        <el-table-column prop="ammonia" label="氨气(ppm)" width="100" />
        <el-table-column prop="lightIntensity" label="光照强度(lux)" width="120" />
        <el-table-column prop="timestamp" label="采集时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="设备ID" required>
          <el-input v-model="formData.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="温度(℃)" required>
          <el-input v-model.number="formData.temperature" placeholder="请输入温度" />
        </el-form-item>
        <el-form-item label="湿度(%)" required>
          <el-input v-model.number="formData.humidity" placeholder="请输入湿度" />
        </el-form-item>
        <el-form-item label="CO2(ppm)">
          <el-input v-model.number="formData.co2" placeholder="请输入CO2浓度" />
        </el-form-item>
        <el-form-item label="氨气(ppm)">
          <el-input v-model.number="formData.ammonia" placeholder="请输入氨气浓度" />
        </el-form-item>
        <el-form-item label="光照强度(lux)">
          <el-input v-model.number="formData.lightIntensity" placeholder="请输入光照强度" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { environmentApi } from '../api'
import { Plus, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'Environment',
  components: {
    Plus,
    Download
  },
  setup() {
    const environmentData = ref([])
    const dialogVisible = ref(false)
    const dialogTitle = ref('添加环境数据')
    const searchForm = ref({
      deviceId: ''
    })
    const formData = ref({
      id: '',
      deviceId: '',
      temperature: '',
      humidity: '',
      co2: '',
      ammonia: '',
      lightIntensity: '',
      timestamp: new Date().toISOString()
    })
    
    // 加载环境数据
    const loadEnvironmentData = async () => {
      try {
        const data = await environmentApi.getAll()
        environmentData.value = data
      } catch (error) {
        console.error('加载环境数据失败:', error)
      }
    }
    
    // 搜索环境数据
    const handleSearch = async () => {
      try {
        if (searchForm.value.deviceId) {
          const data = await environmentApi.getByDeviceId(searchForm.value.deviceId)
          environmentData.value = data
        } else {
          await loadEnvironmentData()
        }
      } catch (error) {
        console.error('搜索环境数据失败:', error)
      }
    }
    
    // 重置表单
    const resetForm = () => {
      searchForm.value.deviceId = ''
      loadEnvironmentData()
    }
    
    // 打开添加对话框
    const handleAdd = () => {
      dialogTitle.value = '添加环境数据'
      formData.value = {
        id: '',
        deviceId: '',
        temperature: '',
        humidity: '',
        co2: '',
        ammonia: '',
        lightIntensity: '',
        timestamp: new Date().toISOString()
      }
      dialogVisible.value = true
    }
    
    // 打开编辑对话框
    const handleEdit = (row) => {
      dialogTitle.value = '编辑环境数据'
      formData.value = { ...row }
      dialogVisible.value = true
    }
    
    // 提交表单
    const handleSubmit = async () => {
      try {
        if (formData.value.id) {
          await environmentApi.update(formData.value.id, formData.value)
        } else {
          await environmentApi.create(formData.value)
        }
        dialogVisible.value = false
        await loadEnvironmentData()
      } catch (error) {
        console.error('提交环境数据失败:', error)
      }
    }
    
    // 删除环境数据
    const handleDelete = async (id) => {
      try {
        await environmentApi.delete(id)
        await loadEnvironmentData()
      } catch (error) {
        console.error('删除环境数据失败:', error)
      }
    }
    
    // 导出环境数据
    const handleExport = () => {
      if (environmentData.value.length === 0) {
        ElMessage.warning('暂无数据可导出')
        return
      }
      
      try {
        // 生成CSV格式的数据
        const headers = ['ID', '设备ID', '温度(℃)', '湿度(%)', 'CO2(ppm)', '氨气(ppm)', '光照强度(lux)', '采集时间']
        const csvContent = [
          headers.join(','),
          ...environmentData.value.map(row => 
            headers.map(header => {
              const key = header.toLowerCase().replace(/[()]/g, '').replace(/[^a-z]/g, '')
              let value = row[key]
              if (key === 'timestamp' && value) {
                value = new Date(value).toLocaleString()
              }
              return value || '-'
            }).join(',')
          )
        ].join('\n')
        
        // 创建下载链接
        const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
        const link = document.createElement('a')
        const url = URL.createObjectURL(blob)
        link.setAttribute('href', url)
        link.setAttribute('download', `环境数据_${new Date().toISOString().split('T')[0]}.csv`)
        link.style.visibility = 'hidden'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        
        ElMessage.success('数据导出成功')
      } catch (error) {
        console.error('导出数据失败:', error)
        ElMessage.error('导出数据失败，请重试')
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (datetime) => {
      if (!datetime) return ''
      return new Date(datetime).toLocaleString()
    }
    
    onMounted(() => {
      loadEnvironmentData()
    })
    
    return {
      environmentData,
      dialogVisible,
      dialogTitle,
      searchForm,
      formData,
      handleSearch,
      resetForm,
      handleAdd,
      handleEdit,
      handleSubmit,
      handleDelete,
      handleExport,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.environment-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
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

/* 卡片样式 */
.el-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 表格样式 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
}

.el-table tr:hover {
  background-color: #f0f9eb;
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 16px;
}

/* 按钮样式 */
.el-button {
  border-radius: 4px;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 输入框样式 */
.el-input {
  border-radius: 4px;
  transition: all 0.3s ease;
}

.el-input:focus {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 对话框样式 */
.el-dialog {
  border-radius: 8px;
  overflow: hidden;
}

.el-dialog__header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

/* 加载动画 */
.el-loading {
  background-color: rgba(255, 255, 255, 0.8);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .environment-container {
    padding: 10px;
  }
  
  .el-table {
    font-size: 14px;
  }
  
  .el-table-column {
    width: auto !important;
  }
}
</style>