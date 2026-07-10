<template>
  <div class="user-manage">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="search-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索用户名或姓名..."
            clearable
            style="width: 260px"
            @keyup.enter="fetchData"
            @clear="fetchData"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </div>
        <div class="toolbar-actions">
          <template v-if="selectedIds.length > 0">
            <span class="selected-count">已选 {{ selectedIds.length }} 项</span>
            <el-button type="danger" plain @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="warning" plain @click="openBatchRoleDialog">
              <el-icon><Setting /></el-icon>
              批量设置角色
            </el-button>
            <el-button type="success" plain @click="() => handleBatchStatus(1)">
              <el-icon><Check /></el-icon>
              批量启用
            </el-button>
            <el-button type="info" plain @click="() => handleBatchStatus(0)">
              <el-icon><Close /></el-icon>
              批量禁用
            </el-button>
          </template>
          <el-button type="success" plain @click="openBatchCreateDialog">
            <el-icon><DocumentAdd /></el-icon>
            批量创建
          </el-button>
          <el-button type="warning" plain @click="openImportDialog">
            <el-icon><Upload /></el-icon>
            Excel导入
          </el-button>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </div>

      <el-table :data="list" v-loading="loading" border stripe style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" :selectable="checkSelectable" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="姓名" width="150" />
        <el-table-column prop="role" label="角色" width="140">
          <template #default="{ row }">
            <el-tag :type="roleTag(row.role)" effect="plain">
              {{ roleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 'ACTIVE'"
              :disabled="row.id === currentUserId"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              :disabled="row.id === currentUserId || row.role === 'ADMIN'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>

      <el-empty v-if="!loading && list.length === 0" description="暂无用户" />
    </el-card>

    <!-- Create/Edit User Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="520px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? undefined : 'password'">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" :value="1" />
            <el-option label="学生/教师" :value="2" />
            <el-option label="科研秘书" :value="3" />
            <el-option label="学院领导" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确认
        </el-button>
      </template>
    </el-dialog>

    <!-- Batch Role Dialog -->
    <el-dialog
      v-model="batchRoleDialogVisible"
      title="批量设置角色"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="选择角色">
          <el-select v-model="batchRoleId" placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.roleName || roleLabel(role.roleName)"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchRoleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchSubmitting" @click="handleBatchRoleSubmit">
          确认设置
        </el-button>
      </template>
    </el-dialog>

    <!-- Batch Create Dialog -->
    <el-dialog
      v-model="batchCreateDialogVisible"
      title="批量创建用户"
      width="950px"
      :close-on-click-modal="false"
      @closed="resetBatchCreate"
    >
      <!-- Quick Generate Panel -->
      <el-card shadow="never" style="margin-bottom: 12px">
        <div class="quick-gen-panel">
          <span class="gen-label">快速生成：</span>
          <el-input v-model="gen.prefix" placeholder="前缀" size="small" style="width: 110px" />
          <span class="gen-sep">从</span>
          <el-input-number v-model="gen.start" :min="1" :max="999" size="small" style="width: 100px" />
          <span class="gen-sep">到</span>
          <el-input-number v-model="gen.end" :min="1" :max="999" size="small" style="width: 100px" />
          <span class="gen-sep">角色</span>
          <el-select v-model="gen.roleId" size="small" style="width: 120px">
            <el-option label="管理员" :value="1" />
            <el-option label="学生/教师" :value="2" />
            <el-option label="科研秘书" :value="3" />
            <el-option label="学院领导" :value="4" />
          </el-select>
          <span class="gen-sep">密码</span>
          <el-input v-model="gen.password" size="small" style="width: 100px" />
          <el-button type="primary" size="small" @click="generateRows">
            <el-icon><MagicStick /></el-icon>
            生成
          </el-button>
          <el-button size="small" @click="fillDemoRows">示例</el-button>
        </div>
      </el-card>
      <div style="margin-bottom: 12px; display: flex; gap: 8px">
        <el-button type="primary" plain size="small" @click="addCreateRow">
          <el-icon><Plus /></el-icon>
          手动添加
        </el-button>
        <el-button size="small" plain type="danger" @click="batchCreateRows = []" :disabled="batchCreateRows.length === 0">
          清空列表
        </el-button>
        <span v-if="batchCreateRows.length > 0" style="margin-left: 8px; color: #909399; font-size: 13px; line-height: 24px">
          共 {{ batchCreateRows.length }} 个待创建用户
        </span>
      </div>
      <el-table :data="batchCreateRows" border stripe style="width: 100%" max-height="400">
        <el-table-column label="用户名" width="130">
          <template #default="{ row, $index }">
            <el-input v-model="row.username" placeholder="必填" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="密码" width="120">
          <template #default="{ row }">
            <el-input v-model="row.password" placeholder="必填" size="small" show-password />
          </template>
        </el-table-column>
        <el-table-column label="姓名" width="110">
          <template #default="{ row }">
            <el-input v-model="row.realName" placeholder="选填" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="角色" width="140">
          <template #default="{ row }">
            <el-select v-model="row.roleId" placeholder="必选" size="small" style="width: 100%">
              <el-option label="管理员" :value="1" />
              <el-option label="学生/教师" :value="2" />
              <el-option label="科研秘书" :value="3" />
              <el-option label="学院领导" :value="4" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="140">
          <template #default="{ row }">
            <el-input v-model="row.phone" placeholder="选填" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="邮箱" width="160">
          <template #default="{ row }">
            <el-input v-model="row.email" placeholder="选填" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="65" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" link size="small" @click="removeCreateRow($index)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="batchCreateRows.length === 0" description="暂无数据，点击「添加一行」开始" :image-size="60" />
      <template #footer>
        <el-button @click="batchCreateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchCreateSubmitting" @click="handleBatchCreateSubmit">
          确认创建 {{ batchCreateRows.length }} 个用户
        </el-button>
      </template>
    </el-dialog>

    <!-- Excel Import Dialog -->
    <el-dialog
      v-model="importDialogVisible"
      title="Excel导入用户"
      width="860px"
      :close-on-click-modal="false"
      @closed="resetImport"
    >
      <!-- Step 1: Upload -->
      <div v-show="importStep === 1">
        <el-alert
          title="操作说明"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 16px"
        >
          <template #default>
            <ol style="margin: 4px 0; padding-left: 20px; font-size: 13px; line-height: 1.8">
              <li>请按照模板格式填写用户信息（用户名、密码、角色为必填项）</li>
              <li>上传Excel文件后会先预览数据并校验格式，确认无误后再导入</li>
              <li>角色名称需与系统中已有角色一致：<b>{{ roleNameList }}</b></li>
            </ol>
          </template>
        </el-alert>

        <div class="import-upload-area">
          <el-upload
            ref="uploadRef"
            drag
            :auto-upload="false"
            :limit="1"
            accept=".xlsx,.xls"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将Excel文件拖到此处，或<em>点击选择文件</em>
            </div>
          </el-upload>
        </div>

        <div style="margin-top: 12px; text-align: center">
          <el-button type="primary" plain @click="downloadTemplate">
            <el-icon><Download /></el-icon>
            下载导入模板
          </el-button>
        </div>

        <div style="margin-top: 20px; text-align: center">
          <el-button
            type="primary"
            :loading="importParsing"
            :disabled="!selectedFile"
            @click="handleParseExcel"
          >
            <el-icon><Search /></el-icon>
            解析并预览
          </el-button>
        </div>
        <div style="color: #909399; font-size: 12px; margin-top: 8px; text-align: center">
          支持 .xlsx / .xls 格式，单次最多导入500条
        </div>
      </div>

      <!-- Step 2: Preview -->
      <div v-show="importStep === 2">
        <div class="import-summary">
          <el-tag type="success" size="large">有效数据：{{ validUsers.length }} 条</el-tag>
          <el-tag v-if="errorRows.length > 0" type="danger" size="large">异常数据：{{ errorRows.length }} 条</el-tag>
          <span style="margin-left: 8px; color: #909399; font-size: 13px">
            文件：{{ importFilename }}
          </span>
        </div>

        <!-- Valid Users Preview -->
        <div v-if="validUsers.length > 0" style="margin-top: 16px">
          <h4 style="margin: 0 0 8px 0; color: #67c23a">即将导入的用户（{{ validUsers.length }}条）</h4>
          <el-table :data="validUsers" border stripe max-height="250" size="small">
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column label="角色" width="120">
              <template #default="{ row }">
                <el-tag :type="roleTag(roleLabelByRoleId(row.roleId))" effect="plain" size="small">
                  {{ roleLabelByRoleId(row.roleId) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column prop="email" label="邮箱" min-width="160" />
          </el-table>
        </div>

        <!-- Error Rows -->
        <div v-if="errorRows.length > 0" style="margin-top: 16px">
          <h4 style="margin: 0 0 8px 0; color: #f56c6c">校验失败的数据（{{ errorRows.length }}条，将跳过不导入）</h4>
          <el-table :data="errorRows" border stripe max-height="200" size="small">
            <el-table-column prop="row" label="行号" width="60" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column prop="roleName" label="角色" width="100" />
            <el-table-column label="错误原因" min-width="200">
              <template #default="{ row }">
                <div v-for="(err, idx) in row.errors" :key="idx" style="color: #f56c6c; font-size: 12px">
                  {{ err }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <template #footer>
        <template v-if="importStep === 1">
          <el-button @click="importDialogVisible = false">取消</el-button>
        </template>
        <template v-else>
          <el-button @click="importStep = 1">返回上一步</el-button>
          <el-button
            type="primary"
            :loading="importSubmitting"
            :disabled="validUsers.length === 0"
            @click="handleImportConfirm"
          >
            确认导入 {{ validUsers.length }} 个用户
          </el-button>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Setting, Check, Close, DocumentAdd, MagicStick,
         Upload, UploadFilled, Download } from '@element-plus/icons-vue'
import { getUserPage, createUser, updateUser, deleteUser, updateUserStatus,
         batchDeleteUsers, batchCreateUsers, batchUpdateUserRole,
         batchUpdateUserStatus, getAllRoles, importExcel, importConfirm } from '../../../api/user'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editId = ref(null)
const formRef = ref(null)

const currentUserId = computed(() => {
  const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return info.id
})

const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  roleId: null
})

// Batch operation state
const selectedIds = ref([])
const batchSubmitting = ref(false)
const batchRoleDialogVisible = ref(false)
const batchRoleId = ref(null)
const roleList = ref([])

// Batch create state
const batchCreateDialogVisible = ref(false)
const batchCreateSubmitting = ref(false)
const batchCreateRows = ref([])

const defaultRow = () => ({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  roleId: null
})

// Quick generate config
const gen = reactive({
  prefix: 'student',
  start: 1,
  end: 10,
  roleId: 2,
  password: 'admin'
})

const roleMap = {
  STUDENT: { label: '学生/教师', tag: '' },
  SECRETARY: { label: '科研秘书', tag: 'warning' },
  LEADER: { label: '学院领导', tag: 'success' },
  ADMIN: { label: '管理员', tag: 'danger' }
}

function roleLabel(role) {
  return roleMap[role]?.label || role
}

function roleTag(role) {
  return roleMap[role]?.tag || ''
}

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 30, message: '用户名长度应为3-30个字符', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      page: pageNum.value,
      pageSize: pageSize.value
    }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res = await getUserPage(params)
    list.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.username = ''
  form.password = ''
  form.realName = ''
  form.phone = ''
  form.email = ''
  form.roleId = null
  editId.value = null
  isEdit.value = false
  formRef.value?.resetFields()
}

