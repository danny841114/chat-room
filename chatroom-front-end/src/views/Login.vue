<template>
  <div class="container mt-5 justify-content-center align-items-center overall">
    <h3 class="text-center">登入</h3>
    <div>
      <form @submit.prevent="handleLogin">
        <div class="input-group mb-3">
          <label class="input-group-text" for="account">帳號</label>
          <input type="text" class="form-control" id="account" v-model="account" required />
        </div>

        <div class="input-group mb-3">
          <label class="input-group-text" for="password">密碼</label>
          <input type="password" class="form-control" id="password" v-model="password" required />
        </div>

        <div class="text-center">
          <button type="submit" class="btn btn-primary">登入</button>
        </div>
      </form>
    </div>

    <hr />

    <div class="text-center quick-login">
      <h3>快速登入</h3>
      <button class="btn btn-success quick-login-btn" @click="quickLogin('user01', 'aaa123')">
        user01
      </button>
      <button class="btn btn-success quick-login-btn" @click="quickLogin('user02', 'bbb456')">
        user02
      </button>
      <button class="btn btn-success quick-login-btn" @click="quickLogin('user03', 'ccc789')">
        user03
      </button>
      <button class="btn btn-success quick-login-btn" @click="quickLogin('user04', 'ddd101')">
        user04
      </button>
      <button class="btn btn-success" @click="quickLogin('user05', 'eee202')">
        user05
      </button>
    </div>

    <div>
      <a href="http://localhost:8080/oauth2/authorization/google">使用 Google 帳號登入</a>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { userApi } from "@/api/userApi";
import Swal from "sweetalert2";

const account = ref("");
const password = ref("");
const errorMsg = ref(null);
const router = useRouter();
const authStore = useAuthStore();

const handleLogin = async () => {
  try {
    const res = await userApi.login(account.value, password.value)
    authStore.setLogin(res);

    await Swal.fire({
      title: "登入成功",
      icon: "success",
      text: "確定後跳轉首頁",
      confirmButtonText: "確定",
    });

    router.replace("/");
  } catch (error) {
    errorMsg.value = error.response.data.message;
    await Swal.fire({
      title: "登入失敗",
      icon: "error",
      text: errorMsg.value,
      confirmButtonText: "確定",
    });
  }
};

const quickLogin = async (a, b) => {
  account.value = a;
  password.value = b;
  handleLogin();
};
</script>

<style scoped>
.overall {
  width: 60%;
}

.quick-login {
  margin-top: 20px;
}

.quick-login-btn {
  margin-right: 10px;
}
</style>
