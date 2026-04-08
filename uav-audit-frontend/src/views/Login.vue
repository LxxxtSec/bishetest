<template>
  <div class="login-shell">
    <div class="cinematic-glow"></div>
    <div class="login-wrap">
      
      <section class="login-brand" aria-label="平台信息">
        <div class="brand-top">
          <div class="login-badge uav-mono">无人机交通巡检系统</div>
        </div>
        
        <div class="brand-center">
          <h1 class="login-title uav-title">
            操作安全<br>
            <span class="uav-title-italic">审计平台</span>
          </h1>
          <p class="login-desc">
            以事件为中心记录、关联与追溯。把每一次操作变成可检索的证据链。
          </p>
        </div>

        <div class="brand-bottom">
          <div class="login-chips">
            <div class="uav-chip">
              <span class="uav-chip__dot" style="background: var(--uav-muted)"></span>
              <span class="uav-mono">审计日志</span>
            </div>
            <div class="uav-chip">
              <span class="uav-chip__dot" style="background: var(--uav-cyan)"></span>
              <span class="uav-mono">告警管理</span>
            </div>
            <div class="uav-chip">
              <span class="uav-chip__dot" style="background: var(--uav-primary)"></span>
              <span class="uav-mono">追溯链路</span>
            </div>
          </div>
        </div>
      </section>

      <section class="login-panel uav-panel" aria-label="登录">
        <div class="panel-inner">
          <div class="panel-top">
            <h2 class="uav-title">身份验证</h2>
            <div class="panel-line">请输入账号密码以继续</div>
          </div>

          <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="用户名" 
                size="large" 
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                size="large"
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-form-item class="login-actions">
              <el-button type="primary" size="large" :loading="loading" class="login-button" @click="handleLogin">
                登录
              </el-button>
            </el-form-item>
          </el-form>

          <div class="panel-foot">
            登录即表示你同意平台记录关键操作用于安全审计。
          </div>
        </div>
      </section>
      
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const data = await request.post('/auth/login', loginForm)
      userStore.setToken(data.token)
      userStore.setUserInfo({
        userId: data.userId,
        username: data.username,
        realName: data.realName,
        role: data.role
      })
      ElMessage.success('登录成功')
      router.push('/')
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 40px 20px;
  position: relative;
  background-color: var(--uav-bg);
  background-image:
    radial-gradient(900px 620px at 22% 30%, rgba(226, 228, 234, 0.18), transparent 60%),
    radial-gradient(860px 600px at 80% 70%, rgba(94, 139, 149, 0.18), transparent 62%);
}

.cinematic-glow {
  position: absolute;
  top: 50%;
  left: 30%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(226, 228, 234, 0.12) 0%, transparent 72%);
  transform: translate(-50%, -50%);
  pointer-events: none;
  z-index: 0;
  filter: blur(40px);
}

.login-wrap {
  width: min(1100px, 100%);
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 60px;
  align-items: stretch;
}

.login-brand {
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.login-badge {
  display: inline-block;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--uav-border-strong);
  color: var(--uav-primary);
  font-size: 11px;
  letter-spacing: 0.2em;
}

.brand-center {
  margin: auto 0;
}

.login-title {
  font-size: clamp(48px, 6vw, 72px);
  line-height: 1.05;
  color: var(--uav-text);
  margin-bottom: 32px;
}

.login-title .uav-title-italic {
  color: var(--uav-primary);
}

.login-desc {
  font-size: 15px;
  line-height: 1.8;
  color: var(--uav-muted);
  max-width: 480px;
  font-family: 'Manrope', sans-serif;
  font-weight: 300;
}

.login-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.login-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  background: linear-gradient(145deg, rgba(255,255,255,0.03) 0%, rgba(0,0,0,0.2) 100%);
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.6) !important;
}

.panel-inner {
  max-width: 320px;
  margin: 0 auto;
  width: 100%;
}

.panel-top {
  margin-bottom: 40px;
}

.panel-top h2 {
  font-size: 28px;
  margin-bottom: 12px;
  color: var(--uav-text);
}

.panel-line {
  font-size: 13px;
  color: var(--uav-muted);
  line-height: 1.6;
  font-family: 'Manrope', sans-serif;
  font-weight: 300;
}

.login-form {
  margin-top: 10px;
}

.login-actions {
  margin-top: 40px;
}

.login-actions :deep(.el-form-item__content) {
  justify-content: stretch;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 14px;
  letter-spacing: 0.1em;
}

.panel-foot {
  margin-top: 32px;
  font-size: 11px;
  color: var(--uav-faint);
  line-height: 1.6;
  font-family: 'Manrope', sans-serif;
}

@media (max-width: 900px) {
  .login-wrap {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  .login-brand {
    padding: 20px 0;
  }
  .cinematic-glow {
    left: 50%;
  }
}
</style>
