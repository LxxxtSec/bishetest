<template>
  <div class="dashboard">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon :size="28"><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUavs }}</div>
              <div class="stat-label">无人机总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon :size="28"><VideoPlay /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.onlineUavs }}</div>
              <div class="stat-label">在线无人机</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayLogs }}</div>
              <div class="stat-label">今日操作</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
              <el-icon :size="28"><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingAlerts }}</div>
              <div class="stat-label">待处理告警</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 中间地图和图表区域 -->
    <el-row :gutter="20" class="main-row">
      <!-- 无人机运行地图 -->
      <el-col :span="12">
        <el-card class="map-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><Location /></el-icon> 无人机实时运行地图</span>
              <el-tag type="success" size="small">实时更新</el-tag>
            </div>
          </template>
          <div class="map-container" ref="mapChartRef"></div>
        </el-card>
      </el-col>

      <!-- 右侧数据面板 -->
      <el-col :span="12">
        <el-row :gutter="20">
          <!-- 任务统计 -->
          <el-col :span="24">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span><el-icon><PieChart /></el-icon> 任务类型分布</span>
                </div>
              </template>
              <div class="chart-container" ref="taskChartRef"></div>
            </el-card>
          </el-col>

          <!-- 操作趋势 -->
          <el-col :span="24">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span><el-icon><TrendCharts /></el-icon> 7日操作趋势</span>
                </div>
              </template>
              <div class="chart-container" ref="trendChartRef"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <!-- 底部最近记录 -->
    <el-row :gutter="20" class="bottom-row">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span><el-icon><List /></el-icon> 最近操作记录</span>
            </div>
          </template>
          <el-table :data="recentLogs" style="width: 100%">
            <el-table-column prop="username" label="操作用户" width="100" />
            <el-table-column prop="operationType" label="操作类型" width="120">
              <template #default="{ row }">
                <el-tag size="small" :type="getOperationType(row.operationType)">
                  {{ row.operationType }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="operationDesc" label="操作描述" />
            <el-table-column prop="ipAddress" label="IP地址" width="120" />
            <el-table-column prop="logTime" label="时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span><el-icon><Warning /></el-icon> 告警分布</span>
            </div>
          </template>
          <div class="alert-chart" ref="alertChartRef"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Monitor, VideoPlay, Document, Bell, Location, PieChart, TrendCharts, List, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const mapChartRef = ref(null)
const taskChartRef = ref(null)
const trendChartRef = ref(null)
const alertChartRef = ref(null)

let mapChart = null
let taskChart = null
let trendChart = null
let alertChart = null

const stats = ref({
  totalUavs: 12,
  onlineUavs: 8,
  todayLogs: 156,
  pendingAlerts: 3
})

const recentLogs = ref([])

// 无人机位置数据（模拟）
const uavPositions = [
  { name: 'UAV-001', coord: [116.4074, 39.9042], status: 'online', battery: 85 },
  { name: 'UAV-002', coord: [116.4274, 39.9142], status: 'online', battery: 72 },
  { name: 'UAV-003', coord: [116.3874, 39.8942], status: 'offline', battery: 0 },
  { name: 'UAV-004', coord: [116.4474, 39.9242], status: 'online', battery: 91 },
  { name: 'UAV-005', coord: [116.3974, 39.8842], status: 'online', battery: 65 },
  { name: 'UAV-006', coord: [116.4174, 39.9342], status: 'online', battery: 78 },
  { name: 'UAV-007', coord: [116.3774, 39.9142], status: 'maintenance', battery: 0 },
  { name: 'UAV-008', coord: [116.4574, 39.8942], status: 'online', battery: 88 }
]

const initMapChart = () => {
  if (!mapChartRef.value) return

  mapChart = echarts.init(mapChartRef.value)

  // 模拟地图区域
  const points = uavPositions.map(uav => ({
    name: uav.name,
    value: [...uav.coord, uav.battery],
    itemStyle: {
      color: uav.status === 'online' ? '#67c23a' : uav.status === 'maintenance' ? '#e6a23c' : '#909399'
    }
  }))

  const option = {
    backgroundColor: '#f5f7fa',
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        const uav = uavPositions.find(p => p.name === params.name)
        if (uav) {
          return `<b>${uav.name}</b><br/>
                  状态: ${uav.status === 'online' ? '在线' : uav.status === 'maintenance' ? '维护中' : '离线'}<br/>
                  电量: ${uav.battery}%<br/>
                  位置: ${uav.coord[0].toFixed(4)}, ${uav.coord[1].toFixed(4)}`
        }
        return params.name
      }
    },
    xAxis: {
      type: 'value',
      min: 116.35,
      max: 116.50,
      show: false
    },
    yAxis: {
      type: 'value',
      min: 39.85,
      max: 39.95,
      show: false
    },
    series: [
      {
        type: 'scatter',
        symbolSize: (val) => val[2] / 3,
        data: points,
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{b}',
          fontSize: 10
        }
      },
      {
        // 连接线
        type: 'lines',
        coordinateSystem: 'cartesian2d',
        data: [
          { coords: [uavPositions[0].coord, uavPositions[1].coord] },
          { coords: [uavPositions[1].coord, uavPositions[3].coord] },
          { coords: [uavPositions[3].coord, uavPositions[5].coord] }
        ],
        lineStyle: {
          color: '#409eff',
          width: 1,
          type: 'dashed'
        },
        effect: {
          show: true,
          period: 4,
          trailLength: 0.3,
          color: '#409eff'
        }
      }
    ]
  }

  mapChart.setOption(option)
}

