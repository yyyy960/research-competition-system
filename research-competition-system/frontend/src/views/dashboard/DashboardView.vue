<template>
  <div class="dashboard">
    <!-- ======== KPI CARDS ======== -->
    <div class="kpi-grid">
      <div class="kpi-card kpi-card--blue" v-for="card in kpiCards" :key="card.key">
        <div class="kpi-card__icon">
          <svg viewBox="0 0 40 40" fill="none">
            <template v-if="card.key === 'competition'">
              <circle cx="20" cy="20" r="16" stroke="currentColor" stroke-width="1.5"/>
              <path d="M12 28l6-10 4 6 6-12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="28" cy="10" r="2" fill="currentColor"/>
            </template>
            <template v-else-if="card.key === 'innovation'">
              <rect x="6" y="8" width="12" height="10" rx="1.5" stroke="currentColor" stroke-width="1.5"/>
              <rect x="22" y="12" width="12" height="10" rx="1.5" stroke="currentColor" stroke-width="1.5"/>
              <rect x="14" y="22" width="12" height="10" rx="1.5" stroke="currentColor" stroke-width="1.5"/>
            </template>
            <template v-else-if="card.key === 'copyright'">
              <circle cx="20" cy="20" r="15" stroke="currentColor" stroke-width="1.5"/>
              <path d="M14 16c0-2 2-4 6-4s6 2 6 4-2 4-6 4-6-2-6-4z" stroke="currentColor" stroke-width="1.5"/>
              <path d="M22 24l2 4M18 24l-2 4" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
            </template>
            <template v-else>
              <path d="M10 8h14l6 6v15a2 2 0 01-2 2H10a2 2 0 01-2-2V10a2 2 0 012-2z" stroke="currentColor" stroke-width="1.5"/>
              <path d="M24 8v6h6" stroke="currentColor" stroke-width="1.5"/>
              <line x1="14" y1="18" x2="26" y2="18" stroke="currentColor" stroke-width="1.2"/>
              <line x1="14" y1="22" x2="22" y2="22" stroke="currentColor" stroke-width="1.2"/>
            </template>
          </svg>
        </div>
        <div class="kpi-card__info">
          <div class="kpi-card__value">
            <span v-if="!loading" class="count-up">{{ card.value }}</span>
            <span v-else class="skeleton-text">--</span>
          </div>
          <div class="kpi-card__label">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <!-- Reviewer Work Panel (Secretary / Leader) -->
    <div v-if="isReviewer && !loading" class="reviewer-panel">
      <div class="reviewer-card">
        <div class="reviewer-card__left">
          <div class="reviewer-card__icon">
            <svg viewBox="0 0 40 40" fill="none">
              <rect x="4" y="6" width="32" height="28" rx="3" stroke="currentColor" stroke-width="1.5"/>
              <line x1="12" y1="14" x2="28" y2="14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="12" y1="19" x2="24" y2="19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="12" y1="24" x2="20" y2="24" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              <circle cx="30" cy="24" r="5" fill="#ef4444" stroke="#fff" stroke-width="1.2"/>
              <text x="30" y="26.5" text-anchor="middle" fill="#fff" font-size="7" font-weight="600">{{ pendingReviewCount }}{{ leaderPendingCount }}</text>
            </svg>
          </div>
          <div class="reviewer-card__info">
            <h4>{{ reviewerTitle }}</h4>
            <p>{{ reviewerDesc }}</p>
          </div>
        </div>
        <div class="reviewer-card__right">
          <div class="reviewer-stats">
            <div class="reviewer-stat-item">
              <span class="reviewer-stat__num urgent">{{ pendingReviewCount || leaderPendingCount }}</span>
              <span class="reviewer-stat__lbl">待处理</span>
            </div>
            <div class="reviewer-stat-item" v-if="isSecretary">
              <span class="reviewer-stat__num">{{ (overview?.underReviewCount) || 0 }}</span>
              <span class="reviewer-stat__lbl">审核中</span>
            </div>
            <div class="reviewer-stat-item" v-if="isLeader">
              <span class="reviewer-stat__num">{{ (overview?.totalCompetitions || 0) + (overview?.totalInnovations || 0) + (overview?.totalCopyrights || 0) + (overview?.totalPapers || 0) }}</span>
              <span class="reviewer-stat__lbl">成果总数</span>
            </div>
          </div>
          <el-button type="primary" size="large" @click="$router.push('/review')">
            进入审核管理
            <el-icon style="margin-left:4px"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Student / New User Guidance -->
    <div v-if="isStudent && !loading && totalAchievements === 0" class="student-guide">
      <div class="guide-card">
        <div class="guide-icon">📝</div>
        <div class="guide-content">
          <h4>欢迎使用科研竞赛管理系统！</h4>
          <p>您还没有提交任何科研成果。请点击下方按钮开始提交您的学科竞赛、大创项目、软件著作权或学术论文成果。</p>
          <div class="guide-actions">
            <el-button type="primary" @click="$router.push('/competition/create')">提交竞赛成果</el-button>
            <el-button type="success" @click="$router.push('/innovation/create')">提交大创项目</el-button>
            <el-button type="warning" @click="$router.push('/copyright/create')">提交软件著作权</el-button>
            <el-button type="info" @click="$router.push('/paper/create')">提交学术论文</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- ======== CHARTS ROW 1: Trend + Announcements ======== -->
    <div class="chart-row">
      <div class="chart-panel chart-panel--wide">
        <div class="panel-header">
          <h3 class="panel-title">📈 成果趋势分析</h3>
          <el-radio-group v-model="trendYear" size="small" @change="fetchTrendData">
            <el-radio-button v-for="y in yearOptions" :key="y" :value="y">{{ y }}年</el-radio-button>
          </el-radio-group>
        </div>
        <div class="panel-body chart-container" v-loading="trendLoading">
          <v-chart v-if="!trendLoading" :option="trendOption" autoresize />
          <div v-else class="chart-placeholder">加载中...</div>
        </div>
      </div>

      <div class="chart-panel chart-panel--narrow">
        <div class="panel-header">
          <h3 class="panel-title">📢 公告栏</h3>
          <el-button v-if="isAdmin" link type="primary" size="small" @click="$router.push('/system/announcement')">
            管理
          </el-button>
        </div>
        <div class="panel-body announce-body">
          <div v-if="announcements.length === 0" class="empty-state">暂无公告</div>
          <div v-else class="announce-list">
            <div
              v-for="item in announcements"
              :key="item.id"
              class="announce-item"
              @click="$router.push(`/announcement/${item.id}`)"
            >
              <div class="announce-item__top">
                <el-tag v-if="item.isTop" size="small" type="warning" effect="dark">置顶</el-tag>
                <span class="announce-item__title">{{ item.title }}</span>
              </div>
              <div class="announce-item__meta">
                <span>{{ item.publishTime }}</span>
                <span>·</span>
                <span>{{ item.publisher }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ======== CHARTS ROW 2: Pie Charts ======== -->
    <div class="chart-row">
      <div class="chart-panel">
        <div class="panel-header">
          <h3 class="panel-title">🏆 获奖等级分布</h3>
        </div>
        <div class="panel-body chart-container" v-loading="loading">
          <v-chart v-if="!loading" :option="gradePieOption" autoresize />
        </div>
      </div>
      <div class="chart-panel">
        <div class="panel-header">
          <h3 class="panel-title">📊 竞赛类别统计</h3>
        </div>
        <div class="panel-body chart-container" v-loading="loading">
          <v-chart v-if="!loading" :option="categoryBarOption" autoresize />
        </div>
      </div>
      <div class="chart-panel">
        <div class="panel-header">
          <h3 class="panel-title">🎯 成果类型分布</h3>
        </div>
        <div class="panel-body chart-container" v-loading="loading">
          <v-chart v-if="!loading" :option="achievementPieOption" autoresize />
        </div>
      </div>
    </div>

    <!-- ======== BOTTOM ROW: Latest Submissions + Review Progress ======== -->
    <div class="chart-row">
      <div class="chart-panel chart-panel--wide">
        <div class="panel-header">
          <h3 class="panel-title">📋 最新提交</h3>
          <el-button link type="primary" size="small" @click="$router.push('/competition')">查看全部</el-button>
        </div>
        <div class="panel-body table-body">
          <el-table
            :data="recentSubmissions"
            v-loading="loading"
            stripe
            size="small"
            style="width:100%"
            empty-text="暂无数据"
          >
            <el-table-column label="类型" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="typeTagMap[row.type]" size="small" effect="light">
                  {{ typeLabelMap[row.type] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="名称" min-width="160" show-overflow-tooltip />
            <el-table-column label="提交人" width="90" align="center">
              <template #default="{ row }">{{ row.submitter }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="statusTagMap[row.status]" size="small" effect="light">
                  {{ statusLabelMap[row.status] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="time" label="提交时间" width="110" align="center" />
          </el-table>
        </div>
      </div>

      <div class="chart-panel chart-panel--narrow">
        <div class="panel-header">
          <h3 class="panel-title">⏳ 审核进度</h3>
        </div>
        <div class="panel-body review-body">
          <div v-loading="loading">
            <div v-for="item in reviewStats" :key="item.label" class="review-stat">
              <div class="review-stat__header">
                <span class="review-stat__label">{{ item.label }}</span>
                <span class="review-stat__count">{{ item.count }}</span>
              </div>
              <div class="review-stat__bar">
                <div
                  class="review-stat__fill"
                  :style="{
                    width: item.percent + '%',
                    background: item.color
                  }"
                ></div>
              </div>
            </div>
            <div v-if="reviewStats.length === 0 && !loading" class="empty-state">暂无审核数据</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import 'echarts'
import { getOverview } from '../../api/statistics'
import { getCompetitionPage } from '../../api/competition'
import { getInnovationPage } from '../../api/innovation'
import { getCopyrightPage } from '../../api/copyright'
import { getPaperPage } from '../../api/paper'
import { getAnnouncementPage } from '../../api/announcement'
import { getPersonalOverview, getPersonalAchievements } from '../../api/personal'

// ── State ──
const loading = ref(false)
const trendLoading = ref(false)
const overview = ref(null)
const announcements = ref([])
const trendYear = ref(new Date().getFullYear())
const recentSubmissions = ref([])
const reviewStats = ref([])
let refreshTimer = null

const yearOptions = computed(() => {
  const current = new Date().getFullYear()
  return [current - 2, current - 1, current]
})

const userInfo = computed(() => {
  try { return JSON.parse(localStorage.getItem('userInfo') || '{}') } catch { return {} }
})
const isAdmin = computed(() => userInfo.value.role === 'ADMIN')
const isSecretary = computed(() => userInfo.value.role === 'SECRETARY')
const isLeader = computed(() => userInfo.value.role === 'LEADER')
const isStudent = computed(() => userInfo.value.role === 'STUDENT')
const isReviewer = computed(() => ['SECRETARY', 'LEADER'].includes(userInfo.value.role))
// Admin, Secretary, Leader see all data; Student sees only their own
const isManager = computed(() => ['ADMIN', 'SECRETARY', 'LEADER'].includes(userInfo.value.role))

// ── Reviewer panel ──
const pendingReviewCount = computed(() => overview.value?.pendingCount || 0)
const leaderPendingCount = computed(() => overview.value?.underReviewCount || 0)
const reviewerTitle = computed(() => {
  if (isSecretary.value) return '科研秘书 — 待审核成果'
  if (isLeader.value) return '学院领导 — 待终审成果'
  return ''
})
const reviewerDesc = computed(() => {
  const count = isSecretary.value ? pendingReviewCount.value : leaderPendingCount.value
  if (count > 0) return `当前有 ${count} 项成果等待您的审核处理`
  return '暂无待审核成果，辛苦了！'
})

// Pending review breakdown by type
const pendingBreakdown = computed(() => {
  // Compute from overview data or recent submissions
  // For secretary: count pending_review, for leader: count under_review
  const targetStatus = isSecretary.value ? 'pending_review' : 'under_review'
  const items = [
    { type: 'competition', label: '学科竞赛', bg: '#f5f3ff', icon: '<circle cx="12" cy="12" r="9" stroke="#7c3aed" stroke-width="1.2" fill="none"/><path d="M8 16l3-6 2 4 3-7" stroke="#7c3aed" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>' },
    { type: 'innovation',  label: '大创项目', bg: '#ecfdf5', icon: '<rect x="4" y="5" width="7" height="6" rx="1" stroke="#10b981" stroke-width="1.2"/><rect x="13" y="8" width="7" height="6" rx="1" stroke="#10b981" stroke-width="1.2"/><rect x="8" y="13" width="8" height="6" rx="1" stroke="#10b981" stroke-width="1.2"/>' },
    { type: 'copyright',   label: '软件著作权', bg: '#fffbeb', icon: '<circle cx="12" cy="12" r="9" stroke="#f59e0b" stroke-width="1.2"/><path d="M9 10c0-1.5 1-3 3-3s3 1.5 3 3-1 3-3 3-3-1.5-3-3z" stroke="#f59e0b" stroke-width="1.2"/>' },
    { type: 'paper',       label: '学术论文', bg: '#eef2ff', icon: '<path d="M7 5h7l4 4v8a1 1 0 01-1 1H7a1 1 0 01-1-1V6a1 1 0 011-1z" stroke="#6366f1" stroke-width="1.2"/><path d="M14 5v4h4" stroke="#6366f1" stroke-width="1.2"/>' }
  ]
  return items.map(item => ({
    ...item,
    count: 0  // Will be populated by real data
  }))
})
// Total achievements for empty state check
const totalAchievements = computed(() => {
  const d = overview.value || {}
  return (d.totalCompetitions || 0) + (d.totalInnovations || 0) +
         (d.totalCopyrights || 0) + (d.totalPapers || 0)
})

// ── KPI Cards ──
const kpiCards = computed(() => {
  const d = overview.value || {}
  return [
    { key: 'competition', label: '学科竞赛', value: d.totalCompetitions ?? 0, color: '#7c3aed' },
    { key: 'innovation',  label: '大创项目', value: d.totalInnovations ?? 0,  color: '#10b981' },
    { key: 'copyright',   label: '软件著作权', value: d.totalCopyrights ?? 0,   color: '#f59e0b' },
    { key: 'paper',       label: '学术论文', value: d.totalPapers ?? 0,       color: '#6366f1' }
  ]
})

// ── Label maps ──
const typeTagMap = {
  competition: 'primary', innovation: 'success', copyright: 'warning', paper: 'info'
}
const typeLabelMap = {
  competition: '竞赛', innovation: '大创', copyright: '软著', paper: '论文'
}
const statusTagMap = {
  pending_review: 'warning', under_review: 'primary', returned: 'danger', archived: 'success', draft: 'info'
}
const statusLabelMap = {
  pending_review: '待审核', under_review: '审核中', returned: '已退回', archived: '已归档', draft: '草稿'
}

// ── Trend Line Chart ──
const trendOption = computed(() => {
  const d = overview.value || {}
  const months = ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
  const empty = months.map(() => 0)

  // Use real data if available; otherwise show empty (student personal data has no monthly aggregation)
  const hasTrendData = d.monthlyCompetition || d.monthlyPaper || d.monthlyCopyright
  const competitionData = d.monthlyCompetition || (hasTrendData ? empty : null)
  const paperData = d.monthlyPaper || (hasTrendData ? empty : null)
  const copyrightData = d.monthlyCopyright || (hasTrendData ? empty : null)

  const series = []
  if (competitionData) {
    series.push({
      name: '竞赛', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
      data: competitionData,
      lineStyle: { color: '#7c3aed', width: 2.5 },
      itemStyle: { color: '#7c3aed' },
      areaStyle: { color: { type: 'linear', x:0,y:0,x2:0,y2:1,
        colorStops: [{offset:0,color:'rgba(124,58,237,.15)'},{offset:1,color:'rgba(124,58,237,0)'}] }}
    })
  }
  if (paperData) {
    series.push({
      name: '论文', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
      data: paperData,
      lineStyle: { color: '#6366f1', width: 2.5 },
      itemStyle: { color: '#6366f1' },
      areaStyle: { color: { type: 'linear', x:0,y:0,x2:0,y2:1,
        colorStops: [{offset:0,color:'rgba(99,102,241,.15)'},{offset:1,color:'rgba(99,102,241,0)'}] }}
    })
  }
  if (copyrightData) {
    series.push({
      name: '软著', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
      data: copyrightData,
      lineStyle: { color: '#f59e0b', width: 2.5 },
      itemStyle: { color: '#f59e0b' },
      areaStyle: { color: { type: 'linear', x:0,y:0,x2:0,y2:1,
        colorStops: [{offset:0,color:'rgba(245,158,11,.15)'},{offset:1,color:'rgba(245,158,11,0)'}] }}
    })
  }

  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#fff',
      borderColor: '#e5e7eb',
      textStyle: { color: '#1f2937', fontSize: 13 },
      boxShadow: '0 4px 12px rgba(0,0,0,.08)'
    },
    legend: {
      bottom: 0,
      textStyle: { fontSize: 12 },
      itemWidth: 12, itemHeight: 8, itemGap: 20
    },
    grid: { left: '3%', right: '4%', top: '6%', bottom: '14%' },
    xAxis: {
      type: 'category',
      data: months,
      boundaryGap: false,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisTick: { show: false },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    series
  }
})

// ── Grade Pie Chart ──
const gradePieOption = computed(() => {
  const data = (overview.value?.competitionByGrade || []).map(d => ({
    name: d.name || '未知', value: d.value
  })).filter(d => d.value > 0)
  const colors = ['#7c3aed', '#a78bfa', '#c4b5fd', '#6366f1', '#10b981']

  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, textStyle: { fontSize: 11 }, itemWidth: 10, itemHeight: 10, itemGap: 12 },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '45%'],
      label: { show: true, formatter: '{b}\n{d}%', fontSize: 11, lineHeight: 16 },
      data,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      color: colors
    }]
  }
})

