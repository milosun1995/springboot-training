<template>
  <div class="page">
    <header class="topbar">
      <div class="logo">角色管理</div>
      <div class="actions">
        <el-button type="primary" @click="openDialog()" v-perm="'sys:role:create'">新增角色</el-button>
        <el-button link @click="logout">退出登录</el-button>
      </div>
    </header>

    <section class="content">
      <div class="filters">
        <el-input
          v-model="query.keyword"
          placeholder="名称/编码/描述"
          clearable
          style="width: 220px"
          @keyup.enter="loadData"
        />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px" @change="loadData">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>

      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="编码" width="140" />
        <el-table-column prop="name" label="名称" width="160" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)" v-perm="'sys:role:update'">编辑</el-button>
            <el-button size="small" type="warning" @click="handleToggle(row)" v-perm="'sys:role:toggle'">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" v-perm="'sys:role:delete'">删除</el-button>
            <el-button size="small" type="success" @click="openAssignDialog(row)" v-perm="'sys:role:assign'">分配</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          :current-page="query.page + 1"
          :page-size="query.size"
          :page-sizes="[5,10,20,50]"
          @current-change="onPageChange"
          @size-change="onSizeChange"
        />
      </div>
    </section>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色编码" prop="code" v-if="!isEdit">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignDialog" title="角色权限和菜单分配" width="800px" :close-on-click-modal="false">
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="权限分配" name="permission">
          <div class="tree-container">
            <el-tree
              ref="permTreeRef"
              :data="permTree"
              show-checkbox
              node-key="id"
              default-expand-all
              :props="{ label: 'name', children: 'children' }"
              :default-checked-keys="checkedPerms"
              @check="handlePermCheck"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="菜单分配" name="menu">
          <div class="tree-container">
            <el-tree
              ref="menuTreeRef"
              :data="menuTree"
              show-checkbox
              node-key="id"
              default-expand-all
              :props="{ label: 'name', children: 'children' }"
              :default-checked-keys="checkedMenus"
              @check="handleMenuCheck"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="assignDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="activeTab === 'permission' ? savePerms() : saveMenus()"
          >
            {{ activeTab === 'permission' ? '保存权限' : '保存菜单' }}
          </el-button>
          <el-button 
            type="success" 
            @click="saveAll"
            :loading="saving"
          >
            全部保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { fetchRoles, createRole, updateRole, deleteRole, toggleRole, fetchRolePermissions, saveRolePermissions, fetchRoleMenus, saveRoleMenus } from '@/api/role'
import { fetchPermissionTree } from '@/api/permission'
import { fetchMenuTree } from '@/api/menu'
import { useAuthStore } from '@/store/auth'
import { removeToken } from '@/utils/auth'
import router from '@/router'

const query = reactive({
  keyword: '',
  status: null,
  page: 0,
  size: 10
})

const list = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const assignDialog = ref(false)
const activeTab = ref('permission')
const permTreeRef = ref(null)
const menuTreeRef = ref(null)
const permTree = ref([])
const menuTree = ref([])
const checkedPerms = ref([])
const checkedMenus = ref([])
const initialPerms = ref([]) // 初始权限（用于变更检测）
const initialMenus = ref([]) // 初始菜单（用于变更检测）
const currentRoleId = ref(null)
const saving = ref(false)
const form = reactive({
  id: null,
  code: '',
  name: '',
  description: '',
  status: 1
})

