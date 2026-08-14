import { defineStore } from "pinia";
import { userApi } from "@/api/userApi";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    account: localStorage.getItem("account") || null,
    username: localStorage.getItem("username") || null,
    userId: localStorage.getItem("userId") || null,
  }),

  actions: {
    setLogin(data) {
      this.account = data.account;
      this.username = data.username;
      this.userId = data.userId;
    },

    async logout() {
      this.account = null;
      this.username = null;
      this.userId = null;

      try {
        await userApi.logout();
      } catch (e) {
        console.error("Error log out", e);
      }
    },

    async fetchMe() {
      try {
        const res = await userApi.fetchMe();
        this.setLogin(res);
      } catch (e) {
        console.error("Error fetch me", e);
      }
    },
  },
});
