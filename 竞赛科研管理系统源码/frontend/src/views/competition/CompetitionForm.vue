<template>
  <div class="competition-form">
    <el-card shadow="never">
      <template #header>
        <span>{{ isEdit ? '编辑竞赛成果' : '提交竞赛成果' }}</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 800px"
        v-loading="pageLoading"
      >
        <!-- OCR智能识别 -->
        <div class="ocr-section">
          <el-button type="warning" :icon="Camera" @click="openOcrDialog">
            证书智能识别
          </el-button>
          <span class="ocr-tip">上传证书图片，自动识别竞赛名称、级别、等级、时间</span>
        </div>

        <el-divider content-position="left">基本信息</el-divider>

        <el-form-item label="竞赛类别" prop="competitionCategory">
          <el-select v-model="form.competitionCategory" placeholder="请选择竞赛类别" style="width: 200px">
            <el-option label="A类" value="A" />
            <el-option label="B类" value="B" />
            <el-option label="C类" value="C" />
          </el-select>
        </el-form-item>

        <el-form-item label="竞赛名称" prop="competitionName">
          <el-input v-model="form.competitionName" placeholder="请输入竞赛名称" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="主办单位" prop="hostUnit">
          <el-input v-model="form.hostUnit" placeholder="请输入主办单位" maxlength="200" />
        </el-form-item>

        <el-form-item label="承办单位" prop="organizerUnit">
          <el-input v-model="form.organizerUnit" placeholder="请输入承办单位" maxlength="200" />
        </el-form-item>

        <el-form-item label="颁奖单位" prop="awardUnit">
          <el-input v-model="form.awardUnit" placeholder="请输入颁奖单位" maxlength="200" />
        </el-form-item>

        <el-divider content-position="left">获奖信息</el-divider>

        <el-form-item label="获奖作品名称" prop="workName">
          <el-input v-model="form.workName" placeholder="请输入获奖作品名称" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="获奖等级" prop="awardLevel">
          <el-select v-model="form.awardLevel" placeholder="请选择获奖等级" style="width: 200px">
            <el-option label="国家级" value="national" />
            <el-option label="省级" value="provincial" />
            <el-option label="市级" value="municipal" />
            <el-option label="校级" value="school" />
            <el-option label="院级" value="college" />
          </el-select>
        </el-form-item>

        <el-form-item label="获奖级别" prop="awardGrade">
          <el-select v-model="form.awardGrade" placeholder="请选择获奖级别" style="width: 200px">
            <el-option label="一等奖" value="first" />
            <el-option label="二等奖" value="second" />
            <el-option label="三等奖" value="third" />
          </el-select>
        </el-form-item>

        <el-form-item label="获奖时间" prop="awardTime">
          <el-date-picker
            v-model="form.awardTime"
            type="date"
            placeholder="选择获奖日期"
            value-format="YYYY-MM-DD"
            style="width: 200px"
          />
        </el-form-item>

        <el-divider content-position="left">人员信息</el-divider>

        <el-form-item label="指导教师" prop="advisor">
          <el-input v-model="form.advisor" placeholder="请输入指导教师姓名" maxlength="100" />
        </el-form-item>

        <el-form-item label="参与学生" prop="participants">
          <el-input
            v-model="form.participants"
            type="textarea"
            :rows="3"
            placeholder="请输入参与学生姓名，多人用逗号分隔"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-divider content-position="left">附件材料</el-divider>

        <el-form-item label="上传文件">
          <div class="upload-wrapper">
            <el-upload
              ref="uploadRef"
              :http-request="handleUpload"
              :on-remove="handleRemove"
              :file-list="fileList"
              list-type="text"
              multiple
              :before-upload="beforeUpload"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                选择文件
              </el-button>
              <template #tip>
                <div class="upload-tip">支持上传 PDF、Word、Excel、图片等格式文件，单个文件不超过 20MB</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSave">
            {{ submitting ? '提交中...' : '保存' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- OCR Dialog with real tesseract.js integration -->
    <el-dialog v-model="showOcrDialog" title="证书智能识别" width="900px" destroy-on-close>
      <el-row :gutter="24">
        <!-- Left: Image upload -->
        <el-col :span="11">
          <div class="ocr-upload-area" :class="{ 'has-image': certImageUrl }">
            <template v-if="certImageUrl">
              <el-image :src="certImageUrl" fit="contain" class="ocr-preview-image" />
              <el-button class="ocr-reselect-btn" size="small" @click="clearCertImage">
                重新选择
              </el-button>
            </template>
            <template v-else>
              <el-upload
                :http-request="handleCertUpload"
                :accept="'.jpg,.jpeg,.png,.bmp'"
                :limit="1"
                :show-file-list="false"
                drag
              >
                <el-icon :size="44" color="#7c3aed"><Upload /></el-icon>
                <div class="ocr-upload-text">点击或拖拽证书图片上传</div>
                <div class="ocr-upload-hint">支持 JPG、PNG、BMP 格式</div>
              </el-upload>
            </template>
          </div>

          <div v-if="certImageUrl" style="margin-top:12px;text-align:center">
            <el-button
              type="primary"
              :loading="ocrLoading"
              :disabled="ocrLoading"
              @click="runOcr"
            >
              <el-icon><Camera /></el-icon>
              {{ ocrLoading ? `识别中 ${ocrProgress}%` : '开始识别' }}
            </el-button>
          </div>

          <div v-if="ocrError" class="ocr-error">
            <el-icon><WarningFilled /></el-icon>
            {{ ocrError }}
          </div>
        </el-col>

        <!-- Right: Results -->
        <el-col :span="13">
          <div class="ocr-results-panel">
            <div class="ocr-results-header">
              <span>识别结果</span>
              <el-tag v-if="ocrRawText" :type="ocrFieldsFound > 0 ? 'success' : 'info'" size="small">
                已识别 {{ ocrFieldsFound }} 个字段
              </el-tag>
            </div>

            <!-- Raw text output -->
            <div v-if="ocrRawText" class="ocr-raw-text">
              <div class="ocr-raw-label">识别文本：</div>
              <div class="ocr-raw-content">{{ ocrRawText }}</div>
            </div>
            <div v-else-if="!ocrLoading" class="ocr-empty">
              <el-icon :size="36" color="#d1d5db"><Document /></el-icon>
              <p>上传证书图片后点击"开始识别"</p>
            </div>
            <div v-else class="ocr-loading">
              <el-icon :size="28" class="is-loading"><Loading /></el-icon>
              <p>正在识别中，请稍候...</p>
            </div>

            <!-- Parsed fields -->
            <div v-if="ocrFieldsFound > 0" class="ocr-fields">
              <el-form :model="certForm" label-width="80px" size="default">
                <el-form-item label="竞赛名称">
                  <el-input v-model="certForm.competitionName" placeholder="未识别到" />
                </el-form-item>
                <el-form-item label="获奖作品名称">
                  <el-input v-model="certForm.workName" placeholder="未识别到" />
                </el-form-item>
                <el-form-item label="获奖等级">
                  <el-select v-model="certForm.awardLevel" placeholder="选择级别" style="width:100%">
                    <el-option label="国家级" value="national" />
                    <el-option label="省级" value="provincial" />
                    <el-option label="市级" value="municipal" />
                    <el-option label="校级" value="school" />
                    <el-option label="院级" value="college" />
                  </el-select>
                </el-form-item>
                <el-form-item label="获奖级别">
                  <el-select v-model="certForm.awardGrade" placeholder="选择级别" style="width:100%">
                    <el-option label="一等奖" value="first" />
                    <el-option label="二等奖" value="second" />
                    <el-option label="三等奖" value="third" />
                  </el-select>
                </el-form-item>
                <el-form-item label="获奖时间">
                  <el-date-picker
                    v-model="certForm.awardTime"
                    type="date"
                    value-format="YYYY-MM-DD"
                    placeholder="选择日期"
                    style="width:100%"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="applyOcrResult" style="width:100%">
                    应用到表单
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Upload, Camera, Document, Loading, WarningFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCompetitionDetail, createCompetition, updateCompetition
} from '../../api/competition'
import { uploadFile, deleteFile, getFileUrl } from '../../api/file'
import { checkDuplicate } from '../../api/check'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const uploadRef = ref(null)
const pageLoading = ref(false)
const submitting = ref(false)

const competitionId = computed(() => route.query.id)
const isEdit = computed(() => !!competitionId.value)

const form = reactive({
  competitionCategory: '',
  competitionName: '',
  hostUnit: '',
  organizerUnit: '',
  awardUnit: '',
  awardLevel: '',
  awardGrade: '',
  awardTime: '',
  workName: '',
  advisor: '',
  participants: ''
})

const rules = {
  competitionCategory: [{ required: true, message: '请选择竞赛类别', trigger: 'change' }],
  competitionName: [
    { required: true, message: '请输入竞赛名称', trigger: 'blur' },
    { max: 200, message: '竞赛名称不能超过200个字符', trigger: 'blur' }
  ],
  hostUnit: [{ required: true, message: '请输入主办单位', trigger: 'blur' }],
  awardLevel: [{ required: true, message: '请选择获奖等级', trigger: 'change' }],
  awardGrade: [{ required: true, message: '请选择获奖级别', trigger: 'change' }],
  awardTime: [{ required: true, message: '请选择获奖时间', trigger: 'change' }],
  advisor: [{ required: true, message: '请输入指导教师', trigger: 'blur' }]
}

const fileList = ref([])

const toUploadFile = (file) => ({
  id: file.id,
  name: file.originalName || file.name || '未知文件',
  url: file.url || getFileUrl(file.id)
})

const beforeUpload = (rawFile) => {
  const maxSize = 20 * 1024 * 1024
  if (rawFile.size > maxSize) {
    ElMessage.error('单个文件不能超过 20MB')
    return false
  }
  return true
}

const handleUpload = async ({ file, onSuccess, onError }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadFile(formData)
    const uploaded = res.data
    fileList.value.push({
      id: uploaded.id,
      name: uploaded.originalName || file.name,
      url: getFileUrl(uploaded.id)
    })
    onSuccess(res)
    ElMessage.success('文件上传成功')
  } catch (err) {
    onError(err)
    ElMessage.error('文件上传失败')
  }
}

