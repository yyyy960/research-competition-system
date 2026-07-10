<template>
  <div class="app-layout">
    <!-- ============ SIDEBAR ============ -->
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <!-- Logo -->
      <div class="sidebar-logo" @click="$router.push('/dashboard')">
        <div class="sidebar-logo__icon">
          <svg viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="36" height="36" rx="8" fill="#7c3aed"/>
            <path d="M18 8L10 14v8l8 6 8-6v-8l-8-6z" stroke="#fff" stroke-width="1.8" stroke-linejoin="round"/>
            <circle cx="18" cy="18" r="3" fill="#fff"/>
            <path d="M18 15v6M15 18h6" stroke="#7c3aed" stroke-width="1.2" stroke-linecap="round"/>
          </svg>
        </div>
        <transition name="logo-fade">
          <div v-if="!isCollapse" class="sidebar-logo__text">
            <span class="logo-title">科研竞赛管理</span>
            <span class="logo-subtitle">Research CMS</span>
          </div>
        </transition>
      </div>

      <!-- Navigation -->
      <nav class="sidebar-nav">
        <!-- 核心模块 -->
        <div class="nav-group-label" v-show="!isCollapse">核心模块</div>
        <div class="nav-group-label--collapsed" v-show="isCollapse">核心</div>

        <router-link to="/dashboard" class="nav-item" :class="{ active: isActive('/dashboard') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <rect x="2" y="2" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <rect x="11" y="2" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <rect x="2" y="11" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <rect x="11" y="11" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
          </svg>
          <span class="nav-label">数据概览</span>
        </router-link>

        <router-link to="/competition" class="nav-item" :class="{ active: isActive('/competition') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <path d="M6 3h2v14H6V3zM12 3h2v14h-2V3zM3 7h14v2H3V7z" fill="currentColor" opacity="0.85"/>
            <circle cx="10" cy="10" r="7" stroke="currentColor" stroke-width="1.5" fill="none"/>
          </svg>
          <span class="nav-label">学科竞赛</span>
        </router-link>

        <router-link to="/innovation" class="nav-item" :class="{ active: isActive('/innovation') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <path d="M4 3h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4a1 1 0 011-1z" fill="currentColor" opacity="0.7"/>
            <path d="M4 9h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1v-2a1 1 0 011-1z" fill="currentColor" opacity="0.85"/>
            <path d="M4 15h12a1 1 0 011 1v1a1 1 0 01-1 1H4a1 1 0 01-1-1v-1a1 1 0 011-1z" fill="currentColor"/>
          </svg>
          <span class="nav-label">大创项目</span>
        </router-link>

        <router-link to="/copyright" class="nav-item" :class="{ active: isActive('/copyright') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <circle cx="10" cy="10" r="7.5" stroke="currentColor" stroke-width="1.5"/>
            <path d="M7.5 10c0-1.5 1-2.5 2.5-2.5s2.5 1 2.5 2.5-1 2.5-2.5 2.5-2.5-1-2.5-2.5z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M11 13l1 2M9 13l-1 2" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
          </svg>
          <span class="nav-label">软件著作权</span>
        </router-link>

        <router-link to="/paper" class="nav-item" :class="{ active: isActive('/paper') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <path d="M6 2h5l4 4v11a1 1 0 01-1 1H6a1 1 0 01-1-1V3a1 1 0 011-1z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M11 2v4h4" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
            <line x1="7" y1="10" x2="13" y2="10" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
            <line x1="7" y1="13" x2="13" y2="13" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
            <line x1="7" y1="16" x2="10" y2="16" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
          </svg>
          <span class="nav-label">学术论文</span>
        </router-link>

        <router-link to="/ccf" class="nav-item" :class="{ active: isActive('/ccf') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <path d="M3 5h14v10H3z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M3 8h14" stroke="currentColor" stroke-width="1.2"/>
            <path d="M7 5v10" stroke="currentColor" stroke-width="1.2"/>
            <circle cx="5" cy="6.5" r="0.8" fill="currentColor"/>
            <circle cx="5" cy="11.5" r="0.8" fill="currentColor"/>
            <circle cx="9" cy="6.5" r="0.8" fill="currentColor"/>
            <circle cx="9" cy="11.5" r="0.8" fill="currentColor"/>
          </svg>
          <span class="nav-label">CCF目录</span>
        </router-link>

        <!-- 管理功能 -->
        <div class="nav-divider" v-show="!isCollapse"></div>
        <div class="nav-group-label" v-show="!isCollapse">管理功能</div>
        <div class="nav-group-label--collapsed" v-show="isCollapse">管理</div>

        <router-link v-if="isReviewer" to="/review" class="nav-item" :class="{ active: isActive('/review') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <path d="M4 12l3 3 9-9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            <rect x="2" y="2" width="16" height="16" rx="3" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          <span class="nav-label">审核管理</span>
        </router-link>

        <router-link v-if="isReviewer" to="/tasks" class="nav-item" :class="{ active: isActive('/tasks') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <rect x="2" y="3" width="16" height="14" rx="2" stroke="currentColor" stroke-width="1.5"/>
            <line x1="6" y1="8" x2="14" y2="8" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
            <line x1="6" y1="11" x2="12" y2="11" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
            <circle cx="16" cy="5" r="1.5" fill="#ef4444" stroke="#fff" stroke-width="1"/>
          </svg>
          <span class="nav-label">任务中心</span>
        </router-link>

        <router-link to="/file" class="nav-item" :class="{ active: isActive('/file') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <path d="M3 5a1 1 0 011-1h4l2 2h7a1 1 0 011 1v9a1 1 0 01-1 1H4a1 1 0 01-1-1V5z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M3 5a1 1 0 011-1h4l2 2h7a1 1 0 011 1v9a1 1 0 01-1 1H4a1 1 0 01-1-1V5z" fill="currentColor" opacity="0.15"/>
          </svg>
          <span class="nav-label">文件管理</span>
        </router-link>

        <div v-if="isAdmin">
          <div class="nav-group-label" v-show="!isCollapse">系统管理</div>
          <div class="nav-group-label--collapsed" v-show="isCollapse">系统</div>

          <router-link to="/system/user" class="nav-item" :class="{ active: isActive('/system/user') }">
            <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
              <circle cx="8" cy="7" r="3" stroke="currentColor" stroke-width="1.5"/>
              <path d="M2 17v-1a4 4 0 014-4h4a4 4 0 014 4v1" stroke="currentColor" stroke-width="1.5"/>
              <path d="M12 5h6M15 2v6" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
            </svg>
            <span class="nav-label">用户管理</span>
          </router-link>

          <router-link to="/system/log" class="nav-item" :class="{ active: isActive('/system/log') }">
            <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
              <rect x="2" y="3" width="16" height="14" rx="2" stroke="currentColor" stroke-width="1.5"/>
              <line x1="6" y1="8" x2="14" y2="8" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
              <line x1="6" y1="11" x2="12" y2="11" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
              <line x1="6" y1="14" x2="10" y2="14" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
            </svg>
            <span class="nav-label">系统日志</span>
          </router-link>

          <router-link to="/system/announcement" class="nav-item" :class="{ active: isActive('/system/announcement') }">
            <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
              <path d="M8 3h4l3 4v8a1 1 0 01-1 1H6a1 1 0 01-1-1V4a1 1 0 011-1z" stroke="currentColor" stroke-width="1.5"/>
              <path d="M8 3v4h4" stroke="currentColor" stroke-width="1.5"/>
              <circle cx="10" cy="12" r="2" stroke="currentColor" stroke-width="1.2"/>
            </svg>
            <span class="nav-label">系统公告</span>
          </router-link>
        </div>

        <!-- 个人中心 -->
        <div class="nav-divider" v-show="!isCollapse"></div>
        <div class="nav-group-label" v-show="!isCollapse">个人中心</div>

        <router-link to="/profile" class="nav-item" :class="{ active: isActive('/profile') && !isActive('/profile/achievements') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <circle cx="10" cy="7" r="4" stroke="currentColor" stroke-width="1.5"/>
            <path d="M3 18v-1a5 5 0 015-5h4a5 5 0 015 5v1" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          <span class="nav-label">个人信息</span>
        </router-link>

        <router-link to="/profile/achievements" class="nav-item" :class="{ active: isActive('/profile/achievements') }">
          <svg class="nav-icon" viewBox="0 0 20 20" fill="none">
            <rect x="2" y="2" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <rect x="11" y="2" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <rect x="11" y="11" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <path d="M3.5 14l2-2 1.5 1.5 2.5-2.5 2 2" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span class="nav-label">成果概览</span>
        </router-link>
      </nav>

      <!-- Collapse toggle at bottom -->
      <div class="sidebar-collapse-btn" @click="isCollapse = !isCollapse">
        <svg :class="{ rotated: isCollapse }" viewBox="0 0 20 20" fill="none">
          <path d="M12 5l-5 5 5 5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </aside>

    <!-- ============ MAIN AREA ============ -->
    <div class="main-area">
      <!-- Header -->
      <header class="header">
        <div class="header-left">
          <!-- Breadcrumb / Page title could go here -->
        </div>
        <div class="header-right">
          <!-- Global Search -->
          <div class="header-search">
            <svg class="search-icon" viewBox="0 0 20 20" fill="none">
              <circle cx="9" cy="9" r="5.5" stroke="currentColor" stroke-width="1.6"/>
              <path d="M13 13l4 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
            </svg>
            <input
              v-model="searchKeyword"
              type="text"
              class="search-input"
              placeholder="搜索竞赛、论文、项目..."
              @keyup.enter="handleGlobalSearch"
            />
          </div>

          <!-- Notification Bell -->
          <NotificationBell />

          <!-- User Dropdown -->
          <el-dropdown trigger="click" @command="handleUserCommand" popper-class="user-dropdown-popper">
            <div class="user-trigger">
              <div class="user-avatar">
                {{ avatarLetter }}
              </div>
              <span class="user-name">{{ userInfo?.realName || userInfo?.username || '用户' }}</span>
              <svg class="dropdown-arrow" viewBox="0 0 20 20" fill="none">
                <path d="M6 8l4 4 4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <svg class="dropdown-item-icon" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="5" r="3" stroke="currentColor" stroke-width="1.3"/>
                    <path d="M3 14v-1a3.5 3.5 0 013.5-3.5h3A3.5 3.5 0 0113 13v1" stroke="currentColor" stroke-width="1.3"/>
                  </svg>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="achievements">
                  <svg class="dropdown-item-icon" viewBox="0 0 16 16" fill="none">
                    <path d="M2 2h4v4H2zM10 2h4v4h-4zM2 10h4v4H2z" stroke="currentColor" stroke-width="1.3"/>
                    <path d="M7 5h3M7 12h3M10 7v6" stroke="currentColor" stroke-width="1" stroke-linecap="round"/>
                  </svg>
                  成果概览
                </el-dropdown-item>
                <el-dropdown-item command="notifications">
                  <svg class="dropdown-item-icon" viewBox="0 0 16 16" fill="none">
                    <path d="M8 2a4 4 0 00-4 4v2l-1 2h10l-1-2V6a4 4 0 00-4-4z" stroke="currentColor" stroke-width="1.3"/>
                    <circle cx="8" cy="13" r="1" fill="currentColor"/>
                  </svg>
                  通知消息
                </el-dropdown-item>
                <el-divider style="margin:4px 0" />
                <el-dropdown-item command="logout" divided>
                  <svg class="dropdown-item-icon" viewBox="0 0 16 16" fill="none" style="color:#ef4444">
                    <path d="M6 2H3a1 1 0 00-1 1v10a1 1 0 001 1h3M11 11l3-3-3-3M14 8H6" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span style="color:#ef4444">退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- Content -->
      <main class="main-content">
        <router-view v-slot="{ Component, route }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'