function openCreateDialog() {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

function openEditDialog(row) {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  form.username = row.username
  form.realName = row.realName || ''
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.roleId = row.roleId || null
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      const payload = { ...form }
      if (!payload.password) delete payload.password
      delete payload.username
      await updateUser(editId.value, payload)
      ElMessage.success('更新成功')
    } else {
      if (!form.password) {
        ElMessage.warning('新增用户请填写密码')
        submitting.value = false
        return
      }
      await createUser(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${row.realName || row.username}"吗？此操作不可恢复。`,
      '确认删除',
      {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消'
      }
    )
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    await fetchData()
  } catch {
    // cancelled or error
  }
}

async function handleStatusChange(row, enabled) {
  try {
    const newStatus = enabled ? 'ACTIVE' : 'INACTIVE'
    await updateUserStatus(row.id, { status: newStatus })
    ElMessage.success(enabled ? '已启用' : '已禁用')
    await fetchData()
  } catch {
    // Error handled by interceptor
  }
}

// ── Batch Operations ──

function checkSelectable(row) {
  // Cannot select the built-in admin (id=1)
  return row.id !== 1
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(row => row.id)
}

async function handleBatchDelete() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择用户')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个用户吗？此操作不可恢复。`,
      '批量删除',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
    batchSubmitting.value = true
    await batchDeleteUsers(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    await fetchData()
  } catch (err) {
    if (err !== 'cancel') { /* error handled by interceptor */ }
  } finally {
    batchSubmitting.value = false
  }
}

