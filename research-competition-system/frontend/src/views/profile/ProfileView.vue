<template>
  <div class="profile-view">
    <div class="page-header">
      <h2>个人中心</h2>
    </div>

    <el-row :gutter="20">
      <!-- Personal Info Card -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" link size="small" @click="showEditDialog = true">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
            </div>
          </template>
          <div class="user-info">
            <div class="info-avatar">
              <el-avatar :size="80" class="user-avatar-bg">
                {{ avatarLetter }}
              </el-avatar>
            </div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ userInfo.realName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="角色">
                <el-tag :type="roleTag(userInfo.role)" effect="plain">
                  {{ roleLabel(userInfo.role) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="手机号">{{ userInfo.phone || '-' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ userInfo.email || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <!-- Change Password Card -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="120px"
            label-position="right"
            style="max-width: 420px"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="form.oldPassword"
                type="password"
                show-password
                placeholder="请输入原密码"
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="form.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码（至少6位）"
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleChangePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- Edit Profile Dialog -->
    <el-dialog v-model="showEditDialog" title="编辑个人信息" width="460px" destroy-on-close>
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="handleSaveProfile">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { changePassword, getCurrentUser } from '../../api/auth'

const router = useRouter()

const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const avatarLetter = computed(() => {
  const name = userInfo.value?.realName || userInfo.value?.username || 'U'
  return name.charAt(0).toUpperCase()
})

// ── Password Change ──
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const roleMap = {
  STUDENT: { label: '学生/教师', tag: '' },
  SECRETARY: { label: '科研秘书', tag: 'warning' },
  LEADER: { label: '学院领导', tag: 'success' },
  ADMIN: { label: '管理员', tag: 'danger' }
}

function roleLabel(role) { return roleMap[role]?.label || role }
function roleTag(role) { return roleMap[role]?.tag || '' }

const validateConfirm = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

async function handleChangePassword() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await changePassword({ oldPassword: form.oldPassword, newPassword: form.newPassword })
    ElMessage.success('密码修改成功，即将跳转到登录页...')
    // Clear auth state and redirect
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    setTimeout(() => { router.push('/login') }, 1500)
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

// ── Edit Profile ──
const showEditDialog = ref(false)
const editSubmitting = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  phone: '',
  email: ''
})

const editRules = {
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleSaveProfile = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  editSubmitting.value = true
  try {
    // Update local state immediately (backend update would need a PUT /user/profile endpoint)
    userInfo.value = { ...userInfo.value, phone: editForm.phone, email: editForm.email }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    ElMessage.success('个人信息已更新')
    showEditDialog.value = false
  } catch {
    ElMessage.error('更新失败')
  } finally {
    editSubmitting.value = false
  }
}

// Refresh user info from API on mount
const refreshUserInfo = async () => {
  try {
    const res = await getCurrentUser()
    if (res.data) {
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    }
  } catch { /* use cached data */ }
}

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    const parsed = JSON.parse(stored)
    userInfo.value = parsed
    editForm.phone = parsed.phone || ''
    editForm.email = parsed.email || ''
  }
  refreshUserInfo()
})
</script>

<style scoped>
.profile-view {
  max-width: 1100px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 20px;
}
.page-header h2 {
  font-size: 20px;
  color: var(--color-text-primary);
  margin: 0;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.user-info {
  text-align: center;
}
.info-avatar {
  margin-bottom: 20px;
}
.user-avatar-bg {
  background: linear-gradient(135deg, #7c3aed, #a78bfa) !important;
  color: #fff;
  font-size: 32px;
  font-weight: 600;
}
.el-descriptions {
  text-align: left;
  margin-top: 10px;
}
</style>
