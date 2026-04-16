import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuthContext } from "../context/AuthContext";

function EmptyState({ text }) {
  return <p style={{ margin: 0, color: "#6b7280" }}>{text}</p>;
}

export default function RecommendationPage() {
  const navigate = useNavigate();
  const { logout } = useAuthContext();

  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadRecommendations() {
      try {
        setLoading(true);
        setError("");

        await api.post("/api/recommendations/generate/me");
        const response = await api.get("/api/recommendations/me");

        if (isMounted) {
          setRecommendations(response.data ?? []);
        }
      } catch (err) {
        if (err.response?.status === 401 || err.response?.status === 403) {
          logout();
          navigate("/login", { replace: true });
          return;
        }

        if (isMounted) {
          setError("Failed to load recommendations.");
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    }

    loadRecommendations();

    return () => {
      isMounted = false;
    };
  }, [logout, navigate]);

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
      marginBottom: "24px",
    },

    title: {
      fontSize: "32px",
      margin: 0,
    },

    subtitle: {
      margin: "6px 0 0",
      color: "#6b7280",
    },

    button: {
      border: "none",
      background: "#111827",
      color: "#fff",
      padding: "10px 14px",
      borderRadius: "8px",
      cursor: "pointer",
    },

    grid: {
      display: "grid",
      gap: "16px",
    },

    card: {
      background: "#fff",
      borderRadius: "12px",
      padding: "18px",
      boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    },

    badge: {
      padding: "6px 10px",
      borderRadius: "999px",
      background: "#ecfeff",
      color: "#155e75",
      fontWeight: 600,
      fontSize: "12px",
      whiteSpace: "nowrap",
    },

    sectionTitle: {
      margin: "0 0 8px",
      fontSize: "18px",
    },

    box: {
      background: "#f9fafb",
      border: "1px solid #e5e7eb",
      borderRadius: "10px",
      padding: "12px",
    },

    label: {
      margin: 0,
      fontSize: "12px",
      color: "#6b7280",
      fontWeight: 600,
    },

    value: {
      margin: "4px 0 0",
      fontSize: "14px",
      color: "#111827",
    },

    error: {
      color: "crimson",
    },
  };

  return (
    <div style={styles.page}>
      {/* HEADER */}
      <div style={styles.header}>
        <div>
          <h1 style={styles.title}>Recommendations</h1>
          <p style={styles.subtitle}>
            Your personalized interview prep guidance.
          </p>
        </div>

        <button style={styles.button} onClick={() => navigate("/dashboard")}>
          Back to Dashboard
        </button>
      </div>

      {/* LOADING */}
      {loading && (
        <div style={styles.card}>
          <p style={{ margin: 0 }}>Loading recommendations...</p>
        </div>
      )}

      {/* ERROR */}
      {!loading && error && (
        <div style={styles.card}>
          <p style={styles.error}>{error}</p>
        </div>
      )}

      {/* EMPTY */}
      {!loading && !error && recommendations.length === 0 && (
        <div style={styles.card}>
          <EmptyState text="No recommendations yet." />
        </div>
      )}

      {/* LIST */}
      {!loading && !error && recommendations.length > 0 && (
        <div style={styles.grid}>
          {recommendations.map((rec, index) => (
            <div key={rec.id ?? index} style={styles.card}>
              {/* TOP ROW */}
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                  marginBottom: "12px",
                  gap: "12px",
                }}
              >
                <div>
                  <h2 style={styles.sectionTitle}>
                    {rec.recommended ||
                      `Recommendation #${rec.id ?? index + 1}`}
                  </h2>

                  <p style={{ margin: "4px 0 0", color: "#6b7280" }}>
                    User ID: {rec.userId ?? rec.user?.id ?? "N/A"}
                  </p>
                </div>

                <span style={styles.badge}>Personalized</span>
              </div>

              {/* RECOMMENDATION */}
              <div style={{ marginBottom: "12px" }}>
                <p style={styles.label}>Recommendation</p>
                <p style={styles.value}>
                  {rec.recommended || "Untitled Recommendation"}
                </p>
              </div>

              {/* REASON */}
              <div style={{ marginBottom: "12px" }}>
                <p style={styles.label}>Reason</p>
                <div style={styles.box}>
                  <p style={{ margin: 0, lineHeight: 1.6 }}>
                    {rec.reason || "No reason provided."}
                  </p>
                </div>
              </div>

              {/* META */}
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns:
                    "repeat(auto-fit, minmax(180px, 1fr))",
                  gap: "12px",
                }}
              >
                <div style={styles.box}>
                  <p style={styles.label}>Created</p>
                  <p style={styles.value}>
                    {rec.createdAt
                      ? new Date(rec.createdAt).toLocaleString()
                      : "N/A"}
                  </p>
                </div>

                <div style={styles.box}>
                  <p style={styles.label}>User ID</p>
                  <p style={styles.value}>
                    {rec.userId ?? rec.user?.id ?? "N/A"}
                  </p>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}