<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :model="query" :inline="true">
        <el-form-item label="级别">
          <el-select v-model="query.projectLevel" clearable placeholder="全部" style="width:120px" @change="search">
            <el-option label="国家级" value="national" />
            <el-option label="省级" value="provincial" />
            <el-option label="校级" value="school" />
            <el-option label="院级" value="college" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.projectType" clearable placeholder="全部" style="width:150px" @change="search">
            <el-option label="创新训练项目" value="innovation" />
            <el-option label="创业训练项目" value="entrepreneurship" />
            <el-option label="创业实践项目" value="practice" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width:120px" @change="search">
            <el-option label="待审核" value="pending_review" />
            <el-option label="审核中" value="under_review" />
            <el-option label="已退回" value="returned" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="query.keyword" placeholder="搜索项目名称" clearable style="width:200px" @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div style="margin-bottom:16px">
        <el-button type="primary" @click="$router.push('/innovation/create')">提交大创项目</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe border empty-text="暂无数据">
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="级别" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="levelColor(row.projectLevel)" size="small">{{ levelText(row.projectLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="140" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ typeText(row.projectType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="advisor" label="指导教师" width="100" />
        <el-table-column prop="members" label="成员" width="150" show-overflow-tooltip />
        <el-table-column prop="startTime" label="立项时间" width="110" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/innovation/${row.id}`)">查看</el-button>
            <el-button v-if="canEdit(row)" link type="warning" size="small" @click="$router.push({path:'/innovation/create',query:{id:row.id}})">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button v-if="canDelete(row)" link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
          :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
          @size-change="search" @current-change="search" background />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getInnovationPage, deleteInnovation } from '../../api/innovation'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({ page: 1, size: 10, projectLevel: '', projectType: '', status: '', keyword: '' })

const userInfo = computed(() => {
  try { return JSON.parse(localStorage.getItem('userInfo') || '{}') } catch { return {} }
})

const levelMap = { national: '国家级', provincial: '省级', school: '校级', college: '院级' }
const typeMap = { innovation: '创新训练项目', entrepreneurship: '创业训练项目', practice: '创业实践项目' }
const statusMap = { pending_review: '待审核', under_review: '审核中', returned: '已退回', archived: '已归档' }
const statusColors = { pending_review: 'warning', under_review: 'primary', returned: 'danger', archived: 'success' }

const levelText = (v) => levelMap[v] || v
const typeText = (v) => typeMap[v] || v
const statusText = (v) => statusMap[v] || v
const statusTag = (v) => statusColors[v] || 'info'
const levelColor = (l) => ({ national: 'danger', provincial: 'primary', school: 'warning', college: 'info' })[l] || 'info'

const canEdit = (row) => {
  const allow = ['pending_review', 'returned'].includes(row.status)
  const owner = row.submitUserId === userInfo.value.id
  const admin = userInfo.value.role === 'ADMIN'
  return allow && (owner || admin)
}
const canDelete = (row) => {
  const owner = row.submitUserId === userInfo.value.id
  const admin = userInfo.value.role === 'ADMIN'
  return owner || admin
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {}
    if (query.projectLevel) params.projectLevel = query.projectLevel
    if (query.projectType) params.projectType = query.projectType
    if (query.status) params.status = query.status
    if (query.keyword) params.keyword = query.keyword
    params.page = query.page
    params.size = query.size
    const res = await getInnovationPage(params)
    if (res.data) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('Fetch innovation error:', e)
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const search = () => { query.page = 1; fetchData() }
const reset = () => { query.projectLevel = ''; query.projectType = ''; query.status = ''; query.keyword = ''; search() }
const handleDelete = async (row) => {
  try {
    await deleteInnovation(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch { ElMessage.error('删除失败') }
}

onMounted(() => fetchData())
</script>
