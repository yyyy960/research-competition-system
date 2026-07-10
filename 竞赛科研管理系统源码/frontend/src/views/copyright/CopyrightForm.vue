<template>
  <div class="copyright-form">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">{{ isEdit ? '编辑软著信息' : '提交软著' }}</span>
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
        <el-form-item label="软件名称" prop="softwareName">
          <el-input v-model="form.softwareName" placeholder="请输入软件名称" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="所属单位" prop="organization">
          <el-input v-model="form.organization" placeholder="请输入所属单位" />
        </el-form-item>

        <el-form-item label="著作权人" prop="copyrightOwner">
          <el-input v-model="form.copyrightOwner" placeholder="请输入著作权人" />
        </el-form-item>

        <el-form-item label="登记号" prop="registrationNumber">
          <el-input
            v-model="form.registrationNumber"
            placeholder="例如：2025SR11569875"
          />
        </el-form-item>

        <el-form-item label="登记日期" prop="registrationDate">
          <el-date-picker
            v-model="form.registrationDate"
            type="date"
            placeholder="选择登记日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-divider content-position="left">证书附件</el-divider>

        <el-form-item label="证书扫描件">
          <div class="file-upload-wrapper">
            <el-upload
              :http-request="handleUploadCertificate"
              :show-file-list="false"
              accept=".png,.jpg,.jpeg,.pdf"
            >
              <el-button type="primary" :icon="Upload" :disabled="uploading">
                上传文件
              </el-button>
              <template #tip>
                <span class="upload-tip">支持 PNG、JPG、PDF 格式</span>
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
                @click="removeCertificate"
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
import { getCopyrightDetail, createCopyright, updateCopyright } from '../../api/copyright'
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
  softwareName: '',
  organization: '',
  copyrightOwner: '',
  registrationNumber: '',
  registrationDate: '',
  certificateFile: null
})

const rules = {
  softwareName: [
    { required: true, message: '请输入软件名称', trigger: 'blur' }
  ],
  registrationNumber: [
    {
      pattern: /^\d{4}SR\d{7,8}$/,
      message: '登记号格式不正确，应为年份+SR+7~8位数字（如：2025SR11569875）',
      trigger: 'blur'
    }
  ]
}

const buildFormData = () => {
  const data = {
    softwareName: form.softwareName,
    organization: form.organization,
    copyrightOwner: form.copyrightOwner,
    registrationNumber: form.registrationNumber,
    registrationDate: form.registrationDate,
    certificateFileId: form.certificateFile?.id || null
  }
  Object.keys(data).forEach((key) => {
    if (data[key] === '' || data[key] === null || data[key] === undefined) {
      delete data[key]
    }
  })
  return data
}

const handleUploadCertificate = async ({ file }) => {
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadFile(formData)
    form.certificateFile = { id: res.data.id, name: file.name }
    ElMessage.success('文件上传成功')
  } catch {
    ElMessage.error('文件上传失败')
  } finally {
    uploading.value = false
  }
}

const removeCertificate = async () => {
  if (!form.certificateFile) return
  try {
    await deleteFile(form.certificateFile.id)
  } catch {
    // Silently ignore server-side delete errors; remove locally anyway
  }
  form.certificateFile = null
  ElMessage.success('文件已移除')
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // Pre-submit duplicate check (non-blocking)
  try {
    const dupRes = await checkDuplicate({ type: 'copyright', data: { ...form } })
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
      await updateCopyright(route.query.id, data)
      ElMessage.success('更新成功')
    } else {
      await createCopyright(data)
      ElMessage.success('提交成功')
    }
    router.push('/copyright')
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '提交失败')
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.push('/copyright')
}

const loadDetail = async () => {
  if (!isEdit.value) return
  loadingDetail.value = true
  try {
    const res = await getCopyrightDetail(route.query.id)
    const detail = res.data
    form.softwareName = detail.softwareName || ''
    form.organization = detail.organization || ''
    form.copyrightOwner = detail.copyrightOwner || ''
    form.registrationNumber = detail.registrationNumber || ''
    form.registrationDate = detail.registrationDate || ''
    if (detail.certificateFiles && detail.certificateFiles.length > 0) {
      form.certificateFile = {
        id: detail.certificateFiles[0].id,
        name: detail.certificateFiles[0].originalName || detail.certificateFiles[0].fileName || detail.certificateFiles[0].name
      }
    }
  } catch {
    ElMessage.error('加载软著详情失败')
  } finally {
    loadingDetail.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.copyright-form {
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
