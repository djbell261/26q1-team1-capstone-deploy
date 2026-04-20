import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuthContext } from "../context/AuthContext";
import { ui } from "../styles/ui";

function EmptyState({ text }) {
  return <p style={{ margin: 0, color: "#64748b" }}>{text}</p>;
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

  return (
    <div style={ui.page}>
      <div style={ui.container}>
        <section style={ui.hero}>
          <div style={ui.topRow}>
            <div>
              <h1 style={ui.heroTitle}>Recommendations</h1>
              <p style={ui.heroSubtitle}>
                Personalized interview prep guidance based on your recent activity and performance.
              </p>
            </div>

            <button style={ui.secondaryButton} onClick={() => navigate("/dashboard")}>
              Back to Dashboard
            </button>
          </div>
        </section>

        {loading && <div style={ui.card}>Loading recommendations...</div>}

        {!loading && error && <div style={ui.error}>{error}</div>}

        {!loading && !error && recommendations.length === 0 && (
          <div style={ui.card}>
            <EmptyState text="No recommendations yet." />
          </div>
        )}

        {!loading && !error && recommendations.length > 0 && (
          <section style={ui.gridCards}>
            {recommendations.map((rec, index) => (
              <article key={rec.id ?? index} style={ui.card}>
                <div style={{ display: "flex", justifyContent: "space-between", gap: "12px", alignItems: "flex-start" }}>
                  <div>
                    <h2 style={{ margin: 0, fontSize: "22px" }}>
                      {rec.recommended || `Recommendation #${rec.id ?? index + 1}`}
                    </h2>
                    <p style={{ margin: "8px 0 0", ...ui.muted }}>
                      User ID: {rec.userId ?? rec.user?.id ?? "N/A"}
                    </p>
                  </div>

                  <span style={ui.badge}>Personalized</span>
                </div>

                <div style={{ marginTop: "18px" }}>
                  <p style={ui.statLabel}>Recommendation</p>
                  <div style={{ ...ui.infoBox, marginTop: "8px" }}>
                    {rec.recommended || "Untitled Recommendation"}
                  </div>
                </div>

                <div style={{ marginTop: "14px" }}>
                  <p style={ui.statLabel}>Reason</p>
                  <div style={{ ...ui.infoBox, marginTop: "8px", lineHeight: 1.7 }}>
                    {rec.reason || "No reason provided."}
                  </div>
                </div>

                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
                    gap: "12px",
                    marginTop: "14px",
                  }}
                >
                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Created</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                      {rec.createdAt ? new Date(rec.createdAt).toLocaleString() : "N/A"}
                    </p>
                  </div>

                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>User ID</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                      {rec.userId ?? rec.user?.id ?? "N/A"}
                    </p>
                  </div>
                </div>
              </article>
            ))}
          </section>
        )}
      </div>
    </div>
  );
}