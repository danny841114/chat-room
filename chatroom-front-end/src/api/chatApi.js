import axios from "axios";

const apiClient = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}/api/chat-rooms`,
  timeout: 10000,
  withCredentials: true,
});

apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error("API Error:", error);
    return Promise.reject(error);
  }
);

export const chatApi = {
  getChatRooms() {
    return apiClient.get("");
  },

  getChatRoomById(id) {
    return apiClient.get(`/${id}`);
  },

  getMessagesByRoomId(roomId) {
    return apiClient.get(`/${roomId}/messages`);
  },

  removeMessage(roomId, msgId) {
    return apiClient.delete(`/${roomId}/messages/${msgId}`);
  },

  restoreMessage(roomId, msgId) {
    return apiClient.put(`/${roomId}/messages/${msgId}`);
  },

  removeChatRoomMember(roomId, userId) {
    return apiClient.delete(`/${roomId}/users/${userId}`);
  },

  getMembersExceptMe() {
    return apiClient.get(`/available-users`);
  },

  getMembersExceptExisting(roomId) {
    return apiClient.get(`/${roomId}/available-users`);
  },

  addChatRoomMembers(roomId, userIds) {
    return apiClient.post(`/${roomId}/users`, { userIds });
  },

  addChatRoom(name, type, memberIds) {
    return apiClient.post("", {
      name,
      type,
      memberIds,
    });
  },
};
