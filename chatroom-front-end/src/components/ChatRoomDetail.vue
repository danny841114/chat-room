<template>
  <div class="modal-backdrop-custom">
    <!-- 聊天室詳情 -->
    <div class="modal-content-custom" v-if="mode === 'detail'">
      <h5 class="text-center">聊天室成員</h5>

      <ul class="list-group">
        <li
          class="list-group-item d-flex justify-content-between align-items-center"
          v-for="u in props.chatRoom.users"
          :key="u.userId"
        >
          <span>{{ u.username }} ({{ u.account }})</span>
          <button
            class="btn btn-danger"
            v-if="chatRoom.type === 'GROUP'"
            @click="deleteMember(u.userId, u.username, u.account)"
          >
            移除
          </button>
        </li>
      </ul>

      <div class="mt-3 text-center">
        <button
          class="btn btn-primary"
          v-if="chatRoom.type === 'GROUP'"
          @click="openAddMemberMode"
        >
          新增
        </button>
        &nbsp;
        <button class="btn btn-primary" @click="$emit('close')">關閉</button>
      </div>
    </div>

    <!-- 新增聊天室成員 -->
    <div class="modal-content-custom" v-if="mode === 'addMember'">
      <h5 class="text-center">新增成員</h5>

      <div class="input-group mb-3">
        <span class="input-group-text">人員</span>
        <select class="form-control" v-model="selectedUserIds" multiple>
          <option v-for="u in usersForAdding" :key="u.userId" :value="u.userId">
            {{ u.username }} ({{ u.account }})
          </option>
        </select>
      </div>

      <div class="mt-3 text-center">
        <button class="btn btn-primary" @click="addChatRoomMember">新增</button>
        &nbsp;
        <button class="btn btn-primary" @click="mode = 'detail'">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import Swal from "sweetalert2";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const emit = defineEmits(["close", "created"]);
const props = defineProps({
  chatRoom: Object,
});
const mode = ref("detail");
const usersForAdding = ref([]);
const selectedUserIds = ref([]);

const deleteMember = async (userId, username, account) => {
  const ask = await Swal.fire({
    title: "移除聊天室成員",
    icon: "warning",
    text: `確定移除 ${username}(${account}) ?`,
    showCancelButton: true,
    confirmButtonText: "確定",
    cancelButtonText: "返回",
  });

  if (!ask.isConfirmed) return;

  try {
    const response = await axios.delete(
      `${apiBase}/api/chat-rooms/${props.chatRoom.roomId}/users/${userId}`,
      {
        withCredentials: true,
      },
    );

    if (response.status === 204) {
      const index = props.chatRoom.users.findIndex((u) => u.userId === userId);

      if (index !== -1) props.chatRoom.users.splice(index, 1);
    }
  } catch (e) {
    console.error("Error delete member from chat room", e);
  }
};

const getUsersExceptExistingMembers = async () => {
  try {
    const response = await axios.get(
      `${apiBase}/api/chat-rooms/${props.chatRoom.roomId}/available-users`,
      { withCredentials: true },
    );

    usersForAdding.value = response.data;
  } catch (e) {
    console.error("Error loading users", e);
  }
};

const addChatRoomMember = async () => {
  try {
    const response = await axios.post(
      `${apiBase}/api/chat-rooms/${props.chatRoom.roomId}/users`,
      { userIds: selectedUserIds.value },
      { withCredentials: true },
    );

    selectedUserIds.value = [];
    mode.value = "detail";
    emit("created");
  } catch (e) {
    console.error("Error creating room", e);
  }
};

const openAddMemberMode = () => {
  getUsersExceptExistingMembers();
  mode.value = "addMember";
};
</script>

<style scoped>
.modal-backdrop-custom {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);

  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content-custom {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 40%;
  height: auto;
}
</style>