// ── Category Bar Chart ──
const categoryBarOption = computed(() => {
  const data = overview.value?.competitionByCategory || []
  const colors = { 'A': '#7c3aed', 'B': '#6366f1', 'C': '#a78bfa' }
  const names = data.map(d => d.name || '未知')
  const values = data.map(d => d.value)

  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '2%', right: '5%', bottom: '5%', top: '18%', containLabel: true },
    xAxis: {
      type: 'category', data: names,
      axisLabel: { fontSize: 12, color: '#374151', interval: 0 },
      axisLine: { lineStyle: { color: '#e5e7eb' } }
    },
    yAxis: {
      type: 'value', minInterval: 1,
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } }
    },
    series: [{
      type: 'bar', barWidth: '45%',
      data: values.map((v, i) => ({
        value: v,
        itemStyle: {
          color: colors[names[i]] || '#7c3aed',
          borderRadius: [6, 6, 0, 0]
        }
      })),
      label: { show: true, position: 'top', fontWeight: 600, fontSize: 12, color: '#374151', distance: 6 }
    }]
  }
})

// ── Achievement Type Pie Chart ──
const achievementPieOption = computed(() => {
  const d = overview.value || {}
  const data = [
    { name: '学科竞赛', value: d.totalCompetitions || 0 },
    { name: '大创项目', value: d.totalInnovations || 0 },
    { name: '软件著作权', value: d.totalCopyrights || 0 },
    { name: '学术论文', value: d.totalPapers || 0 }
  ].filter(item => item.value > 0)
  const colors = ['#7c3aed', '#10b981', '#f59e0b', '#6366f1']

  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, textStyle: { fontSize: 11 }, itemWidth: 10, itemHeight: 10, itemGap: 12 },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '45%'],
      label: { show: true, formatter: '{b}\n{d}%', fontSize: 11, lineHeight: 16 },
      data: data.map((item, i) => ({ ...item, itemStyle: { color: colors[i % colors.length] } })),
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 }
    }]
  }
})

