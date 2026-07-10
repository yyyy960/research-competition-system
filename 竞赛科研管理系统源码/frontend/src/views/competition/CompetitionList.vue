<template>
  <div class="competition-list">
    <!-- Search Bar -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="竞赛类别">
          <el-select v-model="searchForm.category" placeholder="全部" clearable style="width: 120px">
            <el-option label="A类" value="A" />
            <el-option label="B类" value="B" />
            <el-option label="C类" value="C" />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖等级">
          <el-select v-model="searchForm.level" placeholder="全部" clearable style="width: 120px">
            <el-option label="国家级" value="national" />
            <el-option label="省级" value="provincial" />
            <el-option label="市级" value="municipal" />
            <el-option label="校级" value="school" />
            <el-option label="院级" value="college" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 130px">
            <el-option label="待审核" value="pending_review" />
            <el-option label="审核中" value="under_review" />
            <el-option label="已退回" value="returned" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="竞赛名称 / 作品名称 / 主办单位"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Toolbar -->
    <el-card shadow="never" style="margin-top: 16px">
      <div class="toolbar">
        <span class="toolbar-title">竞赛成果列表</span>
        <router-link to="/competition/create">
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            提交竞赛成果
          </el-button>
        </router-link>
      </div>

      <!-- Table -->
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%; margin-top: 16px"
        empty-text="暂无数据"
      >
        <el-table-column prop="competitionName" label="竞赛名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="workName" label="获奖作品名称" min-width="160" show-overflow-tooltip />
        <el-table-column label="竞赛类别" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="categoryTagType(row.competitionCategory)" size="small" effect="dark">
              {{ row.competitionCategory }}类
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="获奖等级" width="140" align="center">
          <template #default="{ row }">
            {{ awardLevelLabel(row.awardLevel) }}{{ awardGradeLabel(row.awardGrade) }}
          </template>
        </el-table-column>
        <el-table-column prop="awardTime" label="获奖时间" width="120" align="center" />
        <el-table-column label="审核状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small" effect="light">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交人" width="120" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.submitter?.realName || row.submitter?.username || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              v-if="canEdit(row)"
              type="warning"
              link
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-popconfirm v-if="canWithdraw(row)" title="确定要撤回该成果吗？撤回后将通知秘书。" @confirm="handleWithdraw(row)">
              <template #reference>
                <el-button type="primary" link size="small">撤回</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm v-if="canDelete(row)" title="确定要删除该成果吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCompetitionPage, deleteCompetition, withdrawCompetition } from '../../api/competition'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  category: '',
  level: '',
  status: '',
  keyword: ''
})

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
  return map[status] || status
}

const awardLevelLabel = (level) => {
  const map = {
    national: '国家级',
    provincial: '省级',
    municipal: '市级',
    school: '校级',
    college: '院级'
  }
  return map[level] || level || ''
}

const awardGradeLabel = (grade) => {
  const map = {
    first: '一等奖',
    second: '二等奖',
    third: '三等奖'
  }
  return map[grade] || grade || ''
}

const currentUser = JSON.parse(localStorage.getItem('userInfo') || 'null')

const canEdit = (row) => {
  const editableStatuses = ['draft', 'returned']
  const isSubmitter = currentUser && (currentUser.username === row.submitter?.username || currentUser.role === 'ADMIN')
  return isSubmitter && editableStatuses.includes(row.status)
}

const canWithdraw = (row) => {
  return row.status === 'pending_review' && row.submitUserId === (currentUser?.id || JSON.parse(localStorage.getItem('userInfo')||'{}').id)
}

const canDelete = (row) => {
  const isOwner = row.submitUserId === (currentUser?.id || JSON.parse(localStorage.getItem('userInfo')||'{}').id)
  return isOwner || currentUser?.role === 'ADMIN'
}

const handleWithdraw = async (row) => {
  try {
    await withdrawCompetition(row.id)
    ElMessage.success('已撤回')
    fetchData()
  } catch { ElMessage.error('撤回失败') }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    // Remove empty params
    Object.keys(params).forEach(k => {
      if (params[k] === '' || params[k] === null || params[k] === undefined) {
        delete params[k]
      }
    })
    const res = await getCompetitionPage(params)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.category = ''
  searchForm.level = ''
  searchForm.status = ''
  searchForm.keyword = ''
  currentPage.value = 1
  fetchData()
}

const handleView = (row) => {
  router.push(`/competition/${row.id}`)
}

const handleEdit = (row) => {
  router.push(`/competition/create?id=${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除竞赛「${row.competitionName}」吗？此操作不可恢复。`,
      '删除确认',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteCompetition(row.id)
    ElMessage.success('删除成功')
    await fetchData()
  } catch {
    // cancelled or error
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.competition-list {
  padding: 0;
}
.search-card {
  margin-bottom: 0;
}
.search-card .el-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.toolbar-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
