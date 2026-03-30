<template>
  <n-config-provider :theme="darkTheme" :theme-overrides="themeOverrides">
    <n-message-provider>
      <router-view />
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { darkTheme } from 'naive-ui'

const themeOverrides = {
  common: {
    primaryColor: '#2ADB5C',
    primaryColorHover: '#3FE96D',
    primaryColorPressed: '#25C552',
    primaryColorSuppl: '#2ADB5C',
    textColorBase: '#ffffff',
    textColor1: '#ffffff',
    textColor2: '#e0e0e0',
    textColor3: '#94a3b8'
  },
  Button: {
    textColorPrimary: '#ffffff',
    colorPrimary: '#2ADB5C',
    colorHoverPrimary: '#3FE96D',
    colorPressedPrimary: '#25C552',
    borderPrimary: '#2ADB5C',
    borderHoverPrimary: '#3FE96D',
    borderPressedPrimary: '#25C552'
  },
  Input: {
    color: '#1a1b1e',
    colorFocus: '#1a1b1e',
    borderFocus: '#2ADB5C',
    boxShadowFocus: '0 0 0 2px rgba(42, 219, 92, 0.2)'
  }
}
</script>
