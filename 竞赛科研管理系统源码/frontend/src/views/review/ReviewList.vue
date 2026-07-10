<template>
  <div class="review-list">
    <h2 style="margin-bottom:16px">审核管理</h2>

    <el-card style="margin-bottom:16px">
      <el-form :inline="true">
        <el-form-item label="成果类型">
          <el-select v-model="filterType" clearable placeholder="全部" style="width:140px" @change="fetchData">
            <el-option label="学科竞赛" value="competition" />
            <el-option label="大创项目" value="innovation" />
            <el-option label="软件著作权" value="copyright" />
            <el-option label="学术论文" value="paper" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">刷新</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="list" v-loading="loading" stripe border empty-text="暂无待审核成果">
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">查看</el-button>
            <el-button link type="success" size="small" @click="openDialog(row, 'approve')">通过</el-button>
            <el-button link type="danger" size="small" @click="openDialog(row, 'reject')">退回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total"
          :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
          @size-change="fetchData" @current-change="fetchData" background />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogAction === 'approve' ? '审核通过' : '审核退回'" width="500px">
      <el-form>
        <el-form-item label="审核意见" :required="dialogAction === 'reject'">
          <el-input v-model="comment" type="textarea" :rows="3" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button :type="dialogAction === 'approve' ? 'success' : 'danger'" :loading="submitting" @click="submitReview">
          {{ dialogAction === 'approve' ? '确认通过' : '确认退回' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getReviewTodo, approveReview, rejectReview } from '../../api/review'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const filterType = ref('')
const dialogVisible = ref(false)
const dialogAction = ref('approve')
const comment = ref('')
const currentRow = ref(null)

const typeMap = {
  competition: { label: '学科竞赛', tag: 'primary' },
  innovation: { label: '大创项目', tag: 'success' },
  copyright: { label: '软件著作权', tag: 'warning' },
  paper: { label: '学术论文', tag: 'info' }
}
const statusMap = {
  pending_review: { label: '待审核', tag: 'warning' },
  under_review: { label: '审核中', tag: 'primary' },
  returned: { label: '已退回', tag: 'danger' },
  archived: { label: '已归档', tag: 'success' }
}
const typeLabel = (t) => typeMap[t]?.label || t
const typeTag = (t) => typeMap[t]?.tag || 'info'
const statusLabel = (s) => statusMap[s]?.label || s
const statusTag = (s) => statusMap[s]?.tag || 'info'
const formatTime = (t) => t ? new Date(t).toLocaleString('zh-CN') : ''

const detailRoute = {
  competition: '/competition',
  innovation: '/innovation',
  copyright: '/copyright',
  paper: '/paper'
}

const viewDetail = (row) => {
  const path = detailRoute[row.type]
  if (path) router.push(`${path}/${row.id}`)
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (filterType.value) params.achievementType = filterType.value
    const res = await getReviewTodo(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    list.value = []; total.value = 0
  } finally {
    loading.value = false
  }
}

const openDialog = (row, action) => {
  currentRow.value = row
  dialogAction.value = action
  comment.value = ''
  dialogVisible.value = true
}

const submitReview = async () => {
  if (dialogAction.value === 'reject' && !comment.value.trim()) {
    ElMessage.warning('退回时请填写审核意见')
    return
  }
  submitting.value = true
  try {
    const data = {
      achievementType: currentRow.value.type,
      achievementId: currentRow.value.id,
      comment: comment.value
    }
    if (dialogAction.value === 'approve') {
      await approveReview(data)
      ElMessage.success('审核通过')
    } else {
      await rejectReview(data)
      ElMessage.success('已退回')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => fetchData())
</script>

<style scoped>
.review-list { padding: 0; }
</style>
