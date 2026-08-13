<template>
  <div class="task-center">
    <div class="page-header">
      <h2>任务中心</h2>
    </div>

    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane v-if="isReviewer" name="pending">
          <template #label>
            待审核 <el-badge :value="pendingTotal" :hidden="pendingTotal === 0" class="tab-badge" />
          </template>
          <div v-loading="pendingLoading">
            <el-empty v-if="!pendingLoading && pendingList.length === 0" description="暂无待审核项目" />
            <el-table v-else :data="pendingList" stripe style="width: 100%" @row-click="handleRowClick">
              <el-table-column prop="type" label="类型" width="100">
                <template #default="{ row }">
                  <el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="title" label="名称" min-width="180" show-overflow-tooltip />
              <el-table-column prop="submitter" label="提交人" width="120" />
              <el-table-column prop="createdAt" label="提交时间" width="170">
                <template #default="{ row }">{{ row.createdAt }}</template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click.stop="handleReview(row)">审核</el-button>
                  <el-button size="small" @click.stop="handleView(row)">查看</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="pendingTotal > 0" class="pagination-wrapper">
              <el-pagination
                v-model:current-page="pendingPage"
                v-model:page-size="pendingSize"
                :total="pendingTotal"
                layout="total, prev, pager, next"
                @current-change="fetchPending"
              />
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane name="returned">
          <template #label>
            待整改 <el-badge :value="returnedTotal" :hidden="returnedTotal === 0" class="tab-badge" />
          </template>
          <div v-loading="returnedLoading">
            <el-empty v-if="!returnedLoading && returnedList.length === 0" description="暂无待整改项目" />
            <el-table v-else :data="returnedList" stripe style="width: 100%" @row-click="handleRowClick">
              <el-table-column prop="type" label="类型" width="100">
                <template #default="{ row }">
                  <el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="title" label="名称" min-width="180" show-overflow-tooltip />
              <el-table-column prop="rejectionReason" label="退回原因" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  <el-tooltip :content="row.rejectionReason || '-'" placement="top">
                    <span class="rejection-text">{{ row.rejectionReason || '-' }}</span>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column prop="deadline" label="整改期限" width="170">
                <template #default="{ row }">
                  <span v-if="row.deadline" :class="{ 'deadline-urgent': isUrgent(row.deadline) }">{{ row.deadline }}</span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click.stop="handleModify(row)">修改</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="returnedTotal > 0" class="pagination-wrapper">
              <el-pagination
                v-model:current-page="returnedPage"
                v-model:page-size="returnedSize"
                :total="returnedTotal"
                layout="total, prev, pager, next"
                @current-change="fetchReturned"
              />
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane name="archiving">
          <template #label>
            待归档 <el-badge :value="archivingTotal" :hidden="archivingTotal === 0" class="tab-badge" />
          </template>
          <div v-loading="archivingLoading">
            <el-empty v-if="!archivingLoading && archivingList.length === 0" description="暂无待归档项目" />
            <el-table v-else :data="archivingList" stripe style="width: 100%" @row-click="handleRowClick">
              <el-table-column prop="type" label="类型" width="100">
                <template #default="{ row }">
                  <el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="title" label="名称" min-width="180" show-overflow-tooltip />
              <el-table-column prop="createdAt" label="提交时间" width="170" />
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click.stop="handleView(row)">查看</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="archivingTotal > 0" class="pagination-wrapper">
              <el-pagination
                v-model:current-page="archivingPage"
                v-model:page-size="archivingSize"
                :total="archivingTotal"
                layout="total, prev, pager, next"
                @current-change="fetchArchiving"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- Review Dialog -->
    <el-dialog v-model="reviewDialogVisible" title="审核" width="600px" destroy-on-close>
      <el-form :model="reviewForm" label-width="100px">
        <el-form-item label="审核意见" prop="comment">
          <el-input
            v-model="reviewForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="reviewSubmitting" @click="handleReject">退回</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="handleApprove">通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewTodo, approveReview, rejectReview } from '../../api/review'

const router = useRouter()

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isReviewer = computed(() =>
  ['SECRETARY', 'LEADER'].includes(userInfo?.role)
)

const activeTab = ref(isReviewer.value ? 'pending' : 'returned')

// Pending review tab
const pendingList = ref([])
const pendingLoading = ref(false)
const pendingPage = ref(1)
const pendingSize = ref(10)
const pendingTotal = ref(0)

// Returned tab
const returnedList = ref([])
const returnedLoading = ref(false)
const returnedPage = ref(1)
const returnedSize = ref(10)
const returnedTotal = ref(0)

// Archiving tab
const archivingList = ref([])
const archivingLoading = ref(false)
const archivingPage = ref(1)
const archivingSize = ref(10)
const archivingTotal = ref(0)

// Review dialog
const reviewDialogVisible = ref(false)
const reviewSubmitting = ref(false)
const reviewForm = ref({ comment: '' })
const currentReviewItem = ref(null)