const rules = {
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  description: [{ max: 255, message: '描述不能超过255字符', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await fetchRoles({ ...query })
  list.value = res.data.content
  total.value = res.data.totalElements
}

const reset = () => {
  query.keyword = ''
  query.status = null
  query.page = 0
  query.size = 10
  loadData()
}

const onPageChange = (page) => {
  query.page = page - 1
  loadData()
}

const onSizeChange = (size) => {
  query.size = size
  query.page = 0
  loadData()
}

const openDialog = (row = null) => {
  dialogVisible.value = true
  if (row) {
    isEdit.value = true
    form.id = row.id
    form.code = row.code
    form.name = row.name
    form.description = row.description
    form.status = row.status
  } else {
    isEdit.value = false
    form.id = null
    form.code = ''
    form.name = ''
    form.description = ''
    form.status = 1
  }
  formRef.value?.clearValidate()
}

const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateRole(form.id, {
          name: form.name,
          description: form.description,
          status: form.status
        })
        ElMessage.success('更新成功')
      } else {
        await createRole({
          code: form.code,
          name: form.name,
          description: form.description,
          status: form.status
        })
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {}
  })
}

const handleToggle = async (row) => {
  await toggleRole(row.id)
  ElMessage.success('状态已更新')
  loadData()
}

const openAssignDialog = async (row) => {
  currentRoleId.value = row.id
  assignDialog.value = true
  activeTab.value = 'permission' // 默认显示权限标签
  
  // 并行加载权限和菜单数据
  const [permTreeRes, permCheckedRes, menuTreeRes, menuCheckedRes] = await Promise.all([
    fetchPermissionTree({}),
    fetchRolePermissions(row.id),
    fetchMenuTree({}),
    fetchRoleMenus(row.id)
  ])
  
  permTree.value = permTreeRes.data
  checkedPerms.value = permCheckedRes.data
  menuTree.value = menuTreeRes.data
  checkedMenus.value = menuCheckedRes.data
  
  // 保存初始值（用于变更检测）
  initialPerms.value = [...permCheckedRes.data]
  initialMenus.value = [...menuCheckedRes.data]
  
  // 等待 DOM 更新后设置选中状态
  await nextTick()
  if (permTreeRef.value) {
    permTreeRef.value.setCheckedKeys(checkedPerms.value)
  }
  if (menuTreeRef.value) {
    menuTreeRef.value.setCheckedKeys(checkedMenus.value)
  }
}

const handlePermCheck = (data, state) => {
  // 获取所有选中的节点（包括半选中的父节点）
  checkedPerms.value = [
    ...state.checkedKeys,
    ...state.halfCheckedKeys
  ]
}

const handleMenuCheck = (data, state) => {
  // 获取所有选中的节点（包括半选中的父节点）
  checkedMenus.value = [
    ...state.checkedKeys,
    ...state.halfCheckedKeys
  ]
}

// 检查权限是否变更
const hasPermChanged = () => {
  const checkedKeys = permTreeRef.value?.getCheckedKeys() || checkedPerms.value
  const halfCheckedKeys = permTreeRef.value?.getHalfCheckedKeys() || []
  const permIds = checkedKeys.filter(id => !halfCheckedKeys.includes(id))
  
  // 排序后比较（因为顺序可能不同）
  const current = [...permIds].sort((a, b) => a - b)
  const initial = [...initialPerms.value].sort((a, b) => a - b)
  
  return JSON.stringify(current) !== JSON.stringify(initial)
}

// 检查菜单是否变更
const hasMenuChanged = () => {
  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || checkedMenus.value
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
  const menuIds = checkedKeys.filter(id => !halfCheckedKeys.includes(id))
  
  // 排序后比较（因为顺序可能不同）
  const current = [...menuIds].sort((a, b) => a - b)
  const initial = [...initialMenus.value].sort((a, b) => a - b)
  
  return JSON.stringify(current) !== JSON.stringify(initial)
}

// 内部保存函数（不显示提示，用于 saveAll）
const _savePerms = async () => {
  // 获取当前选中的权限ID（只保存叶子节点，避免保存父节点）
  const checkedKeys = permTreeRef.value?.getCheckedKeys() || checkedPerms.value
  const halfCheckedKeys = permTreeRef.value?.getHalfCheckedKeys() || []
  // 只保存完全选中的节点（不包括半选中的父节点）
  const permIds = checkedKeys.filter(id => !halfCheckedKeys.includes(id))
  
  const res = await saveRolePermissions(currentRoleId.value, permIds)
  
  // 更新初始值（保存成功后）
  initialPerms.value = [...permIds]
  
  // 返回通知信息（用于 saveAll）
  return res.data?.notification
}