const initTaskChart = () => {
  if (!taskChartRef.value) return

  taskChart = echarts.init(taskChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399'],
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 35, name: '交通巡逻' },
          { value: 28, name: '违章取证' },
          { value: 20, name: '事故勘察' },
          { value: 12, name: '应急指挥' },
          { value: 5, name: '其他任务' }
        ]
      }
    ]
  }

  taskChart.setOption(option)
}

const initTrendChart = () => {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: {
        lineStyle: { color: '#e4e7ed' }
      },
      axisLabel: { color: '#909399' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#909399' },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [
      {
        name: '操作次数',
        type: 'line',
        smooth: true,
        lineStyle: { color: '#409eff', width: 3 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        data: [120, 132, 145, 128, 156, 178, 165]
      },
      {
        name: '告警次数',
        type: 'line',
        smooth: true,
        lineStyle: { color: '#f56c6c', width: 3 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
            { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
          ])
        },
        data: [8, 5, 12, 6, 9, 7, 4]
      }
    ]
  }

  trendChart.setOption(option)
}

const initAlertChart = () => {
  if (!alertChartRef.value) return

  alertChart = echarts.init(alertChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [
      {
        type: 'pie',
        radius: ['50%', '80%'],
        center: ['50%', '50%'],
        data: [
          { value: 5, name: '低', itemStyle: { color: '#909399' } },
          { value: 8, name: '中', itemStyle: { color: '#e6a23c' } },
          { value: 3, name: '高', itemStyle: { color: '#f56c6c' } }
        ],
        label: {
          show: true,
          position: 'outside',
          formatter: '{b}: {c}'
        }
      }
    ]
  }

  alertChart.setOption(option)
}

const loadData = async () => {
  try {
    // 加载告警统计
    const alertStats = await request.get('/alerts/statistics')
    stats.value.pendingAlerts = alertStats.unresolved || 0

    // 加载操作统计
    const logStats = await request.get('/audit-logs/statistics')
    stats.value.todayLogs = logStats.todayCount || 0

    // 加载最近日志
    const logData = await request.get('/audit-logs', {
      params: { pageNum: 1, pageSize: 5 }
    })
    recentLogs.value = logData.records || []
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const getOperationType = (type) => {
  const map = {
    'LOGIN': 'success',
    'LOGOUT': 'info',
    'USER_CREATE': 'primary',
    'USER_UPDATE': 'warning',
    'USER_DELETE': 'danger',
    'QUERY': 'info'
  }
  return map[type] || 'info'
}

const handleResize = () => {
  mapChart?.resize()
  taskChart?.resize()
  trendChart?.resize()
  alertChart?.resize()
}

onMounted(() => {
  loadData()
  initMapChart()
  initTaskChart()
  initTrendChart()
  initAlertChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  mapChart?.dispose()
  taskChart?.dispose()
  trendChart?.dispose()
  alertChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-card:hover {
  transform: translateY(-2px);
  transition: all 0.3s;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 26px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.main-row {
  margin-bottom: 20px;
}

.map-card, .chart-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.map-card .card-header,
.chart-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.map-container {
  height: 360px;
  width: 100%;
}

.chart-container {
  height: 200px;
  width: 100%;
}

.alert-chart {
  height: 200px;
  width: 100%;
}

.bottom-row .el-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
