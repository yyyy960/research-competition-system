<template>
  <div class="achievement-timeline" v-loading="loading">
    <el-empty v-if="!loading && timelineData.length === 0" description="暂无动态" />

    <el-timeline v-else>
      <el-timeline-item
        v-for="(item, index) in timelineData"
        :key="index"
        :timestamp="item.createdAt"
        :placement="item.placement || 'bottom'"
        :color="nodeColor(item.action)"
        :icon="nodeIcon(item.action)"
        :hollow="item.action === 'returned'"
        size="large"
      >
        <div class="timeline-node">
          <div class="node-header">
            <el-tag :type="nodeTag(item.action)" size="small" effect="dark">
              {{ nodeLabel(item.action) }}
            </el-tag>
          </div>
          <div v-if="item.comment" class="node-comment">
            <p>{{ item.comment }}</p>
          </div>
          <div v-if="item.operator" class="node-operator">
            操作人：{{ item.operator }}
          </div>
        </div>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTimeline } from '../api/timeline'

const props = defineProps({
  achievementType: {
    type: String,
    required: true
  },
  achievementId: {
    type: [String, Number],
    required: true
  }
})

const loading = ref(false)
const timelineData = ref([])

const nodeColor = (action) => {
  const colors = {
    submitted: '#7c3aed',
    secretary_review: '#E6A23C',
    leader_review: '#67C23A',
    returned: '#F56C6C',
    archived: '#67C23A'
  }
  return colors[action] || '#909399'
}

const nodeIcon = (action) => {
  const icons = {
    archived: 'SuccessFilled'
  }
  return icons[action] || ''
}

const nodeTag = (action) => {
  const tags = {
    submitted: 'primary',
    secretary_review: 'warning',
    leader_review: 'success',
    returned: 'danger',
    archived: 'success'
  }
  return tags[action] || 'info'
}

const nodeLabel = (action) => {
  const labels = {
    submitted: '提交成果',
    secretary_review: '秘书审核',
    leader_review: '领导审核',
    returned: '退回修改',
    archived: '已归档'
  }
  return labels[action] || action
}

const fetchTimeline = async () => {
  if (!props.achievementType || !props.achievementId) return

  loading.value = true
  try {
    const res = await getTimeline(props.achievementType, props.achievementId)
    timelineData.value = res.data || []
  } catch {
    ElMessage.error('获取动态时间线失败')
  } finally {
    loading.value = false
  }
}

watch(
  () => [props.achievementType, props.achievementId],
  () => {
    fetchTimeline()
  }
)

onMounted(() => {
  fetchTimeline()
})
</script>

<style scoped>
.achievement-timeline {
  padding: 8px 0;
  min-height: 100px;
}
.timeline-node {
  padding: 4px 0;
}
.node-header {
  margin-bottom: 4px;
}
.node-comment {
  font-size: 13px;
  color: #606266;
  margin: 4px 0;
  background: #f5f7fa;
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 3px solid #dcdfe6;
}
.node-comment p {
  margin: 0;
  line-height: 1.5;
}
.node-operator {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
