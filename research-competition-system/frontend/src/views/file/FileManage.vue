<template>
  <div class="file-manage">
    <div class="page-header">
      <h2>文件管理</h2>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="search-bar">
          <el-select v-model="fileType" placeholder="文件类型" clearable style="width: 140px" @change="fetchData">
            <el-option label="全部" value="" />
            <el-option label="图片" value="IMAGE" />
            <el-option label="文档" value="DOCUMENT" />
            <el-option label="PDF" value="PDF" />
            <el-option label="其他" value="OTHER" />
          </el-select>
          <el-input
            v-model="keyword"
            placeholder="搜索文件名..."
            clearable
            style="width: 240px"
            @keyup.enter="fetchData"
            @clear="fetchData"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </div>
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :show-file-list="false"
          :before-upload="beforeUpload"
        >
          <el-button type="primary">
            <el-icon><Upload /></el-icon>
            上传文件
          </el-button>
        </el-upload>
      </div>

      <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="originalName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="fileType" label="文件类型" width="120">
          <template #default="{ row }">
            <el-tag :type="fileTypeTag(row.fileType)" effect="plain">
              {{ fileTypeLabel(row.fileType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="文件大小" width="120">
          <template #default="{ row }">
            {{ formatSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="downloadFile(row)">
              下载
            </el-button>
            <el-button
              v-if="canPreview(row)"
              type="success"
              link
              size="small"
              @click="previewFile(row)"
            >
              预览
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>

      <el-empty v-if="!loading && list.length === 0" description="暂无文件" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Upload } from '@element-plus/icons-vue'
import request from '../../utils/request'
import { deleteFile, getFileUrl, getPreviewUrl } from '../../api/file'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const fileType = ref('')
const keyword = ref('')

const uploadUrl = '/api/file/upload'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
}))

const fileTypeMap = {
  IMAGE: { label: '图片', tag: 'success' },
  DOCUMENT: { label: '文档', tag: 'primary' },
  PDF: { label: 'PDF', tag: 'danger' },
  OTHER: { label: '其他', tag: 'info' }
}

const previewTypes = ['IMAGE', 'PDF']

function fileTypeLabel(type) {
  return fileTypeMap[type]?.label || type || '未知'
}

function fileTypeTag(type) {
  return fileTypeMap[type]?.tag || ''
}

function formatSize(bytes) {
  if (!bytes && bytes !== 0) return '-'
  bytes = Number(bytes)
  if (bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  let size = bytes
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024
    i++
  }
  return size.toFixed(i > 0 ? 2 : 0) + ' ' + units[i]
}

function canPreview(row) {
  return previewTypes.includes(row.fileType)
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      page: pageNum.value,
      pageSize: pageSize.value
    }
    if (fileType.value) params.fileType = fileType.value
    if (keyword.value.trim()) params.keyword = keyword.value.trim()

    const res = await request.get('/file/page', { params })
    list.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function downloadFile(row) {
  window.open(getFileUrl(row.id), '_blank')
}

function previewFile(row) {
  window.open(getPreviewUrl(row.id), '_blank')
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除文件"${row.originalName}"吗？此操作不可恢复。`, '确认删除', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteFile(row.id)
    ElMessage.success('删除成功')
    await fetchData()
  } catch {
    // cancelled or error
  }
}

function beforeUpload(file) {
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }
  return true
}

function handleUploadSuccess(response) {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    fetchData()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

function handleUploadError() {
  ElMessage.error('上传失败')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.file-manage {
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
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}
.search-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
