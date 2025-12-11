<template>
  <div class="page">
    <header class="topbar">
      <div class="logo">菜单管理</div>
      <div class="actions">
        <el-button type="primary" @click="openDialog()" v-perm="'sys:menu:create'">新增菜单</el-button>
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
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="component" label="组件" />
        <el-table-column prop="icon" label="图标" width="120" />
        <el-table-column prop="sort" label="排序" width="90" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)" v-perm="'sys:menu:update'">编辑</el-button>
            <el-button size="small" type="warning" @click="handleToggle(row)" v-perm="'sys:menu:toggle'">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" v-perm="'sys:menu:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="560px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="父级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="选择父级菜单（留空表示一级菜单）"
            style="width: 100%"
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="如：系统管理、用户管理" />
        </el-form-item>
        <el-form-item label="编码" prop="code" v-if="!isEdit">
          <el-input v-model="form.code" placeholder="如：sys:menu:list" />
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="form.path" placeholder="如：/menus 或 /system/menus" />
        </el-form-item>
        <el-form-item label="组件" prop="component">
          <el-input v-model="form.component" placeholder="如：MenuList 或 views/MenuList.vue" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="如：Menu、Setting、User" />
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
        <el-alert 
          title="💡 菜单层级说明"
          type="info" 
          :closable="false"
          style="margin-bottom: 12px"
        >
          <template #default>
            <div style="font-size: 13px; line-height: 1.6">
              • <strong>一级菜单</strong>：父级菜单留空，显示在顶层导航栏<br>
              • <strong>二级菜单</strong>：选择一级菜单作为父级，显示在侧边栏<br>
              • <strong>三级菜单</strong>：选择二级菜单作为父级，显示为子菜单
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
import { fetchMenuTree, createMenu, updateMenu, deleteMenu, toggleMenu } from '@/api/menu'
import { useAuthStore } from '@/store/auth'

const query = reactive({
  keyword: '',
  status: null
})

const list = ref([])

// 构建父级菜单选择器的选项
const parentOptions = computed(() => {
  const filterNode = (nodes, excludeId) => {
    return nodes
      .filter(node => node.id !== excludeId)
      .map(node => ({
        ...node,
        children: node.children ? filterNode(node.children, excludeId) : []
      }))
  }
  
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
  path: '',
  component: '',
  icon: '',
  sort: 0,
  status: 1
})
const initialForm = ref(null) // 初始表单数据（用于变更检测）

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await fetchMenuTree({ ...query })
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
    form.path = row.path
    form.component = row.component
    form.icon = row.icon
    form.sort = row.sort
    form.status = row.status
    // 保存初始值（用于变更检测）
    initialForm.value = {
      parentId: row.parentId,
      name: row.name,
      code: row.code,
      path: row.path,
      component: row.component,
      icon: row.icon,
      sort: row.sort,
      status: row.status
    }
  } else {
    isEdit.value = false
    form.id = null
    form.parentId = null
    form.name = ''
    form.code = ''
    form.path = ''
    form.component = ''
    form.icon = ''
    form.sort = 0
    form.status = 1
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
    form.path !== initialForm.value.path ||
    form.component !== initialForm.value.component ||
    form.icon !== initialForm.value.icon ||
    form.sort !== initialForm.value.sort ||
    form.status !== initialForm.value.status
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
        
        await updateMenu(form.id, {
          parentId: form.parentId,
          name: form.name,
          code: form.code,
          path: form.path,
          component: form.component,
          icon: form.icon,
          sort: form.sort,
          status: form.status
        })
        ElMessage.success('更新成功')
        // 更新初始值
        initialForm.value = {
          parentId: form.parentId,
          name: form.name,
          code: form.code,
          path: form.path,
          component: form.component,
          icon: form.icon,
          sort: form.sort,
          status: form.status
        }
      } else {
        await createMenu({
          parentId: form.parentId,
          name: form.name,
          code: form.code,
          path: form.path,
          component: form.component,
          icon: form.icon,
          sort: form.sort,
          status: form.status
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
  await toggleMenu(row.id)
  ElMessage.success('状态已更新')
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除菜单「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteMenu(row.id)
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
</style>


