<template>
  <div class="page">
    <header class="topbar">
      <div class="logo">角色管理</div>
      <div class="actions">
        <el-button type="primary" @click="openDialog()">新增角色</el-button>
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleToggle(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { fetchRoles, createRole, updateRole, deleteRole, toggleRole } from '@/api/role'
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
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  })
}

const handleToggle = async (row) => {
  await toggleRole(row.id)
  ElMessage.success('状态已更新')
  loadData()
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
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>


