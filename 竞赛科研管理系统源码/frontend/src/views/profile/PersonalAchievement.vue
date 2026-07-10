<template>
  <div class="personal-achievement">
    <div class="page-header">
      <h2>成果概览</h2>
      <el-button type="primary" :loading="exporting" @click="handleExport">
        <el-icon><Download /></el-icon>
        导出报告
      </el-button>
    </div>

    <!-- Summary Cards -->
    <el-row :gutter="20" class="summary-row">
      <el-col :xs="12" :sm="6" v-for="card in summaryCards" :key="card.type">
        <el-card shadow="hover" :body-style="{ padding: '20px' }" class="summary-card">
          <div class="card-content">
            <div class="card-info">
              <div class="card-label">{{ card.label }}</div>
              <div class="card-value" :style="{ color: card.color }">{{ card.count }}</div>
            </div>
            <el-icon :size="40" :color="card.color" class="card-icon">
              <component :is="card.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Chart & List -->
    <el-row :gutter="20">
      <el-col :span="9">
        <el-card shadow="never">
          <template #header>
            <span>成果分布</span>
          </template>
          <div v-loading="chartLoading" class="chart-wrapper">
            <v-chart v-if="!chartLoading" :option="chartOption" autoresize style="height: 320px" />
            <el-empty v-else-if="!chartLoading && hasNoChartData" description="暂无数据" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="15">
        <el-card shadow="never">
          <template #header>
            <div class="list-header">
              <span>成果列表</span>
              <div class="list-filters">
                <el-select v-model="filterType" placeholder="类型" clearable style="width: 100px" @change="fetchList">
                  <el-option label="竞赛" value="competition" />
                  <el-option label="大创" value="innovation" />
                  <el-option label="软著" value="copyright" />
                  <el-option label="论文" value="paper" />
                </el-select>
                <el-select v-model="filterYear" placeholder="年份" clearable style="width: 100px" @change="fetchList">
                  <el-option v-for="y in yearOptions" :key="y" :label="String(y)" :value="y" />
                </el-select>
                <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 110px" @change="fetchList">
                  <el-option label="待审核" value="pending_review" />
                  <el-option label="审核中" value="under_review" />
                  <el-option label="已通过" value="approved" />
                  <el-option label="已退回" value="returned" />
                  <el-option label="已归档" value="archived" />
                </el-select>
              </div>
            </div>
          </template>

          <div v-loading="listLoading">
            <el-empty v-if="!listLoading && achievementList.length === 0" description="暂无成果" />
            <div v-else class="achievement-items">
              <div v-for="item in achievementList" :key="item.id + '-' + item.type" class="achievement-item" @click="goToDetail(item)">
                <div class="item-left">
                  <el-tag :type="itemTypeTag(item.type)" size="small" class="item-type">
                    {{ itemTypeLabel(item.type) }}
                  </el-tag>
                  <div class="item-info">
                    <div class="item-name">{{ item.workName || item.title }}</div>
                    <div class="item-meta">
                      <span>{{ item.level || '-' }}</span>
                      <el-tag
                        :type="itemStatusTag(item.status)"
                        size="small"
                        effect="plain"
                        class="status-tag"
                      >
                        {{ itemStatusLabel(item.status) }}
                      </el-tag>
                    </div>
                  </div>
                </div>
                <div class="item-actions">
                  <el-tooltip :content="item.pinned ? '取消置顶' : '置顶'" placement="top">
                    <el-icon
                      :size="18"
                      :color="item.pinned ? '#E6A23C' : '#C0C4CC'"
                      class="pin-icon"
                      @click.stop="handleTogglePin(item)"
                    >
                      <StarFilled v-if="item.pinned" />
                      <Star v-else />
                    </el-icon>
                  </el-tooltip>
                </div>
              </div>
            </div>

            <div v-if="total > 0" class="pagination-wrapper">
              <el-pagination
                v-model:current-page="page"
                v-model:page-size="size"
                :total="total"
                layout="total, prev, pager, next"
                @current-change="fetchList"
              />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import 'echarts'
import { getPersonalOverview, getPersonalAchievements, togglePin, exportReport } from '../../api/personal'

const router = useRouter()

// Summary
const summaryCards = ref([
  { type: 'competition', label: '学科竞赛', count: 0, color: '#7c3aed', icon: 'Trophy' },
  { type: 'innovation', label: '大创项目', count: 0, color: '#67C23A', icon: 'Document' },
  { type: 'copyright', label: '软件著作权', count: 0, color: '#E6A23C', icon: 'Files' },
  { type: 'paper', label: '学术论文', count: 0, color: '#F56C6C', icon: 'Reading' }
])

const chartLoading = ref(false)
const hasNoChartData = ref(false)

// Filters
const filterType = ref('')
const filterYear = ref('')
const filterStatus = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

const yearOptions = computed(() => {
  const current = new Date().getFullYear()
  const years = []
  for (let y = current; y >= current - 10; y--) {
    years.push(y)
  }
  return years
})

// List
const achievementList = ref([])
const listLoading = ref(false)

// Export
const exporting = ref(false)

const itemTypeTag = (type) => {
  const map = { competition: 'primary', innovation: 'success', copyright: 'warning', paper: 'info' }
  return map[type] || ''
}

