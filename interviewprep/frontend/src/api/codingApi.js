import client from "./client";

export const importExternalChallenges = async () => {
  const response = await client.post("/admin/challenges/import");
  return response.data;
};