<template>
  <div class="alert-rule">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>预警规则配置</span>
          <el-button type="primary" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加规则
          </el-button>
        </div>
      </template>
      
      <el-table :data="alertRules" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="预警类型" width="150">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)">
              {{ getTypeLabel(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="预警级别" width="120">
          <template #default="scope">
            <el-tag :type="getLevelTagType(scope.row.level)">
              {{ scope.row.level }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="threshold" label="阈值" width="120" />
        <el-table-column prop="operator" label="操作符" width="100" />
        <el-table-column prop="message" label="预警消息" min-width="200" />
        <el-table-column prop="active" label="状态" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.active" @change="handleStatusChange(scope.row)" />
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
    
    <!-- 规则配置对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="预警类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择预警类型">
            <el-option label="温度" value="temperature" />
            <el-option label="湿度" value="humidity" />
            <el-option label="CO2" value="co2" />
            <el-option label="氨气" value="ammonia" />
            <el-option label="光照" value="light" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警级别" prop="level">
          <el-select v-model="formData.level" placeholder="请选择预警级别">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
            <el-option label="紧急" value="紧急" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作符" prop="operator">
          <el-select v-model="formData.operator" placeholder="请选择操作符">
            <el-option label="大于" value=">" />
            <el-option label="小于" value="<" />
            <el-option label="等于" value="=" />
            <el-option label="大于等于" value=">=" />
            <el-option label="小于等于" value="<=" />
          </el-select>
        </el-form-item>
        <el-form-item label="阈值" prop="threshold">
          <el-input v-model.number="formData.threshold" placeholder="请输入阈值" />
        </el-form-item>
        <el-form-item label="预警消息" prop="message">
          <el-input v-model="formData.message" type="textarea" placeholder="请输入预警消息" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.active" />
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
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'AlertRule',
  components: {
    Plus
  },
  setup() {
    const alertRules = ref([])
    const dialogVisible = ref(false)
    const dialogTitle = ref('添加预警规则')
    const formRef = ref(null)
    
    const formData = reactive({
      id: '',
      type: '',
      level: '低',
      operator: '>',
      threshold: '',
      message: '',
      active: true
    })
    
    const rules = {
      type: [
        { required: true, message: '请选择预警类型', trigger: 'blur' }
      ],
      level: [
        { required: true, message: '请选择预警级别', trigger: 'blur' }
      ],
      operator: [
        { required: true, message: '请选择操作符', trigger: 'blur' }
      ],
      threshold: [
        { required: true, message: '请输入阈值', trigger: 'blur' },
        { type: 'number', message: '请输入数字', trigger: 'blur' }
      ],
      message: [
        { required: true, message: '请输入预警消息', trigger: 'blur' }
      ]
    }
    
    // 加载预警规则
    const loadAlertRules = () => {
      // 从本地存储加载预警规则
      const rules = localStorage.getItem('alertRules')
      if (rules) {
        alertRules.value = JSON.parse(rules)
      } else {
        // 默认预警规则
        alertRules.value = [
          { id: 1, type: 'temperature', level: '高', operator: '>', threshold: 35, message: '温度过高', active: true },
          { id: 2, type: 'temperature', level: '低', operator: '<', threshold: 0, message: '温度过低', active: true },
          { id: 3, type: 'humidity', level: '高', operator: '>', threshold: 90, message: '湿度过高', active: true },
          { id: 4, type: 'humidity', level: '低', operator: '<', threshold: 30, message: '湿度过低', active: true },
          { id: 5, type: 'co2', level: '高', operator: '>', threshold: 2000, message: 'CO2浓度过高', active: true },
          { id: 6, type: 'ammonia', level: '高', operator: '>', threshold: 50, message: '氨气浓度过高', active: true }
        ]
        saveAlertRules()
      }
    }
    
    // 保存预警规则
    const saveAlertRules = () => {
      localStorage.setItem('alertRules', JSON.stringify(alertRules.value))
    }
    
    // 打开添加对话框
    const handleAdd = () => {
      dialogTitle.value = '添加预警规则'
      formData.id = ''
      formData.type = ''
      formData.level = '低'
      formData.operator = '>'
      formData.threshold = ''
      formData.message = ''
      formData.active = true
      dialogVisible.value = true
    }
    
    // 打开编辑对话框
    const handleEdit = (row) => {
      dialogTitle.value = '编辑预警规则'
      formData.id = row.id
      formData.type = row.type
      formData.level = row.level
      formData.operator = row.operator
      formData.threshold = row.threshold
      formData.message = row.message
      formData.active = row.active
      dialogVisible.value = true
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!formRef.value) return
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          try {
            if (formData.id) {
              // 编辑现有规则
              const index = alertRules.value.findIndex(rule => rule.id === formData.id)
              if (index !== -1) {
                alertRules.value[index] = { ...formData }
              }
            } else {
              // 添加新规则
              const newRule = {
                ...formData,
                id: Date.now()
              }
              alertRules.value.push(newRule)
            }
            
            saveAlertRules()
            dialogVisible.value = false
            ElMessage.success('规则保存成功')
          } catch (error) {
            console.error('保存规则失败:', error)
            ElMessage.error('保存规则失败，请重试')
          }
        }
      })
    }
    
    // 删除规则
    const handleDelete = (id) => {
      alertRules.value = alertRules.value.filter(rule => rule.id !== id)
      saveAlertRules()
      ElMessage.success('规则删除成功')
    }
    
    // 状态变化
    const handleStatusChange = (row) => {
      saveAlertRules()
      ElMessage.success('状态更新成功')
    }
    
    // 获取类型标签类型
    const getTypeTagType = (type) => {
      const typeMap = {
        temperature: 'warning',
        humidity: 'info',
        co2: 'primary',
        ammonia: 'danger',
        light: 'success'
      }
      return typeMap[type] || 'default'
    }
    
    // 获取类型标签
    const getTypeLabel = (type) => {
      const typeMap = {
        temperature: '温度',
        humidity: '湿度',
        co2: 'CO2',
        ammonia: '氨气',
        light: '光照'
      }
      return typeMap[type] || type
    }
    
    // 获取级别标签类型
    const getLevelTagType = (level) => {
      const levelMap = {
        '低': 'info',
        '中': 'warning',
        '高': 'danger',
        '紧急': 'danger'
      }
      return levelMap[level] || 'default'
    }
    
    onMounted(() => {
      loadAlertRules()
    })
    
    return {
      alertRules,
      dialogVisible,
      dialogTitle,
      formData,
      rules,
      formRef,
      handleAdd,
      handleEdit,
      handleSubmit,
      handleDelete,
      handleStatusChange,
      getTypeTagType,
      getTypeLabel,
      getLevelTagType
    }
  }
}
</script>

<style scoped>
.alert-rule {
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
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .alert-rule {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>