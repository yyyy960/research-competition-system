<template>
  <div class="announcement-detail">
    <div class="page-header">
      <el-button @click="handleBack" :icon="ArrowLeft">
        返回
      </el-button>
    </div>

    <el-card shadow="never" v-loading="pageLoading">
      <!-- Error State -->
      <el-result
        v-if="!pageLoading && error"
        icon="error"
        title="加载失败"
        :sub-title="error"
      >
        <template #extra>
          <el-button type="primary" @click="fetchDetail">重新加载</el-button>
          <el-button @click="handleBack">返回</el-button>
        </template>
      </el-result>

      <!-- Content -->
      <template v-else-if="!pageLoading && data">
        <article class="announce-content">
          <header class="announce-header">
            <div class="announce-title-row">
              <el-tag v-if="data.isTop" type="warning" effect="dark" size="small" class="top-badge">
                置顶
              </el-tag>
              <h1 class="announce-title">{{ data.title }}</h1>
            </div>
            <div class="announce-meta">
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ data.publisher || '系统' }}
              </span>
              <span class="meta-divider">·</span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ data.publishTime || data.createTime || '-' }}
              </span>
            </div>
          </header>

          <el-divider />

          <div class="announce-body" v-html="renderedContent"></div>

          <!-- Attachments -->
          <template v-if="data.files && data.files.length > 0">
            <el-divider />
            <div class="attachments">
              <h4>附件</h4>
              <div v-for="file in data.files" :key="file.id" class="attach-item">
                <el-icon><Document /></el-icon>
                <a :href="getFileUrl(file.id)" target="_blank" class="attach-link">
                  {{ file.originalName || file.name || '附件' }}
                </a>
                <span class="attach-size">{{ formatSize(file.size) }}</span>
              </div>
            </div>
          </template>
        </article>
      </template>

      <!-- Empty fallback -->
      <el-empty v-else-if="!pageLoading" description="公告不存在或已被删除" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, User, Clock, Document } from '@element-plus/icons-vue'
import { getAnnouncementDetail } from '../../api/announcement'
import { getFileUrl } from '../../api/file'

const route = useRoute()
const router = useRouter()

const pageLoading = ref(false)
const data = ref(null)
const error = ref('')

// Render content: handle both HTML content and plain text safely
const renderedContent = computed(() => {
  if (!data.value?.content) return ''

  const content = data.value.content
  // If content contains HTML tags, render as-is (admin-authored rich text)
  if (/<[a-z][\s\S]*>/i.test(content)) {
    return content
  }
  // Plain text: escape HTML then convert newlines to <br>
  return content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n/g, '<br>')
})

function formatSize(bytes) {
  if (!bytes) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) {
    error.value = '缺少公告ID参数'
    return
  }

  pageLoading.value = true
  error.value = ''
  try {
    const res = await getAnnouncementDetail(id)
    if (res.data) {
      data.value = res.data
    } else {
      error.value = '未找到该公告'
    }
  } catch (e) {
    error.value = '加载公告详情失败'
  } finally {
    pageLoading.value = false
  }
}

const handleBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/dashboard')
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.announcement-detail {
  max-width: 900px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 16px;
}

/* Header */
.announce-header {
  text-align: center;
}
.announce-title-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 12px;
}
.top-badge {
  flex-shrink: 0;
}
.announce-title {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  line-height: 1.4;
}
.announce-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 13px;
  color: #9ca3af;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.meta-divider {
  color: #d1d5db;
}

/* Body */
.announce-body {
  padding: 8px 0;
  font-size: 15px;
  line-height: 1.8;
  color: #374151;
}
.announce-body :deep(img) {
  max-width: 100%;
  border-radius: 4px;
}
.announce-body :deep(p) {
  margin-bottom: 12px;
}
.announce-body :deep(ul),
.announce-body :deep(ol) {
  padding-left: 24px;
  margin-bottom: 12px;
}
.announce-body :deep(blockquote) {
  border-left: 4px solid #7c3aed;
  padding: 8px 16px;
  margin: 12px 0;
  background: #f5f3ff;
  border-radius: 0 4px 4px 0;
}

/* Attachments */
.attachments h4 {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}
.attach-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: #f9fafb;
  border-radius: 6px;
  margin-bottom: 8px;
  border: 1px solid #f3f4f6;
}
.attach-link {
  flex: 1;
  color: var(--color-primary);
  text-decoration: none;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.attach-link:hover {
  text-decoration: underline;
}
.attach-size {
  font-size: 11px;
  color: #9ca3af;
  flex-shrink: 0;
}
</style>
