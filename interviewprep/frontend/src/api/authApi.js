import client from "./client";

export const login = async (credentials) => {
  const response = await client.post("/api/auth/login", credentials);
  return response.data;
};

export const register = async (payload) => {
  const response = await client.post("/api/auth/register", payload);
  return response.data;
};