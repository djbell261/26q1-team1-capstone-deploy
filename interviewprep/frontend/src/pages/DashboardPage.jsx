import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

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

  // ===== INLINE STYLES (consistent system) =====
  const styles = {
    page: {
      maxWidth: "1100px",
      margin: "40px auto",
      padding: "20px",
      fontFamily: "Arial, sans-serif",
      color: "#1f2937",
    },

    header: {
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      flexWrap: "wrap",
      gap: "16px",
      marginBottom: "20px",
    },

    title: {
      fontSize: "32px",
      margin: 0,
    },

    subtitle: {
      color: "#6b7280",
      marginTop: "4px",
    },

    button: {
      padding: "10px 14px",
      border: "none",
      borderRadius: "8px",
      cursor: "pointer",
      backgroundColor: "#111827",
      color: "white",
    },

    grid: {
      display: "grid",
      gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
      gap: "16px",
      marginBottom: "20px",
    },

    card: {
      background: "white",
      borderRadius: "12px",
      padding: "18px",
      boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    },

    section: {
      background: "white",
      borderRadius: "12px",
      padding: "18px",
      marginBottom: "16px",
      boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    },

    quickActions: {
      display: "flex",
      flexDirection: "column",
      gap: "12px",
      marginTop: "10px",
    },

    error: {
      color: "red",
      marginBottom: "10px",
    },
  };

  if (!performance || !user) {
    return (
      <div style={styles.page}>
        {error ? <p style={styles.error}>{error}</p> : <p>Loading dashboard...</p>}
      </div>
    );
  }

  return (
    <div style={styles.page}>
      {/* HEADER */}
      <div style={styles.header}>
        <div>
          <h1 style={styles.title}>Dashboard</h1>
          <p style={styles.subtitle}>Welcome back, {user.name}.</p>
        </div>

        <button style={styles.button} onClick={logout}>
          Logout
        </button>
      </div>

      {/* STATS GRID */}
      <div style={styles.grid}>
        <div style={styles.card}>
          <h3>Avg Coding Score</h3>
          <p>{performance.averageCodingScore ?? 0}</p>
        </div>

        <div style={styles.card}>
          <h3>Avg Behavioral Score</h3>
          <p>{performance.averageBehavioralScore ?? 0}</p>
        </div>

        <div style={styles.card}>
          <h3>Overall Score</h3>
          <p>{performance.overallScore ?? 0}</p>
        </div>

        <div style={styles.card}>
          <h3>Recommendations</h3>
          <p>{recommendations.length}</p>
        </div>
      </div>

      {/* PROFILE */}
      <div style={styles.section}>
        <h3>Profile</h3>
        <p><strong>Name:</strong> {user.name}</p>
        <p><strong>Email:</strong> {user.email}</p>
        <p><strong>Role:</strong> {user.role}</p>
      </div>

      {/* QUICK ACTIONS */}
      <div style={styles.section}>
        <h3>Quick Actions</h3>

        <div style={styles.quickActions}>
          <button style={styles.button} onClick={() => navigate("/coding-challenges")}>
            Start Coding Practice
          </button>

          <button style={styles.button} onClick={() => navigate("/behavioral-questions")}>
            Start Behavioral Practice
          </button>

          <button style={styles.button} onClick={() => navigate("/sessions")}>
            View Sessions
          </button>

          <button style={styles.button} onClick={() => navigate("/coding-submissions")}>
            View Coding Submissions
          </button>

          <button style={styles.button} onClick={() => navigate("/behavioral-submissions")}>
            View Behavioral Submissions
          </button>

          <button style={styles.button} onClick={() => navigate("/recommendations")}>
            View Recommendations
          </button>
        </div>
      </div>
    </div>
  );
}