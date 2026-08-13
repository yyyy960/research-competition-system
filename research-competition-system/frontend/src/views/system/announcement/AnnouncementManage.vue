<template>
  <div class="announcement-manage">
    <div class="page-header">
      <h2>系统公告管理</h2>
      <el-button type="primary" @click="openCreate">发布公告</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="list" v-loading="loading" stripe border empty-text="暂无公告">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="置顶" width="70" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="warning" size="small">置顶</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="publisher" label="发布单位" width="150" />
        <el-table-column prop="publishTime" label="发布日期" width="110" />
        <el-table-column label="操作" width="290" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/announcement/${row.id}`)">查看</el-button>
            <el-button link type="warning" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link :type="row.isTop ? 'success' : 'info'" size="small" @click="toggleTop(row)">
              {{ row.isTop ? '取消置顶' : '置顶' }}
            </el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
          background
        />
      </div>
    </el-card>

    <!-- Create/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑公告' : '发布公告'"
      width="860px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="公告标题" maxlength="200" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发布单位" prop="publisher">
              <el-input v-model="form.publisher" placeholder="如：怀化学院党政办公室" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布日期" prop="publishTime">
              <el-date-picker
                v-model="form.publishTime"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>

        <!-- Rich Text Editor -->
        <el-form-item label="内容" prop="content">
          <!-- Toolbar -->
          <div class="editor-toolbar">
            <button type="button" class="tool-btn" title="加粗" @click="execCmd('bold')"><b>B</b></button>
            <button type="button" class="tool-btn" title="斜体" @click="execCmd('italic')"><i>I</i></button>
            <button type="button" class="tool-btn" title="下划线" @click="execCmd('underline')"><u>U</u></button>
            <span class="tool-divider"></span>
            <button type="button" class="tool-btn" title="标题" @click="execCmd('formatBlock', 'h3')">H</button>
            <button type="button" class="tool-btn" title="无序列表" @click="execCmd('insertUnorderedList')">•≡</button>
            <button type="button" class="tool-btn" title="有序列表" @click="execCmd('insertOrderedList')">1.</button>
            <span class="tool-divider"></span>
            <button type="button" class="tool-btn" title="引用" @click="execCmd('formatBlock', 'blockquote')">"</button>
            <button type="button" class="tool-btn" title="插入链接" @click="insertLink">🔗</button>
            <span class="tool-divider"></span>
            <el-radio-group v-model="editorMode" size="small" @change="switchEditorMode">
              <el-radio-button value="visual">可视化</el-radio-button>
              <el-radio-button value="html">HTML</el-radio-button>
            </el-radio-group>
          </div>

          <!-- Visual Editor -->
          <div
            v-show="editorMode === 'visual'"
            ref="editorRef"
            class="editor-content"
            contenteditable="true"
            @input="onEditorInput"
            @paste="onEditorPaste"
            placeholder="请输入公告内容..."
          >
          </div>

          <!-- HTML Source Editor -->
          <el-input
            v-show="editorMode === 'html'"
            v-model="form.content"
            type="textarea"
            :rows="18"
            placeholder="支持HTML格式：<p>段落</p> <b>加粗</b> <i>斜体</i> <h3>标题</h3> <ul><li>列表</li></ul>"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '更新' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getAnnouncementPage, createAnnouncement,
  updateAnnouncement, deleteAnnouncement
} from '../../../api/announcement'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const editorRef = ref(null)
const editorMode = ref('visual')
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const form = reactive({
  title: '', publisher: '', publishTime: '', content: '', isTop: 0
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  publisher: [{ required: true, message: '请输入发布单位', trigger: 'blur' }],
  publishTime: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

// ── Rich Text Editor Logic ──
const execCmd = (command, value = null) => {
  document.execCommand(command, false, value)
  syncContentToForm()
  editorRef.value?.focus()
}

const onEditorInput = () => {
  syncContentToForm()
}

const onEditorPaste = (e) => {
  e.preventDefault()
  const text = e.clipboardData.getData('text/plain')
  document.execCommand('insertText', false, text)
}

const syncContentToForm = () => {
  if (editorRef.value) {
    form.content = editorRef.value.innerHTML
  }
}

const switchEditorMode = (mode) => {
  if (mode === 'visual') {
    nextTick(() => {
      if (editorRef.value) {
        editorRef.value.innerHTML = form.content || ''
      }
    })
  }
}

const insertLink = async () => {
  const url = prompt('请输入链接地址：', 'https://')
  if (url) {
    const selection = window.getSelection()
    const text = selection.toString() || url
    document.execCommand('createLink', false, url)
    syncContentToForm()
  }
}

// Set editor content from form data
watch(() => dialogVisible.value, async (val) => {
  if (val) {
    editorMode.value = 'visual'
    await nextTick()
    if (editorRef.value) {
      editorRef.value.innerHTML = form.content || ''
    }
  }
})

// ── Data ──
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAnnouncementPage({ page: page.value, size: size.value })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  isEdit.value = false; editId.value = null; resetForm(); dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true; editId.value = row.id
  form.title = row.title; form.publisher = row.publisher
  form.publishTime = row.publishTime; form.content = row.content || ''
  form.isTop = row.isTop || 0
  dialogVisible.value = true
}

const resetForm = () => {
  form.title = ''; form.publisher = ''; form.publishTime = ''; form.content = ''; form.isTop = 0
  editorMode.value = 'visual'
}

const handleSubmit = async () => {
  // Ensure content is synced from editor
  if (editorMode.value === 'visual') {
    syncContentToForm()
  }

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateAnnouncement(editId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createAnnouncement({ ...form })
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const toggleTop = async (row) => {
  try {
    await updateAnnouncement(row.id, { isTop: row.isTop ? 0 : 1 })
    ElMessage.success(row.isTop ? '已取消置顶' : '已置顶')
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await deleteAnnouncement(id)
    ElMessage.success('已删除')
    fetchData()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(() => fetchData())
</script>

<style scoped>
.announcement-manage { padding: 0; }
.page-header {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;
}
.page-header h2 { font-size: 20px; color: #303133; margin: 0; }
.pagination-wrapper { margin-top: 16px; display: flex; justify-content: flex-end; }

/* Editor Toolbar */
.editor-toolbar {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 6px 8px;
  background: #fafbfc;
  border: 1px solid #dcdfe6;
  border-bottom: none;
  border-radius: 4px 4px 0 0;
  flex-wrap: wrap;
}
.tool-btn {
  width: 28px;
  height: 28px;
  border: 1px solid transparent;
  border-radius: 4px;
  background: transparent;
  cursor: pointer;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #374151;
  transition: all 0.15s;
}
.tool-btn:hover {
  background: #e5e7eb;
  border-color: #d1d5db;
}
.tool-divider {
  width: 1px;
  height: 20px;
  background: #e5e7eb;
  margin: 0 4px;
}

/* Editor Content */
.editor-content {
  min-height: 400px;
  max-height: 500px;
  overflow-y: auto;
  padding: 12px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 0 0 4px 4px;
  font-size: 14px;
  line-height: 1.8;
  color: #374151;
  outline: none;
}
.editor-content:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(124, 58, 237, 0.1);
}
.editor-content:empty::before {
  content: attr(placeholder);
  color: #c0c4cc;
  pointer-events: none;
}
.editor-content :deep(h3) {
  font-size: 17px;
  margin: 12px 0 8px;
}
.editor-content :deep(blockquote) {
  border-left: 4px solid #7c3aed;
  padding: 6px 14px;
  margin: 10px 0;
  background: #f5f3ff;
  border-radius: 0 4px 4px 0;
  color: #6b7280;
}
.editor-content :deep(ul),
.editor-content :deep(ol) {
  padding-left: 24px;
  margin: 8px 0;
}
.editor-content :deep(a) {
  color: #7c3aed;
}
.editor-content :deep(p) {
  margin-bottom: 8px;
}
</style>
