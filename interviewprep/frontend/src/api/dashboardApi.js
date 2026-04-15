import client from "./client";

export async function getDashboardData() {
  const [userRes, sessionsRes, codingRes, behavioralRes, recommendationsRes] =
    await Promise.all([
      client.get("/users/me"),
      client.get("/sessions/me"),
      client.get("/coding-submissions/me"),
      client.get("/behavioral-submissions/me"),
      client.get("/recommendations/me"),
    ]);

  return {
    user: userRes.data,
    sessions: sessionsRes.data ?? [],
    codingSubmissions: codingRes.data ?? [],
    behavioralSubmissions: behavioralRes.data ?? [],
    recommendations: recommendationsRes.data ?? [],
  };
}