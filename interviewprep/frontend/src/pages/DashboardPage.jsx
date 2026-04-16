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
      const [perfRes, userRes, recRes] = await Promise.all([
        api.get("/api/performance/me"),
        api.get("/api/users/me"),
        api.get("/api/recommendations/me"),
      ]);

      setPerformance(perfRes.data);
      setUser(userRes.data);
      setRecommendations(recRes.data);
    } catch (err) {
      console.error("Dashboard load failed", err);
      setError("Failed to load dashboard.");
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  if (!performance || !user) {
    return (
      <div className="page-container">
        {error ? <p>{error}</p> : <p>Loading dashboard...</p>}
      </div>
    );
  }

  return (
    <div className="page-container">
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <div>
          <h1>Dashboard</h1>
          <p>Welcome back, {user.name}.</p>
        </div>
        <button onClick={logout}>Logout</button>
      </div>

      <div
        className="card-list"
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
          gap: "16px",
        }}
      >
        <div className="simple-card">
          <h3>Avg Coding Score</h3>
          <p>{performance.averageCodingScore}</p>
        </div>

        <div className="simple-card">
          <h3>Avg Behavioral Score</h3>
          <p>{performance.averageBehavioralScore}</p>
        </div>

        <div className="simple-card">
          <h3>Overall Score</h3>
          <p>{performance.overallScore}</p>
        </div>

        <div className="simple-card">
          <h3>Recommendations</h3>
          <p>{recommendations.length}</p>
        </div>
      </div>

      <div className="simple-card">
        <h3>Profile</h3>
        <p><strong>Name:</strong> {user.name}</p>
        <p><strong>Email:</strong> {user.email}</p>
        <p><strong>Role:</strong> {user.role}</p>
      </div>

      <div className="simple-card">
        <h3>Quick Actions</h3>

        <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
          <button onClick={() => navigate("/coding-challenges")}>
            Start Coding Practice
          </button>

          <button onClick={() => navigate("/behavioral-questions")}>
            Start Behavioral Practice
          </button>

          <button onClick={() => navigate("/sessions")}>
            View Sessions
          </button>

          <button onClick={() => navigate("/coding-submissions")}>
            View Coding Submissions
          </button>

          <button onClick={() => navigate("/behavioral-submissions")}>
            View Behavioral Submissions
          </button>

          <button onClick={() => navigate("/recommendations")}>
            View Recommendations
          </button>
        </div>
      </div>
    </div>
  );
}