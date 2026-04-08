<template>
  <div class="alerts">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>告警管理</span>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="告警状态">
          <el-select v-model="queryForm.isResolved" placeholder="请选择" clearable>
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 告警表格 -->
      <el-table :data="alertList" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="alertType" label="告警类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.alertType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertLevel" label="告警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getAlertType(row.alertLevel)" size="small">
              {{ getAlertLevelLabel(row.alertLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertMessage" label="告警消息" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="isResolved" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isResolved === 1 ? 'success' : 'danger'" size="small">
              {{ row.isResolved === 1 ? '已解决' : '未解决' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.isResolved === 0"
              link
              type="primary"
              size="small"
              @click="handleResolve(row)"
            >
              处理
            </el-button>
            <el-button link type="info" size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 处理告警对话框 -->
    <el-dialog v-model="resolveVisible" title="处理告警" width="400px">
      <el-form :model="resolveForm" label-width="80px">
        <el-form-item label="处理备注">
          <el-input
            v-model="resolveForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResolve">确定</el-button>
      </template>
    </el-dialog>

    <!-- 告警详情对话框 -->
    <el-dialog v-model="detailVisible" title="告警详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="告警类型">{{ currentAlert.alertType }}</el-descriptions-item>
        <el-descriptions-item label="告警级别">
          <el-tag :type="getAlertType(currentAlert.alertLevel)">{{ getAlertLevelLabel(currentAlert.alertLevel) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="告警消息">{{ currentAlert.alertMessage }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentAlert.createTime }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentAlert.resolveBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ currentAlert.resolveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理备注">{{ currentAlert.resolveComment || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const alertList = ref([])
const total = ref(0)

const queryForm = reactive({
  isResolved: '',
  pageNum: 1,
  pageSize: 10
})

const resolveVisible = ref(false)
const resolveForm = reactive({
  id: null,
  comment: ''
})

const detailVisible = ref(false)
const currentAlert = ref({})

const getAlertType = (level) => {
  const map = {
    critical: 'danger',
    CRITICAL: 'danger',
    high: 'danger',
    HIGH: 'danger',
    medium: 'warning',
    MEDIUM: 'warning',
    low: 'info',
    LOW: 'info'
  }
  return map[level] || 'info'
}

const getAlertLevelLabel = (level) => {
  const map = {
    critical: '严重',
    CRITICAL: '严重',
    high: '高',
    HIGH: '高',
    medium: '中',
    MEDIUM: '中',
    low: '低',
    LOW: '低'
  }
  return map[level] || level || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await request.get('/alerts', { params: queryForm })
    alertList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('加载告警失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryForm.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryForm.isResolved = ''
  queryForm.pageNum = 1
  loadData()
}

const handleResolve = (row) => {
  resolveForm.id = row.id
  resolveForm.comment = ''
  resolveVisible.value = true
}

const submitResolve = async () => {
  try {
    await request.post(`/alerts/${resolveForm.id}/resolve`, {
      comment: resolveForm.comment
    })
    ElMessage.success('处理成功')
    resolveVisible.value = false
    loadData()
  } catch (error) {
    console.error('处理失败:', error)
  }
}

const handleViewDetail = (row) => {
  currentAlert.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.alerts {
  padding: 20px;
}

.card-header {
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