const typeTag = (type) => {
  const map = { competition: 'primary', innovation: 'success', copyright: 'warning', paper: 'info' }
  return map[type] || ''
}

const typeLabel = (type) => {
  const map = { competition: '竞赛', innovation: '大创', copyright: '软著', paper: '论文' }
  return map[type] || type
}

const statusTag = (status) => {
  const map = { pending_review: 'warning', under_review: 'info', approved: 'success', returned: 'danger', archived: '' }
  return map[status] || 'info'
}

const statusLabel = (status) => {
  const map = { pending_review: '待审核', under_review: '审核中', approved: '已通过', returned: '已退回', archived: '已归档' }
  return map[status] || status
}

const isUrgent = (deadline) => {
  if (!deadline) return false
  const now = new Date()
  const deadlineDate = new Date(deadline)
  const diffDays = Math.ceil((deadlineDate - now) / (1000 * 60 * 60 * 24))
  return diffDays <= 3
}

const fetchPending = async () => {
  pendingLoading.value = true
  try {
    const res = await getReviewTodo({
      page: pendingPage.value,
      size: pendingSize.value
    })
    const data = res.data
    pendingList.value = data?.records || data || []
    pendingTotal.value = data?.total || 0
  } catch {
    ElMessage.error('获取待审核列表失败')
  } finally {
    pendingLoading.value = false
  }
}

const fetchReturned = async () => {
  returnedLoading.value = true
  try {
    const res = await getReviewTodo({
      status: 'returned',
      page: returnedPage.value,
      size: returnedSize.value
    })
    const data = res.data
    returnedList.value = data?.records || data || []
    returnedTotal.value = data?.total || 0
  } catch {
    ElMessage.error('获取待整改列表失败')
  } finally {
    returnedLoading.value = false
  }
}

const fetchArchiving = async () => {
  archivingLoading.value = true
  try {
    const res = await getReviewTodo({
      status: 'under_review',
      page: archivingPage.value,
      size: archivingSize.value
    })
    const data = res.data
    archivingList.value = data?.records || data || []
    archivingTotal.value = data?.total || 0
  } catch {
    ElMessage.error('获取待归档列表失败')
  } finally {
    archivingLoading.value = false
  }
}

const handleTabChange = () => {
  if (activeTab.value === 'pending') fetchPending()
  else if (activeTab.value === 'returned') fetchReturned()
  else if (activeTab.value === 'archiving') fetchArchiving()
}

const handleReview = (row) => {
  currentReviewItem.value = row
  reviewForm.value = { comment: '' }
  reviewDialogVisible.value = true
}

const handleApprove = async () => {
  if (!currentReviewItem.value) return
  reviewSubmitting.value = true
  try {
    await approveReview({
      achievementType: currentReviewItem.value.type,
      achievementId: currentReviewItem.value.id,
      comment: reviewForm.value.comment
    })
    ElMessage.success('审核通过')
    reviewDialogVisible.value = false
    fetchPending()
  } catch {
    // Error handled by interceptor
  } finally {
    reviewSubmitting.value = false
  }
}

const handleReject = async () => {
  if (!currentReviewItem.value) return
  reviewSubmitting.value = true
  try {
    await rejectReview({
      achievementType: currentReviewItem.value.type,
      achievementId: currentReviewItem.value.id,
      comment: reviewForm.value.comment
    })
    ElMessage.success('已退回')
    reviewDialogVisible.value = false
    fetchPending()
  } catch {
    // Error handled by interceptor
  } finally {
    reviewSubmitting.value = false
  }
}

const handleView = (row) => {
  const routes = {
    competition: `/competition/${row.id}`,
    innovation: `/innovation/${row.id}`,
    copyright: `/copyright/${row.id}`,
    paper: `/paper/${row.id}`
  }
  const path = routes[row.type]
  if (path) router.push(path)
}

const handleModify = (row) => {
  const routes = {
    competition: `/competition/create?id=${row.id}`,
    innovation: `/innovation/create?id=${row.id}`,
    copyright: `/copyright/create?id=${row.id}`,
    paper: `/paper/create?id=${row.id}`
  }
  const path = routes[row.type]
  if (path) router.push(path)
}

const handleRowClick = (row) => {
  handleView(row)
}

onMounted(() => {
  if (activeTab.value === 'pending') fetchPending()
  else if (activeTab.value === 'returned') fetchReturned()
})
</script>

<style scoped>
.task-center {
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
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.rejection-text {
  color: #F56C6C;
  font-size: 13px;
}
.deadline-urgent {
  color: #F56C6C;
  font-weight: 600;
}
.tab-badge {
  margin-left: 6px;
}
.tab-badge :deep(.el-badge__content) {
  font-size: 11px;
  height: 18px;
  line-height: 18px;
  padding: 0 5px;
}
</style>
