<template>
  <div class="loading-container">
    <p>Google 登入驗證中，請稍候...</p>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import Swal from "sweetalert2";

const router = useRouter();
const authStore = useAuthStore();

onMounted(async () => {
  try {
    await authStore.fetchMe();

    await Swal.fire({
      title: "登入成功",
      icon: "success",
      text: "確定後跳轉首頁",
      confirmButtonText: "確定",
    });

    router.replace("/");
  } catch (error) {
    console.error("抓取使用者資料失敗:", error);
    router.replace("/login");
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
