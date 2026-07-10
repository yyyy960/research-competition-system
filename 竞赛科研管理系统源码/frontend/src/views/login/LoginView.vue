<template>
  <div class="login-page">
    <!-- Decorative background -->
    <div class="login-bg">
      <div class="bg-shape bg-shape--1"></div>
      <div class="bg-shape bg-shape--2"></div>
      <div class="bg-shape bg-shape--3"></div>
    </div>

    <div class="login-card">
      <!-- Brand -->
      <div class="login-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" fill="none">
            <rect width="48" height="48" rx="12" fill="#7c3aed"/>
            <path d="M24 12L14 18v12l10 6 10-6V18L24 12z" stroke="#fff" stroke-width="2" stroke-linejoin="round"/>
            <circle cx="24" cy="24" r="4" fill="#fff"/>
            <path d="M24 20v8M20 24h8" stroke="#7c3aed" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="brand-title">科研竞赛管理系统</h1>
        <p class="brand-subtitle">Research Competition Management System</p>
      </div>

      <!-- Form -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="UserIcon"
            class="login-input"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="LockIcon"
            show-password
            class="login-input"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- Tips -->
      <div class="login-tips">
        <div class="tips-title">💡 演示账号</div>
        <div class="tips-grid">
          <div class="tips-item">
            <span class="tips-role admin">管理员</span>
            <code>admin</code>
          </div>
          <div class="tips-item">
            <span class="tips-role student">学生/教师</span>
            <code>student1</code>
          </div>
          <div class="tips-item">
            <span class="tips-role secretary">科研秘书</span>
            <code>secretary</code>
          </div>
          <div class="tips-item">
            <span class="tips-role leader">学院领导</span>
            <code>leader</code>
          </div>
        </div>
        <div class="tips-pwd">默认密码：<code>admin123</code></div>
      </div>
    </div>

    <div class="login-footer">
      © 2026 科研竞赛管理系统 · 科研成果申报与审核平台
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, h } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// Inline SVG icon render functions
const UserIcon = h('svg', {
  viewBox: '0 0 16 16', fill: 'none', width: 16, height: 16,
  innerHTML: '<circle cx="8" cy="5.5" r="3.5" stroke="currentColor" stroke-width="1.3"/><path d="M2.5 14v-1a4.5 4.5 0 014.5-4.5h2a4.5 4.5 0 014.5 4.5v1" stroke="currentColor" stroke-width="1.3"/>'
})
const LockIcon = h('svg', {
  viewBox: '0 0 16 16', fill: 'none', width: 16, height: 16,
  innerHTML: '<rect x="3.5" y="7" width="9" height="7" rx="1.5" stroke="currentColor" stroke-width="1.3"/><path d="M5.5 7V4.5a2.5 2.5 0 015 0V7" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>'
})

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/dashboard')
  } catch {
    ElMessage.error('用户名或密码错误，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #5b21b6 100%);
  position: relative;
  overflow: hidden;
}

/* ── Decorative Background ── */
.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
  background: #fff;
}
.bg-shape--1 {
  width: 500px; height: 500px;
  top: -150px; right: -100px;
}
.bg-shape--2 {
  width: 300px; height: 300px;
  bottom: -80px; left: -60px;
}
.bg-shape--3 {
  width: 200px; height: 200px;
  top: 40%; left: 60%;
}

/* ── Card ── */
.login-card {
  position: relative;
  width: 440px;
  padding: 40px 44px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2), 0 0 0 1px rgba(255,255,255,0.1);
  z-index: 1;
}

/* ── Brand ── */
.login-brand {
  text-align: center;
  margin-bottom: 32px;
}
.brand-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
}
.brand-icon svg {
  width: 56px; height: 56px;
}
.brand-title {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 6px;
  letter-spacing: 0.5px;
}
.brand-subtitle {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* ── Form ── */
.login-form {
  margin-bottom: 0;
}
.login-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e5e7eb;
  padding: 2px 14px;
  transition: all 0.2s;
}
.login-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c4b5fd;
}
.login-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #7c3aed, 0 0 0 3px rgba(124,58,237,0.1);
}
.login-input :deep(.el-input__prefix) {
  color: #9ca3af;
}
.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #7c3aed, #6366f1);
  border: none;
  box-shadow: 0 4px 14px rgba(124, 58, 237, 0.35);
  transition: all 0.25s;
}
.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(124, 58, 237, 0.45);
}
.login-btn:active {
  transform: translateY(0);
}
.login-btn.is-loading {
  background: linear-gradient(135deg, #7c3aed, #6366f1);
}

/* ── Tips ── */
.login-tips {
  margin-top: 24px;
  padding: 16px 18px;
  background: #fafbfc;
  border-radius: 10px;
  border: 1px solid #f3f4f6;
}
.tips-title {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 10px;
}
.tips-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
  margin-bottom: 6px;
}
.tips-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
}
.tips-role {
  padding: 1px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}
.tips-role.admin     { background: #f5f3ff; color: #7c3aed; }
.tips-role.student   { background: #ecfdf5; color: #10b981; }
.tips-role.secretary { background: #fffbeb; color: #d97706; }
.tips-role.leader    { background: #eef2ff; color: #6366f1; }
.tips-item code, .tips-pwd code {
  font-size: 12px;
  background: #fff;
  padding: 1px 8px;
  border-radius: 4px;
  color: #6b7280;
  border: 1px solid #e5e7eb;
}
.tips-pwd {
  font-size: 11px;
  color: #9ca3af;
  text-align: center;
}

/* ── Footer ── */
.login-footer {
  position: relative;
  z-index: 1;
  margin-top: 24px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  letter-spacing: 0.5px;
}

/* ── Responsive ── */
@media (max-width: 500px) {
  .login-card {
    width: 92%;
    padding: 32px 24px;
  }
  .tips-grid {
    grid-template-columns: 1fr;
  }
}
</style>
