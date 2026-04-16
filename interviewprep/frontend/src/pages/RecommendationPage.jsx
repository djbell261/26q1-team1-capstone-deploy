import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import client from "../services/client";
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

        const response = await client.get("/recommendations/me");

        if (isMounted) {
          setRecommendations(response.data ?? []);
        }
      } catch (err) {
        console.error("Failed to load recommendations:", err);

        if (err.response?.status === 401 || err.response?.status === 403) {
          logout();
          navigate("/login", { replace: true });
          return;
        }

        if (isMounted) {
          setError("Failed to load recommendations.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    loadRecommendations();

    return () => {
      isMounted = false;
    };
  }, [logout, navigate]);

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
          maxWidth: "1100px",
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
                fontSize: "2.5rem",
                color: "#111827",
              }}
            >
              Recommendations
            </h1>
            <p
              style={{
                margin: "0.5rem 0 0",
                color: "#6b7280",
                fontSize: "1rem",
              }}
            >
              Your personalized interview prep guidance.
            </p>
          </div>

          <button
            onClick={() => navigate("/dashboard")}
            style={topButtonStyle}
          >
            Back to Dashboard
          </button>
        </div>

        {loading && (
          <div style={messageCardStyle}>
            <p style={{ margin: 0 }}>Loading recommendations...</p>
          </div>
        )}

        {!loading && error && (
          <div style={messageCardStyle}>
            <p style={{ margin: 0, color: "crimson" }}>{error}</p>
          </div>
        )}

        {!loading && !error && recommendations.length === 0 && (
          <div style={messageCardStyle}>
            <EmptyState text="No recommendations yet." />
          </div>
        )}

        {!loading && !error && recommendations.length > 0 && (
          <div
            style={{
              display: "grid",
              gap: "1rem",
            }}
          >
            {recommendations.map((recommendation, index) => (
              <div
                key={recommendation.id ?? `recommendation-${index}`}
                style={cardStyle}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "flex-start",
                    gap: "1rem",
                    flexWrap: "wrap",
                    marginBottom: "1rem",
                  }}
                >
                  <div>
                    <h2
                      style={{
                        margin: 0,
                        fontSize: "1.15rem",
                        color: "#111827",
                      }}
                    >
                      {recommendation.recommended ||
                        `Recommendation #${recommendation.id ?? index + 1}`}
                    </h2>

                    <p style={metaTextStyle}>
                      User ID:{" "}
                      {recommendation.userId ?? recommendation.user?.id ?? "N/A"}
                    </p>
                  </div>

                  <div
                    style={{
                      padding: "0.45rem 0.75rem",
                      borderRadius: "999px",
                      background: "#ecfeff",
                      color: "#155e75",
                      fontWeight: 600,
                      fontSize: "0.9rem",
                    }}
                  >
                    Personalized
                  </div>
                </div>

                <div
                  style={{
                    display: "grid",
                    gap: "0.9rem",
                  }}
                >
                  <div>
                    <p style={labelStyle}>Recommendation</p>
                    <p style={valueStyle}>
                      {recommendation.recommended || "Untitled Recommendation"}
                    </p>
                  </div>

                  <div>
                    <p style={labelStyle}>Reason</p>
                    <div style={contentBoxStyle}>
                      <p
                        style={{
                          margin: 0,
                          color: "#111827",
                          lineHeight: 1.6,
                        }}
                      >
                        {recommendation.reason || "No reason provided."}
                      </p>
                    </div>
                  </div>

                  <div
                    style={{
                      display: "grid",
                      gridTemplateColumns:
                        "repeat(auto-fit, minmax(180px, 1fr))",
                      gap: "0.75rem",
                    }}
                  >
                    <div style={smallInfoBoxStyle}>
                      <p style={labelStyle}>Created</p>
                      <p style={valueStyle}>
                        {recommendation.createdAt
                          ? new Date(recommendation.createdAt).toLocaleString()
                          : "N/A"}
                      </p>
                    </div>

                    <div style={smallInfoBoxStyle}>
                      <p style={labelStyle}>User ID</p>
                      <p style={valueStyle}>
                        {recommendation.userId ??
                          recommendation.user?.id ??
                          "N/A"}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

const topButtonStyle = {
  border: "none",
  background: "#111827",
  color: "#ffffff",
  padding: "0.85rem 1.1rem",
  borderRadius: "12px",
  cursor: "pointer",
  fontWeight: 600,
};

const messageCardStyle = {
  background: "#ffffff",
  borderRadius: "16px",
  padding: "1rem 1.25rem",
  border: "1px solid #ececec",
  boxShadow: "0 8px 24px rgba(0,0,0,0.06)",
};

const cardStyle = {
  background: "#ffffff",
  borderRadius: "18px",
  padding: "1.25rem",
  border: "1px solid #ececec",
  boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
};

const contentBoxStyle = {
  background: "#f9fafb",
  border: "1px solid #e5e7eb",
  borderRadius: "12px",
  padding: "1rem",
};

const smallInfoBoxStyle = {
  background: "#f9fafb",
  border: "1px solid #e5e7eb",
  borderRadius: "12px",
  padding: "0.9rem",
};

const labelStyle = {
  margin: 0,
  fontSize: "0.85rem",
  color: "#6b7280",
  fontWeight: 600,
};

const valueStyle = {
  margin: "0.35rem 0 0",
  fontSize: "0.98rem",
  color: "#111827",
  fontWeight: 500,
};

const metaTextStyle = {
  margin: "0.45rem 0 0",
  color: "#6b7280",
  fontSize: "0.95rem",
};