const handleRemove = async (uploadFileItem) => {
  try {
    if (uploadFileItem.id) {
      await deleteFile(uploadFileItem.id)
    }
    const index = fileList.value.findIndex(f => f.id === uploadFileItem.id)
    if (index > -1) fileList.value.splice(index, 1)
    ElMessage.success('文件已删除')
  } catch {
    ElMessage.error('删除文件失败')
  }
}

const loadCompetition = async () => {
  if (!isEdit.value) return
  pageLoading.value = true
  try {
    const res = await getCompetitionDetail(competitionId.value)
    const data = res.data
    form.competitionCategory = data.competitionCategory || ''
    form.competitionName = data.competitionName || ''
    form.hostUnit = data.hostUnit || ''
    form.organizerUnit = data.organizerUnit || ''
    form.awardUnit = data.awardUnit || ''
    form.awardLevel = data.awardLevel || ''
    form.awardGrade = data.awardGrade || ''
    form.awardTime = data.awardTime || ''
    form.workName = data.workName || ''
    form.advisor = data.advisor || ''
    form.participants = data.participants || ''
    if (data.files && Array.isArray(data.files)) {
      fileList.value = data.files.map(toUploadFile)
    }
  } catch {
    ElMessage.error('获取竞赛信息失败')
    router.push('/competition')
  } finally {
    pageLoading.value = false
  }
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const dupRes = await checkDuplicate({ type: 'competition', data: { ...form } })
    if (dupRes.data?.hasDuplicate) {
      await ElMessageBox.confirm('发现可能重复的成果，是否继续提交？', '重复警告', {
        type: 'warning', confirmButtonText: '继续提交', cancelButtonText: '取消'
      })
    }
  } catch { /* ignore */ }

  submitting.value = true
  try {
    const payload = { ...form, fileIds: fileList.value.map(f => f.id) }
    if (isEdit.value) {
      await updateCompetition(competitionId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await createCompetition(payload)
      ElMessage.success('提交成功')
    }
    router.push('/competition')
  } catch { /* handled by interceptor */ }
  finally { submitting.value = false }
}

