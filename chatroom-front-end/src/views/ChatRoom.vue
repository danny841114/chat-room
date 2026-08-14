<template>
  <div class="chat-layout">
    <!-- 左側聊天室列表 -->
    <aside class="chat-sidebar">
      <h4>聊天室</h4>

      <button class="btn btn-primary mb-3" @click="openModal">新增聊天</button>

      <div v-for="room in chatRooms" :key="room.roomId" class="list-group" style="cursor: pointer"
        @click="loadMessages(room.roomId)">
        <div class="list-group-item list-group-item-action room-name d-flex justify-content-between">
          <span>
            <strong class="type-badge">{{
              room.type === "PRIVATE" ? "私" : "群"
            }}</strong>
            {{ room.name }}
          </span>
          <span class="unread-badge" v-if="room.unreadMessagesCount > 0">{{
            room.unreadMessagesCount
          }}</span>
        </div>
      </div>
    </aside>

    <!-- 右側聊天室主體 -->
    <main class="chat-main">
      <div class="chat-header" style="cursor: pointer" @click="openChatRoomDetailModal">
        {{ currentChatRoom.name }}
      </div>

      <div class="messages">
        <div v-for="msg in messages" :key="msg.id" class="message" :class="{
          'my-message': Number(msg.senderId) === Number(authStore.userId),
        }">
          <strong>{{ msg.senderName }} ({{ msg.senderAccount }}) :</strong>
          &nbsp;
          <span v-if="!msg.deletedAt">{{ msg.content }}</span>
          <span v-else style="color: gray">--- 訊息已被刪除 ---</span>

          <button v-if="msg.senderId === Number(authStore.userId)" class="message-menu-btn"
            @click="toggleMenu(msg.messageId)">
            ⋮
          </button>
          <div v-if="openedMenuId === msg.messageId" class="message-menu">
            <button v-if="!msg.deletedAt" class="btn btn-danger" @click="recallMessage(msg.messageId)">
              撤回訊息
            </button>
            <button v-else class="btn btn-danger" @click="restoreDeletedMessage(msg.messageId)">
              復原訊息
            </button>
          </div>

          <div class="message-time">
            {{ formatTime(msg.createdAt) }}
          </div>
        </div>
      </div>

      <div class="input-area">
        <input v-model="message" class="form-control" placeholder="輸入訊息..." :disabled="isChatRoomIdEmpty"
          @keyup.enter="sendMessage" />
        <button class="btn btn-primary send-btn" :disabled="isChatRoomIdEmpty" @click="sendMessage">
          送出
        </button>
      </div>
    </main>
  </div>

  <AddChatRoom v-show="showAddChatRoomModal" @close="showAddChatRoomModal = false" @created="handleCreated">
  </AddChatRoom>

  <ChatRoomDetail v-show="showChatRoomDetailModal" :chat-room="currentChatRoom" @created="handleAddMember"
    @close="showChatRoomDetailModal = false"></ChatRoomDetail>
</template>

<script setup>
import { ref, watch, computed, onMounted, onBeforeUnmount } from "vue";
import { Client } from "@stomp/stompjs";
import { useAuthStore } from "@/stores/auth";
import SockJS from "sockjs-client";
import axios from "axios";
import AddChatRoom from "@/components/AddChatRoom.vue";
import ChatRoomDetail from "@/components/ChatRoomDetail.vue";
import { chatApi } from "@/api/chatApi";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const authStore = useAuthStore();
const chatRooms = ref([]);
const currentChatRoom = ref({
  roomId: "",
  name: "",
  type: "",
  users: [],
});
const message = ref("");
const messages = ref([]);
const showAddChatRoomModal = ref(false);
const showChatRoomDetailModal = ref(false);
const openedMenuId = ref(null);

const isChatRoomIdEmpty = computed(() => {
  return (
    typeof currentChatRoom.value.roomId === "string" &&
    currentChatRoom.value.roomId.trim() === ""
  );
});

const loadChatRooms = async () => {
  try {
    chatRooms.value = await chatApi.getChatRooms();
  } catch (e) {
    console.error("Error loading chat rooms", e);
  }
};

const loadMessages = async (roomId) => {
  openedMenuId.value = null;

  try {
    currentChatRoom.value = await chatApi.getChatRoomById(roomId);
    messages.value = await chatApi.getMessagesByRoomId(roomId);
  } catch (e) {
    console.error("Error loading chat room info", e);
  }

  // update unread message count 0
  const room = chatRooms.value.find((r) => r.roomId === roomId);
  if (room) room.unreadMessagesCount = 0;
};

const formatTime = (timeStr) => {
  const date = new Date(timeStr);

  return date.toLocaleString("zh-TW", {
    timeZone: "Asia/Taipei",
    hour12: false,
  });
};

const toggleMenu = (messageId) => {
  openedMenuId.value = openedMenuId.value === messageId ? null : messageId;
};

