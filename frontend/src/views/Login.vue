<template>
  <div class="login-page">
    <div class="login-card">
      <!-- Logo -->
      <div class="logo-section">
        <img src="https://img.alicdn.com/imgextra/i3/O1CN01KliT1u1jEq947NlKH_!!6000000004517-55-tps-180-180.svg" alt="Logo" class="logo" />
        <h1>登录</h1>
      </div>

      <!-- 社交登录 -->
      <div class="social-login">
        <n-button size="large" block class="social-btn">
          <template #icon>
            <n-icon :component="LogoGoogle" />
          </template>
          使用 Google 登录
        </n-button>
        <n-button size="large" block class="social-btn">
          <template #icon>
            <n-icon :component="LogoGithub" />
          </template>
          使用 GitHub 登录
        </n-button>
      </div>

      <!-- 分隔线 -->
      <div class="divider">
        <span>或</span>
      </div>

      <!-- 邮箱登录 -->
      <n-form ref="formRef" :model="formData" :rules="rules" class="login-form">
        <n-form-item path="email" label="邮箱">
          <n-input 
            v-model:value="formData.email" 
            placeholder="请输入邮箱地址"
            size="large"
          />
        </n-form-item>
        
        <n-form-item v-if="showPassword" path="password" label="密码">
          <n-input 
            v-model:value="formData.password" 
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password-on="click"
          />
        </n-form-item>
        
        <n-button 
          type="primary" 
          block 
          size="large"
          :loading="loading"
          @click="handleContinue"
        >
          {{ showPassword ? '登录' : '继续' }}
        </n-button>
      </n-form>

      <!-- 注册链接 -->
      <div class="register-link">
        没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { LogoGithub, LogoGoogle } from '@vicons/ionicons5'
import { login } from '../api/auth'
import { useUserStore } from '../stores/user'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const showPassword = ref(false)

const formData = reactive({
  email: '',
  password: ''
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const handleContinue = async () => {
  if (!showPassword.value) {
    // 验证邮箱
    try {
      await formRef.value?.validate(
        () => {},
        (rule) => rule?.key === 'email'
      )
      showPassword.value = true
    } catch {}
    return
  }
  
  // 登录
  try {
    await formRef.value?.validate()
    loading.value = true
    
    const res = await login({
      email: formData.email,
      password: formData.password
    })
    
    if (res.code === 200) {
      userStore.setUser(res.data)
      message.success('登录成功！')
      router.push('/')
    } else {
      message.error(res.message || '登录失败')
    }
  } catch (error) {
    if (error.message) {
      message.error(error.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #090A0B;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: #18181c;
  border-radius: 16px;
  padding: 40px;
}

.logo-section {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

.logo-section h1 {
  font-size: 24px;
  font-weight: 500;
  color: #fff;
}

.social-login {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.social-btn {
  background: transparent !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
}

.social-btn:hover {
  border-color: rgba(255, 255, 255, 0.3) !important;
  background: rgba(255, 255, 255, 0.05) !important;
}

.divider {
  position: relative;
  text-align: center;
  margin: 24px 0;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 45%;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  font-size: 14px;
  color: #666;
  padding: 0 12px;
  background: #18181c;
}

.login-form {
  margin-bottom: 24px;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #94a3b8;
}

.register-link a {
  color: #2ADB5C;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
