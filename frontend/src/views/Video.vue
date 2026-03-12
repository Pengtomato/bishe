<template>
  <div class="video-container">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>视频监控管理</span>
          <el-button type="primary" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加监控
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="监控名称">
          <el-input v-model="searchForm.name" placeholder="请输入监控名称" style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 120px">
            <el-option label="在线" value="在线" />
            <el-option label="离线" value="离线" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="videoMonitors" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="监控名称" width="150" />
        <el-table-column prop="deviceId" label="设备ID" width="120" />
        <el-table-column prop="rtspUrl" label="RTSP地址" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '在线' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" @click="handleView(scope.row)">
              查看
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
        <el-form-item label="监控名称" required>
          <el-input v-model="formData.name" placeholder="请输入监控名称" />
        </el-form-item>
        <el-form-item label="设备ID" required>
          <el-input v-model="formData.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="RTSP地址">
          <el-input v-model="formData.rtspUrl" placeholder="请输入RTSP地址" />
        </el-form-item>
        <el-form-item label="状态" required>
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="在线" value="在线" />
            <el-option label="离线" value="离线" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="formData.location" placeholder="请输入位置" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 视频查看对话框 -->
    <el-dialog v-model="viewVisible" title="视频监控" width="800px">
      <div v-if="currentVideo" class="video-player">
        <el-alert
          title="视频流地址"
          :closable="false"
          type="info"
          show-icon
          class="mb-4"
        >
          {{ currentVideo.rtspUrl }}
        </el-alert>
        <div class="video-placeholder">
          <el-icon class="video-icon"><VideoCamera /></el-icon>
          <p>视频播放区域</p>
          <p class="text-gray">实际项目中，这里会集成视频播放器</p>
        </div>
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
import { videoApi } from '../api'
import { Plus, VideoCamera } from '@element-plus/icons-vue'

export default {
  name: 'Video',
  components: {
    Plus,
    VideoCamera
  },
  setup() {
    const videoMonitors = ref([])
    const dialogVisible = ref(false)
    const viewVisible = ref(false)
    const dialogTitle = ref('添加视频监控')
    const currentVideo = ref(null)
    const searchForm = ref({
      name: '',
      status: ''
    })
    const formData = ref({
      id: '',
      name: '',
      deviceId: '',
      rtspUrl: '',
      status: '离线',
      location: ''
    })
    
    // 加载视频监控数据
    const loadVideoMonitors = async () => {
      try {
        const data = await videoApi.getAll()
        videoMonitors.value = data
      } catch (error) {
        console.error('加载视频监控数据失败:', error)
      }
    }
    
    // 搜索视频监控
    const handleSearch = async () => {
      try {
        if (searchForm.value.name) {
          const data = await videoApi.searchByName(searchForm.value.name)
          videoMonitors.value = data
        } else if (searchForm.value.status) {
          const data = await videoApi.getByStatus(searchForm.value.status)
          videoMonitors.value = data
        } else {
          await loadVideoMonitors()
        }
      } catch (error) {
        console.error('搜索视频监控失败:', error)
      }
    }
    
    // 重置表单
    const resetForm = () => {
      searchForm.value.name = ''
      searchForm.value.status = ''
      loadVideoMonitors()
    }
    
    // 打开添加对话框
    const handleAdd = () => {
      dialogTitle.value = '添加视频监控'
      formData.value = {
        id: '',
        name: '',
        deviceId: '',
        rtspUrl: '',
        status: '离线',
        location: ''
      }
      dialogVisible.value = true
    }
    
    // 打开编辑对话框
    const handleEdit = (row) => {
      dialogTitle.value = '编辑视频监控'
      formData.value = { ...row }
      dialogVisible.value = true
    }
    
    // 打开视频查看对话框
    const handleView = (row) => {
      currentVideo.value = row
      viewVisible.value = true
    }
    
    // 提交表单
    const handleSubmit = async () => {
      try {
        if (formData.value.id) {
          await videoApi.update(formData.value.id, formData.value)
        } else {
          await videoApi.create(formData.value)
        }
        dialogVisible.value = false
        await loadVideoMonitors()
      } catch (error) {
        console.error('提交视频监控数据失败:', error)
      }
    }
    
    // 删除视频监控
    const handleDelete = async (id) => {
      try {
        await videoApi.delete(id)
        await loadVideoMonitors()
      } catch (error) {
        console.error('删除视频监控失败:', error)
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (datetime) => {
      if (!datetime) return ''
      return new Date(datetime).toLocaleString()
    }
    
    onMounted(() => {
      loadVideoMonitors()
    })
    
    return {
      videoMonitors,
      dialogVisible,
      viewVisible,
      dialogTitle,
      currentVideo,
      searchForm,
      formData,
      handleSearch,
      resetForm,
      handleAdd,
      handleEdit,
      handleView,
      handleSubmit,
      handleDelete,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.video-container {
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

.video-player {
  width: 100%;
}

.video-placeholder {
  width: 100%;
  height: 400px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 1px solid #e4e7ed;
}

.video-icon {
  font-size: 48px;
  color: #909399;
  margin-bottom: 16px;
}

.text-gray {
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
}
</style>