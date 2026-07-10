<template>
  <div class="innovation-form">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">{{ isEdit ? '编辑大创项目' : '提交大创项目' }}</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        label-position="right"
        style="max-width: 800px"
        v-loading="loadingDetail"
      >
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="项目级别" prop="projectLevel">
          <el-select v-model="form.projectLevel" placeholder="请选择项目级别" style="width: 100%">
            <el-option label="国家级" value="国家级" />
            <el-option label="省级" value="省级" />
            <el-option label="校级" value="校级" />
            <el-option label="院级" value="院级" />
          </el-select>
        </el-form-item>

        <el-form-item label="项目类型" prop="projectType">
          <el-select v-model="form.projectType" placeholder="请选择项目类型" style="width: 100%">
            <el-option label="创新训练项目" value="创新训练项目" />
            <el-option label="创业训练项目" value="创业训练项目" />
            <el-option label="创业实践项目" value="创业实践项目" />
          </el-select>
        </el-form-item>

        <el-form-item label="指导教师" prop="advisor">
          <el-input v-model="form.advisor" placeholder="请输入指导教师姓名" />
        </el-form-item>

        <el-form-item label="项目成员" prop="members">
          <el-input
            v-model="form.members"
            type="textarea"
            :rows="3"
            placeholder="请输入项目成员姓名，多个成员请用逗号或换行分隔"
          />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="date"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-divider content-position="left">文件附件</el-divider>

        <el-form-item label="立项申报书">
          <div class="file-upload-wrapper">
            <el-upload
              :http-request="handleUploadProposal"
              :show-file-list="false"
              accept=".doc,.docx,.pdf"
            >
              <el-button type="primary" :icon="Upload" :disabled="uploading">
                上传文件
              </el-button>
              <template #tip>
                <span class="upload-tip">支持 Word、PDF 格式</span>
              </template>
            </el-upload>
            <div v-if="form.proposalFile" class="uploaded-file">
              <el-icon><Document /></el-icon>
              <span class="file-name">{{ form.proposalFile.name }}</span>
              <el-button
                text
                type="danger"
                :icon="Delete"
                size="small"
                @click="removeFile('proposal')"
              >
                删除
              </el-button>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="结题材料">
          <div class="file-upload-wrapper">
            <el-upload
              :http-request="handleUploadCompletion"
              :show-file-list="false"
              accept=".zip,.rar,.7z"
            >
              <el-button type="primary" :icon="Upload" :disabled="uploading">
                上传文件
              </el-button>
              <template #tip>
                <span class="upload-tip">支持 ZIP、RAR 格式</span>
              </template>
            </el-upload>
            <div v-if="form.completionFile" class="uploaded-file">
              <el-icon><Folder /></el-icon>
              <span class="file-name">{{ form.completionFile.name }}</span>
              <el-button
                text
                type="danger"
                :icon="Delete"
                size="small"
                @click="removeFile('completion')"
              >
                删除
              </el-button>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="结题证书">
          <div class="file-upload-wrapper">
            <el-upload
              :http-request="handleUploadCertificate"
              :show-file-list="false"
              accept=".jpg,.jpeg,.png,.pdf"
            >
              <el-button type="primary" :icon="Upload" :disabled="uploading">
                上传文件
              </el-button>
              <template #tip>
                <span class="upload-tip">支持 JPG、PNG、PDF 格式</span>
              </template>
            </el-upload>
            <div v-if="form.certificateFile" class="uploaded-file">
              <el-icon><Picture /></el-icon>
              <span class="file-name">{{ form.certificateFile.name }}</span>
              <el-button
                text
                type="danger"
                :icon="Delete"
                size="small"
                @click="removeFile('certificate')"
              >
                删除
              </el-button>
            </div>
          </div>
        </el-form-item>

        <el-divider />

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ submitting ? '保存中...' : '保存' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getInnovationDetail, createInnovation, updateInnovation } from '../../api/innovation'
import { uploadFile, deleteFile } from '../../api/file'
import { checkDuplicate, validateFields } from '../../api/check'

const router = useRouter()
const route = useRoute()

