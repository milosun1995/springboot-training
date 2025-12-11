<template>
  <div class="page">
    <header class="topbar">
      <div class="logo">数据字典</div>
      <div class="actions">
        <el-button type="primary" @click="openDialog()">新增字典</el-button>
        <el-button link @click="logout">退出登录</el-button>
      </div>
    </header>

    <section class="content">
      <div class="filters">
        <el-input
          v-model="query.keyword"
          placeholder="编码/标签/值/类型"
          clearable
          style="width: 240px"
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
        <el-table-column prop="code" label="编码" width="180" />
        <el-table-column prop="label" label="标签" width="160" />
        <el-table-column prop="value" label="值" width="140" />
        <el-table-column prop="type" label="类型" width="140" />
        <el-table-column prop="sort" label="排序" width="90" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字典' : '新增字典'" width="560px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="编码" prop="code" v-if="!isEdit">
          <el-input v-model="form.code" placeholder="如：gender_male" />
        </el-form-item>
        <el-form-item label="标签" prop="label">
          <el-input v-model="form.label" placeholder="如：男" />
        </el-form-item>
        <el-form-item label="值" prop="value">
          <el-input v-model="form.value" placeholder="如：1" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-input v-model="form.type" placeholder="如：gender" />
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
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="form.remark" :rows="3" />
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
import { fetchDicts, createDict, updateDict, deleteDict, toggleDict } from '@/api/dict'
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
  label: '',
  value: '',
  type: '',
  sort: 0,
  status: 1,
  remark: ''
})

const rules = {
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  label: [{ required: true, message: '请输入标签', trigger: 'blur' }],
  value: [{ required: true, message: '请输入值', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await fetchDicts({ ...query })
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
    form.label = row.label
    form.value = row.value
    form.type = row.type
    form.sort = row.sort
    form.status = row.status
    form.remark = row.remark
  } else {
    isEdit.value = false
    form.id = null
    form.code = ''
    form.label = ''
    form.value = ''
    form.type = ''
    form.sort = 0
    form.status = 1
    form.remark = ''
  }
  formRef.value?.clearValidate()
}

const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateDict(form.id, {
          code: form.code,
          label: form.label,
          value: form.value,
          type: form.type,
          sort: form.sort,
          status: form.status,
          remark: form.remark
        })
        ElMessage.success('更新成功')
      } else {
        await createDict({
          code: form.code,
          label: form.label,
          value: form.value,
          type: form.type,
          sort: form.sort,
          status: form.status,
          remark: form.remark
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
  await toggleDict(row.id)
  ElMessage.success('状态已更新')
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除字典「${row.label}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteDict(row.id)
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


