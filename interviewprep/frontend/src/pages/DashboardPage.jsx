import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getDashboardData } from "../api/dashboardApi";
import { useAuthContext } from "../context/AuthContext";

function StatCard({ title, value, subtitle }) {
  return (
    <div
      style={{
        background: "#ffffff",
        borderRadius: "16px",
        padding: "1.25rem",
        boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
        border: "1px solid #ececec",
      }}
    >
      <p
        style={{
          margin: 0,
          fontSize: "0.95rem",
          color: "#6b7280",
          fontWeight: 600,
        }}
      >
        {title}
      </p>

      <h2
        style={{
          margin: "0.5rem 0 0.25rem",
          fontSize: "2rem",
          color: "#111827",
        }}
      >
        {value}
      </h2>

      <p
        style={{
          margin: 0,
          fontSize: "0.9rem",
          color: "#9ca3af",
        }}
      >
        {subtitle}
      </p>
    </div>
  );
}

function SectionCard({ title, children }) {
  return (
    <section
      style={{
        background: "#ffffff",
        borderRadius: "18px",
        padding: "1.25rem",
        boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
        border: "1px solid #ececec",
      }}
    >
      <h3
        style={{
          marginTop: 0,
          marginBottom: "1rem",
          fontSize: "1.2rem",
          color: "#111827",
        }}
      >
        {title}
      </h3>
      {children}
    </section>
  );
}

function EmptyState({ text }) {
  return <p style={{ margin: 0, color: "#6b7280" }}>{text}</p>;
}

function formatScore(value) {
  if (value === null || value === undefined) {
    return "0.0";
  }

  return Number(value).toFixed(1);
}

function formatDate(value) {
  if (!value) {
    return "N/A";
  }

  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) {
    return "N/A";
  }

  return parsed.toLocaleString();
}

