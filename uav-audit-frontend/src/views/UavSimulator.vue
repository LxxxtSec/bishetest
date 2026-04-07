<template>
  <div class="simulator-container">
    <el-card class="simulator-card">
      <template #header>
        <div class="card-header">
          <span>无人机状态模拟器</span>
          <el-button type="primary" @click="sendStatus" :loading="loading">
            发送状态数据
          </el-button>
        </div>
      </template>

      <el-form :model="form" label-width="120px">
        <el-form-item label="无人机编号">
          <el-select v-model="form.uavId" placeholder="选择无人机">
            <el-option v-for="uav in uavs" :key="uav.uavId" :label="uav.uavName" :value="uav.uavId" />
          </el-select>
        </el-form-item>

        <el-form-item label="经度">
          <el-input-number v-model="form.longitude" :precision="6" :step="0.0001" :min="121" :max="122" />
        </el-form-item>

        <el-form-item label="纬度">
          <el-input-number v-model="form.latitude" :precision="6" :step="0.0001" :min="31" :max="32" />
        </el-form-item>

        <el-form-item label="飞行高度(m)">
          <el-input-number v-model="form.altitude" :min="0" :max="1000" />
        </el-form-item>

        <el-form-item label="飞行速度(km/h)">
          <el-input-number v-model="form.speed" :min="0" :max="200" />
        </el-form-item>

        <el-form-item label="电池电量(%)">
          <el-slider v-model="form.battery" :min="0" :max="100" show-input />
        </el-form-item>

        <el-form-item label="信号强度">
          <el-slider v-model="form.signalStrength" :min="0" :max="100" show-input />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="normal">正常</el-radio>
            <el-radio label="warning">警告</el-radio>
            <el-radio label="error">错误</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-divider>快速测试场景</el-divider>

        <el-form-item>
          <el-space>
            <el-button @click="testLowBattery">低电量</el-button>
            <el-button @click="testHighAltitude">高度超限</el-button>
            <el-button @click="testHighSpeed">速度超限</el-button>
            <el-button @click="testNoFlyZone">进入禁飞区</el-button>
            <el-button @click="testNormal">正常飞行</el-button>
          </el-space>
        </el-form-item>
      </el-form>

      <el-divider>响应结果</el-divider>

      <pre v-if="response" :class="['response', response.code === 200 ? 'success' : 'error']">
{{ JSON.stringify(response, null, 2) }}
      </pre>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const response = ref(null)
const uavs = ref([])

const form = ref({
  uavId: 'UAV001',
  uavName: '巡检无人机1号',
  latitude: 31.2304,
  longitude: 121.4737,
  altitude: 120,
  speed: 45,
  battery: 85,
  signalStrength: 95,
  status: 'normal'
})

// 加载无人机列表
const loadUavs = async () => {
  try {
    const res = await request.get('/uav/list', { params: { pageNum: 1, pageSize: 100 } })
    uavs.value = res.records || []
    if (uavs.value.length > 0) {
      form.value.uavId = uavs.value[0].uavId
      form.value.uavName = uavs.value[0].uavName
    }
  } catch (error) {
    console.error('加载无人机列表失败:', error)
  }
}

// 发送状态数据
const sendStatus = async () => {
  loading.value = true
  response.value = null

  try {
    // 找到对应的无人机名称
    const uav = uavs.value.find(u => u.uavId === form.value.uavId)
    if (uav) {
      form.value.uavName = uav.uavName
    }

    const res = await request.post('/uav/status', form.value)
    response.value = res
    ElMessage.success('状态发送成功')

    // 刷新无人机列表
    loadUavs()
  } catch (error) {
    response.value = { code: 500, message: error.message }
    ElMessage.error('发送失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 快速测试场景
const testLowBattery = () => {
  form.value.battery = 10
  form.value.status = 'error'
  sendStatus()
}

const testHighAltitude = () => {
  form.value.altitude = 600
  form.value.status = 'error'
  sendStatus()
}

const testHighSpeed = () => {
  form.value.speed = 150
  form.value.status = 'warning'
  sendStatus()
}

const testNoFlyZone = () => {
  // 禁飞区: 31.2300-31.2400, 121.4700-121.4800
  form.value.latitude = 31.235
  form.value.longitude = 121.475
  form.value.altitude = 100
  sendStatus()
}

const testNormal = () => {
  form.value.latitude = 31.2304
  form.value.longitude = 121.4737
  form.value.altitude = 120
  form.value.speed = 45
  form.value.battery = 85
  form.value.signalStrength = 95
  form.value.status = 'normal'
  sendStatus()
}

onMounted(() => {
  loadUavs()
})
</script>

<style scoped>
.simulator-container {
  padding: 20px;
}

.simulator-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.response {
  padding: 15px;
  border-radius: 4px;
  max-height: 300px;
  overflow: auto;
  font-size: 12px;
}

.response.success {
  background: #f0f9ff;
  border: 1px solid #a0cfff;
}

.response.error {
  background: #fff0f0;
  border: 1px solid #fca5a5;
}
</style>
