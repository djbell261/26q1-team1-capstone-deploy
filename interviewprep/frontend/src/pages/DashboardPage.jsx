import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { ui } from "../styles/ui";

export default function DashboardPage() {
  const navigate = useNavigate();

  const [performance, setPerformance] = useState(null);
  const [user, setUser] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [perfRes, userRes] = await Promise.all([
        api.get("/api/performance/me"),
        api.get("/api/users/me"),
      ]);

      try {
        await api.post("/api/recommendations/generate/me");
      } catch (recGenerateErr) {
        console.error(recGenerateErr);
      }

      const recRes = await api.get("/api/recommendations/me");

      setPerformance(perfRes.data);
      setUser(userRes.data);
      setRecommendations(recRes.data ?? []);
    } catch (err) {
      setError("Failed to load dashboard.");
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  if (!performance || !user) {
    return (
      <div style={ui.page}>
        <div style={ui.container}>
          <div style={ui.card}>
            {error ? <div style={ui.error}>{error}</div> : <p>Loading dashboard...</p>}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={ui.page}>
      <div style={ui.container}>
        <section style={ui.hero}>
          <div style={ui.topRow}>
            <div>
              <h1 style={ui.heroTitle}>Interview Prep Dashboard</h1>
              <p style={ui.heroSubtitle}>
                Welcome back, {user.name}. Practice technical and behavioral
                interviews, review AI feedback, and track your progress over time.
              </p>
            </div>

            <button style={ui.secondaryButton} onClick={logout}>
              Logout
            </button>
          </div>
        </section>

        {error && <div style={ui.error}>{error}</div>}

        <section style={ui.statGrid}>
          <div style={ui.statCard}>
            <p style={ui.statLabel}>Avg Coding Score</p>
            <p style={ui.statValue}>{performance.averageCodingScore ?? 0}</p>
          </div>

          <div style={ui.statCard}>
            <p style={ui.statLabel}>Avg Behavioral Score</p>
            <p style={ui.statValue}>{performance.averageBehavioralScore ?? 0}</p>
          </div>

          <div style={ui.statCard}>
            <p style={ui.statLabel}>Overall Score</p>
            <p style={ui.statValue}>{performance.overallScore ?? 0}</p>
          </div>

          <div style={ui.statCard}>
            <p style={ui.statLabel}>Recommendations</p>
            <p style={ui.statValue}>{recommendations.length}</p>
          </div>
        </section>

        <section style={ui.grid2}>
          <div style={{ display: "grid", gap: "20px" }}>
            <div style={ui.card}>
              <h2 style={ui.sectionTitle}>Quick Actions</h2>
              <p style={ui.sectionSubtitle}>
                Start practice sessions, view submissions, and review recommendations.
              </p>

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
                  gap: "14px",
                  marginTop: "18px",
                }}
              >
                <button style={ui.button} onClick={() => navigate("/coding-challenges")}>
                  Start Coding Practice
                </button>

                <button style={ui.button} onClick={() => navigate("/behavioral-questions")}>
                  Start Behavioral Practice
                </button>

                <button style={ui.secondaryButton} onClick={() => navigate("/sessions")}>
                  View Sessions
                </button>

                <button style={ui.secondaryButton} onClick={() => navigate("/coding-submissions")}>
                  Coding Submissions
                </button>

                <button style={ui.secondaryButton} onClick={() => navigate("/behavioral-submissions")}>
                  Behavioral Submissions
                </button>

                <button style={ui.secondaryButton} onClick={() => navigate("/recommendations")}>
                  Recommendations
                </button>
              </div>
            </div>

            <div style={ui.card}>
              <h2 style={ui.sectionTitle}>Performance Snapshot</h2>
              <p style={ui.sectionSubtitle}>
                See how your practice is trending across both tracks.
              </p>

              <div style={{ ...ui.grid3, marginTop: "18px" }}>
                <div style={ui.infoBox}>
                  <p style={ui.statLabel}>Coding Track</p>
                  <p style={{ margin: "8px 0 0", fontSize: "18px", fontWeight: 700 }}>
                    {performance.averageCodingScore ?? 0}
                  </p>
                </div>

                <div style={ui.infoBox}>
                  <p style={ui.statLabel}>Behavioral Track</p>
                  <p style={{ margin: "8px 0 0", fontSize: "18px", fontWeight: 700 }}>
                    {performance.averageBehavioralScore ?? 0}
                  </p>
                </div>

                <div style={ui.infoBox}>
                  <p style={ui.statLabel}>Overall Readiness</p>
                  <p style={{ margin: "8px 0 0", fontSize: "18px", fontWeight: 700 }}>
                    {performance.overallScore ?? 0}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <aside style={{ display: "grid", gap: "20px" }}>
            <div style={ui.card}>
              <h2 style={ui.sectionTitle}>Profile</h2>
              <p style={ui.sectionSubtitle}>Your account overview.</p>

              <div style={{ display: "grid", gap: "12px", marginTop: "18px" }}>
                <div style={ui.infoBox}>
                  <strong>Name:</strong> {user.name}
                </div>
                <div style={ui.infoBox}>
                  <strong>Email:</strong> {user.email}
                </div>
                <div style={ui.infoBox}>
                  <strong>Role:</strong> {user.role}
                </div>
              </div>
            </div>

            <div style={ui.card}>
              <h2 style={ui.sectionTitle}>Practice Focus</h2>
              <p style={ui.sectionSubtitle}>
                Recommendations update as you complete more sessions and submissions.
              </p>

              <div style={{ marginTop: "18px", ...ui.infoBox }}>
                <p style={ui.statLabel}>Current Recommendation Count</p>
                <p style={{ margin: "8px 0 0", fontSize: "30px", fontWeight: 700 }}>
                  {recommendations.length}
                </p>
              </div>
            </div>
          </aside>
        </section>
      </div>
    </div>
  );
}