import { getCurrentUser } from '../api/auth'
import NotificationBell from '../components/NotificationBell.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const searchKeyword = ref('')
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

// Sidebar collapse responsive — auto-collapse on small screens
const mql = window.matchMedia('(max-width: 1024px)')
const onMediaChange = (e) => { isCollapse.value = e.matches }
onMounted(() => {
  mql.addEventListener('change', onMediaChange)
  if (mql.matches) isCollapse.value = true
})
onUnmounted(() => { mql.removeEventListener('change', onMediaChange) })

const isActive = (path) => {
  if (path === '/dashboard') return route.path === '/dashboard'
  if (path === '/profile/achievements') return route.path === '/profile/achievements'
  if (path === '/profile') return route.path === '/profile'
  // For system routes, exact match to avoid all matching /system
  if (path.startsWith('/system')) return route.path === path
  return route.path.startsWith(path)
}

const avatarLetter = computed(() => {
  const name = userInfo.value?.realName || userInfo.value?.username || 'U'
  return name.charAt(0).toUpperCase()
})

const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
const isReviewer = computed(() =>
  ['ADMIN', 'SECRETARY', 'LEADER'].includes(userInfo.value?.role)
)

const handleGlobalSearch = () => {
  const kw = searchKeyword.value.trim()
  if (!kw) return
  // Navigate based on a simple heuristic or go to competition as default
  if (kw.includes('竞赛') || kw.includes('比赛')) {
    router.push({ path: '/competition', query: { keyword: kw } })
  } else if (kw.includes('论文') || kw.includes('期刊') || kw.includes('CCF')) {
    router.push({ path: '/paper', query: { keyword: kw } })
  } else if (kw.includes('项目') || kw.includes('大创')) {
    router.push({ path: '/innovation', query: { keyword: kw } })
  } else if (kw.includes('软著') || kw.includes('著作权')) {
    router.push({ path: '/copyright', query: { keyword: kw } })
  } else {
    router.push({ path: '/competition', query: { keyword: kw } })
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'achievements':
      router.push('/profile/achievements')
      break
    case 'notifications':
      router.push('/notifications')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗?', '提示', { type: 'warning' })
  } catch {
    return
  }
  userStore.logout()
  router.push('/login')
}

