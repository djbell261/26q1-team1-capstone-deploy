import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { ui } from "../styles/ui";

export default function BehavioralQuestionLibraryPage() {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchQuestions();
  }, []);

  const fetchQuestions = async () => {
    try {
      const response = await api.get("/api/behavioral-questions");
      setQuestions(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load behavioral questions.");
    } finally {
      setLoading(false);
    }
  };

  const startSession = async (question) => {
    try {
      const now = new Date();
      const expires = new Date(now.getTime() + 15 * 60 * 1000);

      const response = await api.post("/api/sessions", {
        type: "BEHAVIORAL",
        startedAt: now.toISOString(),
        expiresAt: expires.toISOString(),
        status: "ACTIVE",
      });

      const session = response.data;

      navigate(`/behavioral-session/${session.id}`, {
        state: { session, question },
      });
    } catch (err) {
      setError("Failed to start behavioral session.");
    }
  };

  if (loading) {
    return (
      <div style={ui.page}>
        <div style={ui.container}>
          <div style={ui.card}>Loading behavioral questions...</div>
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
              <h1 style={ui.heroTitle}>Behavioral Question Library</h1>
              <p style={ui.heroSubtitle}>
                Practice STAR-based interview answers in timed sessions and review your AI feedback.
              </p>
            </div>

            <button style={ui.secondaryButton} onClick={() => navigate("/dashboard")}>
              Back to Dashboard
            </button>
          </div>
        </section>

        {error && <div style={ui.error}>{error}</div>}

        {questions.length === 0 ? (
          <div style={ui.card}>No behavioral questions found.</div>
        ) : (
          <section style={ui.gridCards}>
            {questions.map((question) => (
              <article
                key={question.id}
                style={{
                  ...ui.card,
                  display: "flex",
                  flexDirection: "column",
                  minHeight: "290px",
                }}
              >
                <div style={{ display: "flex", justifyContent: "space-between", gap: "12px" }}>
                  <h3 style={{ margin: 0, fontSize: "22px" }}>{question.category}</h3>
                  <span style={ui.badge}>{question.difficulty}</span>
                </div>

                <p style={{ ...ui.muted, lineHeight: 1.7, margin: "14px 0 18px" }}>
                  {question.questionText}
                </p>

                <div style={ui.infoBox}>
                  <p style={ui.statLabel}>Interview Focus</p>
                  <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                    {question.category || "Behavioral"}
                  </p>
                </div>

                <div style={{ marginTop: "auto", paddingTop: "18px" }}>
                  <button style={ui.button} onClick={() => startSession(question)}>
                    Start Question
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