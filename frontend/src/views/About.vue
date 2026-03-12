<template>
  <div class="about">
    <h1>关于项目</h1>
    <div class="content">
      <p>这是一个基于 SpringBoot + Vue 3 的毕业设计项目</p>
      <div class="tech">
        <h3>技术栈</h3>
        <ul>
          <li><strong>后端:</strong> SpringBoot 3.2, JPA, MySQL</li>
          <li><strong>前端:</strong> Vue 3, Vite, Vue Router, Axios</li>
        </ul>
      </div>
      <div class="info">
        <h3>项目信息</h3>
        <p>版本: 1.0.0</p>
        <p>作者: 学生</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about {
  padding: 20px;
}
.content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  margin-top: 20px;
}
.tech, .info {
  margin-top: 20px;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  padding: 8px 0;
}
</style>