onMounted(async () => {
  try {
    const res = await getCurrentUser()
    if (res.data) {
      const newInfo = res.data
      // Detect role change and force sidebar refresh
      const oldRole = userInfo.value?.role
      if (oldRole && oldRole !== newInfo.role) {
        // Role changed — update immediately and force reactivity
        userInfo.value = null
      }
      userInfo.value = newInfo
      localStorage.setItem('userInfo', JSON.stringify(newInfo))
    }
  } catch {
    // Keep existing userInfo from localStorage — don't clear it
    if (!userInfo.value) {
      userInfo.value = JSON.parse(localStorage.getItem('userInfo') || 'null')
    }
  }
})
</script>

<style scoped>
/* ============================
   APP LAYOUT
   ============================ */
.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* ============================
   SIDEBAR
   ============================ */
.sidebar {
  width: var(--sidebar-width);
  min-width: var(--sidebar-width);
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  transition: width var(--transition-base), min-width var(--transition-base);
  overflow: hidden;
  user-select: none;
  z-index: 100;
}
.sidebar.collapsed {
  width: var(--sidebar-collapsed);
  min-width: var(--sidebar-collapsed);
}

/* Logo */
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 16px;
  cursor: pointer;
  background: var(--sidebar-bg-dark);
  min-height: 64px;
  transition: padding var(--transition-base);
}
.sidebar.collapsed .sidebar-logo {
  padding: 16px 14px;
  justify-content: center;
}
.sidebar-logo__icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
}
.sidebar-logo__icon svg {
  width: 32px;
  height: 32px;
}
.sidebar-logo__text {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  white-space: nowrap;
}
.logo-title {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
}
.logo-subtitle {
  font-size: 10px;
  color: #a0aec0;
  letter-spacing: 1px;
  text-transform: uppercase;
}
.logo-fade-enter-active, .logo-fade-leave-active {
  transition: opacity var(--transition-base);
}
.logo-fade-enter-from, .logo-fade-leave-to {
  opacity: 0;
}

