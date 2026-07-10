<template>
  <div class="paper-detail" v-loading="loading">
    <div class="detail-header">
      <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
      <div class="header-actions" v-if="canEdit || canDelete">
        <el-button
          v-if="canEdit"
          type="warning"
          :icon="Edit"
          @click="handleEdit"
        >
          编辑
        </el-button>
        <el-button
          v-if="canDelete"
          type="danger"
          :icon="Delete"
          @click="handleDelete"
        >
          删除
        </el-button>
      </div>
    </div>

    <el-card class="detail-card" v-if="detail">
      <template #header>
        <div class="card-title-row">
          <span>论文详情</span>
          <el-tag :type="statusType(detail.status)" size="default">
            {{ statusLabel(detail.status) }}
          </el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="论文标题" :span="2">
          {{ detail.title }}
        </el-descriptions-item>
        <el-descriptions-item label="期刊/会议名称">
          {{ detail.journalName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="期刊级别">
          <el-tag v-if="detail.journalLevel" :type="journalLevelTagType(detail.journalLevel)" size="small">
            {{ detail.journalLevel }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="投稿日期">
          {{ detail.submissionDate || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="录用日期">
          {{ detail.acceptanceDate || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="关键词" :span="2">
          <div v-if="keywordList.length > 0" class="keyword-list">
            <el-tag
              v-for="(kw, idx) in keywordList"
              :key="idx"
              size="small"
              style="margin-right: 6px; margin-bottom: 4px"
            >
              {{ kw }}
            </el-tag>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="作者" :span="2">
          {{ detail.authors || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="提交人">
          {{ detail.submitterName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">
          {{ detail.createTime || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="detail-card" v-if="hasAnyFile">
      <template #header>附件文件</template>

      <div class="file-section" v-if="detail.draftFileId">
        <div class="file-label">投稿初稿：</div>
        <el-link type="primary" :underline="false" @click="openFile(detail.draftFileId)">
          <el-icon style="margin-right: 4px"><Link /></el-icon>
          {{ detail.draftFileName || '下载文件' }}
        </el-link>
      </div>

      <div class="file-section" v-if="detail.finalFileId">
        <div class="file-label">录用终稿：</div>
        <el-link type="primary" :underline="false" @click="openFile(detail.finalFileId)">
          <el-icon style="margin-right: 4px"><Link /></el-icon>
          {{ detail.finalFileName || '下载文件' }}
        </el-link>
      </div>

      <div class="file-section" v-if="detail.reviewFileId">
        <div class="file-label">专家审稿意见及回复：</div>
        <el-link type="primary" :underline="false" @click="openFile(detail.reviewFileId)">
          <el-icon style="margin-right: 4px"><Link /></el-icon>
          {{ detail.reviewFileName || '下载文件' }}
        </el-link>
      </div>
    </el-card>

    <div class="detail-footer" v-if="!detail">
      <el-empty description="未找到论文信息" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Edit, Delete, Link } from '@element-plus/icons-vue'
import { getPaperDetail, deletePaper } from '../../api/paper'
import { getFileUrl } from '../../api/file'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const detail = ref(null)
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const keywordList = computed(() => {
  if (!detail.value?.keywords) return []
  return detail.value.keywords
    .split(/[,，]/)
    .map((s) => s.trim())
    .filter(Boolean)
})

const hasAnyFile = computed(() => {
  if (!detail.value) return false
  return !!(detail.value.draftFileId || detail.value.finalFileId || detail.value.reviewFileId)
})

const canEdit = computed(() => {
  if (!detail.value) return false
  const isOwner = detail.value.submitterId === userInfo.id || userInfo.role === 'ADMIN'
  return isOwner && (detail.value.status === 'pending_review' || detail.value.status === 'returned')
})

const canDelete = computed(() => {
  if (!detail.value) return false
  const isOwner = detail.value.submitterId === userInfo.id || userInfo.role === 'ADMIN'
  return isOwner
})

const statusType = (status) => {
  const map = {
    pending_review: 'warning',
    under_review: 'primary',
    returned: 'danger',
    archived: 'success'
  }
  return map[status] || 'info'
}

const statusLabel = (status) => {
  const map = {
    pending_review: '待审核',
    under_review: '审核中',
    returned: '已退回',
    archived: '已归档'
  }
  return map[status] || status
}

const journalLevelTagType = (level) => {
  if (!level) return 'info'
  if (level.includes('SCI') || level.includes('CCF A')) return 'danger'
  if (level.includes('CCF B') || level.includes('EI期刊')) return 'warning'
  return 'primary'
}

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('缺少论文ID')
    router.push('/paper')
    return
  }

  loading.value = true
  try {
    const res = await getPaperDetail(id)
    detail.value = res.data || res
  } catch {
    ElMessage.error('获取论文详情失败')
  } finally {
    loading.value = false
  }
}

const openFile = (fileId) => {
  if (fileId) {
    window.open(getFileUrl(fileId), '_blank')
  }
}

const goBack = () => {
  router.push('/paper')
}

const handleEdit = () => {
  if (detail.value) {
    router.push(`/paper/create?id=${detail.value.id}`)
  }
}

const handleDelete = async () => {
  if (!detail.value) return
  try {
    await ElMessageBox.confirm('确定要删除该论文记录吗？', '确认删除', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deletePaper(detail.value.id)
    ElMessage.success('删除成功')
    router.push('/paper')
  } catch {
    // cancelled or error
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.paper-detail {
  max-width: 900px;
  margin: 0 auto;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.header-actions {
  display: flex;
  gap: 10px;
}
.detail-card {
  margin-bottom: 20px;
}
.card-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.keyword-list {
  display: flex;
  flex-wrap: wrap;
}
.file-section {
  display: flex;
  align-items: center;
  padding: 8px 0;
}
.file-section + .file-section {
  border-top: 1px solid #f0f0f0;
}
.file-label {
  width: 160px;
  font-weight: 500;
  color: #606266;
  flex-shrink: 0;
}
.detail-footer {
  padding: 40px 0;
}
</style>
