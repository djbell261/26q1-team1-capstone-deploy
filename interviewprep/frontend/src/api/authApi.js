import client from "./client";

export const login = async (data) => {
  const response = await client.post("/api/auth/login", data);
  return response.data;
};

export const register = async (data) => {
  const response = await client.post("/api/auth/register", data);
  return response.data;
};