// 公开的保存权限函数（显示提示）
const savePerms = async () => {
  try {
    // 检查是否有变更
    if (!hasPermChanged()) {
      ElMessage.info('权限未做任何修改')
      return
    }
    
    const notification = await _savePerms()
    ElMessage.success('权限已保存')
    
    // 检查是否有权限变更通知
    if (notification) {
      if (notification.requireRelogin) {
        ElMessageBox.confirm(
          notification.message || '权限已变更，需要重新登录以获取最新权限',
          '提示',
          {
            type: 'warning',
            confirmButtonText: '重新登录',
            cancelButtonText: '稍后',
            showCancelButton: true
          }
        ).then(() => {
          const authStore = useAuthStore()
          authStore.logout()
          removeToken()
          router.push('/login')
        }).catch(() => {})
      } else if (notification.message) {
        ElMessage.info(notification.message)
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
    throw error
  }
}

// 内部保存菜单函数（不显示提示，用于 saveAll）
const _saveMenus = async () => {
  // 获取当前选中的菜单ID（只保存叶子节点，避免保存父节点）
  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || checkedMenus.value
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
  // 只保存完全选中的节点（不包括半选中的父节点）
  const menuIds = checkedKeys.filter(id => !halfCheckedKeys.includes(id))
  
  await saveRoleMenus(currentRoleId.value, menuIds)
  
  // 更新初始值（保存成功后）
  initialMenus.value = [...menuIds]
}

// 公开的保存菜单函数（显示提示）
const saveMenus = async () => {
  try {
    // 检查是否有变更
    if (!hasMenuChanged()) {
      ElMessage.info('菜单未做任何修改')
      return
    }
    
    await _saveMenus()
    ElMessage.success('菜单已保存')
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
    throw error
  }
}

// 全部保存（只显示一个统一的成功提示）
const saveAll = async () => {
  saving.value = true
  try {
    // 检查是否有变更
    const permChanged = hasPermChanged()
    const menuChanged = hasMenuChanged()
    
    if (!permChanged && !menuChanged) {
      ElMessage.info('权限和菜单均未做任何修改')
      saving.value = false
      return
    }
    
    let notification = null
    
    // 如果有权限变更，保存权限（不显示提示）
    if (permChanged) {
      notification = await _savePerms()
    }
    
    // 如果有菜单变更，保存菜单（不显示提示）
    if (menuChanged) {
      await _saveMenus()
    }
    
    // 根据变更情况显示不同的提示
    if (permChanged && menuChanged) {
      ElMessage.success('权限和菜单已全部保存')
    } else if (permChanged) {
      ElMessage.success('权限已保存')
    } else if (menuChanged) {
      ElMessage.success('菜单已保存')
    }
    
    // 如果权限变更需要重新登录，显示通知
    if (notification?.requireRelogin) {
      ElMessageBox.confirm(
        notification.message || '权限已变更，需要重新登录以获取最新权限',
        '提示',
        {
          type: 'warning',
          confirmButtonText: '重新登录',
          cancelButtonText: '稍后',
          showCancelButton: true
        }
      ).then(() => {
        const authStore = useAuthStore()
        authStore.logout()
        removeToken()
        router.push('/login')
      }).catch(() => {})
    }
    
    assignDialog.value = false
  } catch (error) {
    // 错误已在各自的函数中处理
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除角色「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteRole(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fb;
}
.topbar {
  height: 56px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.logo {
  font-weight: 600;
  font-size: 16px;
}
.actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
.content {
  padding: 16px;
}
.filters {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.tree-container {
  height: 400px;
  overflow-y: auto;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>


