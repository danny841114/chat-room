import { createRouter, createWebHistory } from "vue-router";
import Index from "@/views/Index.vue";
import Login from "@/views/Login.vue";
import OAuth2RedirectView from "@/views/OAuth2RedirectView.vue";
import ChatRoom from "@/views/ChatRoom.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "Index",
      component: Index,
      alias: "/index",
    },
    {
      path: "/login",
      name: "Login",
      component: Login,
    },
    {
      path: '/oauth2-success',
      name: 'OAuth2Redirect',
      component: OAuth2RedirectView,
    },
    {
      path: "/chat",
      name: "ChatRoom",
      component: ChatRoom,
    },
  ],
});

export default router;