const recallMessage = async (msgId) => {
  try {
    await chatApi.removeMessage(currentChatRoom.value.roomId, msgId)

    const msg = messages.value.find((m) => m.messageId === msgId);
    if (msg) msg.deletedAt = new Date();
  } catch (e) {
    console.error("Error delete message", e);
  }

  openedMenuId.value = null;
};

const restoreDeletedMessage = async (msgId) => {
  try {
    chatApi.restoreMessage(currentChatRoom.value.roomId, msgId)

    const msg = messages.value.find((m) => m.messageId === msgId);
    if (msg) msg.deletedAt = null;
  } catch (e) {
    console.error("Error delete message", e);
  }

  openedMenuId.value = null;
};

const openModal = () => {
  showAddChatRoomModal.value = true;
};

const handleCreated = (roomId) => {
  showAddChatRoomModal.value = false;
  loadChatRooms();
  loadMessages(roomId);
};

const openChatRoomDetailModal = () => {
  showChatRoomDetailModal.value = true;
};

const handleAddMember = () => {
  showChatRoomDetailModal.value = false;
  loadMessages(currentChatRoom.value.roomId);
};

let stompClient = null;
let subscription = null;
let roomListSubscription = null;

const connectWebSocket = () => {
  stompClient = new Client({
    webSocketFactory: () => new SockJS(`${apiBase}/ws`),
    connectHeaders: {
      Authorization: `Bearer ${authStore.token}`,
    },
    debug: (str) => {
      console.log(str);
    },
    reconnectDelay: 5000,

    onConnect: () => {
      console.log("WebSocket connected");
      subscribeRoomList(); // 先訂閱聊天室動態
    },

    onStompError: (frame) => {
      console.error("STOMP error", frame);
    },
  });

  stompClient.activate();
};

const subscribeRoom = (roomId) => {
  if (subscription) subscription.unsubscribe();

  subscription = stompClient.subscribe(
    `/topic/rooms/${roomId}`,
    (messageBody) => {
      const newMessage = JSON.parse(messageBody.body);
      messages.value.push(newMessage);
    }
  );
};

const subscribeRoomList = () => {
  if (roomListSubscription) roomListSubscription.unsubscribe();

  roomListSubscription = stompClient.subscribe(
    `/topic/users/${authStore.userId}/rooms`,
    (messageBody) => {
      chatRooms.value = JSON.parse(messageBody.body);
    }
  );
};

const sendMessage = () => {
  if (!message.value.trim()) return;

  stompClient.publish({
    destination: `/app/chat.sendMessage/${currentChatRoom.value.roomId}`,
    body: JSON.stringify({
      senderId: authStore.userId,
      content: message.value,
    }),
  });

  message.value = "";
};

onMounted(() => {
  loadChatRooms();
  connectWebSocket();
});

onBeforeUnmount(() => {
  if (subscription) subscription.unsubscribe();
  if (roomListSubscription) roomListSubscription.unsubscribe();
  if (stompClient) stompClient.deactivate();
});

watch(currentChatRoom, (currentChatRoom) => {
  const newRoomId = currentChatRoom.roomId;
  if (newRoomId === undefined || newRoomId === null || newRoomId === "") return;

  subscribeRoom(newRoomId);
  if (!roomListSubscription) subscribeRoomList();
});
</script>

<style scoped>
.chat-layout {
  display: flex;
  height: 85vh;
  overflow: hidden;
}

.chat-sidebar {
  width: 260px;
  flex-shrink: 0;
  background-color: #f8f9fa;
  border-right: 1px solid #ddd;
  padding: 16px;
  overflow-y: auto;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-header {
  height: 56px;
  padding: 16px;
  border-bottom: 1px solid #ddd;
  font-weight: bold;
}

.messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background-color: #fff;
}

.message {
  margin-bottom: 12px;
}

.my-message {
  margin-left: auto;
  text-align: right;
}

.message-time {
  font-size: 12px;
  color: gray;
  margin-top: 4px;
}

.input-area {
  display: flex;
  gap: 8px;
  padding: 12px;
  border-top: 1px solid #ddd;
}

.room-item {
  cursor: pointer;
  padding-top: 10px;
}

.room-name {
  white-space: nowrap;
  /* 不換行 */
  overflow: hidden;
  /* 超出隱藏 */
  text-overflow: ellipsis;
  /* 顯示 ... */
}

.send-btn {
  width: 70px;
}

.unread-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  min-width: 22px;
  height: 22px;
  padding: 0 6px;

  background-color: red;
  color: white;

  border-radius: 999px;
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  min-width: 22px;
  height: 22px;
  padding: 0 6px;

  background-color: green;
  color: white;

  border-radius: 999px;
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
}

.message-menu-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 18px;
}

.message-menu {
  position: absolute;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 6px;
  /* z-index: 10; */
}
</style>
