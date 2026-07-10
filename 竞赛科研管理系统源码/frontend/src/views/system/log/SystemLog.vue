<template>
  <div>
    <div class="page-header">
      <h2>系统操作日志</h2>
    </div>
    <el-card>
      <el-form :inline="true" @submit.prevent="search">
        <el-form-item label="操作类型">
          <el-select v-model="query.action" clearable placeholder="全部" style="width:130px" @change="search">
            <el-option label="登录" value="LOGIN" />
            <el-option label="提交" value="SUBMIT" />
            <el-option label="修改" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="审核" value="REVIEW" />
            <el-option label="撤回" value="WITHDRAW" />
            <el-option label="上传" value="UPLOAD" />
            <el-option label="导出" value="EXPORT" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="用户名/操作" clearable style="width:200px" @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" stripe border empty-text="暂无日志" max-height="600">
        <el-table-column label="操作类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="actionTag(row.action)" size="small">{{ actionLabel(row.action) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户" width="100" />
        <el-table-column prop="operation" label="操作描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column label="状态" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status==='OK'?'success':'danger'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
          :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
          @size-change="fetchData" @current-change="fetchData" background />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../../utils/request'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 20, action: '', keyword: '' })

const actionMap = {
  LOGIN: { label: '登录', tag: 'info' },
  SUBMIT: { label: '提交', tag: 'success' },
  UPDATE: { label: '修改', tag: 'warning' },
  DELETE: { label: '删除', tag: 'danger' },
  REVIEW: { label: '审核', tag: 'primary' },
  WITHDRAW: { label: '撤回', tag: 'info' },
  UPLOAD: { label: '上传', tag: 'success' },
  EXPORT: { label: '导出', tag: 'warning' }
}
const actionLabel = (a) => actionMap[a]?.label || a
const actionTag = (a) => actionMap[a]?.tag || 'info'

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (query.action) params.action = query.action
    if (query.keyword) params.keyword = query.keyword
    const res = await request.get('/log/page', { params })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { list.value = []; total.value = 0 }
  finally { loading.value = false }
}

const search = () => { query.page = 1; fetchData() }
const reset = () => { query.action = ''; query.keyword = ''; search() }
const formatTime = (t) => t ? new Date(t).toLocaleString('zh-CN') : ''

onMounted(() => fetchData())
</script>

<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { font-size: 20px; color: #303133; margin: 0; }
</style>
