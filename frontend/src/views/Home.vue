<template>
  <div class="home">
    <h1>欢迎来到毕业设计系统</h1>
    <div class="cards">
      <div class="card" @click="$router.push('/user')">
        <h3>用户管理</h3>
        <p>管理用户信息</p>
      </div>
      <div class="card" @click="$router.push('/about')">
        <h3>关于</h3>
        <p>项目介绍</p>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  padding: 20px;
}
.cards {
  display: flex;
  gap: 20px;
  margin-top: 30px;
}
.card {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s;
}
.card:hover {
  transform: translateY(-5px);
}
</style>
