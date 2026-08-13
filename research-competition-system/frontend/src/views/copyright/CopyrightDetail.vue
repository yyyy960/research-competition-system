<template>
  <div class="copyright-detail">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div class="detail-header">
          <span class="card-title">软著详情</span>
          <div class="header-actions">
            <el-button
              v-if="canEdit"
              type="warning"
              :icon="Edit"
              @click="handleEdit"
            >
              编辑
            </el-button>
            <el-popconfirm
              v-if="canDelete"
              title="确定要删除该软著记录吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete"
            >
              <template #reference>
                <el-button type="danger" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
            <el-button :icon="Back" @click="handleBack">返回</el-button>
          </div>
        </div>
      </template>

      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="软件名称" :span="2">
            {{ detail.softwareName }}
          </el-descriptions-item>
          <el-descriptions-item label="所属单位">
            {{ detail.organization || '未填写' }}
          </el-descriptions-item>
          <el-descriptions-item label="著作权人">
            {{ detail.copyrightOwner || '未填写' }}
          </el-descriptions-item>
          <el-descriptions-item label="登记号">
            {{ detail.registrationNumber || '未填写' }}
          </el-descriptions-item>
          <el-descriptions-item label="登记日期">
            {{ detail.registrationDate || '未填写' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态" :span="2">
            <el-tag :type="statusMap[detail.status]?.type" effect="dark" size="small">
              {{ statusMap[detail.status]?.label || detail.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交人">
            {{ detail.submitter || '未填写' }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">证书文件</el-divider>

        <div class="files-section">
          <div class="file-group">
            <span class="file-group-label">证书扫描件：</span>
            <template v-if="detail.certificateFiles && detail.certificateFiles.length > 0">
              <div
                v-for="file in detail.certificateFiles"
                :key="file.id"
                class="file-item"
              >
                <el-icon><Picture /></el-icon>
                <a
                  :href="getFileUrl(file.id)"
                  target="_blank"
                  class="file-link"
                >
                  {{ file.originalName || file.fileName || file.name || '证书扫描件' }}
                </a>
                <el-button
                  text
                  type="primary"
                  size="small"
                  @click="downloadFile(file.id)"
                >
                  下载
                </el-button>
              </div>
            </template>
            <span v-else class="no-file">暂无文件</span>
          </div>
        </div>
      </template>

      <el-empty v-else-if="!loading" description="未找到软著信息" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCopyrightDetail, deleteCopyright } from '../../api/copyright'
import { getFileUrl } from '../../api/file'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const detail = ref(null)

const statusMap = {
  pending_review: { label: '待审核', type: 'warning' },
  under_review: { label: '审核中', type: 'primary' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已退回', type: 'danger' }
}

const canEdit = computed(() => {
  if (!detail.value) return false
  const isOwner = detail.value.userId === userStore.userInfo?.id
  const isAdmin = userStore.userInfo?.role === 'ADMIN'
  const canModifyStatus = ['pending_review', 'rejected'].includes(detail.value.status)
  return canModifyStatus && (isOwner || isAdmin)
})

const canDelete = computed(() => {
  if (!detail.value) return false
  const isOwner = detail.value.userId === userStore.userInfo?.id
  const isAdmin = userStore.userInfo?.role === 'ADMIN'
  return isOwner || isAdmin
})

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('缺少软著ID')
    return
  }
  loading.value = true
  try {
    const res = await getCopyrightDetail(id)
    detail.value = res.data
  } catch {
    ElMessage.error('获取软著详情失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  router.push({ path: '/copyright/create', query: { id: detail.value.id } })
}

const handleDelete = async () => {
  try {
    await deleteCopyright(detail.value.id)
    ElMessage.success('删除成功')
    router.push('/copyright')
  } catch {
    ElMessage.error('删除失败')
  }
}

const handleBack = () => {
  router.push('/copyright')
}

const downloadFile = (fileId) => {
  window.open(getFileUrl(fileId), '_blank')
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.copyright-detail {
  padding: 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.files-section {
  padding: 0 20px;
}

.file-group {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  gap: 8px;
}

.file-group-label {
  font-weight: 500;
  color: #606266;
  white-space: nowrap;
  min-width: 80px;
  line-height: 32px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.file-link {
  color: #409eff;
  text-decoration: none;
  font-size: 13px;
}

.file-link:hover {
  text-decoration: underline;
}

.no-file {
  color: #909399;
  font-size: 13px;
  line-height: 32px;
}
</style>
