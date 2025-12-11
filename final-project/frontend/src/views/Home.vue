<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <h2>
            <el-icon><House /></el-icon>
            欢迎使用后台管理系统
          </h2>
        </div>
      </template>
      
      <div class="welcome-content">
        <p class="greeting">你好，{{ authStore.nickname || authStore.username }}！</p>
        <p class="description">这是一个基于 Spring Boot + Vue 3 的权限管理系统示例</p>
        
        <el-divider />
        
        <div class="stats-container">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic title="我的角色" :value="authStore.roles.length">
                <template #prefix>
                  <el-icon><Avatar /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="8">
              <el-statistic title="我的权限" :value="authStore.permissions.length">
                <template #prefix>
                  <el-icon><Key /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="8">
              <el-statistic title="可用菜单" :value="menuCount">
                <template #prefix>
                  <el-icon><Menu /></el-icon>
                </template>
              </el-statistic>
            </el-col>
          </el-row>
        </div>
        
        <el-divider />
        
        <div class="quick-links">
          <h3>快速访问</h3>
          <el-space wrap :size="15">
            <el-button 
              v-for="menu in flatMenus" 
              :key="menu.code"
              type="primary"
              plain
              @click="router.push(menu.path)"
            >
              {{ menu.name }}
            </el-button>
          </el-space>
        </div>
      </div>
    </el-card>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>系统信息</h3>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统名称">Spring Boot 后台管理系统</el-descriptions-item>
            <el-descriptions-item label="版本号">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端技术">Spring Boot 3.2 + Spring Security</el-descriptions-item>
            <el-descriptions-item label="前端技术">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="数据库">H2 (开发) / MySQL (生产)</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>功能特性</h3>
            </div>
          </template>
          <ul class="feature-list">
            <li><el-icon><Check /></el-icon> 用户管理 - CRUD操作</li>
            <li><el-icon><Check /></el-icon> 角色管理 - 权限分配</li>
            <li><el-icon><Check /></el-icon> 权限管理 - 树形结构</li>
            <li><el-icon><Check /></el-icon> 菜单管理 - 动态路由</li>
            <li><el-icon><Check /></el-icon> 字典管理 - 系统配置</li>
            <li><el-icon><Check /></el-icon> JWT认证 - 无状态授权</li>
            <li><el-icon><Check /></el-icon> API权限 - 方法级保护</li>
            <li><el-icon><Check /></el-icon> 按钮权限 - 细粒度控制</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { House, Avatar, Key, Menu as MenuIcon, Check } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 计算菜单总数
const menuCount = computed(() => {
  let count = 0
  const countMenus = (menus) => {
    menus.forEach(menu => {
      count++
      if (menu.children && menu.children.length) {
        countMenus(menu.children)
      }
    })
  }
  countMenus(authStore.menus || [])
  return count
})

// 扁平化菜单（用于快速访问）
const flatMenus = computed(() => {
  const result = []
  const flatten = (menus) => {
    menus.forEach(menu => {
      if (menu.path) {
        result.push(menu)
      }
      if (menu.children && menu.children.length) {
        flatten(menu.children)
      }
    })
  }
  flatten(authStore.menus || [])
  return result
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header h2,
.card-header h3 {
  margin: 0;
  color: #409eff;
}

.welcome-content {
  padding: 20px 0;
}

.greeting {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.description {
  font-size: 16px;
  color: #606266;
  margin-bottom: 20px;
}

.stats-container {
  padding: 20px 0;
}

.quick-links h3 {
  margin-bottom: 15px;
  color: #303133;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  color: #606266;
}

.feature-list li .el-icon {
  color: #67c23a;
}
</style>

