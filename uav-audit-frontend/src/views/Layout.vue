<template>
  <el-container class="cinematic-shell">
    <el-aside width="260px" class="cinematic-aside">
      <div class="cinematic-brand">
        <div class="brand-line"></div>
        <div class="brand-text">
          <div class="brand-title uav-title">审计平台</div>
          <div class="brand-sub uav-mono">无人机交通巡检系统</div>
        </div>
      </div>

      <div class="cinematic-session">
        <div class="session-label uav-mono">当前会话</div>
        <div class="session-val uav-mono">{{ sessionCode }}</div>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        class="cinematic-menu"
        background-color="transparent"
        text-color="var(--uav-muted)"
        active-text-color="var(--uav-primary)"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/audit-logs">
          <el-icon><Document /></el-icon>
          <span>审计日志</span>
        </el-menu-item>
        <el-menu-item index="/alerts">
          <el-icon><Bell /></el-icon>
          <span>告警管理</span>
        </el-menu-item>
        <el-menu-item index="/users" v-if="userStore.userInfo.role === 'admin'">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/uav-simulator">
          <el-icon><Connection /></el-icon>
          <span>无人机模拟器</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="cinematic-body">
      <el-header class="cinematic-header" height="80px">
        <div class="header-left">
          <h1 class="page-title uav-title">{{ pageTitle }}</h1>
          <div class="page-meta uav-mono">
            <span class="meta-dot"></span>
            <span>{{ nowText }}</span>
            <span class="meta-sep">/</span>
            <span style="color: var(--uav-primary)">角色：{{ roleText }}</span>
          </div>
        </div>

        <div class="header-right">
          <div class="user-info">
            <div class="user-name">{{ userStore.userInfo.realName || userStore.userInfo.username }}</div>
            <div class="user-id uav-mono">用户ID：{{ userStore.userInfo.userId || '-' }}</div>
          </div>
          <el-button class="logout-btn" type="info" plain size="small" @click="handleLogout">
            退出登录
          </el-button>
        </div>
      </el-header>

      <el-main class="cinematic-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { House, Document, Bell, User, Connection } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta?.title || '控制台')
const roleText = computed(() => {
  const map = {
    admin: '管理员',
    operator: '操作员',
    auditor: '审计员',
    user: '用户'
  }
  return map[userStore.userInfo.role] || '用户'
})

const sessionCode = ref(Math.random().toString(36).slice(2, 10).toUpperCase())
const nowText = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
let timer = null

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  timer = window.setInterval(() => {
    nowText.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
  }, 1000)
})

onUnmounted(() => {
  if (timer) window.clearInterval(timer)
})
</script>

<style scoped>
.cinematic-shell {
  min-height: 100vh;
  background: var(--uav-bg);
}

.cinematic-aside {
  background: var(--uav-panel-solid);
  border-right: 1px solid var(--uav-border);
  display: flex;
  flex-direction: column;
}

.cinematic-brand {
  padding: 40px 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.brand-line {
  width: 24px;
  height: 2px;
  background: var(--uav-primary);
}

.brand-title {
  font-size: 24px;
  color: var(--uav-text);
  line-height: 1.2;
}

.brand-sub {
  margin-top: 8px;
  font-size: 10px;
  color: var(--uav-faint);
  letter-spacing: 0.1em;
}

.cinematic-session {
  padding: 0 32px 32px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.session-label {
  font-size: 10px;
  color: var(--uav-faint);
}

.session-val {
  font-size: 12px;
  color: var(--uav-text);
  letter-spacing: 0.1em;
}

.cinematic-menu {
  flex: 1;
  border-right: none;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 16px;
  padding: 0 16px !important;
  border-radius: var(--uav-radius);
  font-family: 'Manrope', sans-serif;
  font-weight: 400;
  font-size: 13px;
  letter-spacing: 0.05em;
  transition: all 0.3s ease;
}

.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.02) !important;
  color: var(--uav-text) !important;
}

.el-menu-item.is-active {
  background: rgba(197, 168, 128, 0.05) !important;
  color: var(--uav-primary) !important;
}

.el-menu-item .el-icon {
  margin-right: 12px;
  font-size: 16px;
  opacity: 0.7;
}

.el-menu-item.is-active .el-icon {
  opacity: 1;
}

.cinematic-body {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.cinematic-header {
  padding: 0 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--uav-border);
  background: rgba(11, 12, 16, 0.55);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.page-title {
  font-size: 26px;
  color: var(--uav-text);
}

.page-meta {
  font-size: 11px;
  color: var(--uav-faint);
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--uav-lime);
  box-shadow: 0 0 8px var(--uav-lime);
}

.meta-sep {
  color: var(--uav-border-strong);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.user-info {
  text-align: right;
}

.user-name {
  font-size: 13px;
  color: var(--uav-text);
  font-family: 'Manrope', sans-serif;
  font-weight: 500;
  letter-spacing: 0.05em;
}

.user-id {
  margin-top: 4px;
  font-size: 10px;
  color: var(--uav-faint);
}

.logout-btn {
  font-family: 'Manrope', sans-serif;
  letter-spacing: 0.05em;
  font-size: 11px;
  padding: 6px 16px;
}

.cinematic-main {
  padding: 40px;
  flex: 1;
  position: relative;
}

.cinematic-main::before {
  content: "";
  position: absolute;
  top: -200px;
  left: 50%;
  transform: translateX(-50%);
  width: 800px;
  height: 400px;
  background: radial-gradient(ellipse, rgba(197, 168, 128, 0.04) 0%, transparent 70%);
  pointer-events: none;
  z-index: 0;
}
</style>
