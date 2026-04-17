import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { ui } from "../styles/ui";

export default function CodingChallengeLibraryPage() {
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchChallenges();
  }, []);

  const fetchChallenges = async () => {
    try {
      const response = await api.get("/api/coding-challenges");
      setChallenges(response.data);
    } catch (err) {
      setError("Failed to load coding challenges.");
    } finally {
      setLoading(false);
    }
  };

  const startSession = async (challenge) => {
    try {
      const now = new Date();
      const expires = new Date(
        now.getTime() + (challenge.timeLimitMinutes || 30) * 60 * 1000
      );

      const response = await api.post("/api/sessions", {
        type: "CODING",
        startedAt: now.toISOString(),
        expiresAt: expires.toISOString(),
        status: "ACTIVE",
      });

      const session = response.data;

      navigate(`/coding-session/${session.id}`, {
        state: { session, challenge },
      });
    } catch (err) {
      setError("Failed to start coding session.");
    }
  };

  if (loading) {
    return (
      <div style={ui.page}>
        <div style={ui.container}>
          <div style={ui.card}>Loading coding challenges...</div>
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
              <h1 style={ui.heroTitle}>Coding Challenge Library</h1>
              <p style={ui.heroSubtitle}>
                Choose a challenge, start a timed session, and get AI-powered feedback on your solution.
              </p>
            </div>

            <button style={ui.secondaryButton} onClick={() => navigate("/dashboard")}>
              Back to Dashboard
            </button>
          </div>
        </section>

        {error && <div style={ui.error}>{error}</div>}

        {challenges.length === 0 ? (
          <div style={ui.card}>No coding challenges found.</div>
        ) : (
          <section style={ui.gridCards}>
            {challenges.map((challenge) => (
              <article
                key={challenge.id}
                style={{
                  ...ui.card,
                  display: "flex",
                  flexDirection: "column",
                  minHeight: "290px",
                }}
              >
                <div style={{ display: "flex", justifyContent: "space-between", gap: "12px" }}>
                  <h3 style={{ margin: 0, fontSize: "22px" }}>{challenge.title}</h3>
                  <span style={ui.badge}>{challenge.difficulty}</span>
                </div>

                <p style={{ ...ui.muted, lineHeight: 1.7, margin: "14px 0 18px" }}>
                  {challenge.description || "No description provided."}
                </p>

                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
                    gap: "12px",
                    marginBottom: "18px",
                  }}
                >
                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Category</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                      {challenge.category || "General"}
                    </p>
                  </div>

                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Time Limit</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                      {challenge.timeLimitMinutes || 30} min
                    </p>
                  </div>
                </div>

                <div style={{ marginTop: "auto" }}>
                  <button style={ui.button} onClick={() => startSession(challenge)}>
                    Start Challenge
                  </button>
                </div>
              </article>
            ))}
          </section>
        )}
      </div>
    </div>
  );
}