<template>
  <div class="ccf-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>CCF推荐国际学术会议与期刊目录 (2022)</span>
        </div>
      </template>

      <el-form :model="query" :inline="true">
        <el-form-item label="类型">
          <el-select v-model="query.venueType" placeholder="全部" clearable style="width:120px" @change="handleSearch">
            <el-option label="期刊" value="journal" />
            <el-option label="会议" value="conference" />
          </el-select>
        </el-form-item>
        <el-form-item label="研究方向">
          <el-select v-model="query.area" placeholder="全部" clearable style="width:240px" @change="handleSearch">
            <el-option v-for="a in areas" :key="a" :label="a" :value="a" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="query.level" placeholder="全部" clearable style="width:100px" @change="handleSearch">
            <el-option label="A类" value="A" />
            <el-option label="B类" value="B" />
            <el-option label="C类" value="C" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="query.keyword" placeholder="搜索简称或全称" clearable style="width:260px"
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="venueType" label="类型" width="70">
          <template #default="{ row }">
            <el-tag :type="row.venueType === 'journal' ? 'success' : 'warning'" size="small">
              {{ row.venueType === 'journal' ? '期刊' : '会议' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="70">
          <template #default="{ row }">
            <el-tag :type="levelType(row.level)" size="small">CCF-{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="abbreviation" label="简称" width="150" show-overflow-tooltip />
        <el-table-column prop="fullName" label="全称" min-width="280" show-overflow-tooltip />
        <el-table-column prop="area" label="研究方向" width="200" show-overflow-tooltip />
        <el-table-column prop="publisher" label="出版社" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.url" link type="primary" size="small"
              @click="window.open(row.url, '_blank')">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:16px; text-align:right">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSearch"
          @current-change="handleSearch" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCcfPage, getCcfAreas } from '../../api/ccf'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const areas = ref([])

const query = reactive({
  page: 1,
  size: 20,
  venueType: '',
  area: '',
  level: '',
  keyword: ''
})

const levelType = (level) => {
  return { A: 'danger', B: 'warning', C: 'info' }[level] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCcfPage(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.page = 1
  fetchData()
}

const handleReset = () => {
  query.venueType = ''
  query.area = ''
  query.level = ''
  query.keyword = ''
  handleSearch()
}

onMounted(async () => {
  fetchData()
  const res = await getCcfAreas()
  areas.value = res.data
})
</script>

<style scoped>
.ccf-container {
  padding: 0;
}
.card-header {
  font-size: 16px;
  font-weight: 600;
}
</style>