// ==================== OCR with tesseract.js ====================
const showOcrDialog = ref(false)
const certImageUrl = ref('')
const ocrLoading = ref(false)
const ocrProgress = ref(0)
const ocrError = ref('')
const ocrRawText = ref('')
const ocrFieldsFound = ref(0)

const certForm = reactive({
  competitionName: '',
  workName: '',
  awardLevel: '',
  awardGrade: '',
  awardTime: ''
})

const handleCertUpload = async (options) => {
  ocrError.value = ''
  ocrRawText.value = ''
  ocrFieldsFound.value = 0
  certImageUrl.value = URL.createObjectURL(options.file)
}

const clearCertImage = () => {
  if (certImageUrl.value && certImageUrl.value.startsWith('blob:')) {
    URL.revokeObjectURL(certImageUrl.value)
  }
  certImageUrl.value = ''
  ocrRawText.value = ''
  ocrFieldsFound.value = 0
  ocrError.value = ''
}

/**
 * Run tesseract.js OCR on the uploaded certificate image.
 * Recognizes Chinese text (chi_sim) with English fallback.
 */
const runOcr = async () => {
  if (!certImageUrl.value) {
    ocrError.value = '请先上传证书图片'
    return
  }

  ocrLoading.value = true
  ocrProgress.value = 0
  ocrError.value = ''
  ocrRawText.value = ''

  try {
    // Dynamically import tesseract.js (browser-side OCR)
    const Tesseract = await import('tesseract.js')

    const result = await Tesseract.recognize(certImageUrl.value, 'chi_sim+eng', {
      logger: (m) => {
        if (m.status === 'recognizing text') {
          ocrProgress.value = Math.round(m.progress * 100)
        }
      }
    })

    ocrRawText.value = result.data.text.trim()
    ocrProgress.value = 100

    if (!ocrRawText.value) {
      ocrError.value = '未能识别到文字，请确认图片清晰度或手动填写'
      return
    }

    // Parse recognized text to extract fields
    parseOcrText(ocrRawText.value)
  } catch (e) {
    console.error('OCR error:', e)
    ocrError.value = 'OCR识别失败：' + (e.message || '未知错误')
  } finally {
    ocrLoading.value = false
  }
}

