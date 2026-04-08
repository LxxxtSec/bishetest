<template>
  <div class="audit-logs">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审计日志查询</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleExportExcel">导出Excel</el-button>
            <el-button type="success" @click="handleVerifyIntegrity">验证完整性</el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="操作用户">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryForm.operationType" placeholder="请选择" clearable>
            <el-option label="用户登录" value="LOGIN" />
            <el-option label="用户登出" value="LOGOUT" />
            <el-option label="用户管理" value="USER" />
            <el-option label="查询操作" value="QUERY" />
            <el-option label="数据导出" value="EXPORT" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作对象">
          <el-input v-model="queryForm.operationObject" placeholder="请输入操作对象" clearable />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 日志表格 -->
      <el-table :data="logList" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="username" label="操作用户" width="100" />
        <el-table-column prop="operationType" label="操作类型" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="getOperationTagType(row.operationType)">
              {{ getOperationTypeLabel(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationDesc" label="操作描述" min-width="150" />
        <el-table-column prop="operationObject" label="操作对象" width="120" />
        <el-table-column prop="ipAddress" label="IP地址" width="120" />
        <el-table-column prop="requestMethod" label="请求方法" width="80" />
        <el-table-column prop="responseStatus" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.responseStatus === 200 ? 'success' : 'danger'" size="small">
              {{ row.responseStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="logTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">
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

    <!-- 日志详情对话框 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作用户">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ getOperationTypeLabel(currentLog.operationType) }}</el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">{{ currentLog.operationDesc }}</el-descriptions-item>
        <el-descriptions-item label="操作对象">{{ currentLog.operationObject }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentLog.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ currentLog.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre class="params-content">{{ currentLog.requestParams }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应状态">{{ currentLog.responseStatus }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.logTime }}</el-descriptions-item>
        <el-descriptions-item label="哈希值" :span="2">
          <el-tooltip :content="currentLog.hashValue" placement="top">
            <span class="hash-value">{{ currentLog.hashValue }}</span>
          </el-tooltip>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 完整性验证结果 -->
    <el-dialog v-model="verifyVisible" title="完整性验证" width="400px">
      <div class="verify-result">
        <el-result
          :icon="verifyResult.valid ? 'success' : 'error'"
          :title="verifyResult.valid ? '验证通过' : '验证失败'"
          :sub-title="verifyResult.message"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { getOperationTagType, getOperationTypeLabel } from '@/utils/operationType'

const loading = ref(false)
const logList = ref([])
const total = ref(0)
const dateRange = ref([])

const queryForm = reactive({
  username: '',
  operationType: '',
  operationObject: '',
  startTime: '',
  endTime: '',
  pageNum: 1,
  pageSize: 10
})

const detailVisible = ref(false)
const currentLog = ref({})
const verifyVisible = ref(false)
const verifyResult = ref({})

const loadData = async () => {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      queryForm.startTime = dateRange.value[0]
      queryForm.endTime = dateRange.value[1]
    } else {
      queryForm.startTime = ''
      queryForm.endTime = ''
    }

    const data = await request.get('/audit-logs', { params: queryForm })
    logList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('加载日志失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryForm.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryForm.username = ''
  queryForm.operationType = ''
  queryForm.operationObject = ''
  dateRange.value = []
  queryForm.pageNum = 1
  loadData()
}

const handleViewDetail = (row) => {
  currentLog.value = row
  detailVisible.value = true
}

const handleExportExcel = async () => {
  try {
    const response = await request.get('/audit-logs/export/excel', {
      params: queryForm,
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `审计日志_${new Date().toLocaleDateString()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handleVerifyIntegrity = async () => {
  try {
    const data = await request.get('/audit-logs/verify-integrity')
    verifyResult.value = data
    verifyVisible.value = true
  } catch (error) {
    console.error('验证失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.audit-logs {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.params-content {
  margin: 0;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  max-height: 150px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.hash-value {
  display: inline-block;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
  color: #999;
}
</style>
