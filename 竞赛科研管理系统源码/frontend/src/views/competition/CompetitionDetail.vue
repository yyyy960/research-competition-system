<template>
  <div class="competition-detail">
    <el-card shadow="never" v-loading="pageLoading">
      <template #header>
        <div class="detail-header">
          <span>{{ detail.competitionName || '竞赛详情' }}</span>
          <el-tag v-if="detail.status" :type="statusTagType(detail.status)" effect="dark">
            {{ statusLabel(detail.status) }}
          </el-tag>
        </div>
      </template>

      <template v-if="!pageLoading && detail.id">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="竞赛名称" :span="2">
            {{ detail.competitionName }}
          </el-descriptions-item>
          <el-descriptions-item label="获奖作品名称" :span="2">
            {{ detail.workName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="竞赛类别">
            <el-tag :type="categoryTagType(detail.competitionCategory)" size="small" effect="dark">
              {{ detail.competitionCategory }}类
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="statusTagType(detail.status)" size="small" effect="light">
              {{ statusLabel(detail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="主办单位" :span="2">
            {{ detail.hostUnit }}
          </el-descriptions-item>
          <el-descriptions-item label="承办单位" :span="2">
            {{ detail.organizerUnit || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="颁奖单位" :span="2">
            {{ detail.awardUnit || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="获奖等级">
            {{ awardLevelLabel(detail.awardLevel) }}
          </el-descriptions-item>
          <el-descriptions-item label="获奖级别">
            {{ awardGradeLabel(detail.awardGrade) }}
          </el-descriptions-item>
          <el-descriptions-item label="获奖时间">
            {{ detail.awardTime || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="指导教师">
            {{ detail.advisor || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="参与学生" :span="2">
            {{ detail.participants || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="提交人">
            {{ detail.submitter?.realName || detail.submitter?.username || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ detail.createTime || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- Attached Files -->
        <el-divider content-position="left">附件材料</el-divider>
        <div v-if="detail.files && detail.files.length > 0" class="file-list">
          <div v-for="file in detail.files" :key="file.id" class="file-item">
            <el-icon><Document /></el-icon>
            <a :href="getFileUrl(file.id)" target="_blank" class="file-link">
              {{ file.originalName || file.name || '未知文件' }}
            </a>
          </div>
        </div>
        <el-empty v-else description="暂无附件" :image-size="60" />

        <!-- Action Buttons -->
        <div v-if="canEdit || canDelete" class="action-buttons">
          <el-button v-if="canEdit" type="warning" @click="handleEdit">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button v-if="canDelete" type="danger" @click="handleDelete">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>

        <!-- Back Button -->
        <div class="back-button">
          <el-button @click="handleBack">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </template>

      <el-empty v-else-if="!pageLoading" description="未找到竞赛信息" :image-size="80" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Edit, Delete, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCompetitionDetail, deleteCompetition } from '../../api/competition'
import { getFileUrl } from '../../api/file'

const route = useRoute()
const router = useRouter()

const pageLoading = ref(false)
const detail = ref({})

const categoryTagType = (category) => {
  const map = { A: 'danger', B: 'warning', C: 'info' }
  return map[category] || 'info'
}

const statusTagType = (status) => {
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
  return map[status] || status || '-'
}

const awardLevelLabel = (level) => {
  const map = {
    national: '国家级',
    provincial: '省级',
    municipal: '市级',
    school: '校级',
    college: '院级'
  }
  return map[level] || level || '-'
}

const awardGradeLabel = (grade) => {
  const map = {
    first: '一等奖',
    second: '二等奖',
    third: '三等奖'
  }
  return map[grade] || grade || '-'
}

const currentUser = JSON.parse(localStorage.getItem('userInfo') || 'null')

const isSubmitter = computed(() => {
  if (!currentUser || !detail.value.submitter) return false
  return currentUser.username === detail.value.submitter.username
})

const isAdmin = computed(() => currentUser?.role === 'ADMIN')

const canEdit = computed(() => {
  if (!detail.value.id) return false
  const editableStatuses = ['draft', 'returned']
  return (isSubmitter.value || isAdmin.value) && editableStatuses.includes(detail.value.status)
})

const canDelete = computed(() => {
  if (!detail.value.id) return false
  return isSubmitter.value || isAdmin.value
})

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('参数错误')
    router.push('/competition')
    return
  }

  pageLoading.value = true
  try {
    const res = await getCompetitionDetail(id)
    if (res.data) {
      detail.value = res.data
    }
  } catch {
    detail.value = {}
  } finally {
    pageLoading.value = false
  }
}

const handleEdit = () => {
  router.push(`/competition/create?id=${detail.value.id}`)
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除竞赛「${detail.value.competitionName}」吗？此操作不可恢复。`,
      '删除确认',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteCompetition(detail.value.id)
    ElMessage.success('删除成功')
    router.push('/competition')
  } catch {
    // cancelled or error
  }
}

const handleBack = () => {
  router.push('/competition')
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.competition-detail {
  padding: 0;
}
.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 600;
}
.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}
.file-link {
  color: var(--color-primary);
  text-decoration: none;
  font-size: 14px;
}
.file-link:hover {
  text-decoration: underline;
}
.action-buttons {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}
.back-button {
  margin-top: 16px;
}
</style>