// ── Data Fetching ──

/**
 * Fetch overview statistics.
 * - Managers (Admin/Secretary/Leader): system-wide overview via /statistics/overview
 * - Students: personal overview via /personal/overview, then transform to unified format
 */
const fetchOverview = async () => {
  loading.value = true
  try {
    const res = isManager.value
      ? await getOverview()
      : await getPersonalOverview()
    const raw = res.data || {}

    // Transform personal overview data to match system overview format
    if (isManager.value) {
      overview.value = raw // system format already matches
    } else {
      overview.value = transformPersonalOverview(raw)
    }
  } catch {
    overview.value = null
  } finally {
    loading.value = false
  }
}

/**
 * Convert personal overview response to the same shape as system overview.
 * The backend now returns flat keys (totalCompetitions, competitionByCategory, etc.)
 * alongside the nested detail structure, so mostly we pass through directly.
 */
const transformPersonalOverview = (data) => {
  // Backend now includes all chart-ready fields; add safe defaults for any missing ones
  return {
    totalCompetitions: data.totalCompetitions || 0,
    totalInnovations: data.totalInnovations || 0,
    totalCopyrights: data.totalCopyrights || 0,
    totalPapers: data.totalPapers || 0,
    archivedCount: data.archivedCount || 0,
    underReviewCount: data.underReviewCount || 0,
    pendingCount: data.pendingCount || 0,
    returnedCount: data.returnedCount || 0,
    competitionByCategory: data.competitionByCategory || [],
    competitionByGrade: data.competitionByGrade || [],
    competitionByLevel: data.competitionByLevel || [],
    monthlyCompetition: data.monthlyCompetition || [],
    monthlyPaper: data.monthlyPaper || [],
    monthlyCopyright: data.monthlyCopyright || [],
    monthlyTrend: data.monthlyTrend || [],
    achievementDistribution: data.achievementDistribution || []
  }
}

