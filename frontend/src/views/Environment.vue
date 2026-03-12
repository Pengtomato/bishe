<template>
  <div class="environment-container">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>环境数据管理</span>
          <el-button type="primary" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加数据
          </el-button>
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
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'Environment',
  components: {
    Plus
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
      formatDateTime
    }
  }
}
</script>

<style scoped>
.environment-container {
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
</style>