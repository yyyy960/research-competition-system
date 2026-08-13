<template>
  <div class="notifications-page">
    <div class="page-header">
      <h2>通知消息</h2>
      <el-button type="primary" size="small" @click="handleMarkAllRead" :disabled="allRead">
        全部已读
      </el-button>
    </div>

    <el-card shadow="never">
      <template #header>
        <div class="list-toolbar">
          <div class="filter-group">
            <el-select v-model="filterType" placeholder="类型" clearable style="width: 120px" @change="fetchList">
              <el-option label="审核通知" value="review" />
              <el-option label="退回通知" value="return" />
              <el-option label="归档通知" value="archive" />
              <el-option label="系统通知" value="system" />
            </el-select>
            <el-select v-model="filterRead" placeholder="状态" clearable style="width: 110px" @change="fetchList">
              <el-option label="未读" value="unread" />
              <el-option label="已读" value="read" />
            </el-select>
          </div>
        </div>
      </template>

      <div v-loading="loading">
        <el-empty v-if="!loading && notifications.length === 0" description="暂无通知" />

        <div v-else class="notification-list">
          <div
            v-for="item in notifications"
            :key="item.id"
            class="notification-card"
            :class="{ unread: !item.read }"
            @click="handleClick(item)"
          >
            <div class="notif-left">
              <div class="notif-icon" :class="'icon-' + (item.type || 'system')">
                <el-icon v-if="item.type === 'review'" color="#E6A23C" :size="20"><Checked /></el-icon>
                <el-icon v-else-if="item.type === 'return'" color="#F56C6C" :size="20"><CloseBold /></el-icon>
                <el-icon v-else-if="item.type === 'archive'" color="#67C23A" :size="20"><SuccessFilled /></el-icon>
                <el-icon v-else color="#7c3aed" :size="20"><Message /></el-icon>
              </div>
            </div>
            <div class="notif-body">
              <div class="notif-header">
                <span class="notif-title">{{ item.title }}</span>
                <div class="notif-meta">
                  <el-tag v-if="!item.read" size="small" type="danger" effect="plain">未读</el-tag>
                  <span class="notif-time">{{ item.createdAt }}</span>
                </div>
              </div>
              <div class="notif-content">{{ item.content }}</div>
            </div>
            <div class="notif-action">
              <el-icon v-if="!item.read" color="#7c3aed" :size="16"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <div v-if="total > 0" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @current-change="fetchList"
            @size-change="fetchList"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getNotificationPage, markRead, markAllRead } from '../../api/notification'

const router = useRouter()

const notifications = ref([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const size = ref(10)

const filterType = ref('')
const filterRead = ref('')

const allRead = computed(() => {
  return notifications.value.length > 0 && notifications.value.every(n => n.read)
})

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (filterType.value) params.type = filterType.value
    if (filterRead.value) params.read = filterRead.value === 'read'

    const res = await getNotificationPage(params)
    const data = res.data
    notifications.value = data?.records || data || []
    total.value = data?.total || 0
  } catch {
    ElMessage.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

const handleClick = async (item) => {
  // Mark as read if unread
  if (!item.read) {
    try {
      await markRead(item.id)
      item.read = true
    } catch {
      // Ignore
    }
  }

  // Navigate to related achievement if available
  if (item.link) {
    router.push(item.link)
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllRead()
    // Optimistically update local state
    notifications.value.forEach(n => { n.read = true })
    ElMessage.success('已全部标记为已读')
  } catch {
    ElMessage.error('操作失败，请重试')
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.notifications-page {
  padding: 0;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-header h2 {
  font-size: 20px;
  color: #303133;
  margin: 0;
}
.list-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.filter-group {
  display: flex;
  gap: 8px;
}
.notification-list {
  max-height: 600px;
  overflow-y: auto;
}
.notification-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
  border-radius: 4px;
}
.notification-card:hover {
  background-color: #f5f7fa;
}
.notification-card.unread {
  background-color: #f5f3ff;
}
.notification-card.unread:hover {
  background-color: #ede9fe;
}
.notif-left {
  flex-shrink: 0;
}
.notif-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #f5f7fa;
}
.icon-review {
  background-color: #fdf6ec;
}
.icon-return {
  background-color: #fef0f0;
}
.icon-archive {
  background-color: #f0f9eb;
}
.icon-system {
  background-color: #f5f3ff;
}
.notif-body {
  flex: 1;
  min-width: 0;
}
.notif-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}
.notif-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}
.notif-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.notif-time {
  font-size: 12px;
  color: #c0c4cc;
  white-space: nowrap;
}
.notif-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.notif-action {
  flex-shrink: 0;
  color: #c0c4cc;
}
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
