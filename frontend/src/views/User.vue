<template>
  <div class="user">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" size="small" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>
      
      <el-table :data="users" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'info'">
              {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editUser(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingUser ? '编辑用户' : '添加用户'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model.number="form.age" type="number" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '../api'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const users = ref([])
const showAddDialog = ref(false)
const editingUser = ref(null)
const formRef = ref(null)
const form = ref({
  username: '',
  password: '',
  email: '',
  phone: '',
  nickname: '',
  age: null,
  role: 'user'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const loadUsers = async () => {
  try {
    users.value = await userApi.getAll()
  } catch (error) {
    console.error('加载用户失败:', error)
  }
}

const editUser = (user) => {
  editingUser.value = user
  form.value = { ...user, password: '' }
  showAddDialog.value = true
}

const deleteUser = async (id) => {
  if (confirm('确定删除?')) {
    try {
      await userApi.delete(id)
      ElMessage.success('删除成功')
      loadUsers()
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (editingUser.value) {
          await userApi.update(editingUser.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await userApi.create(form.value)
          ElMessage.success('添加成功')
        }
        closeDialog()
        loadUsers()
      } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error('操作失败，请重试')
      }
    }
  })
}

const closeDialog = () => {
  showAddDialog.value = false
  editingUser.value = null
  form.value = {
    username: '',
    password: '',
    email: '',
    phone: '',
    nickname: '',
    age: null,
    role: 'user'
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user {
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