function openBatchRoleDialog() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择用户')
    return
  }
  batchRoleId.value = null
  batchRoleDialogVisible.value = true
}

async function handleBatchRoleSubmit() {
  if (!batchRoleId.value) {
    ElMessage.warning('请选择角色')
    return
  }
  batchSubmitting.value = true
  try {
    await batchUpdateUserRole(selectedIds.value, batchRoleId.value)
    ElMessage.success('批量设置角色成功')
    batchRoleDialogVisible.value = false
    selectedIds.value = []
    await fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    batchSubmitting.value = false
  }
}

async function handleBatchStatus(status) {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择用户')
    return
  }
  const actionText = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(
      `确定要批量${actionText}选中的 ${selectedIds.value.length} 个用户吗？`,
      `批量${actionText}`,
      { type: 'warning', confirmButtonText: actionText, cancelButtonText: '取消' }
    )
    batchSubmitting.value = true
    await batchUpdateUserStatus(selectedIds.value, status)
    ElMessage.success(`批量${actionText}成功`)
    selectedIds.value = []
    await fetchData()
  } catch (err) {
    if (err !== 'cancel') { /* error handled by interceptor */ }
  } finally {
    batchSubmitting.value = false
  }
}

// ── Batch Create ──

function openBatchCreateDialog() {
  resetBatchCreate()
  batchCreateDialogVisible.value = true
}