/**
 * Parse OCR text output to extract competition-related fields.
 * Uses regex patterns for Chinese certificate text.
 */
const parseOcrText = (text) => {
  // Reset fields
  certForm.competitionName = ''
  certForm.workName = ''
  certForm.awardLevel = ''
  certForm.awardGrade = ''
  certForm.awardTime = ''
  ocrFieldsFound.value = 0

  // --- Competition Name ---
  // Pattern: "竞赛名称：XXX" or "XXXX竞赛" or "第X届XXXX"
  const namePatterns = [
    /竞赛名称[：:]\s*(.+?)(?:[\n\r]|$)/,
    /([一-龥]+(?:大赛|竞赛|挑战赛|联赛|比赛))/,
    /第[一二三四五六七八九十\d]+届\s*([一-龥]+(?:大赛|竞赛|挑战赛))/
  ]
  for (const p of namePatterns) {
    const m = text.match(p)
    if (m) {
      certForm.competitionName = m[1].trim()
      ocrFieldsFound.value++
      break
    }
  }

  // --- Work Name (获奖作品名称) ---
  const workNamePatterns = [
    /作品名称[：:]\s*(.+?)(?:[\n\r]|$)/,
    /项目名称[：:]\s*(.+?)(?:[\n\r]|$)/,
    /获奖作品[：:]\s*(.+?)(?:[\n\r]|$)/
  ]
  for (const p of workNamePatterns) {
    const m = text.match(p)
    if (m) {
      certForm.workName = m[1].trim()
      ocrFieldsFound.value++
      break
    }
  }

  // --- Award Level (国家级/省级/市级/校级/院级) ---
  const levelPatterns = [
    /获奖等级[：:]\s*(.+?)(?:[\n\r]|$)/,
    /竞赛级别[：:]\s*(.+?)(?:[\n\r]|$)/
  ]
  for (const p of levelPatterns) {
    const m = text.match(p)
    if (m) {
      const levelText = m[1].trim()
      if (/国家/.test(levelText)) certForm.awardLevel = 'national'
      else if (/省/.test(levelText)) certForm.awardLevel = 'provincial'
      else if (/市/.test(levelText)) certForm.awardLevel = 'municipal'
      else if (/校/.test(levelText)) certForm.awardLevel = 'school'
      else if (/院/.test(levelText)) certForm.awardLevel = 'college'
      if (certForm.awardLevel) ocrFieldsFound.value++
      break
    }
  }
  // Fallback: scan text directly for level keywords
  if (!certForm.awardLevel) {
    if (/国家级/.test(text)) { certForm.awardLevel = 'national'; ocrFieldsFound.value++ }
    else if (/省(级|赛)/.test(text)) { certForm.awardLevel = 'provincial'; ocrFieldsFound.value++ }
    else if (/市(级|赛)/.test(text)) { certForm.awardLevel = 'municipal'; ocrFieldsFound.value++ }
    else if (/校(级|赛|内)/.test(text)) { certForm.awardLevel = 'school'; ocrFieldsFound.value++ }
  }

  // --- Award Grade (一等奖/二等奖/三等奖) ---
  const gradePatterns = [
    /获奖级别[：:]\s*(.+?)(?:[\n\r]|$)/,
    /奖项[：:]\s*(.+?)(?:[\n\r]|$)/
  ]
  for (const p of gradePatterns) {
    const m = text.match(p)
    if (m) {
      const gradeText = m[1].trim()
      if (/一|1/.test(gradeText)) certForm.awardGrade = 'first'
      else if (/二|2/.test(gradeText)) certForm.awardGrade = 'second'
      else if (/三|3/.test(gradeText)) certForm.awardGrade = 'third'
      if (certForm.awardGrade) ocrFieldsFound.value++
      break
    }
  }
  // Fallback
  if (!certForm.awardGrade) {
    if (/一等奖/.test(text)) { certForm.awardGrade = 'first'; ocrFieldsFound.value++ }
    else if (/二等奖/.test(text)) { certForm.awardGrade = 'second'; ocrFieldsFound.value++ }
    else if (/三等奖/.test(text)) { certForm.awardGrade = 'third'; ocrFieldsFound.value++ }
    else if (/特等奖/.test(text)) { certForm.awardGrade = 'first'; ocrFieldsFound.value++ }
  }

  // --- Award Time ---
  const timePatterns = [
    /获奖时间[：:]\s*(\d{4}[年.-]\d{1,2}[月.-]\d{1,2}[日]?)/,
    /颁发日期[：:]\s*(\d{4}[年.-]\d{1,2}[月.-]\d{1,2}[日]?)/,
    /(\d{4}[年.-]\d{1,2}[月.-]\d{1,2}[日]?)/,
    /(\d{4}-\d{2}-\d{2})/
  ]
  for (const p of timePatterns) {
    const m = text.match(p)
    if (m) {
      let timeStr = m[1].replace(/[年月]/g, '-').replace(/日/g, '')
      certForm.awardTime = timeStr
      ocrFieldsFound.value++
      break
    }
  }
}

