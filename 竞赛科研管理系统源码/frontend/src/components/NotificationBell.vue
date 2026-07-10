<template>
  <el-popover placement="bottom-end" :width="360" trigger="click" popper-class="notification-popover">
    <template #reference>
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge" :max="99">
        <el-icon :size="22" class="bell-icon" @click="refresh"><Bell /></el-icon>
      </el-badge>
    </template>

    <div class="notification-header">
      <span class="notification-title">通知消息</span>
      <el-button text type="primary" size="small" @click="handleMarkAllRead">全部已读</el-button>
    </div>

    <div v-loading="loading" class="notification-list">
      <div v-if="!loading && notifications.length === 0" class="empty-tip">暂无通知</div>
      <div v-for="item in notifications" :key="item.id" class="notification-item"
        :class="{ unread: !item.isRead }" @click="handleClick(item)">
        <div class="notif-icon">
          <el-icon v-if="item.notificationType === 'review'" color="#E6A23C"><Checked /></el-icon>
          <el-icon v-else-if="item.notificationType === 'return'" color="#F56C6C"><CloseBold /></el-icon>
          <el-icon v-else-if="item.notificationType === 'archive'" color="#67C23A"><SuccessFilled /></el-icon>
          <el-icon v-else color="#7c3aed"><Message /></el-icon>
        </div>
        <div class="notif-content">
          <div class="notif-title">{{ item.title }}</div>
          <div class="notif-preview">{{ item.content }}</div>
          <div class="notif-time">{{ formatTime(item.createTime) }}</div>
        </div>
        <div v-if="!item.isRead" class="notif-dot" />
        <el-icon class="notif-delete" :size="14" @click.stop="handleDelete(item)"><Close /></el-icon>
      </div>
    </div>

    <div class="notification-footer">
      <el-link type="primary" :underline="false" @click="$router.push('/notifications')">查看全部</el-link>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNotificationPage, getUnreadCount, markRead, markAllRead, deleteNotification
} from '../api/notification'

const router = useRouter()
const notifications = ref([])
const unreadCount = ref(0)
const loading = ref(false)
let pollTimer = null

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await getNotificationPage({ page: 1, size: 5 })
    notifications.value = res.data?.records || []
  } catch { /* ignore */ }
  finally { loading.value = false }
}

const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch { /* ignore */ }
}

const refresh = async () => {
  await Promise.all([fetchNotifications(), fetchUnreadCount()])
}

const handleClick = async (item) => {
  if (!item.isRead) {
    try { await markRead(item.id) } catch { /* ignore */ }
  }
  // Navigate to the related achievement
  if (item.relatedType && item.relatedId) {
    const pathMap = {
      competition: `/competition/${item.relatedId}`,
      innovation: `/innovation/${item.relatedId}`,
      copyright: `/copyright/${item.relatedId}`,
      paper: `/paper/${item.relatedId}`
    }
    const path = pathMap[item.relatedType]
    if (path) {
      router.push(path)
      return
    }
  }
  router.push('/tasks')
}

const handleMarkAllRead = async () => {
  try {
    await markAllRead()
    ElMessage.success('已全部标记为已读')
    await refresh()
  } catch { ElMessage.error('操作失败，请重试') }
}

const handleDelete = async (item) => {
  try {
    await deleteNotification(item.id)
    notifications.value = notifications.value.filter(n => n.id !== item.id)
    if (!item.isRead) await fetchUnreadCount()
    ElMessage.success('已删除')
  } catch { ElMessage.error('删除失败') }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchUnreadCount()
  pollTimer = setInterval(fetchUnreadCount, 30000)
})

onUnmounted(() => { if (pollTimer) clearInterval(pollTimer) })
</script>

<style scoped>
.notification-badge { cursor: pointer; line-height: 1; }
.bell-icon:hover { color: var(--color-primary); }
.notification-header { display: flex; align-items: center; justify-content: space-between; padding-bottom: 8px; border-bottom: 1px solid #ebeef5; margin-bottom: 4px; }
.notification-title { font-weight: 600; font-size: 14px; color: #303133; }
.notification-list { max-height: 360px; overflow-y: auto; }
.empty-tip { text-align: center; color: #909399; padding: 24px 0; font-size: 14px; }
.notification-item { display: flex; align-items: flex-start; gap: 10px; padding: 10px 4px; cursor: pointer; border-radius: 4px; position: relative; transition: background-color 0.2s; }
.notification-item:hover { background-color: #f5f7fa; }
.notification-item.unread { background-color: #f5f3ff; }
.notif-icon { flex-shrink: 0; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; border-radius: 50%; background-color: #f5f7fa; }
.notif-content { flex: 1; min-width: 0; }
.notif-title { font-size: 14px; font-weight: 500; color: #303133; margin-bottom: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.notif-preview { font-size: 12px; color: #909399; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 2px; }
.notif-time { font-size: 11px; color: #c0c4cc; }
.notif-dot { width: 8px; height: 8px; border-radius: 50%; background-color: #F56C6C; flex-shrink: 0; margin-top: 4px; }
.notification-footer { text-align: center; padding-top: 8px; border-top: 1px solid #ebeef5; }
.notif-delete { flex-shrink: 0; color: #c0c4cc; cursor: pointer; opacity: 0; transition: opacity 0.2s; }
.notification-item:hover .notif-delete { opacity: 1; }
.notif-delete:hover { color: #F56C6C; }
</style>
