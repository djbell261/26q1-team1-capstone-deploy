import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    const isAuthRoute =
      config.url?.includes("/api/auth/login") ||
      config.url?.includes("/api/auth/register") ||
      config.url?.includes("/auth/login") ||
      config.url?.includes("/auth/register");

    if (token && !isAuthRoute) {
      config.headers.Authorization = `Bearer ${token}`;
    } else if (config.headers?.Authorization) {
      delete config.headers.Authorization;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

export default api;