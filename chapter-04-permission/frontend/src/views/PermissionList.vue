<template>
  <div class="page">
    <header class="topbar">
      <div class="logo">权限管理</div>
      <div class="actions">
        <el-button type="primary" @click="openDialog()">新增权限</el-button>
        <el-button link @click="logout">退出登录</el-button>
      </div>
    </header>

    <section class="content">
      <div class="filters">
        <el-input
          v-model="query.keyword"
          placeholder="名称/编码/路径"
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

      <el-table
        :data="list"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
        style="width: 100%"
      >
        <el-table-column prop="name" label="名称" width="160" />
        <el-table-column prop="code" label="编码" width="200" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="method" label="方法" width="90" />
        <el-table-column prop="sort" label="排序" width="90" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleToggle(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="560px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="父级权限" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="选择父级权限（留空表示顶级权限）"
            style="width: 100%"
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="如：系统管理、用户管理" />
        </el-form-item>
        <el-form-item label="编码" prop="code" v-if="!isEdit">
          <el-input v-model="form.code" placeholder="如：sys:user:list" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="选择权限类型">
            <el-option label="菜单（menu）" value="menu" />
            <el-option label="按钮（button）" value="button" />
            <el-option label="接口（api）" value="api" />
          </el-select>
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="form.path" placeholder="菜单:/users 或 接口:/api/users" />
        </el-form-item>
        <el-form-item label="方法" prop="method">
          <el-select v-model="form.method" clearable placeholder="GET/POST/PUT/DELETE...">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="PATCH" value="PATCH" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :step="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="权限功能说明" />
        </el-form-item>
        <el-alert 
          title="💡 层级关系说明"
          type="info" 
          :closable="false"
          style="margin-bottom: 12px"
        >
          <template #default>
            <div style="font-size: 13px; line-height: 1.6">
              • <strong>一级权限</strong>：父级权限留空（如：系统管理）<br>
              • <strong>二级权限</strong>：选择一级权限作为父级（如：系统管理 → 用户管理）<br>
              • <strong>三级权限</strong>：选择二级权限作为父级（如：用户管理 → 新增按钮）
            </div>
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { fetchPermissionTree, createPermission, updatePermission, deletePermission, togglePermission } from '@/api/permission'
import { useAuthStore } from '@/store/auth'
import { removeToken } from '@/utils/auth'
import router from '@/router'

const query = reactive({
  keyword: '',
  status: null
})

const list = ref([])

// 构建父级权限选择器的选项（不含查询条件，显示全部权限树）
const parentOptions = computed(() => {
  // 递归过滤掉当前正在编辑的权限及其子级（避免循环引用）
  const filterNode = (nodes, excludeId) => {
    return nodes
      .filter(node => node.id !== excludeId)
      .map(node => ({
        ...node,
        children: node.children ? filterNode(node.children, excludeId) : []
      }))
  }
  
  // 如果是编辑模式，排除当前权限及其子级
  const filteredList = isEdit.value ? filterNode(list.value, form.id) : list.value
  return filteredList
})
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  parentId: null,
  name: '',
  code: '',
  type: 'menu',
  path: '',
  method: '',
  sort: 0,
  status: 1,
  description: ''
})
const initialForm = ref(null) // 初始表单数据（用于变更检测）

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const loadData = async () => {
  const res = await fetchPermissionTree({ ...query })
  list.value = res.data
}

const reset = () => {
  query.keyword = ''
  query.status = null
  loadData()
}

const openDialog = (row = null) => {
  dialogVisible.value = true
  if (row) {
    isEdit.value = true
    form.id = row.id
    form.parentId = row.parentId
    form.name = row.name
    form.code = row.code
    form.type = row.type
    form.path = row.path
    form.method = row.method
    form.sort = row.sort
    form.status = row.status
    form.description = row.description
    // 保存初始值（用于变更检测）
    initialForm.value = {
      parentId: row.parentId,
      name: row.name,
      code: row.code,
      type: row.type,
      path: row.path,
      method: row.method,
      sort: row.sort,
      status: row.status,
      description: row.description
    }
  } else {
    isEdit.value = false
    form.id = null
    form.parentId = null
    form.name = ''
    form.code = ''
    form.type = 'menu'
    form.path = ''
    form.method = ''
    form.sort = 0
    form.status = 1
    form.description = ''
    initialForm.value = null
  }
  formRef.value?.clearValidate()
}

// 检查表单是否变更（仅编辑模式）
const hasFormChanged = () => {
  if (!isEdit.value || !initialForm.value) return true // 新增模式或没有初始值，认为有变更
  return (
    form.parentId !== initialForm.value.parentId ||
    form.name !== initialForm.value.name ||
    form.code !== initialForm.value.code ||
    form.type !== initialForm.value.type ||
    form.path !== initialForm.value.path ||
    form.method !== initialForm.value.method ||
    form.sort !== initialForm.value.sort ||
    form.status !== initialForm.value.status ||
    form.description !== initialForm.value.description
  )
}

const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        // 检查是否有变更
        if (!hasFormChanged()) {
          ElMessage.info('未做任何修改')
          return
        }
        
        await updatePermission(form.id, {
          parentId: form.parentId,
          name: form.name,
          code: form.code,
          type: form.type,
          path: form.path,
          method: form.method,
          sort: form.sort,
          status: form.status,
          description: form.description
        })
        ElMessage.success('更新成功')
        // 更新初始值
        initialForm.value = {
          parentId: form.parentId,
          name: form.name,
          code: form.code,
          type: form.type,
          path: form.path,
          method: form.method,
          sort: form.sort,
          status: form.status,
          description: form.description
        }
      } else {
        await createPermission({
          parentId: form.parentId,
          name: form.name,
          code: form.code,
          type: form.type,
          path: form.path,
          method: form.method,
          sort: form.sort,
          status: form.status,
          description: form.description
        })
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  })
}

const handleToggle = async (row) => {
  await togglePermission(row.id)
  ElMessage.success('状态已更新')
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除权限「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deletePermission(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

const logout = () => {
  const authStore = useAuthStore()
  authStore.logout()
  removeToken()
  router.push('/login')
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
</style>