function resetBatchCreate() {
  batchCreateRows.value = []
}

function addCreateRow() {
  batchCreateRows.value.push({ ...defaultRow() })
}

function removeCreateRow(index) {
  batchCreateRows.value.splice(index, 1)
}

function generateRows() {
  const count = gen.end - gen.start + 1
  if (count <= 0 || count > 200) {
    ElMessage.warning('编号范围无效或超过200个')
    return
  }
  const rows = []
  for (let i = gen.start; i <= gen.end; i++) {
    rows.push({
      username: gen.prefix + i,
      password: gen.password,
      realName: gen.prefix + i,
      phone: '',
      email: '',
      roleId: gen.roleId
    })
  }
  batchCreateRows.value = rows
  ElMessage.success(`已生成 ${rows.length} 个用户`)
}

function fillDemoRows() {
  const demo = [
    { username: 'teacher2', password: 'admin', realName: '王老师', phone: '', email: '', roleId: 2 },
    { username: 'student2', password: 'admin', realName: '赵同学', phone: '', email: '', roleId: 2 },
    { username: 'teacher3', password: 'admin', realName: '刘老师', phone: '', email: '', roleId: 2 }
  ]
  // Append to existing rows instead of replacing
  batchCreateRows.value = [...batchCreateRows.value, ...demo]
  ElMessage.success('已追加3条示例数据')
}

// ── Excel Import ──

const importDialogVisible = ref(false)
const importStep = ref(1) // 1: upload, 2: preview
const importParsing = ref(false)
const importSubmitting = ref(false)
const selectedFile = ref(null)
const fileList = ref([])
const validUsers = ref([])
const errorRows = ref([])
const importFilename = ref('')
const uploadRef = ref(null)

// Computed role name list for display
const roleNameList = computed(() => {
  return roleList.value.map(r => r.roleName).join('、')
})

function roleLabelByRoleId(roleId) {
  const role = roleList.value.find(r => r.id === roleId)
  return role ? role.roleName : '未知'
}

function openImportDialog() {
  resetImport()
  importDialogVisible.value = true
}

function resetImport() {
  importStep.value = 1
  selectedFile.value = null
  fileList.value = []
  validUsers.value = []
  errorRows.value = []
  importFilename.value = ''
  importParsing.value = false
  importSubmitting.value = false
}

function handleFileChange(file) {
  selectedFile.value = file.raw
  fileList.value = [file]
}

function handleFileRemove() {
  selectedFile.value = null
  fileList.value = []
}