/* Nav */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 0;
}
.sidebar-nav::-webkit-scrollbar { width: 3px; }
.sidebar-nav::-webkit-scrollbar-thumb { background: rgba(255,255,255,.1); border-radius: 2px; }

.nav-group-label {
  padding: 16px 18px 6px;
  font-size: 11px;
  font-weight: 600;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 1.5px;
}
.nav-group-label--collapsed {
  padding: 12px 0 4px;
  text-align: center;
  font-size: 10px;
  font-weight: 600;
  color: #718096;
  letter-spacing: 1px;
}
.nav-divider {
  height: 1px;
  background: rgba(255,255,255,.06);
  margin: 8px 16px;
}

/* Nav Item */
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 18px;
  margin: 2px 8px;
  border-radius: 8px;
  color: var(--sidebar-text);
  text-decoration: none;
  font-size: 14px;
  font-weight: 400;
  transition: all var(--transition-fast);
  position: relative;
}
.sidebar.collapsed .nav-item {
  padding: 10px;
  justify-content: center;
  margin: 2px 8px;
}
.nav-item:hover {
  color: var(--sidebar-text-hover);
  background: rgba(255,255,255,.08);
}
.nav-item.active {
  color: #fff;
  background: var(--sidebar-active-bg);
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(124,58,237,.35);
}
.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}
.nav-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Collapse Button at bottom */
.sidebar-collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px;
  cursor: pointer;
  color: #718096;
  transition: all var(--transition-fast);
  border-top: 1px solid rgba(255,255,255,.06);
}
.sidebar-collapse-btn:hover {
  color: #fff;
  background: rgba(255,255,255,.05);
}
.sidebar-collapse-btn svg {
  width: 20px;
  height: 20px;
  transition: transform var(--transition-base);
}
.sidebar-collapse-btn svg.rotated {
  transform: rotate(180deg);
}