const itemTypeLabel = (type) => {
  const map = { competition: '竞赛', innovation: '大创', copyright: '软著', paper: '论文' }
  return map[type] || type
}

const itemStatusTag = (status) => {
  const map = { pending_review: 'warning', under_review: 'info', approved: 'success', returned: 'danger', archived: '' }
  return map[status] || 'info'
}

const itemStatusLabel = (status) => {
  const map = { pending_review: '待审核', under_review: '审核中', approved: '已通过', returned: '已退回', archived: '已归档' }
  return map[status] || status
}

// Radar chart option
const chartOption = computed(() => {
  const d = overviewData.value?.distribution || []
  const values = [
    d.find(x => x.name === '学科竞赛')?.value || 0,
    d.find(x => x.name === '大创项目')?.value || 0,
    d.find(x => x.name === '软件著作权')?.value || 0,
    d.find(x => x.name === '学术论文')?.value || 0
  ]
  const maxVal = Math.max(...values, 1)
  return {
    tooltip: {},
    radar: {
      indicator: [
        { name: '学科竞赛', max: maxVal },
        { name: '大创项目', max: maxVal },
        { name: '软件著作权', max: maxVal },
        { name: '学术论文', max: maxVal }
      ],
      shape: 'circle',
      center: ['50%', '50%'],
      radius: '65%'
    },
    series: [{
      type: 'radar',
      data: [{
        value: values,
        name: '成果分布',
        areaStyle: { color: 'rgba(124, 58, 237, 0.25)' },
        lineStyle: { color: '#7c3aed', width: 2 },
        itemStyle: { color: '#7c3aed' }
      }]
    }]
  }
})

const overviewData = ref({ distribution: [] })

const fetchOverview = async () => {
  chartLoading.value = true
  try {
    const res = await getPersonalOverview()
    const data = res.data || {}
    overviewData.value = data

    // Build counts from backend
    const counts = {
      competition: data.competitions?.total || 0,
      innovation: data.innovations?.total || 0,
      copyright: data.copyrights?.total || 0,
      paper: data.papers?.total || 0
    }
    summaryCards.value = summaryCards.value.map(card => ({
      ...card,
      count: counts[card.type] || 0
    }))

    const distribution = [
      { name: '学科竞赛', value: counts.competition },
      { name: '大创项目', value: counts.innovation },
      { name: '软件著作权', value: counts.copyright },
      { name: '学术论文', value: counts.paper }
    ].filter(d => d.value > 0)
    overviewData.value.distribution = distribution
    hasNoChartData.value = distribution.length === 0
  } catch {
    ElMessage.error('获取概览数据失败')
  } finally {
    chartLoading.value = false
  }
}

const fetchList = async () => {
  listLoading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (filterType.value) params.type = filterType.value
    if (filterYear.value) params.year = filterYear.value
    if (filterStatus.value) params.status = filterStatus.value

    const res = await getPersonalAchievements(params)
    const data = res.data
    const records = data?.records || data || []
    // Map isPinned -> pinned and unify level field for display
    achievementList.value = records.map(r => ({
      ...r,
      pinned: r.isPinned === 1,
      level: r.awardLevel || r.projectLevel || r.journalLevel || r.registrationNumber || ''
    }))
    total.value = data?.total || 0
  } catch {
    ElMessage.error('获取成果列表失败')
  } finally {
    listLoading.value = false
  }
}

const handleTogglePin = async (item) => {
  try {
    await togglePin(item.type, item.id)
    item.pinned = !item.pinned
    ElMessage.success(item.pinned ? '已置顶' : '已取消置顶')
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleExport = async () => {
  exporting.value = true
  try {
    const res = await exportReport()
    const blob = res.data || res
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `科研成果报告_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

const goToDetail = (item) => {
  const routes = {
    competition: `/competition/${item.id}`,
    innovation: `/innovation/${item.id}`,
    copyright: `/copyright/${item.id}`,
    paper: `/paper/${item.id}`
  }
  const path = routes[item.type]
  if (path) router.push(path)
}

onMounted(() => {
  fetchOverview()
  fetchList()
})
</script>

<style scoped>
.personal-achievement {
  padding: 0;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-header h2 {
  font-size: 20px;
  color: #303133;
  margin: 0;
}
.summary-row {
  margin-bottom: 20px;
}
.summary-card {
  cursor: default;
}
.summary-card .card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.card-value {
  font-size: 28px;
  font-weight: 700;
}
.card-icon {
  opacity: 0.6;
}
.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}
.list-filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.chart-wrapper {
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.achievement-items {
  max-height: 500px;
  overflow-y: auto;
}
.achievement-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 8px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}
.achievement-item:hover {
  background-color: #f5f7fa;
}
.item-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}
.item-type {
  flex-shrink: 0;
}
.item-info {
  flex: 1;
  min-width: 0;
}
.item-name {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 2px;
}
.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}
.status-tag {
  font-size: 11px;
}
.item-actions {
  flex-shrink: 0;
  margin-left: 8px;
}
.pin-icon {
  cursor: pointer;
  transition: color 0.2s;
}
.pin-icon:hover {
  color: #E6A23C !important;
}
.pagination-wrapper {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
