import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

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

  const styles = {
    page: {
      maxWidth: "1000px",
      margin: "40px auto",
      padding: "20px",
      fontFamily: "Arial, sans-serif",
      color: "#1f2937",
    },

    title: {
      textAlign: "center",
      marginBottom: "20px",
      fontSize: "28px",
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
      gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))",
      gap: "16px",
      marginTop: "20px",
    },

    card: {
      background: "white",
      borderRadius: "12px",
      padding: "16px",
      boxShadow: "0 4px 10px rgba(0,0,0,0.08)",
    },
  };

  if (loading) {
    return (
      <div style={styles.page}>
        <p>Loading behavioral questions...</p>
      </div>
    );
  }

  return (
    <div style={styles.page}>
      <h2 style={styles.title}>Behavioral Question Library</h2>

      <div style={{ marginBottom: "16px" }}>
        <button style={styles.button} onClick={() => navigate("/dashboard")}>
          Back to Dashboard
        </button>
      </div>

      {error && <p style={{ color: "red" }}>{error}</p>}

      {questions.length === 0 ? (
        <p>No behavioral questions found.</p>
      ) : (
        <div style={styles.grid}>
          {questions.map((question) => (
            <div key={question.id} style={styles.card}>
              <h3>{question.category}</h3>
              <p>{question.questionText}</p>
              <p>
                <strong>Difficulty:</strong> {question.difficulty}
              </p>

              <button style={{ ...styles.button, marginTop: "10px" }} onClick={() => startSession(question)}>
                Start Question
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}