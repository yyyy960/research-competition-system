<template>
  <div class="copyright-list">
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" :inline="true" size="default">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable placeholder="全部状态" style="width: 130px">
            <el-option label="待审核" value="pending_review" />
            <el-option label="审核中" value="under_review" />
            <el-option label="已通过" value="approved" />
            <el-option label="已退回" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="软件名称 / 著作权人"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="$router.push('/copyright/create')">
          <el-icon><Plus /></el-icon>
          提交软著
        </el-button>
      </div>

      <el-table
        :data="list"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
        empty-text="暂无数据"
      >
        <el-table-column prop="softwareName" label="软件名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="organization" label="所属单位" width="160" show-overflow-tooltip />
        <el-table-column prop="copyrightOwner" label="著作权人" width="150" show-overflow-tooltip />
        <el-table-column prop="registrationNumber" label="登记号" width="160" />
        <el-table-column prop="registrationDate" label="登记日期" width="110" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small" effect="dark">
              {{ statusMap[row.status]?.label || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">
              <el-icon><View /></el-icon> 查看
            </el-button>
            <el-button
              v-if="canEdit(row)"
              type="warning"
              link
              size="small"
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-popconfirm
              v-if="canDelete(row)"
              title="确定要删除该软著记录吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link size="small">
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCopyrightPage, deleteCopyright } from '../../api/copyright'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const list = ref([])
const total = ref(0)

const queryParams = reactive({
  keyword: '',
  status: '',
  page: 1,
  pageSize: 10
})

const statusMap = {
  pending_review: { label: '待审核', type: 'warning' },
  under_review: { label: '审核中', type: 'primary' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已退回', type: 'danger' }
}

const currentUser = computed(() => userStore.userInfo)

const canEdit = (row) => {
  const isOwner = row.userId === currentUser.value?.id
  const isAdmin = currentUser.value?.role === 'ADMIN'
  const canModifyStatus = ['pending_review', 'rejected'].includes(row.status)
  return canModifyStatus && (isOwner || isAdmin)
}

const canDelete = (row) => {
  const isOwner = row.userId === currentUser.value?.id
  const isAdmin = currentUser.value?.role === 'ADMIN'
  return isOwner || isAdmin
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    const res = await getCopyrightPage(params)
    list.value = res.data?.records || res.data?.list || []
    total.value = res.data?.total || 0
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  queryParams.keyword = ''
  queryParams.status = ''
  queryParams.page = 1
  fetchData()
}

const handleSizeChange = (size) => {
  queryParams.pageSize = size
  queryParams.page = 1
  fetchData()
}

const handleCurrentChange = (page) => {
  queryParams.page = page
  fetchData()
}

const handleView = (row) => {
  router.push(`/copyright/${row.id}`)
}

const handleEdit = (row) => {
  router.push({ path: '/copyright/create', query: { id: row.id } })
}

const handleDelete = async (row) => {
  try {
    await deleteCopyright(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.copyright-list {
  padding: 0;
}

.search-card {
  margin-bottom: 16px;
}

.table-card {
  min-height: 400px;
}

.table-header {
  margin-bottom: 16px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