const formRef = ref(null)
const submitting = ref(false)
const loadingDetail = ref(false)
const uploading = ref(false)

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  projectName: '',
  projectLevel: '',
  projectType: '',
  advisor: '',
  members: '',
  startTime: '',
  proposalFile: null,
  completionFile: null,
  certificateFile: null
})

const rules = {
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  projectLevel: [
    { required: true, message: '请选择项目级别', trigger: 'change' }
  ],
  projectType: [
    { required: true, message: '请选择项目类型', trigger: 'change' }
  ],
  advisor: [
    { required: false, message: '请输入指导教师', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ]
}

const buildFormData = () => {
  const data = {
    projectName: form.projectName,
    projectLevel: form.projectLevel,
    projectType: form.projectType,
    advisor: form.advisor,
    members: form.members,
    startTime: form.startTime,
    proposalFileId: form.proposalFile?.id || null,
    completionFileId: form.completionFile?.id || null,
    certificateFileId: form.certificateFile?.id || null
  }
  Object.keys(data).forEach((key) => {
    if (data[key] === '' || data[key] === null || data[key] === undefined) {
      delete data[key]
    }
  })
  return data
}

const handleUpload = async (file, target) => {
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadFile(formData)
    const fileInfo = { id: res.data.id, name: file.name }
    form[target] = fileInfo
    ElMessage.success('文件上传成功')
  } catch {
    ElMessage.error('文件上传失败')
  } finally {
    uploading.value = false
  }
}

const handleUploadProposal = ({ file }) => {
  handleUpload(file, 'proposalFile')
}

const handleUploadCompletion = ({ file }) => {
  handleUpload(file, 'completionFile')
}

const handleUploadCertificate = ({ file }) => {
  handleUpload(file, 'certificateFile')
}

const removeFile = async (target) => {
  const fileInfo = form[target]
  if (!fileInfo) return
  try {
    await deleteFile(fileInfo.id)
  } catch {
    // Silently ignore server-side delete errors; remove locally anyway
  }
  form[target] = null
  ElMessage.success('文件已移除')
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // Pre-submit duplicate check (non-blocking)
  try {
    const dupRes = await checkDuplicate({ type: 'innovation', data: { ...form } })
    if (dupRes.data?.hasDuplicate) {
      await ElMessageBox.confirm('发现可能重复的成果，是否继续提交？', '重复警告', {
        type: 'warning', confirmButtonText: '继续提交', cancelButtonText: '取消'
      })
    }
  } catch { /* ignore */ }

  submitting.value = true
  try {
    const data = buildFormData()
    if (isEdit.value) {
      await updateInnovation(route.query.id, data)
      ElMessage.success('更新成功')
    } else {
      await createInnovation(data)
      ElMessage.success('提交成功')
    }
    router.push('/innovation')
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '提交失败')
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.push('/innovation')
}

const loadDetail = async () => {
  if (!isEdit.value) return
  loadingDetail.value = true
  try {
    const res = await getInnovationDetail(route.query.id)
    const detail = res.data
    form.projectName = detail.projectName || ''
    form.projectLevel = detail.projectLevel || ''
    form.projectType = detail.projectType || ''
    form.advisor = detail.advisor || ''
    form.members = detail.members || ''
    form.startTime = detail.startTime || ''
    if (detail.proposalFiles && detail.proposalFiles.length > 0) {
      form.proposalFile = {
        id: detail.proposalFiles[0].id,
        name: detail.proposalFiles[0].originalName || detail.proposalFiles[0].fileName || detail.proposalFiles[0].name
      }
    }
    if (detail.completionFiles && detail.completionFiles.length > 0) {
      form.completionFile = {
        id: detail.completionFiles[0].id,
        name: detail.completionFiles[0].originalName || detail.completionFiles[0].fileName || detail.completionFiles[0].name
      }
    }
    if (detail.certificateFiles && detail.certificateFiles.length > 0) {
      form.certificateFile = {
        id: detail.certificateFiles[0].id,
        name: detail.certificateFiles[0].originalName || detail.certificateFiles[0].fileName || detail.certificateFiles[0].name
      }
    }
  } catch {
    ElMessage.error('加载项目详情失败')
  } finally {
    loadingDetail.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.innovation-form {
  padding: 0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.file-upload-wrapper {
  width: 100%;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.uploaded-file {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.file-name {
  flex: 1;
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