const fetchTrendData = async () => {
  trendLoading.value = true
  try {
    await fetchOverview()
  } finally {
    trendLoading.value = false
  }
}

/**
 * Fetch latest submissions.
 * - Managers: all submissions across all 4 types (system-wide)
 * - Students: only their own recent achievements via /personal/achievements
 */
const fetchRecentSubmissions = async () => {
  try {
    if (isManager.value) {
      // System-wide: fetch all 4 types in parallel
      const [compRes, innoRes, copyRes, paperRes] = await Promise.allSettled([
        getCompetitionPage({ page: 1, size: 3, sort: 'create_time,desc' }),
        getInnovationPage({ page: 1, size: 3, sort: 'create_time,desc' }),
        getCopyrightPage({ page: 1, size: 3, sort: 'create_time,desc' }),
        getPaperPage({ page: 1, size: 3, sort: 'create_time,desc' })
      ])

      const items = []
      const addItems = (res, type) => {
        if (res.status === 'fulfilled' && res.value?.data?.records) {
          res.value.data.records.forEach(r => {
            items.push({
              type,
              name: r.competitionName || r.projectName || r.softwareName || r.title || '-',
              submitter: r.submitter?.realName || r.submitter?.username || r.studentName || '-',
              status: r.status || 'draft',
              time: r.createTime || r.awardTime || r.registrationTime || '-'
            })
          })
        }
      }
      addItems(compRes, 'competition')
      addItems(innoRes, 'innovation')
      addItems(copyRes, 'copyright')
      addItems(paperRes, 'paper')

      items.sort((a, b) => (b.time || '').localeCompare(a.time || ''))
      recentSubmissions.value = items.slice(0, 8)
    } else {
      // Student: use personal achievements API (returns only their own)
      const res = await getPersonalAchievements({ page: 1, size: 8 })
      const records = res.data?.records || []
      recentSubmissions.value = records.map(r => ({
        type: r.type || 'competition',
        name: r.title || '-',
        submitter: userInfo.value.realName || userInfo.value.username || '-',
        status: r.status || 'draft',
        time: r.createTime || r.time || '-'
      }))
    }
  } catch {
    recentSubmissions.value = []
  }
}

