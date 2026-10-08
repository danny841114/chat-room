import apiClient from "./axios";

const API_PREFIX = "/api/chat-rooms";

export const chatApi = {
  getChatRooms() {
    return apiClient.get(API_PREFIX);
  },

  getChatRoomById(id) {
    return apiClient.get(`${API_PREFIX}/${id}`);
  },

  getMessagesByRoomId(roomId) {
    return apiClient.get(`${API_PREFIX}/${roomId}/messages`);
  },

  removeMessage(roomId, msgId) {
    return apiClient.delete(`${API_PREFIX}/${roomId}/messages/${msgId}`);
  },

  restoreMessage(roomId, msgId) {
    return apiClient.put(`${API_PREFIX}/${roomId}/messages/${msgId}`);
  },

  removeChatRoomMember(roomId, userId) {
    return apiClient.delete(`${API_PREFIX}/${roomId}/users/${userId}`);
  },

  getMembersExceptMe() {
    return apiClient.get(`${API_PREFIX}/available-users`);
  },

  getMembersExceptExisting(roomId) {
    return apiClient.get(`${API_PREFIX}/${roomId}/available-users`);
  },

  addChatRoomMembers(roomId, userIds) {
    return apiClient.post(`${API_PREFIX}/${roomId}/users`, { userIds });
  },

  addChatRoom(name, type, memberIds) {
    return apiClient.post(API_PREFIX, {
      name,
      type,
      memberIds,
    });
  },
};