export default function DashboardPage() {
  const navigate = useNavigate();
  const { logout } = useAuthContext();

  const [data, setData] = useState({
    user: null,
    sessions: [],
    codingSubmissions: [],
    behavioralSubmissions: [],
    recommendations: [],
    performance: {
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
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadDashboard() {
      try {
        setLoading(true);
        setError("");

        const result = await getDashboardData();

        if (isMounted) {
          setData(result);
        }
      } catch (err) {
        console.error("Dashboard load failed:", err);

        if (err.response?.status === 401 || err.response?.status === 403) {
          logout();
          navigate("/login", { replace: true });
          return;
        }

        if (isMounted) {
          setError("Failed to load dashboard data.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    loadDashboard();

    return () => {
      isMounted = false;
    };
  }, [logout, navigate]);

  const performance = data.performance ?? {};

  const recentCoding = useMemo(
    () =>
      performance.recentCodingSubmissions?.length
        ? performance.recentCodingSubmissions.slice(0, 5)
        : [...data.codingSubmissions].slice(0, 5),
    [performance.recentCodingSubmissions, data.codingSubmissions]
  );

  const recentBehavioral = useMemo(
    () =>
      performance.recentBehavioralSubmissions?.length
        ? performance.recentBehavioralSubmissions.slice(0, 5)
        : [...data.behavioralSubmissions].slice(0, 5),
    [performance.recentBehavioralSubmissions, data.behavioralSubmissions]
  );

  const recentRecommendations = useMemo(
    () => [...data.recommendations].slice(0, 5),
    [data.recommendations]
  );

  const difficultyBreakdown = useMemo(
    () => Object.entries(performance.codingAverageByDifficulty ?? {}),
    [performance.codingAverageByDifficulty]
  );

  const weakAreas = useMemo(
    () => performance.weakAreas ?? [],
    [performance.weakAreas]
  );

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  if (loading) {
    return (
      <div style={{ padding: "2rem", fontSize: "1.1rem" }}>
        Loading dashboard...
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: "2rem" }}>
        <h1 style={{ marginBottom: "0.5rem" }}>Dashboard</h1>
        <p style={{ color: "crimson" }}>{error}</p>
      </div>
    );
  }

  return (
    <div
      style={{
        minHeight: "100vh",
        background: "#f5f7fb",
        padding: "2rem",
      }}
    >
      <div
        style={{
          maxWidth: "1200px",
          margin: "0 auto",
        }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            gap: "1rem",
            marginBottom: "2rem",
            flexWrap: "wrap",
          }}
        >
          <div>
            <h1
              style={{
                margin: 0,
                fontSize: "3rem",
                color: "#111827",
              }}
            >
              Dashboard
            </h1>

            <p
              style={{
                margin: "0.75rem 0 0",
                fontSize: "1.2rem",
                color: "#6b7280",
              }}
            >
              Welcome back, {data.user?.name || "User"}.
            </p>
          </div>

          <button
            onClick={handleLogout}
            style={{
              border: "none",
              background: "#111827",
              color: "#ffffff",
              padding: "0.85rem 1.2rem",
              borderRadius: "12px",
              cursor: "pointer",
              fontWeight: 600,
            }}
          >
            Logout
          </button>
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
            gap: "1rem",
            marginBottom: "1.5rem",
          }}
        >
          <StatCard
            title="Avg Coding Score"
            value={formatScore(performance.averageCodingScore)}
            subtitle="AI evaluated coding"
          />
          <StatCard
            title="Avg Behavioral Score"
            value={formatScore(performance.averageBehavioralScore)}
            subtitle="AI evaluated behavioral"
          />
          <StatCard
            title="Overall Score"
            value={formatScore(performance.overallAverageScore)}
            subtitle="Combined interview performance"
          />
          <StatCard
            title="Recommendations"
            value={data.recommendations.length}
            subtitle="Personalized coaching tips"
          />
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1.1fr 0.9fr",
            gap: "1rem",
          }}
        >
          <div
            style={{
              display: "grid",
              gap: "1rem",
            }}
          >
            <SectionCard title="Profile">
              <div style={{ display: "grid", gap: "0.65rem" }}>
                <p style={{ margin: 0 }}>
                  <strong>Name:</strong> {data.user?.name || "N/A"}
                </p>
                <p style={{ margin: 0 }}>
                  <strong>Email:</strong> {data.user?.email || "N/A"}
                </p>
                <p style={{ margin: 0 }}>
                  <strong>Role:</strong> {data.user?.role || "N/A"}
                </p>
              </div>
            </SectionCard>

            <SectionCard title="Weak Areas">
              {weakAreas.length === 0 ? (
                <EmptyState text="No weak areas detected." />
              ) : (
                <div style={{ display: "grid", gap: "0.75rem" }}>
                  {weakAreas.map((area, index) => (
                    <div
                      key={`${area}-${index}`}
                      style={{
                        padding: "0.9rem 1rem",
                        borderRadius: "12px",
                        background: "#fef2f2",
                        border: "1px solid #fecaca",
                        color: "#991b1b",
                        fontWeight: 500,
                      }}
                    >
                      {area}
                    </div>
                  ))}
                </div>
              )}
            </SectionCard>

            <SectionCard title="Coding Score by Difficulty">
              {difficultyBreakdown.length === 0 ? (
                <EmptyState text="No coding difficulty analytics yet." />
              ) : (
                <div style={{ display: "grid", gap: "0.75rem" }}>
                  {difficultyBreakdown.map(([difficulty, score]) => (
                    <div
                      key={difficulty}
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        padding: "0.9rem 1rem",
                        borderRadius: "12px",
                        background: "#f9fafb",
                        border: "1px solid #ececec",
                      }}
                    >
                      <span style={{ fontWeight: 600, color: "#111827" }}>
                        {difficulty}
                      </span>
                      <span style={{ color: "#4b5563" }}>
                        {formatScore(score)}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </SectionCard>

            <SectionCard title="Recent Coding Submissions">
              {recentCoding.length === 0 ? (
                <EmptyState text="No coding submissions yet." />
              ) : (
                <div style={{ display: "grid", gap: "0.75rem" }}>
                  {recentCoding.map((submission, index) => (
                    <div
                      key={submission.submissionId ?? submission.id ?? `coding-${index}`}
                      style={{
                        padding: "0.9rem 1rem",
                        borderRadius: "12px",
                        background: "#f9fafb",
                        border: "1px solid #ececec",
                      }}
                    >
                      <p style={{ margin: 0, fontWeight: 600 }}>
                        {submission.title || `Submission #${submission.id ?? index + 1}`}
                      </p>
                      <p style={{ margin: "0.35rem 0 0", color: "#6b7280" }}>
                        Difficulty: {submission.difficulty || "N/A"}
                      </p>
                      <p style={{ margin: "0.25rem 0 0", color: "#6b7280" }}>
                        Score: {submission.score ?? "N/A"}
                      </p>
                      <p style={{ margin: "0.25rem 0 0", color: "#9ca3af", fontSize: "0.9rem" }}>
                        {formatDate(submission.submittedAt)}
                      </p>
                    </div>
                  ))}
                </div>
              )}
            </SectionCard>

            <SectionCard title="Recent Behavioral Submissions">
              {recentBehavioral.length === 0 ? (
                <EmptyState text="No behavioral submissions yet." />
              ) : (
                <div style={{ display: "grid", gap: "0.75rem" }}>
                  {recentBehavioral.map((submission, index) => (
                    <div
                      key={submission.submissionId ?? submission.id ?? `behavioral-${index}`}
                      style={{
                        padding: "0.9rem 1rem",
                        borderRadius: "12px",
                        background: "#f9fafb",
                        border: "1px solid #ececec",
                      }}
                    >
                      <p style={{ margin: 0, fontWeight: 600 }}>
                        {submission.title || `Submission #${submission.id ?? index + 1}`}
                      </p>
                      <p style={{ margin: "0.35rem 0 0", color: "#6b7280" }}>
                        Difficulty: {submission.difficulty || "N/A"}
                      </p>
                      <p style={{ margin: "0.25rem 0 0", color: "#6b7280" }}>
                        Score: {submission.score ?? "N/A"}
                      </p>
                      <p style={{ margin: "0.25rem 0 0", color: "#9ca3af", fontSize: "0.9rem" }}>
                        {formatDate(submission.submittedAt)}
                      </p>
                    </div>
                  ))}
                </div>
              )}
            </SectionCard>
          </div>

          <div
            style={{
              display: "grid",
              gap: "1rem",
              alignContent: "start",
            }}
          >
            <SectionCard title="Recommendations">
              {recentRecommendations.length === 0 ? (
                <EmptyState text="No recommendations yet." />
              ) : (
                <div style={{ display: "grid", gap: "0.75rem" }}>
                  {recentRecommendations.map((recommendation, index) => (
                    <div
                      key={recommendation.id ?? `recommendation-${index}`}
                      style={{
                        padding: "1rem",
                        borderRadius: "12px",
                        background: "#f9fafb",
                        border: "1px solid #ececec",
                      }}
                    >
                      <p style={{ margin: 0, fontWeight: 600, color: "#111827" }}>
                        {recommendation.recommended || `Recommendation #${index + 1}`}
                      </p>
                      <p style={{ margin: "0.4rem 0 0", color: "#6b7280", lineHeight: 1.5 }}>
                        {recommendation.reason || "Recommendation available."}
                      </p>
                    </div>
                  ))}
                </div>
              )}
            </SectionCard>

            <SectionCard title="Quick Actions">
              <div style={{ display: "grid", gap: "0.75rem" }}>
                <button
                  onClick={() => navigate("/sessions")}
                  style={buttonStyle}
                >
                  View Sessions
                </button>
                <button
                  onClick={() => navigate("/coding-submissions")}
                  style={buttonStyle}
                >
                  View Coding Submissions
                </button>
                <button
                  onClick={() => navigate("/behavioral-submissions")}
                  style={buttonStyle}
                >
                  View Behavioral Submissions
                </button>
                <button
                  onClick={() => navigate("/recommendations")}
                  style={buttonStyle}
                >
                  View Recommendations
                </button>
              </div>
            </SectionCard>
          </div>
        </div>
      </div>
    </div>
  );
}

const buttonStyle = {
  border: "1px solid #d1d5db",
  background: "#ffffff",
  color: "#111827",
  padding: "0.9rem 1rem",
  borderRadius: "12px",
  cursor: "pointer",
  fontWeight: 600,
  textAlign: "left",
};