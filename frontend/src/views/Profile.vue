<template>
  <div class="profile">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form :model="userForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model.number="userForm.age" type="number" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="userForm.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="userForm.role === 'admin' ? 'danger' : 'info'">
            {{ userForm.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { userApi } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'Profile',
  setup() {
    const formRef = ref(null)
    const userForm = ref({
      id: '',
      username: '',
      nickname: '',
      email: '',
      phone: '',
      age: null,
      gender: '',
      role: ''
    })
    
    const rules = {
      nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入电话', trigger: 'blur' }
      ]
    }
    
    // 加载用户信息
    const loadUserInfo = () => {
      const user = JSON.parse(localStorage.getItem('user'))
      if (user) {
        userForm.value = { ...user }
      }
    }
    
    // 提交表单
    const submitForm = async () => {
      if (!formRef.value) return
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          try {
            // 模拟更新用户信息
            const updatedUser = await userApi.update(userForm.value.id, userForm.value)
            // 更新本地存储中的用户信息
            localStorage.setItem('user', JSON.stringify(updatedUser))
            ElMessage.success('个人信息更新成功')
          } catch (error) {
            console.error('更新个人信息失败:', error)
            ElMessage.error('更新失败，请重试')
          }
        }
      })
    }
    
    // 重置表单
    const resetForm = () => {
      loadUserInfo()
    }
    
    onMounted(() => {
      loadUserInfo()
    })
    
    return {
      formRef,
      userForm,
      rules,
      submitForm,
      resetForm
    }
  }
}
</script>

<style scoped>
.profile {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>