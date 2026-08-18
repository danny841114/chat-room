<template>
    <div class="loading-container">
        <p>Google 登入驗證中，請稍候...</p>
    </div>
</template>
<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth' // 若有使用 Pinia 管理狀態

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

onMounted(async () => {
    // 1. 從 URL query 取得後端傳回的 token 或 error
    const token = route.query.token
    const error = route.query.error

    if (token) {
        // 2. 存入 localStorage
        localStorage.setItem('token', token)

        // 3. (可選) 更新 Pinia / Vuex 中的全域登入狀態或 User 資料
        if (authStore) {
            authStore.setToken(token)
            // await authStore.fetchUserProfile() // 可在此順便向後端請求用戶詳細資料
        }

        // 4. 跳轉到目標頁面 (建議用 replace 避免使用者按「上一頁」回到 Redirect 頁)
        router.replace('/dashboard')
    } else {
        console.error('OAuth2 登入失敗:', error)
        alert('登入失敗，請重新登入！')
        router.replace('/login')
    }
})
</script>
<style scoped>
.loading-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    font-size: 1.2rem;
}
</style>