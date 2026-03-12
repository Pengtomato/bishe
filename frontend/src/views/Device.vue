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
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button v-if="scope.row.status === '关闭'" type="success" size="small" @click="handleControl(scope.row.id, '开启')">
              开启
            </el-button>
            <el-button v-if="scope.row.status === '开启'" type="warning" size="small" @click="handleControl(scope.row.id, '关闭')">
              关闭
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
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { deviceApi } from '../api'
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'Device',
  components: {
    Plus
  },
  setup() {
    const devices = ref([])
    const dialogVisible = ref(false)
    const paramsVisible = ref(false)
    const dialogTitle = ref('添加设备')
    const currentDevice = ref(null)
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
    
    // 加载设备数据
    const loadDevices = async () => {
      try {
        const data = await deviceApi.getAll()
        devices.value = data
      } catch (error) {
        console.error('加载设备数据失败:', error)
      }
    }
    
    // 搜索设备
    const handleSearch = async () => {
      try {
        if (searchForm.value.name) {
          const data = await deviceApi.searchByName(searchForm.value.name)
          devices.value = data
        } else if (searchForm.value.deviceType) {
          const data = await deviceApi.getByType(searchForm.value.deviceType)
          devices.value = data
        } else if (searchForm.value.status) {
          const data = await deviceApi.getByStatus(searchForm.value.status)
          devices.value = data
        } else {
          await loadDevices()
        }
      } catch (error) {
        console.error('搜索设备失败:', error)
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
    
    // 提交表单
    const handleSubmit = async () => {
      try {
        if (formData.value.id) {
          await deviceApi.update(formData.value.id, formData.value)
        } else {
          await deviceApi.create(formData.value)
        }
        dialogVisible.value = false
        await loadDevices()
      } catch (error) {
        console.error('提交设备数据失败:', error)
      }
    }
    
    // 控制设备
    const handleControl = async (id, status) => {
      try {
        await deviceApi.updateStatus(id, status)
        await loadDevices()
      } catch (error) {
        console.error('控制设备失败:', error)
      }
    }
    
    // 删除设备
    const handleDelete = async (id) => {
      try {
        await deviceApi.delete(id)
        await loadDevices()
      } catch (error) {
        console.error('删除设备失败:', error)
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
      dialogTitle,
      currentDevice,
      searchForm,
      formData,
      handleSearch,
      resetForm,
      handleAdd,
      handleEdit,
      handleViewParams,
      handleSubmit,
      handleControl,
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
}

.params-detail {
  width: 100%;
}

pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 0;
}
</style>