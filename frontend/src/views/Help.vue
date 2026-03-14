<template>
  <div class="help">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>帮助中心</span>
        </div>
      </template>
      
      <div class="help-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="系统介绍" name="introduction">
            <div class="tab-content fade-in">
              <h3>系统简介</h3>
              <p>规模化养殖环境监控平台是一个专为养殖场设计的环境监测和管理系统，通过实时监控养殖环境参数，及时预警异常情况，帮助养殖场提高管理效率和养殖质量。</p>
              
              <h3>系统功能</h3>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-card shadow="hover" class="feature-card card-hover">
                    <h4>环境监测</h4>
                    <p>实时监测温度、湿度、CO2、氨气、光照等环境参数，提供历史数据查询和趋势分析。</p>
                  </el-card>
                </el-col>
                <el-col :span="12">
                  <el-card shadow="hover" class="feature-card card-hover">
                    <h4>视频监控</h4>
                    <p>远程查看养殖场实时视频，支持多摄像头切换和历史视频回放。</p>
                  </el-card>
                </el-col>
                <el-col :span="12">
                  <el-card shadow="hover" class="feature-card card-hover">
                    <h4>智能预警</h4>
                    <p>当环境参数超出预设阈值时，系统自动发出预警通知，支持多种预警级别。</p>
                  </el-card>
                </el-col>
                <el-col :span="12">
                  <el-card shadow="hover" class="feature-card card-hover">
                    <h4>设备控制</h4>
                    <p>远程控制养殖场设备，如通风机、加热器、加湿器等，实现自动化管理。</p>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="使用指南" name="guide">
            <div class="tab-content fade-in">
              <h3>登录系统</h3>
              <ol>
                <li>打开系统登录页面，输入用户名和密码</li>
                <li>点击登录按钮或按Enter键登录</li>
                <li>系统根据用户角色显示对应的功能菜单</li>
              </ol>
              
              <h3>查看环境数据</h3>
              <ol>
                <li>点击左侧导航栏的"环境数据"</li>
                <li>在环境数据页面查看实时数据和历史数据</li>
                <li>可通过设备ID搜索特定设备的环境数据</li>
                <li>点击"导出"按钮导出环境数据为CSV格式</li>
              </ol>
              
              <h3>查看视频监控</h3>
              <ol>
                <li>点击左侧导航栏的"视频监控"</li>
                <li>在视频监控页面查看实时视频</li>
                <li>点击摄像头列表切换不同摄像头</li>
              </ol>
              
              <h3>管理预警信息</h3>
              <ol>
                <li>点击左侧导航栏的"预警管理"</li>
                <li>查看未处理和已处理的预警信息</li>
                <li>点击"处理"按钮标记预警为已处理</li>
                <li>点击"删除"按钮删除预警信息</li>
              </ol>
              
              <h3>配置预警规则</h3>
              <ol>
                <li>点击左侧导航栏的"预警规则"（仅管理员可见）</li>
                <li>查看当前的预警规则</li>
                <li>点击"添加规则"按钮添加新的预警规则</li>
                <li>点击"编辑"按钮修改现有规则</li>
                <li>点击"删除"按钮删除规则</li>
              </ol>
              
              <h3>控制设备</h3>
              <ol>
                <li>点击左侧导航栏的"设备控制"</li>
                <li>查看设备列表和状态</li>
                <li>点击"控制"按钮打开设备控制对话框</li>
                <li>设置设备参数并点击"确定"按钮发送控制指令</li>
              </ol>
              
              <h3>查看数据报表</h3>
              <ol>
                <li>点击左侧导航栏的"数据报表"</li>
                <li>选择报表类型（日报、周报、月报）</li>
                <li>选择日期范围</li>
                <li>点击"生成报表"按钮生成报表</li>
                <li>点击"导出"按钮导出报表为CSV格式</li>
              </ol>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="常见问题" name="faq">
            <div class="tab-content fade-in">
              <el-collapse>
                <el-collapse-item title="如何修改登录密码？">
                  <div class="faq-content">
                    <p>1. 登录系统后，点击右上角的用户头像</p>
                    <p>2. 选择"个人信息"</p>
                    <p>3. 在个人信息页面点击"修改密码"按钮</p>
                    <p>4. 输入旧密码和新密码，点击"确定"按钮</p>
                  </div>
                </el-collapse-item>
                
                <el-collapse-item title="忘记密码怎么办？">
                  <div class="faq-content">
                    <p>1. 在登录页面点击"忘记密码"链接</p>
                    <p>2. 输入用户名，点击"下一步"</p>
                    <p>3. 系统会发送重置密码的链接到您的邮箱</p>
                    <p>4. 点击邮箱中的链接重置密码</p>
                  </div>
                </el-collapse-item>
                
                <el-collapse-item title="如何添加新设备？">
                  <div class="faq-content">
                    <p>1. 点击左侧导航栏的"设备控制"</p>
                    <p>2. 点击"添加设备"按钮</p>
                    <p>3. 填写设备信息，点击"确定"按钮</p>
                  </div>
                </el-collapse-item>
                
                <el-collapse-item title="如何设置预警阈值？">
                  <div class="faq-content">
                    <p>1. 点击左侧导航栏的"预警规则"（仅管理员可见）</p>
                    <p>2. 点击"添加规则"按钮</p>
                    <p>3. 选择预警类型、级别、操作符和阈值，填写预警消息</p>
                    <p>4. 点击"确定"按钮保存规则</p>
                  </div>
                </el-collapse-item>
                
                <el-collapse-item title="为什么环境数据没有更新？">
                  <div class="faq-content">
                    <p>1. 检查设备是否正常连接</p>
                    <p>2. 检查网络连接是否正常</p>
                    <p>3. 点击"刷新数据"按钮手动刷新数据</p>
                    <p>4. 联系系统管理员检查设备状态</p>
                  </div>
                </el-collapse-item>
                
                <el-collapse-item title="如何导出数据？">
                  <div class="faq-content">
                    <p>1. 在环境数据页面，点击"导出"按钮</p>
                    <p>2. 系统会自动生成CSV格式的文件并下载</p>
                    <p>3. 用Excel或其他表格软件打开CSV文件查看数据</p>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="联系我们" name="contact">
            <div class="tab-content fade-in">
              <h3>联系信息</h3>
              <el-descriptions :column="1" border>
                <el-descriptions-item label="系统管理员">admin@example.com</el-descriptions-item>
                <el-descriptions-item label="技术支持">support@example.com</el-descriptions-item>
                <el-descriptions-item label="联系电话">400-123-4567</el-descriptions-item>
                <el-descriptions-item label="工作时间">周一至周五 9:00-18:00</el-descriptions-item>
              </el-descriptions>
              
              <h3>反馈建议</h3>
              <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="100px">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="feedbackForm.name" placeholder="请输入您的姓名" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="feedbackForm.email" placeholder="请输入您的邮箱" />
                </el-form-item>
                <el-form-item label="主题" prop="subject">
                  <el-input v-model="feedbackForm.subject" placeholder="请输入反馈主题" />
                </el-form-item>
                <el-form-item label="内容" prop="content">
                  <el-input v-model="feedbackForm.content" type="textarea" placeholder="请输入反馈内容" :rows="4" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleFeedbackSubmit">提交反馈</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'Help',
  setup() {
    const activeTab = ref('introduction')
    
    const feedbackForm = reactive({
      name: '',
      email: '',
      subject: '',
      content: ''
    })
    
    const feedbackRules = {
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ],
      subject: [
        { required: true, message: '请输入反馈主题', trigger: 'blur' }
      ],
      content: [
        { required: true, message: '请输入反馈内容', trigger: 'blur' }
      ]
    }
    
    const feedbackFormRef = ref(null)
    
    const handleFeedbackSubmit = async () => {
      if (!feedbackFormRef.value) return
      
      await feedbackFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            // 模拟提交反馈
            setTimeout(() => {
              ElMessage.success('反馈提交成功，我们会尽快处理您的问题')
              // 重置表单
              feedbackForm.name = ''
              feedbackForm.email = ''
              feedbackForm.subject = ''
              feedbackForm.content = ''
              feedbackFormRef.value.resetFields()
            }, 1000)
          } catch (error) {
            console.error('提交反馈失败:', error)
            ElMessage.error('提交反馈失败，请重试')
          }
        }
      })
    }
    
    return {
      activeTab,
      feedbackForm,
      feedbackRules,
      feedbackFormRef,
      handleFeedbackSubmit
    }
  }
}
</script>

<style scoped>
.help {
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

.help-content {
  padding: 20px 0;
}

.tab-content {
  padding: 20px 0;
}

.tab-content h3 {
  margin-bottom: 16px;
  color: #303133;
}

.tab-content p {
  margin-bottom: 8px;
  line-height: 1.5;
}

.tab-content ol {
  margin-left: 20px;
  margin-bottom: 20px;
}

.tab-content li {
  margin-bottom: 8px;
  line-height: 1.5;
}

.feature-card {
  padding: 20px;
  margin-bottom: 20px;
}

.feature-card h4 {
  margin-bottom: 10px;
  color: #409EFF;
}

.faq-content {
  padding: 10px 0;
}

.faq-content p {
  margin-bottom: 5px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .help {
    padding: 10px;
  }
  
  .tab-content {
    padding: 10px 0;
  }
  
  .feature-card {
    padding: 15px;
  }
}

@media (max-width: 480px) {
  .help {
    padding: 5px;
  }
  
  .tab-content h3 {
    font-size: 1.2rem;
  }
  
  .feature-card h4 {
    font-size: 1rem;
  }
}
</style>