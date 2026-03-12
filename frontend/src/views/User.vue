<template>
  <div class="user">
    <h1>用户管理</h1>
    <div class="actions">
      <button @click="showAddDialog = true">添加用户</button>
    </div>
    <table class="user-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>邮箱</th>
          <th>电话</th>
          <th>昵称</th>
          <th>年龄</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.phone }}</td>
          <td>{{ user.nickname }}</td>
          <td>{{ user.age }}</td>
          <td>
            <button @click="editUser(user)">编辑</button>
            <button @click="deleteUser(user.id)" class="danger">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    
    <div v-if="showAddDialog" class="dialog">
      <div class="dialog-content">
        <h2>{{ editingUser ? '编辑用户' : '添加用户' }}</h2>
        <form @submit.prevent="submitForm">
          <input v-model="form.username" placeholder="用户名" required>
          <input v-model="form.password" placeholder="密码" type="password">
          <input v-model="form.email" placeholder="邮箱">
          <input v-model="form.phone" placeholder="电话">
          <input v-model="form.nickname" placeholder="昵称">
          <input v-model.number="form.age" placeholder="年龄" type="number">
          <div class="dialog-actions">
            <button type="submit">提交</button>
            <button type="button" @click="closeDialog">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '../api'

const users = ref([])
const showAddDialog = ref(false)
const editingUser = ref(null)
const form = ref({
  username: '',
  password: '',
  email: '',
  phone: '',
  nickname: '',
  age: null
})

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
    await userApi.delete(id)
    loadUsers()
  }
}

const submitForm = async () => {
  if (editingUser.value) {
    await userApi.update(editingUser.value.id, form.value)
  } else {
    await userApi.create(form.value)
  }
  closeDialog()
  loadUsers()
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
    age: null
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
.actions {
  margin-bottom: 20px;
}
.actions button {
  padding: 10px 20px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.user-table {
  width: 100%;
  border-collapse: collapse;
}
.user-table th, .user-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}
.user-table th {
  background: #f5f5f5;
}
.user-table button {
  padding: 5px 10px;
  margin-right: 5px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}
.user-table button.danger {
  background: #f44336;
  color: white;
}
.dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}
.dialog-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  min-width: 400px;
}
.dialog-content form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.dialog-content input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.dialog-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}
.dialog-actions button {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.dialog-actions button:first-child {
  background: #4CAF50;
  color: white;
}
</style>