/**
 * Compute review progress stats from overview data.
 * Works for both system-wide overview and personal overview.
 */
const fetchReviewStats = () => {
  const d = overview.value || {}
  // For personal overview, the API may return per-status counts under different keys
  const total = (d.totalCompetitions || 0) + (d.totalInnovations || 0) +
                (d.totalCopyrights || 0) + (d.totalPapers || 0) || 1
  const archived = d.archivedCount ?? d.archived ?? 0
  const reviewing = d.underReviewCount ?? d.reviewing ?? 0
  const pending = d.pendingCount ?? d.pending ?? 0
  const returned = d.returnedCount ?? d.returned ?? 0

  reviewStats.value = [
    { label: '已归档', count: archived, percent: Math.round(archived / total * 100), color: 'linear-gradient(90deg, #10b981, #34d399)' },
    { label: '审核中', count: reviewing, percent: Math.round(reviewing / total * 100), color: 'linear-gradient(90deg, #7c3aed, #a78bfa)' },
    { label: '待审核', count: pending, percent: Math.round(pending / total * 100), color: 'linear-gradient(90deg, #f59e0b, #fbbf24)' },
    { label: '已退回', count: returned, percent: Math.round(returned / total * 100), color: 'linear-gradient(90deg, #ef4444, #f87171)' }
  ].filter(item => item.count >= 0)
}

