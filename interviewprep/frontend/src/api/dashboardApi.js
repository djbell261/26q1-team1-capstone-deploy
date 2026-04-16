import client from "./client";

export async function getDashboardData() {
  const [
    userRes,
    sessionsRes,
    codingRes,
    behavioralRes,
    recommendationsRes,
    performanceRes,
  ] = await Promise.all([
    client.get("/users/me"),
    client.get("/sessions/me"),
    client.get("/coding-submissions/me"),
    client.get("/behavioral-submissions/me"),
    client.get("/recommendations/me"),
    client.get("/performance/me"),
  ]);

  return {
    user: userRes.data,
    sessions: sessionsRes.data ?? [],
    codingSubmissions: codingRes.data ?? [],
    behavioralSubmissions: behavioralRes.data ?? [],
    recommendations: recommendationsRes.data ?? [],
    performance: performanceRes.data ?? {
      averageCodingScore: 0,
      averageBehavioralScore: 0,
      overallAverageScore: 0,
      totalCodingSubmissions: 0,
      totalBehavioralSubmissions: 0,
      codingAverageByDifficulty: {},
      recentCodingSubmissions: [],
      recentBehavioralSubmissions: [],
      weakAreas: [],
    },
  };
}