<template>
  <div class="login-container">
    <div class="login-wrapper">
      <el-card shadow="hover" class="login-card">
        <template #header>
          <div class="login-header">
            <div class="logo">
              <el-avatar :size="60" :src="logoUrl"></el-avatar>
              <h2>规模化养殖监控平台</h2>
            </div>
          </div>
        </template>
        
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" class="form-input" @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password class="form-input" @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item class="form-actions">
            <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
            <el-button type="text" @click="handleForgotPassword">忘记密码？</el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-footer">
          <p>© 2026 规模化养殖监控平台</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { userApi } from '../api'
import router from '../router'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const loginFormRef = ref(null)
    const loading = ref(false)
    const logoUrl = ref('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20agriculture%20logo%20with%20digital%20monitoring%20icon&image_size=square')
    
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ]
    }
    
    const handleLogin = async () => {
      if (!loginFormRef.value) return
      
      await loginFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            loading.value = true
            // 模拟登录验证
            const users = await userApi.getAll()
            const user = users.find(u => u.username === loginForm.username && u.password === loginForm.password)
            
            if (user) {
              // 存储用户信息到本地存储
              localStorage.setItem('user', JSON.stringify(user))
              localStorage.setItem('token', 'mock-token') // 模拟token
              
              ElMessage.success('登录成功')
              router.push('/')
            } else {
              ElMessage.error('用户名或密码错误')
            }
          } catch (error) {
            console.error('登录失败:', error)
            ElMessage.error('登录失败，请重试')
          } finally {
            loading.value = false
          }
        }
      })
    }
    
    const handleForgotPassword = () => {
      ElMessage.info('忘记密码功能暂未实现，请联系管理员')
    }
    
    return {
      loginFormRef,
      loginForm,
      rules,
      handleLogin,
      handleForgotPassword,
      loading,
      logoUrl
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: white;
  position: relative;
  overflow: hidden;
}

.login-wrapper {
  position: relative;
  z-index: 1;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-card {
  width: 420px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  background: white;
  transition: all 0.3s ease;
  border: 1px solid #e8e8e8;
}

.login-card:hover {
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.login-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30px 0;
  background: white;
  color: #1e88e5;
  border-bottom: 1px solid #e8e8e8;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.logo h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1e88e5;
}

.login-form {
  padding: 30px;
}

.form-input {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.form-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.login-btn {
  width: 100%;
  padding: 12px 0;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  background-color: #1e88e5;
  border: none;
  color: white;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 136, 229, 0.4);
  background-color: #1976d2;
}

.form-actions .el-button--text {
  color: #1e88e5;
  font-size: 14px;
  padding: 0;
  margin-top: 5px;
}

.form-actions .el-button--text:hover {
  color: #1976d2;
  text-decoration: underline;
}

.login-footer {
  padding: 20px 30px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.login-footer p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    width: 90%;
    margin: 0 20px;
  }
  
  .login-form {
    padding: 20px;
  }
  
  .logo h2 {
    font-size: 20px;
  }
}
</style>