const applyOcrResult = () => {
  if (certForm.competitionName) form.competitionName = certForm.competitionName
  if (certForm.workName) form.workName = certForm.workName
  if (certForm.awardLevel) form.awardLevel = certForm.awardLevel
  if (certForm.awardGrade) form.awardGrade = certForm.awardGrade
  if (certForm.awardTime) form.awardTime = certForm.awardTime
  showOcrDialog.value = false
  clearCertImage()
  Object.keys(certForm).forEach(k => certForm[k] = '')
  ElMessage.success(`已应用 ${ocrFieldsFound.value} 个识别字段到表单`)
}

const openOcrDialog = () => {
  clearCertImage()
  showOcrDialog.value = true
}

const handleCancel = () => {
  if (isEdit.value) {
    router.push(`/competition/${competitionId.value}`)
  } else {
    router.push('/competition')
  }
}

onMounted(() => {
  loadCompetition()
})
</script>

<style scoped>
.competition-form { padding: 0; }
.ocr-section {
  background: #fefce8;
  border: 1px dashed #f59e0b;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.ocr-tip {
  font-size: 12px;
  color: #92400e;
}
.upload-wrapper { width: 100%; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 4px; }

/* OCR Dialog */
.ocr-upload-area {
  min-height: 320px;
  border: 2px dashed #e5e7eb;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: #fafbfc;
}
.ocr-upload-area.has-image {
  border-style: solid;
  border-color: #e5e7eb;
  padding: 8px;
}
.ocr-preview-image {
  max-height: 400px;
  width: 100%;
  object-fit: contain;
}
.ocr-reselect-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
}
.ocr-upload-text {
  margin-top: 10px;
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}
.ocr-upload-hint {
  margin-top: 4px;
  font-size: 12px;
  color: #9ca3af;
}
.ocr-error {
  margin-top: 12px;
  padding: 10px 14px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  color: #dc2626;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* OCR Results */
.ocr-results-panel {
  min-height: 320px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
}
.ocr-results-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}
.ocr-raw-text {
  margin-bottom: 12px;
}
.ocr-raw-label {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
}
.ocr-raw-content {
  background: #f9fafb;
  border: 1px solid #f3f4f6;
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 13px;
  line-height: 1.6;
  color: #6b7280;
  max-height: 160px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
.ocr-empty, .ocr-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 260px;
  color: #9ca3af;
  gap: 8px;
  font-size: 13px;
}
.ocr-fields {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.is-loading {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
