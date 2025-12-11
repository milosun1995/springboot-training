<template>
  <div class="home-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>欢迎，{{ authStore.nickname || authStore.username }}！</span>
          <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </template>
      <div class="welcome-content">
        <h2>登录成功！</h2>
        <p>您已成功登录后台管理系统</p>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ authStore.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ authStore.nickname || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    authStore.logout()
    router.push('/login')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-content {
  padding: 20px 0;
}

.welcome-content h2 {
  margin-bottom: 10px;
  color: #409eff;
}

.welcome-content p {
  margin-bottom: 20px;
  color: #606266;
}
</style>

