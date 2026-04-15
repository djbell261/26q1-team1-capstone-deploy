import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import client from "../api/client";
import { useAuthContext } from "../context/AuthContext";

function EmptyState({ text }) {
  return <p style={{ margin: 0, color: "#6b7280" }}>{text}</p>;
}

export default function BehaviorSubmission() {
  const navigate = useNavigate();
  const { logout } = useAuthContext();

  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadBehavioralSubmissions() {
      try {
        setLoading(true);
        setError("");

        const response = await client.get("/behavioral-submissions/me");

        if (isMounted) {
          setSubmissions(response.data ?? []);
        }
      } catch (err) {
        console.error("Failed to load behavioral submissions:", err);

        if (err.response?.status === 401 || err.response?.status === 403) {
          logout();
          navigate("/login", { replace: true });
          return;
        }

        if (isMounted) {
          setError("Failed to load behavioral submissions.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    loadBehavioralSubmissions();

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
              Behavioral Submissions
            </h1>
            <p
              style={{
                margin: "0.5rem 0 0",
                color: "#6b7280",
                fontSize: "1rem",
              }}
            >
              Review your past behavioral interview responses.
            </p>
          </div>

          <button onClick={() => navigate("/dashboard")} style={topButtonStyle}>
            Back to Dashboard
          </button>
        </div>

        {loading && (
          <div style={messageCardStyle}>
            <p style={{ margin: 0 }}>Loading behavioral submissions...</p>
          </div>
        )}

        {!loading && error && (
          <div style={messageCardStyle}>
            <p style={{ margin: 0, color: "crimson" }}>{error}</p>
          </div>
        )}

        {!loading && !error && submissions.length === 0 && (
          <div style={messageCardStyle}>
            <EmptyState text="No behavioral submissions yet." />
          </div>
        )}

        {!loading && !error && submissions.length > 0 && (
          <div
            style={{
              display: "grid",
              gap: "1rem",
            }}
          >
            {submissions.map((submission, index) => (
              <div
                key={submission.id ?? `behavioral-submission-${index}`}
                style={cardStyle}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "flex-start",
                    gap: "1rem",
                    flexWrap: "wrap",
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
                      Submission #{submission.id ?? index + 1}
                    </h2>

                    <p style={metaTextStyle}>
                      Question ID:{" "}
                      {submission.questionId ??
                        submission.question?.id ??
                        "N/A"}
                    </p>

                    <p style={metaTextStyle}>
                      Session ID:{" "}
                      {submission.sessionId ?? submission.session?.id ?? "N/A"}
                    </p>
                  </div>

                  <div
                    style={{
                      padding: "0.45rem 0.75rem",
                      borderRadius: "999px",
                      background: "#fef3c7",
                      color: "#92400e",
                      fontWeight: 600,
                      fontSize: "0.9rem",
                    }}
                  >
                    Behavioral Practice
                  </div>
                </div>

                <div
                  style={{
                    marginTop: "1rem",
                    display: "grid",
                    gap: "0.75rem",
                  }}
                >
                  <div>
                    <p style={labelStyle}>Response</p>
                    <div style={contentBoxStyle}>
                      <p
                        style={{
                          margin: 0,
                          color: "#111827",
                          lineHeight: 1.6,
                          whiteSpace: "pre-wrap",
                          wordBreak: "break-word",
                        }}
                      >
                        {submission.response ||
                          submission.answer ||
                          submission.content ||
                          "No response available."}
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
                      <p style={labelStyle}>Status</p>
                      <p style={valueStyle}>
                        {submission.status || "Submitted"}
                      </p>
                    </div>

                    <div style={smallInfoBoxStyle}>
                      <p style={labelStyle}>Score</p>
                      <p style={valueStyle}>
                        {submission.score ?? "N/A"}
                      </p>
                    </div>

                    <div style={smallInfoBoxStyle}>
                      <p style={labelStyle}>Confidence</p>
                      <p style={valueStyle}>
                        {submission.confidenceScore ?? "N/A"}
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