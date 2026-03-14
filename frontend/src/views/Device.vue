<template>
  <div class="device-container">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>设备控制管理</span>
          <el-button type="primary" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加设备
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.name" placeholder="请输入设备名称" style="width: 150px" />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="searchForm.deviceType" placeholder="请选择设备类型" style="width: 120px">
            <el-option label="风机" value="风机" />
            <el-option label="水泵" value="水泵" />
            <el-option label="照明" value="照明" />
            <el-option label="温控" value="温控" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 120px">
            <el-option label="开启" value="开启" />
            <el-option label="关闭" value="关闭" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="devices" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="设备名称" width="150" />
        <el-table-column prop="deviceId" label="设备ID" width="120" />
        <el-table-column prop="deviceType" label="设备类型" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '开启' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column prop="controlParams" label="控制参数" width="200">
          <template #default="scope">
            <el-button type="text" @click="handleViewParams(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="success" size="small" @click="handleControlDialog(scope.row)">
              <el-icon><Setting /></el-icon>
              控制
            </el-button>
            <el-button v-if="scope.row.status === '关闭'" type="info" size="small" @click="handleQuickControl(scope.row.id, '开启')">
              开启
            </el-button>
            <el-button v-if="scope.row.status === '开启'" type="warning" size="small" @click="handleQuickControl(scope.row.id, '关闭')">
              关闭
            </el-button>
            <el-button type="info" size="small" @click="handleViewHistory(scope.row)">
              <el-icon><Clock /></el-icon>
              历史
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
        <el-form-item label="设备名称" required>
          <el-input v-model="formData.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备ID" required>
          <el-input v-model="formData.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="设备类型" required>
          <el-select v-model="formData.deviceType" placeholder="请选择设备类型">
            <el-option label="风机" value="风机" />
            <el-option label="水泵" value="水泵" />
            <el-option label="照明" value="照明" />
            <el-option label="温控" value="温控" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" required>
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="开启" value="开启" />
            <el-option label="关闭" value="关闭" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="formData.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="控制参数">
          <el-input
            v-model="formData.controlParams"
            type="textarea"
            placeholder="请输入控制参数（JSON格式）"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 控制参数对话框 -->
    <el-dialog v-model="paramsVisible" title="控制参数" width="500px">
      <div v-if="currentDevice" class="params-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="设备名称">{{ currentDevice.name }}</el-descriptions-item>
          <el-descriptions-item label="设备ID">{{ currentDevice.deviceId }}</el-descriptions-item>
          <el-descriptions-item label="设备类型">{{ currentDevice.deviceType }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">{{ currentDevice.status }}</el-descriptions-item>
          <el-descriptions-item label="控制参数">
            <pre>{{ formatJson(currentDevice.controlParams) }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="paramsVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 设备控制对话框 -->
    <el-dialog v-model="controlVisible" :title="`控制 ${currentDevice?.name || '设备'}`" width="500px">
      <div v-if="currentDevice" class="control-panel">
        <el-form :model="controlForm" label-width="100px">
          <el-form-item label="设备状态">
            <el-switch v-model="controlForm.status" active-text="开启" inactive-text="关闭" />
          </el-form-item>
          
          <!-- 风机控制参数 -->
          <template v-if="currentDevice.deviceType === '风机'">
            <el-form-item label="风速">
              <el-slider v-model="controlForm.speed" :min="0" :max="100" show-input />
            </el-form-item>
            <el-form-item label="运行模式">
              <el-select v-model="controlForm.mode" placeholder="请选择运行模式">
                <el-option label="自动" value="auto" />
                <el-option label="手动" value="manual" />
              </el-select>
            </el-form-item>
          </template>
          
          <!-- 水泵控制参数 -->
          <template v-else-if="currentDevice.deviceType === '水泵'">
            <el-form-item label="水量">
              <el-slider v-model="controlForm.waterFlow" :min="0" :max="100" show-input />
            </el-form-item>
            <el-form-item label="运行时间">
              <el-input-number v-model="controlForm.runTime" :min="1" :max="60" :step="1" label="分钟" />
            </el-form-item>
          </template>
          
          <!-- 照明控制参数 -->
          <template v-else-if="currentDevice.deviceType === '照明'">
            <el-form-item label="亮度">
              <el-slider v-model="controlForm.brightness" :min="0" :max="100" show-input />
            </el-form-item>
            <el-form-item label="灯光颜色">
              <el-color-picker v-model="controlForm.color" />
            </el-form-item>
            <el-form-item label="定时开关">
              <el-time-picker v-model="controlForm.schedule" range />
            </el-form-item>
          </template>
          
          <!-- 温控设备控制参数 -->
          <template v-else-if="currentDevice.deviceType === '温控'">
            <el-form-item label="目标温度">
              <el-input-number v-model="controlForm.targetTemp" :min="0" :max="50" :step="0.5" label="℃" />
            </el-form-item>
            <el-form-item label="温度误差">
              <el-input-number v-model="controlForm.tempError" :min="0.1" :max="5" :step="0.1" label="℃" />
            </el-form-item>
            <el-form-item label="运行模式">
              <el-select v-model="controlForm.mode" placeholder="请选择运行模式">
                <el-option label="制冷" value="cool" />
                <el-option label="制热" value="heat" />
                <el-option label="自动" value="auto" />
              </el-select>
            </el-form-item>
          </template>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="controlVisible = false">取消</el-button>
          <el-button type="primary" @click="handleDeviceControl">确定控制</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 控制历史对话框 -->
    <el-dialog v-model="historyVisible" title="控制历史" width="600px">
      <div v-if="controlHistory.length > 0">
        <el-table :data="controlHistory" style="width: 100%" stripe>
          <el-table-column prop="time" label="控制时间" width="180" />
          <el-table-column prop="action" label="操作" width="120" />
          <el-table-column prop="params" label="控制参数">
            <template #default="scope">
              <pre>{{ formatJson(scope.row.params) }}</pre>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else class="empty-history">
        暂无控制历史
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="historyVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { deviceApi } from '../api'
import { Plus, Setting, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'Device',
  components: {
    Plus,
    Setting,
    Clock
  },
  setup() {
    const devices = ref([])
    const dialogVisible = ref(false)
    const paramsVisible = ref(false)
    const controlVisible = ref(false)
    const historyVisible = ref(false)
    const dialogTitle = ref('添加设备')
    const currentDevice = ref(null)
    const controlHistory = ref([])
    const searchForm = ref({
      name: '',
      deviceType: '',
      status: ''
    })
    const formData = ref({
      id: '',
      name: '',
      deviceId: '',
      deviceType: '风机',
      status: '关闭',
      location: '',
      controlParams: '{}'
    })
    const controlForm = ref({
      status: false,
      speed: 0,
      mode: 'auto',
      waterFlow: 0,
      runTime: 10,
      brightness: 50,
      color: '#ffffff',
      schedule: null,
      targetTemp: 25,
      tempError: 1
    })
    
    // 加载设备数据
    const loadDevices = async () => {
      try {
        const data = await deviceApi.getAll()
        devices.value = data
        ElMessage.success('设备数据加载成功')
      } catch (error) {
        console.error('加载设备数据失败:', error)
        ElMessage.error(`加载设备数据失败: ${error.message || '未知错误'}`)
      }
    }
    
    // 搜索设备
    const handleSearch = async () => {
      try {
        if (searchForm.value.name) {
          const data = await deviceApi.searchByName(searchForm.value.name)
          devices.value = data
          ElMessage.success(`搜索到 ${data.length} 条设备数据`)
        } else if (searchForm.value.deviceType) {
          const data = await deviceApi.getByType(searchForm.value.deviceType)
          devices.value = data
          ElMessage.success(`搜索到 ${data.length} 条设备数据`)
        } else if (searchForm.value.status) {
          const data = await deviceApi.getByStatus(searchForm.value.status)
          devices.value = data
          ElMessage.success(`搜索到 ${data.length} 条设备数据`)
        } else {
          await loadDevices()
        }
      } catch (error) {
        console.error('搜索设备失败:', error)
        ElMessage.error(`搜索设备失败: ${error.message || '未知错误'}`)
      }
    }
    
    // 重置表单
    const resetForm = () => {
      searchForm.value.name = ''
      searchForm.value.deviceType = ''
      searchForm.value.status = ''
      loadDevices()
    }
    
    // 打开添加对话框
    const handleAdd = () => {
      dialogTitle.value = '添加设备'
      formData.value = {
        id: '',
        name: '',
        deviceId: '',
        deviceType: '风机',
        status: '关闭',
        location: '',
        controlParams: '{}'
      }
      dialogVisible.value = true
    }
    
    // 打开编辑对话框
    const handleEdit = (row) => {
      dialogTitle.value = '编辑设备'
      formData.value = { ...row }
      dialogVisible.value = true
    }
    
    // 打开控制参数对话框
    const handleViewParams = (row) => {
      currentDevice.value = row
      paramsVisible.value = true
    }
    
    // 打开控制对话框
    const handleControlDialog = (row) => {
      currentDevice.value = row
      // 初始化控制表单
      controlForm.value = {
        status: row.status === '开启',
        speed: 0,
        mode: 'auto',
        waterFlow: 0,
        runTime: 10,
        brightness: 50,
        color: '#ffffff',
        schedule: null,
        targetTemp: 25,
        tempError: 1
      }
      // 尝试从控制参数中解析值
      try {
        if (row.controlParams) {
          const params = JSON.parse(row.controlParams)
          Object.assign(controlForm.value, params)
        }
      } catch (e) {
        console.error('解析控制参数失败:', e)
      }
      controlVisible.value = true
    }
    
    // 加载控制历史
    const loadControlHistory = (deviceId) => {
      // 模拟控制历史数据
      controlHistory.value = [
        {
          time: new Date().toLocaleString(),
          action: '开启设备',
          params: { status: true, speed: 50, mode: 'auto' }
        },
        {
          time: new Date(Date.now() - 3600000).toLocaleString(),
          action: '关闭设备',
          params: { status: false }
        },
        {
          time: new Date(Date.now() - 7200000).toLocaleString(),
          action: '调整参数',
          params: { status: true, speed: 70, mode: 'manual' }
        }
      ]
    }
    
    // 打开控制历史对话框
    const handleViewHistory = (row) => {
      currentDevice.value = row
      loadControlHistory(row.deviceId)
      historyVisible.value = true
    }
    
    // 提交表单
    const handleSubmit = async () => {
      try {
        if (formData.value.id) {
          await deviceApi.update(formData.value.id, formData.value)
          ElMessage.success('设备信息更新成功')
        } else {
          await deviceApi.create(formData.value)
          ElMessage.success('设备添加成功')
        }
        dialogVisible.value = false
        await loadDevices()
      } catch (error) {
        console.error('提交设备数据失败:', error)
        ElMessage.error(`提交设备数据失败: ${error.message || '未知错误'}`)
      }
    }
    
    // 控制设备
    const handleDeviceControl = async () => {
      try {
        // 构建控制参数
        const controlParams = {
          ...controlForm.value
        }
        
        // 更新设备状态
        const status = controlForm.value.status ? '开启' : '关闭'
        await deviceApi.updateStatus(currentDevice.value.id, status)
        
        // 更新控制参数
        await deviceApi.updateParams(currentDevice.value.id, JSON.stringify(controlParams))
        
        ElMessage.success('设备控制成功')
        controlVisible.value = false
        await loadDevices()
      } catch (error) {
        console.error('控制设备失败:', error)
        ElMessage.error(`控制设备失败: ${error.message || '未知错误'}`)
      }
    }
    
    // 控制设备（快速开启/关闭）
    const handleQuickControl = async (id, status) => {
      try {
        await deviceApi.updateStatus(id, status)
        ElMessage.success(`${status === '开启' ? '设备开启' : '设备关闭'}成功`)
        await loadDevices()
      } catch (error) {
        console.error('控制设备失败:', error)
        ElMessage.error(`控制设备失败: ${error.message || '未知错误'}`)
      }
    }
    
    // 删除设备
    const handleDelete = async (id) => {
      try {
        await deviceApi.delete(id)
        ElMessage.success('设备删除成功')
        await loadDevices()
      } catch (error) {
        console.error('删除设备失败:', error)
        ElMessage.error(`删除设备失败: ${error.message || '未知错误'}`)
      }
    }
    
    // 格式化 JSON
    const formatJson = (json) => {
      if (!json) return '{}'
      try {
        return JSON.stringify(JSON.parse(json), null, 2)
      } catch (e) {
        return json
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (datetime) => {
      if (!datetime) return ''
      return new Date(datetime).toLocaleString()
    }
    
    onMounted(() => {
      loadDevices()
    })
    
    return {
      devices,
      dialogVisible,
      paramsVisible,
      controlVisible,
      historyVisible,
      dialogTitle,
      currentDevice,
      controlHistory,
      searchForm,
      formData,
      controlForm,
      handleSearch,
      resetForm,
      handleAdd,
      handleEdit,
      handleViewParams,
      handleControlDialog,
      handleViewHistory,
      handleSubmit,
      handleQuickControl,
      handleDeviceControl,
      handleDelete,
      formatJson,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.device-container {
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
  gap: 10px;
}

.params-detail {
  width: 100%;
}

.control-panel {
  width: 100%;
}

pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 0;
  font-size: 12px;
}

.empty-history {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .device-container {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .el-table-column {
    min-width: 100px;
  }
  
  .el-table-column--fixed-right {
    width: 200px !important;
  }
  
  .el-button {
    margin-bottom: 5px;
  }
}

@media (max-width: 480px) {
  .el-table {
    font-size: 12px;
  }
  
  .el-table-column {
    min-width: 80px;
  }
  
  .el-button {
    padding: 4px 8px;
    font-size: 12px;
  }
}
</style>