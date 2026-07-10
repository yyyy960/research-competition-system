<template>
  <div class="paper-form">
    <h2 class="form-title">{{ isEdit ? '编辑论文' : '提交论文' }}</h2>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="form-body"
      v-loading="loadingDetail"
    >
      <el-card class="form-card">
        <template #header>基本信息</template>

        <el-form-item label="论文标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入论文标题" maxlength="200" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="投稿日期" prop="submissionDate">
              <el-date-picker
                v-model="form.submissionDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="录用日期" prop="acceptanceDate">
              <el-date-picker
                v-model="form.acceptanceDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="期刊/会议名称" prop="journalName">
          <el-input v-model="form.journalName" placeholder="请输入期刊或会议名称" maxlength="200">
            <template #append>
              <el-button @click="openCcfDialog">CCF查询</el-button>
              <el-button @click="matchCcf">自动匹配</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="论文摘要" prop="abstract">
          <el-input
            v-model="form.abstract"
            type="textarea"
            :rows="4"
            placeholder="请输入论文摘要"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="关键词" prop="keywords">
          <el-input v-model="form.keywords" placeholder="多个关键词用逗号分隔" />
        </el-form-item>

        <el-form-item label="DOI / URL" prop="doi">
          <el-input v-model="form.doi" placeholder="论文DOI号或访问链接" />
        </el-form-item>

        <el-form-item label="期刊级别" prop="journalLevel">
          <el-select v-model="form.journalLevel" placeholder="请选择期刊级别" style="width: 100%">
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

        <el-form-item label="论文类型" prop="paperType">
          <el-select v-model="form.paperType" placeholder="请选择论文类型" style="width: 240px">
            <el-option label="研究论文" value="research" />
            <el-option label="综述论文" value="review" />
            <el-option label="短文/快报" value="short" />
            <el-option label="海报论文" value="poster" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="第一作者" prop="firstAuthor">
          <el-input v-model="form.firstAuthor" placeholder="第一作者姓名" style="width: 240px" />
        </el-form-item>

        <el-form-item label="作者" prop="authors">
          <el-input
            v-model="form.authors"
            type="textarea"
            :rows="3"
            placeholder="请输入全部作者信息，多个作者用逗号分隔"
          />
        </el-form-item>
      </el-card>

      <el-card class="form-card">
        <template #header>附件上传</template>

        <el-form-item label="投稿初稿">
          <el-upload
            ref="draftUploadRef"
            :accept="'.pdf'"
            :file-list="draftFileList"
            :http-request="(opts) => handleUpload(opts, 'draft')"
            :on-remove="() => handleRemove('draft')"
            :limit="1"
            :on-exceed="() => ElMessage.warning('仅支持上传一个文件')"
          >
            <el-button type="primary" :icon="Upload">选择文件</el-button>
            <template #tip>
              <span style="font-size: 12px; color: #909399; margin-left: 10px">仅支持PDF格式</span>
            </template>
          </el-upload>
          <div v-if="draftFileInfo" class="file-info">
            <el-link type="primary" :underline="false" @click="previewFile(draftFileInfo.id)">
              {{ draftFileInfo.name }}
            </el-link>
          </div>
        </el-form-item>

        <el-form-item label="录用终稿">
          <el-upload
            ref="finalUploadRef"
            :accept="'.pdf'"
            :file-list="finalFileList"
            :http-request="(opts) => handleUpload(opts, 'final')"
            :on-remove="() => handleRemove('final')"
            :limit="1"
            :on-exceed="() => ElMessage.warning('仅支持上传一个文件')"
          >
            <el-button type="primary" :icon="Upload">选择文件</el-button>
            <template #tip>
              <span style="font-size: 12px; color: #909399; margin-left: 10px">仅支持PDF格式</span>
            </template>
          </el-upload>
          <div v-if="finalFileInfo" class="file-info">
            <el-link type="primary" :underline="false" @click="previewFile(finalFileInfo.id)">
              {{ finalFileInfo.name }}
            </el-link>
          </div>
        </el-form-item>

        <el-form-item label="审稿意见">
          <el-upload
            ref="reviewUploadRef"
            :accept="'.pdf'"
            :file-list="reviewFileList"
            :http-request="(opts) => handleUpload(opts, 'review')"
            :on-remove="() => handleRemove('review')"
            :limit="1"
            :on-exceed="() => ElMessage.warning('仅支持上传一个文件')"
          >
            <el-button type="primary" :icon="Upload">选择文件</el-button>
            <template #tip>
              <span style="font-size: 12px; color: #909399; margin-left: 10px">仅支持PDF格式</span>
            </template>
          </el-upload>
          <div v-if="reviewFileInfo" class="file-info">
            <el-link type="primary" :underline="false" @click="previewFile(reviewFileInfo.id)">
              {{ reviewFileInfo.name }}
            </el-link>
          </div>
        </el-form-item>
      </el-card>

      <div class="form-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </div>
    </el-form>

    <!-- CCF查询对话框 -->
    <el-dialog v-model="showCcfDialog" title="CCF推荐期刊/会议查询" width="900px" destroy-on-close>
      <el-form :model="ccfQuery" :inline="true">
        <el-form-item label="类型">
          <el-select v-model="ccfQuery.venueType" placeholder="全部" clearable style="width:100px" @change="searchCcf">
            <el-option label="期刊" value="journal" />
            <el-option label="会议" value="conference" />
          </el-select>
        </el-form-item>
        <el-form-item label="方向">
          <el-select v-model="ccfQuery.area" placeholder="全部" clearable style="width:180px" @change="searchCcf">
            <el-option v-for="a in ccfAreas" :key="a" :label="a" :value="a" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="ccfQuery.level" placeholder="全部" clearable style="width:80px" @change="searchCcf">
            <el-option label="A" value="A" />
            <el-option label="B" value="B" />
            <el-option label="C" value="C" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="ccfQuery.keyword" placeholder="搜索名称" clearable style="width:180px"
            @keyup.enter="searchCcf" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchCcf">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="ccfTableData" v-loading="ccfLoading" height="400" highlight-current-row
        @row-click="selectCcfVenue">
        <el-table-column prop="venueType" label="类型" width="60">
          <template #default="{ row }">
            <el-tag :type="row.venueType==='journal'?'success':'warning'" size="small">
              {{ row.venueType==='journal'?'期刊':'会议' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="60">
          <template #default="{ row }">
            <el-tag :type="{A:'danger',B:'warning',C:'info'}[row.level]" size="small">CCF-{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="abbreviation" label="简称" width="130" />
        <el-table-column prop="fullName" label="全称" min-width="280" show-overflow-tooltip />
        <el-table-column prop="publisher" label="出版社" width="100" />
      </el-table>
      <div style="margin-top:12px; text-align:right">
        <el-pagination v-model:current-page="ccfQuery.page" v-model:page-size="ccfQuery.size"
          :total="ccfTotal" :page-sizes="[20,50]" layout="total, prev, pager, next"
          @current-change="searchCcf" @size-change="searchCcf" small />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { getPaperDetail, createPaper, updatePaper } from '../../api/paper'
import { getCcfPage, getCcfAreas } from '../../api/ccf'
import { uploadFile, deleteFile, getFileUrl } from '../../api/file'
import { checkDuplicate, validateFields } from '../../api/check'
import request from '../../utils/request'

const router = useRouter()
const route = useRoute()

const formRef = ref(null)
const submitting = ref(false)
const loadingDetail = ref(false)

const draftUploadRef = ref(null)
const finalUploadRef = ref(null)
const reviewUploadRef = ref(null)

const isEdit = computed(() => !!route.query.id)

const draftFileInfo = ref(null)
const finalFileInfo = ref(null)
const reviewFileInfo = ref(null)

const draftFileList = computed(() => {
  return draftFileInfo.value ? [{ name: draftFileInfo.value.name, url: '' }] : []
})
const finalFileList = computed(() => {
  return finalFileInfo.value ? [{ name: finalFileInfo.value.name, url: '' }] : []
})
const reviewFileList = computed(() => {
  return reviewFileInfo.value ? [{ name: reviewFileInfo.value.name, url: '' }] : []
})

const form = reactive({
  title: '',
  submissionDate: '',
  acceptanceDate: '',
  journalName: '',
  abstract: '',
  keywords: '',
  doi: '',
  journalLevel: '',
  paperType: '',
  firstAuthor: '',
  authors: '',
  draftFileId: null,
  finalFileId: null,
  reviewFileId: null
})

const rules = {
  title: [{ required: true, message: '请输入论文标题', trigger: 'blur' }],
  journalName: [{ required: true, message: '请输入期刊/会议名称', trigger: 'blur' }],
  authors: [{ required: true, message: '请输入作者信息', trigger: 'blur' }]
}

const handleUpload = async (options, field) => {
  const file = options.file
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await uploadFile(formData)
    const fileId = res.data.id || res.data.fileId
    const fileName = res.data.name || file.name

    // Store file info
    const info = { id: fileId, name: fileName }
    if (field === 'draft') {
      draftFileInfo.value = info
      form.draftFileId = fileId
    } else if (field === 'final') {
      finalFileInfo.value = info
      form.finalFileId = fileId
    } else if (field === 'review') {
      reviewFileInfo.value = info
      form.reviewFileId = fileId
    }
    ElMessage.success(`${options.filename || '文件'}上传成功`)
  } catch {
    ElMessage.error('文件上传失败')
  }
}

const handleRemove = async (field) => {
  const fileInfo = field === 'draft' ? draftFileInfo.value
    : field === 'final' ? finalFileInfo.value
    : reviewFileInfo.value

  if (fileInfo && fileInfo.id) {
    try {
      await deleteFile(fileInfo.id)
    } catch {
      // ignore delete failure
    }
  }

  if (field === 'draft') {
    draftFileInfo.value = null
    form.draftFileId = null
  } else if (field === 'final') {
    finalFileInfo.value = null
    form.finalFileId = null
  } else if (field === 'review') {
    reviewFileInfo.value = null
    form.reviewFileId = null
  }
}

const previewFile = (id) => {
  if (id) {
    window.open(getFileUrl(id), '_blank')
  }
}

const loadPaperDetail = async () => {
  const id = route.query.id
  if (!id) return

  loadingDetail.value = true
  try {
    const res = await getPaperDetail(id)
    const data = res.data || res
    form.title = data.title || ''
    form.submissionDate = data.submissionDate || ''
    form.acceptanceDate = data.acceptanceDate || ''
    form.journalName = data.journalName || ''
    form.abstract = data.abstract || ''
    form.keywords = data.keywords || ''
    form.doi = data.doi || ''
    form.journalLevel = data.journalLevel || ''
    form.paperType = data.paperType || ''
    form.firstAuthor = data.firstAuthor || ''
    form.authors = data.authors || ''
    form.draftFileId = data.draftFileId || null
    form.finalFileId = data.finalFileId || null
    form.reviewFileId = data.reviewFileId || null

    if (data.draftFileId && data.draftFileName) {
      draftFileInfo.value = { id: data.draftFileId, name: data.draftFileName }
    }
    if (data.finalFileId && data.finalFileName) {
      finalFileInfo.value = { id: data.finalFileId, name: data.finalFileName }
    }
    if (data.reviewFileId && data.reviewFileName) {
      reviewFileInfo.value = { id: data.reviewFileId, name: data.reviewFileName }
    }
  } catch {
    ElMessage.error('获取论文信息失败')
    router.push('/paper')
  } finally {
    loadingDetail.value = false
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // Pre-submit duplicate check (non-blocking)
  try {
    const dupRes = await checkDuplicate({ type: 'paper', data: { ...form } })
    if (dupRes.data?.hasDuplicate) {
      await ElMessageBox.confirm('发现可能重复的成果，是否继续提交？', '重复警告', {
        type: 'warning', confirmButtonText: '继续提交', cancelButtonText: '取消'
      })
    }
  } catch { /* ignore */ }

  submitting.value = true
  try {
    const payload = {
      title: form.title,
      submissionDate: form.submissionDate || null,
      acceptanceDate: form.acceptanceDate || null,
      journalName: form.journalName,
      abstract: form.abstract,
      keywords: form.keywords,
      doi: form.doi,
      journalLevel: form.journalLevel,
      paperType: form.paperType,
      firstAuthor: form.firstAuthor,
      authors: form.authors,
      draftFileId: form.draftFileId,
      finalFileId: form.finalFileId,
      reviewFileId: form.reviewFileId
    }

    if (isEdit.value) {
      const id = route.query.id
      await updatePaper(id, payload)
      ElMessage.success('论文更新成功')
    } else {
      await createPaper(payload)
      ElMessage.success('论文提交成功')
    }
    router.push('/paper')
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '提交失败')
  } finally {
    submitting.value = false
  }
}

// CCF lookup
const showCcfDialog = ref(false)
const ccfLoading = ref(false)
const ccfTableData = ref([])
const ccfTotal = ref(0)
const ccfAreas = ref([])
const ccfQuery = reactive({ page: 1, size: 20, venueType: '', area: '', level: '', keyword: '' })

const searchCcf = async () => {
  ccfLoading.value = true
  try {
    const res = await getCcfPage(ccfQuery)
    ccfTableData.value = res.data.records
    ccfTotal.value = res.data.total
  } finally {
    ccfLoading.value = false
  }
}

const selectCcfVenue = (row) => {
  form.journalName = row.fullName
  if (row.venueType === 'conference') {
    form.journalLevel = `CCF ${row.level}类会议`
  } else {
    form.journalLevel = `CCF ${row.level}类期刊`
  }
  showCcfDialog.value = false
}

const openCcfDialog = async () => {
  showCcfDialog.value = true
  if (ccfAreas.value.length === 0) {
    const res = await getCcfAreas()
    ccfAreas.value = res.data
  }
  ccfQuery.page = 1
  await searchCcf()
}

const matchCcf = async () => {
  if (!form.journalName) {
    ElMessage.warning('请先输入期刊/会议名称')
    return
  }
  try {
    const res = await request.get('/ccf/match', { params: { name: form.journalName } })
    if (res.data) {
      form.journalLevel = res.data.level ? `CCF ${res.data.level}类${res.data.venueType === 'journal' ? '期刊' : '会议'}` : res.data.journalLevel || ''
      ElMessage.success(`已匹配: ${res.data.fullName || res.data.journalName} (CCF-${res.data.level})`)
    } else {
      ElMessage.info('未在CCF目录中找到匹配')
    }
  } catch {
    ElMessage.error('CCF匹配查询失败')
  }
}

const handleCancel = () => {
  router.push('/paper')
}

onMounted(() => {
  if (isEdit.value) {
    loadPaperDetail()
  }
})
</script>

<style scoped>
.paper-form {
  max-width: 900px;
  margin: 0 auto;
}
.form-title {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
.form-body {
  background: #fff;
  padding: 20px 20px 0;
  border-radius: 4px;
}
.form-card {
  margin-bottom: 20px;
}
.form-card :deep(.el-card__header) {
  font-weight: 600;
  color: #303133;
  background: #fafafa;
}
.file-info {
  margin-top: 8px;
  font-size: 13px;
}
.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px 0;
}
</style>
