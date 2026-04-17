import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { ui } from "../styles/ui";

export default function CodingSubmissionPage() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchSubmissions();
  }, []);

  const fetchSubmissions = async () => {
    try {
      const response = await api.get("/api/coding-submissions/me");
      setSubmissions(response.data);
    } catch (err) {
      setError("Failed to load coding submissions.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={ui.page}>
        <div style={ui.container}>
          <div style={ui.card}>Loading submissions...</div>
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
              <h1 style={ui.heroTitle}>My Coding Submissions</h1>
              <p style={ui.heroSubtitle}>
                Review your saved coding attempts, statuses, and scores.
              </p>
            </div>

            <button style={ui.secondaryButton} onClick={() => navigate("/dashboard")}>
              Back to Dashboard
            </button>
          </div>
        </section>

        {error && <div style={ui.error}>{error}</div>}

        {submissions.length === 0 ? (
          <div style={ui.card}>No submissions found.</div>
        ) : (
          <section style={ui.gridCards}>
            {submissions.map((sub) => (
              <article key={sub.id} style={ui.card}>
                <div style={{ display: "flex", justifyContent: "space-between", gap: "12px", alignItems: "center", flexWrap: "wrap" }}>
                  <h3 style={{ margin: 0, fontSize: "20px" }}>Submission #{sub.id}</h3>
                  <span style={ui.badge}>{sub.status}</span>
                </div>

                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
                    gap: "12px",
                    marginTop: "16px",
                  }}
                >
                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Score</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                      {sub.score ?? "Pending"}
                    </p>
                  </div>

                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Challenge ID</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>{sub.challengeId}</p>
                  </div>

                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Session ID</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>{sub.sessionId}</p>
                  </div>
                </div>

                <div style={{ ...ui.infoBox, marginTop: "12px" }}>
                  <p style={ui.statLabel}>Submitted At</p>
                  <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                    {sub.submittedAt}
                  </p>
                </div>
              </article>
            ))}
          </section>
        )}
      </div>
    </div>
  );
}