/* ============================
   MAIN AREA
   ============================ */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ============================
   HEADER
   ============================ */
.header {
  height: var(--header-height);
  min-height: var(--header-height);
  background: var(--header-bg);
  box-shadow: var(--header-shadow);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 50;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* Search */
.header-search {
  position: relative;
  display: flex;
  align-items: center;
}
.search-icon {
  position: absolute;
  left: 12px;
  width: 16px;
  height: 16px;
  color: #9ca3af;
  pointer-events: none;
}
.search-input {
  width: 260px;
  height: 36px;
  padding: 0 16px 0 36px;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  background: #f9fafb;
  font-size: 13px;
  color: var(--color-text-primary);
  outline: none;
  transition: all var(--transition-fast);
}
.search-input::placeholder {
  color: #9ca3af;
}
.search-input:focus {
  border-color: var(--color-primary);
  background: #fff;
  box-shadow: 0 0 0 3px rgba(124,58,237,.1);
}

/* User Trigger */
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background var(--transition-fast);
}
.user-trigger:hover {
  background: #f5f3ff;
}
.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.dropdown-arrow {
  width: 14px;
  height: 14px;
  color: #9ca3af;
  flex-shrink: 0;
}

/* Dropdown item icon */
.dropdown-item-icon {
  width: 15px;
  height: 15px;
  margin-right: 8px;
  vertical-align: middle;
  color: #6b7280;
}

/* ============================
   MAIN CONTENT
   ============================ */
.main-content {
  flex: 1;
  overflow-y: auto;
  background: var(--main-bg);
  padding: 20px 24px;
}

/* Page transition */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ============================
   RESPONSIVE
   ============================ */
@media (max-width: 768px) {
  .sidebar {
    width: var(--sidebar-collapsed);
    min-width: var(--sidebar-collapsed);
  }
  .header {
    padding: 0 12px;
  }
  .search-input {
    width: 160px;
  }
  .user-name {
    display: none;
  }
  .main-content {
    padding: 12px;
  }
}
</style>

<!-- Global (unscoped) style for user dropdown popper -->
<style>
.user-dropdown-popper {
  margin-top: 6px !important;
  border-radius: 10px !important;
  box-shadow: 0 8px 24px rgba(0,0,0,.12) !important;
  border: 1px solid #f3f4f6 !important;
}
.user-dropdown-popper .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  padding: 9px 14px;
  font-size: 13px;
}
</style>
