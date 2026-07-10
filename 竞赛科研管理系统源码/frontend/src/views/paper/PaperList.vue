<template>
  <div class="paper-list">
    <div class="search-bar">
      <el-form :model="queryParams" inline>
        <el-form-item label="期刊级别">
          <el-select v-model="queryParams.journalLevel" placeholder="请选择" clearable style="width: 180px">
            <el-option label="CCF A类会议" value="CCF A类会议" />
            <el-option label="CCF B类会议" value="CCF B类会议" />
            <el-option label="CCF C类会议" value="CCF C类会议" />
            <el-option label="EI会议" value="EI会议" />
            <el-option label="SCI一区" value="SCI一区" />
            <el-option label="SCI二区" value="SCI二区" />
            <el-option label="SCI三区" value="SCI三区" />
            <el-option label="SCI四区" value="SCI四区" />
            <el-option label="EI期刊" value="EI期刊" />
            <el-option label="北大核心期刊" value="北大核心期刊" />
            <el-option label="省级期刊" value="省级期刊" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 140px">
            <el-option label="待审核" value="pending_review" />
            <el-option label="审核中" value="under_review" />
            <el-option label="已退回" value="returned" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="标题/期刊名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="action-bar">
      <router-link to="/paper/create">
        <el-button type="primary" :icon="Plus">提交论文</el-button>
      </router-link>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
      <el-table-column prop="title" label="论文标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="journalName" label="期刊/会议名称" width="160" show-overflow-tooltip />
      <el-table-column prop="journalLevel" label="期刊级别" width="130">
        <template #default="{ row }">
          <el-tag v-if="row.journalLevel" :type="journalLevelType(row.journalLevel)" size="small">
            {{ row.journalLevel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submissionDate" label="提交日期" width="110" />
      <el-table-column prop="acceptanceDate" label="录用日期" width="110" />
      <el-table-column prop="status" label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitterName" label="提交人" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" :icon="View" @click="handleView(row)">
            查看
          </el-button>
          <el-button
            v-if="canEdit(row)"
            type="warning"
            link
            size="small"
            :icon="Edit"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            v-if="canDelete(row)"
            type="danger"
            link
            size="small"
            :icon="Delete"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next, sizes"
        :page-sizes="[10, 20, 50]"
        @current-change="fetchData"
        @size-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, View, Edit, Delete } from '@element-plus/icons-vue'
import { getPaperPage, deletePaper } from '../../api/paper'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  journalLevel: '',
  status: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const journalLevelType = (level) => {
  if (!level) return 'info'
  if (level.includes('SCI') || level.includes('CCF A')) return 'danger'
  if (level.includes('CCF B') || level.includes('EI期刊')) return 'warning'
  return 'primary'
}

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

const canEdit = (row) => {
  const isOwner = row.submitterId === userInfo.id || userInfo.role === 'ADMIN'
  return isOwner && (row.status === 'pending_review' || row.status === 'returned')
}

const canDelete = (row) => {
  const isOwner = row.submitterId === userInfo.id || userInfo.role === 'ADMIN'
  return isOwner
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    Object.keys(params).forEach((k) => {
      if (params[k] === '' || params[k] === null || params[k] === undefined) {
        delete params[k]
      }
    })
    const res = await getPaperPage(params)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch {
    ElMessage.error('获取论文列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  queryParams.journalLevel = ''
  queryParams.status = ''
  queryParams.keyword = ''
  queryParams.page = 1
  fetchData()
}

const handleView = (row) => {
  router.push(`/paper/${row.id}`)
}

const handleEdit = (row) => {
  router.push(`/paper/create?id=${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该论文记录吗？', '确认删除', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deletePaper(row.id)
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
.paper-list {
  padding: 0;
}
.search-bar {
  background: #fff;
  padding: 20px 20px 0;
  border-radius: 4px;
  margin-bottom: 16px;
}
.action-bar {
  margin-bottom: 16px;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
