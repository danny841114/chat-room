import apiClient from "./axios";

const API_PREFIX = "/api/auth";

export const userApi = {
  login(account, password) {
    return apiClient.post(`${API_PREFIX}/login`, {
      account,
      password,
    });
  },

  logout() {
    return apiClient.post(`${API_PREFIX}/logout`);
  },

  fetchMe() {
    return apiClient.get(`${API_PREFIX}/fetchMe`);
  },
};
