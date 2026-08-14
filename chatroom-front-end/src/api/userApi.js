import axios from "axios";

const apiClient = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}/api/auth`,
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

export const userApi = {
  login(account, password) {
    return apiClient.post("/login", {
      account,
      password,
    });
  },

  logout() {
    return apiClient.post("/logout");
  },

  fetchMe() {
    return apiClient.get("/fetchMe");
  },
};