const fetchAnnouncements = async () => {
  try {
    const res = await getAnnouncementPage({ page: 1, size: 10 })
    announcements.value = res.data?.records || []
  } catch { /* ignore */ }
}

/** Refresh all dashboard data (called on mount and on timer) */
const refreshAll = async () => {
  await Promise.all([
    fetchOverview(),
    fetchRecentSubmissions()
  ])
  fetchReviewStats()
}

onMounted(async () => {
  await refreshAll()
  await fetchAnnouncements()
  // Auto-refresh every 30 seconds for real-time data sync
  refreshTimer = setInterval(refreshAll, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1440px;
  margin: 0 auto;
}

/* ======== REVIEWER WORK PANEL ======== */
.reviewer-panel {
  margin-bottom: 20px;
}
.reviewer-card {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 1px solid #fcd34d;
  border-radius: var(--radius-lg);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}
.reviewer-card__left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.reviewer-card__icon {
  width: 48px; height: 48px;
  flex-shrink: 0;
  color: #d97706;
  background: #fffbeb;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
}
.reviewer-card__icon svg {
  width: 36px; height: 36px;
}
.reviewer-card__info h4 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  color: #92400e;
}
.reviewer-card__info p {
  margin: 0;
  font-size: 13px;
  color: #a16207;
}
.reviewer-card__right {
  display: flex;
  align-items: center;
  gap: 24px;
}
.reviewer-stats {
  display: flex;
  gap: 20px;
}
.reviewer-stat-item {
  text-align: center;
}
.reviewer-stat__num {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #92400e;
  line-height: 1.2;
}
.reviewer-stat__num.urgent {
  color: #dc2626;
  animation: pulse-num 1.5s ease-in-out infinite;
}
@keyframes pulse-num {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}
.reviewer-stat__lbl {
  font-size: 11px;
  color: #a16207;
}

/* ======== PENDING BREAKDOWN ======== */
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px;
}
.pending-breakdown {
  margin-bottom: 20px;
}
.pending-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.pending-item {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: box-shadow 0.2s;
}
.pending-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,.06);
}
.pending-item__icon {
  width: 40px; height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.pending-item__icon svg {
  width: 24px; height: 24px;
}
.pending-item__info {
  display: flex;
  flex-direction: column;
}
.pending-item__count {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
}
.pending-item__label {
  font-size: 12px;
  color: #6b7280;
}

@media (max-width: 768px) {
  .pending-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* ======== STUDENT GUIDE ======== */
.student-guide {
  margin-bottom: 20px;
}
.guide-card {
  background: linear-gradient(135deg, #f5f3ff 0%, #eef2ff 100%);
  border: 1px solid #c4b5fd;
  border-radius: var(--radius-lg);
  padding: 24px 28px;
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.guide-icon {
  font-size: 36px;
  flex-shrink: 0;
}
.guide-content h4 {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}
.guide-content p {
  margin: 0 0 16px;
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}
.guide-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* ======== KPI CARDS ======== */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.kpi-card {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--card-shadow);
  transition: transform var(--transition-fast), box-shadow var(--transition-fast);
  cursor: default;
  border: 1px solid var(--color-border-light);
  position: relative;
  overflow: hidden;
}
.kpi-card::after {
  content: '';
  position: absolute;
  top: 0; right: 0;
  width: 80px; height: 80px;
  border-radius: 0 0 0 80px;
  opacity: 0.06;
  transition: opacity var(--transition-base);
}
.kpi-card--blue::after  { background: #7c3aed; }
.kpi-card--green::after { background: #10b981; }
.kpi-card--amber::after { background: #f59e0b; }
.kpi-card--indigo::after { background: #6366f1; }
.kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}
.kpi-card:hover::after {
  opacity: 0.1;
}
.kpi-card__icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.kpi-card--blue .kpi-card__icon   { color: #7c3aed; background: #f5f3ff; }
.kpi-card--green .kpi-card__icon  { color: #10b981; background: #ecfdf5; }
.kpi-card--amber .kpi-card__icon  { color: #f59e0b; background: #fffbeb; }
.kpi-card--indigo .kpi-card__icon { color: #6366f1; background: #eef2ff; }
.kpi-card__icon svg {
  width: 36px; height: 36px;
}
.kpi-card__info {
  flex: 1; min-width: 0;
}
.kpi-card__value {
  font-size: var(--font-size-3xl);
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.1;
}
.kpi-card__label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: 4px;
}
.skeleton-text {
  color: #d1d5db;
}

/* ======== CHART ROW ======== */
.chart-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.chart-panel {
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--card-shadow);
  border: 1px solid var(--color-border-light);
  overflow: hidden;
}
.chart-panel--wide {
  grid-column: span 2;
}
.chart-panel--narrow {
  grid-column: span 1;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
}
.panel-title {
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}
.panel-body {
  padding: 12px 16px;
}
.chart-container {
  height: 360px;
  min-height: 320px;
}
.chart-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 14px;
}

/* ── Announce Panel ── */
.announce-body {
  padding: 0;
  max-height: 296px;
  overflow-y: auto;
}
.announce-list {
  padding: 0;
}
.announce-item {
  padding: 14px 20px;
  border-bottom: 1px solid #f9fafb;
  cursor: pointer;
  transition: background var(--transition-fast);
}
.announce-item:hover {
  background: #fafafa;
}
.announce-item:last-child {
  border-bottom: none;
}
.announce-item__top {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}
.announce-item__title {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.announce-item__meta {
  font-size: 11px;
  color: #9ca3af;
  display: flex;
  gap: 6px;
}

/* ── Table Panel ── */
.table-body {
  padding: 0;
}
.table-body :deep(.el-table) {
  border-radius: 0;
}
.table-body :deep(.el-table th.el-table__cell) {
  background: #fafbfc;
  color: #6b7280;
  font-weight: 500;
  font-size: 12px;
}

/* ── Review Panel ── */
.review-body {
  max-height: 300px;
  overflow-y: auto;
}
.review-stat {
  margin-bottom: 16px;
}
.review-stat:last-child {
  margin-bottom: 0;
}
.review-stat__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.review-stat__label {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-primary);
}
.review-stat__count {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-regular);
}
.review-stat__bar {
  height: 8px;
  background: #f3f4f6;
  border-radius: 4px;
  overflow: hidden;
}
.review-stat__fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 2px;
}

/* ── Empty State ── */
.empty-state {
  text-align: center;
  color: #9ca3af;
  padding: 32px 0;
  font-size: 13px;
}

/* ── Responsive ── */
@media (max-width: 1400px) {
  .chart-container { height: 340px; min-height: 300px; }
}
@media (max-width: 1200px) {
  .kpi-grid { grid-template-columns: repeat(2, 1fr); }
  .chart-row { grid-template-columns: repeat(2, 1fr); }
  .chart-panel--wide { grid-column: span 2; }
  .chart-panel--narrow { grid-column: span 1; }
  .chart-container { height: 360px; }
}
@media (max-width: 768px) {
  .kpi-grid { grid-template-columns: 1fr; }
  .chart-row { grid-template-columns: 1fr; }
  .chart-panel--wide { grid-column: span 1; }
  .chart-panel--narrow { grid-column: span 1; }
  .chart-container { height: 340px; min-height: 280px; }
}
</style>
