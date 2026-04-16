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
        state: {
          session,
          question,
        },
      });
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          err.message ||
          "Failed to start behavioral session."
      );
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <p>Loading behavioral questions...</p>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h2>Behavioral Question Library</h2>

      <div style={{ marginBottom: "16px" }}>
        <button onClick={() => navigate("/dashboard")}>Back to Dashboard</button>
      </div>

      {error && <p>{error}</p>}

      {questions.length === 0 ? (
        <p>No behavioral questions found.</p>
      ) : (
        <div className="card-list">
          {questions.map((question) => (
            <div key={question.id} className="simple-card">
              <h3>{question.category}</h3>
              <p>{question.questionText}</p>
              <p><strong>Difficulty:</strong> {question.difficulty}</p>

              <button onClick={() => startSession(question)}>
                Start Question
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}