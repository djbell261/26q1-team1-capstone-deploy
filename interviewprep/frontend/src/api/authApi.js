import client from "./client";

export async function loginUser(credentials) {
  const response = await client.post("/auth/login", credentials);
  return response.data;
}

export async function registerUser(userData) {
  const response = await client.post("/auth/register", userData);
  return response.data;
}