async function handleParseExcel() {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择Excel文件')
    return
  }

  const filename = selectedFile.value.name
  if (!filename.endsWith('.xlsx') && !filename.endsWith('.xls')) {
    ElMessage.error('仅支持 .xlsx 或 .xls 格式的Excel文件')
    return
  }

  importParsing.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    const res = await importExcel(formData)
    const data = res.data
    validUsers.value = data.validUsers || []
    errorRows.value = data.errorRows || []
    importFilename.value = data.filename || filename
    importStep.value = 2

    if (errorRows.value.length > 0) {
      ElMessage.warning(`解析完成：${data.validCount}条有效，${data.errorCount}条异常需修正`)
    } else {
      ElMessage.success(`解析完成：${data.validCount}条数据全部校验通过`)
    }
  } catch {
    // Error handled by interceptor
    importStep.value = 1
  } finally {
    importParsing.value = false
  }
}

async function handleImportConfirm() {
  if (validUsers.value.length === 0) {
    ElMessage.warning('没有可导入的有效数据')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要导入 ${validUsers.value.length} 个用户吗？用户名重复的用户将被跳过。`,
      '确认导入',
      {
        type: 'info',
        confirmButtonText: '确认导入',
        cancelButtonText: '取消'
      }
    )
    importSubmitting.value = true
    const res = await importConfirm(validUsers.value)
    const count = res.data?.importedCount || 0
    ElMessage.success(`成功导入 ${count} 个用户`)
    importDialogVisible.value = false
    await fetchData()
  } catch (err) {
    if (err !== 'cancel') { /* error handled by interceptor */ }
  } finally {
    importSubmitting.value = false
  }
}

function downloadTemplate() {
  // Build the download URL with token for authentication
  const token = localStorage.getItem('token')
  const a = document.createElement('a')
  a.href = `/api/user/import-template`
  // Use fetch to download with auth header
  fetch('/api/user/import-template', {
    headers: { 'Authorization': `Bearer ${token}` }
  })
    .then(response => {
      if (!response.ok) throw new Error('下载失败')
      return response.blob()
    })
    .then(blob => {
      const url = window.URL.createObjectURL(blob)
      a.href = url
      a.download = '用户导入模板.xlsx'
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
      ElMessage.success('模板下载成功')
    })
    .catch(() => {
      ElMessage.error('模板下载失败，请重试')
    })
}

async function handleBatchCreateSubmit() {
  if (batchCreateRows.value.length === 0) {
    ElMessage.warning('请至少添加一行用户')
    return
  }
  // Validate
  for (let i = 0; i < batchCreateRows.value.length; i++) {
    const row = batchCreateRows.value[i]
    const line = i + 1
    if (!row.username.trim()) {
      ElMessage.warning(`第${line}行：用户名不能为空`)
      return
    }
    if (!row.password) {
      ElMessage.warning(`第${line}行：密码不能为空`)
      return
    }
    if (!row.roleId) {
      ElMessage.warning(`第${line}行：请选择角色`)
      return
    }
  }
  batchCreateSubmitting.value = true
  try {
    const payload = batchCreateRows.value.map(row => ({
      username: row.username.trim(),
      password: row.password,
      realName: row.realName || undefined,
      phone: row.phone || undefined,
      email: row.email || undefined,
      roleId: row.roleId,
      status: 1
    }))
    await batchCreateUsers(payload)
    ElMessage.success(`成功创建 ${batchCreateRows.value.length} 个用户`)
    batchCreateDialogVisible.value = false
    await fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    batchCreateSubmitting.value = false
  }
}

// ── Role List ──

async function fetchRoles() {
  try {
    const res = await getAllRoles()
    roleList.value = res.data || []
  } catch {
    roleList.value = []
  }
}

onMounted(() => {
  fetchData()
  fetchRoles()
})
</script>

<style scoped>
.user-manage {
  padding: 0;
}
.page-header {
  margin-bottom: 20px;
}
.page-header h2 {
  font-size: 20px;
  color: #303133;
  margin: 0;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}
.search-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}
.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.selected-count {
  font-size: 13px;
  color: #409eff;
  font-weight: 500;
  margin-right: 4px;
}
.quick-gen-panel {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}
.gen-label {
  font-weight: 600;
  font-size: 13px;
  color: #303133;
  margin-right: 2px;
}
.gen-sep {
  font-size: 12px;
  color: #909399;
}
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* Excel Import */
.import-upload-area {
  display: flex;
  justify-content: center;
}
.import-summary {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
</style>
