<template>
  <div class="modal-backdrop-custom">
    <div class="modal-content-custom">
      <h5 class="text-center">新增聊天室</h5>

      <div class="input-group mb-3">
        <span class="input-group-text">型別</span>
        <select v-model="roomType" class="form-control">
          <option value="PRIVATE">PRIVATE</option>
          <option value="GROUP">GROUP</option>
        </select>
      </div>

      <div class="input-group mb-3" v-if="roomType === 'GROUP'">
        <span class="input-group-text">名稱</span>
        <input v-model="roomName" type="text" class="form-control" required />
      </div>

      <div class="input-group mb-3">
        <span class="input-group-text">人員</span>

        <select
          class="form-control"
          v-model="selectedPrivateUserId"
          v-if="roomType === 'PRIVATE'"
        >
          <option disabled :value="null">請選擇聊天對象</option>
          <option v-for="u in users" :key="u.userId" :value="u.userId">
            {{ u.username }} ({{ u.account }})
          </option>
        </select>

        <select
          class="form-control"
          v-model="selectedGroupUserIds"
          v-if="roomType === 'GROUP'"
          multiple
        >
          <option v-for="u in users" :key="u.userId" :value="u.userId">
            {{ u.username }} ({{ u.account }})
          </option>
        </select>
      </div>

      <div class="mt-3 text-center">
        <button class="btn btn-primary" @click="createRoom">建立</button>&nbsp;
        <button class="btn btn-primary" @click="$emit('close')">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import axios from "axios";
import Swal from "sweetalert2";

const apiBase = import.meta.env.VITE_API_BASE_URL;
const emit = defineEmits(["close", "created"]);
const users = ref([]);

const roomName = ref(null);
const roomType = ref("PRIVATE");
const selectedPrivateUserId = ref(null);
const selectedGroupUserIds = ref([]);

watch(
  () => roomType.value,
  (currentType) => {
    if (currentType === "PRIVATE") roomName.value = null;
  },
);

const loadUsers = async () => {
  try {
    const response = await axios.get(
      `${apiBase}/api/chat-rooms/available-users`,
      { withCredentials: true },
    );

    users.value = response.data;
  } catch (e) {
    console.error("Error loading users", e);
  }
};

const createRoom = async () => {
  try {
    if (roomType.value === "GROUP" && roomName.value === null) {
      await Swal.fire({
        title: "名稱不得為空",
        icon: "warning",
        allowOutsideClick: false,
        showCancelButton: false,
        confirmButtonText: "確認",
      });

      return;
    }

    let memberIds = null;
    if (roomType.value === "PRIVATE") {
      memberIds = [selectedPrivateUserId.value];
    } else {
      memberIds = selectedGroupUserIds.value;
    }

    const response = await axios.post(
      `${apiBase}/api/chat-rooms`,
      {
        name: roomName.value,
        type: roomType.value,
        memberIds: memberIds,
      },
      {
        withCredentials: true,
      },
    );

    roomName.value = null;
    selectedGroupUserIds.value = [];

    emit("created", response.data.roomId);
  } catch (e) {
    console.error("Error creating room", e);
  }
};

onMounted(() => {
  loadUsers();
});
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
