import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

export default function BehavioralQuestionSessionPage() {
  const { sessionId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  const passedSession = location.state?.session || null;
  const passedQuestion = location.state?.question || null;

  const [session] = useState(passedSession);
  const [question] = useState(passedQuestion);
  const [responseText, setResponseText] = useState("");
  const [feedback, setFeedback] = useState(null);
  const [submission, setSubmission] = useState(null);
  const [timeLeft, setTimeLeft] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!session?.expiresAt) return;

    const interval = setInterval(() => {
      const now = new Date().getTime();
      const end = new Date(session.expiresAt).getTime();
      const diff = end - now;

      if (diff <= 0) {
        setTimeLeft("Expired");
        clearInterval(interval);
        return;
      }

      const minutes = Math.floor(diff / 1000 / 60);
      const seconds = Math.floor((diff / 1000) % 60);
      setTimeLeft(`${minutes}:${seconds.toString().padStart(2, "0")}`);
    }, 1000);

    return () => clearInterval(interval);
  }, [session]);

  const sessionExpired = useMemo(() => {
    if (!session?.expiresAt) return false;
    return new Date() > new Date(session.expiresAt);
  }, [session, timeLeft]);

  const fetchFeedback = async (submissionId) => {
    try {
      const response = await api.get(
        `/api/feedback/behavioral-submission/${submissionId}`
      );
      setFeedback(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!question) {
      setError("Question data missing. Please go back and start again.");
      return;
    }

    if (sessionExpired) {
      setError("This session has expired. You can no longer submit.");
      return;
    }

    setSubmitting(true);
    setError("");

    try {
      const response = await api.post("/api/behavioral-submissions", {
        responseText,
        score: null,
        submittedAt: new Date().toISOString(),
        status: "SUBMITTED",
        questionId: question.id,
        sessionId: Number(sessionId),
      });

      setSubmission(response.data);
      await fetchFeedback(response.data.id);
      await api.post("/api/recommendations/generate/me");
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          err.message ||
          "Failed to submit response."
      );
    } finally {
      setSubmitting(false);
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
      fontSize: "28px",
      marginBottom: "20px",
    },

    topBar: {
      marginBottom: "16px",
    },

    button: {
      padding: "10px 14px",
      border: "none",
      borderRadius: "8px",
      cursor: "pointer",
      backgroundColor: "#111827",
      color: "white",
      transition: "0.2s ease",
    },

    card: {
      background: "white",
      borderRadius: "12px",
      padding: "18px",
      marginBottom: "16px",
      boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    },

    expiredCard: {
      border: "1px solid #ef4444",
    },

    textarea: {
      width: "100%",
      marginTop: "10px",
      padding: "10px",
      borderRadius: "8px",
      border: "1px solid #d1d5db",
      fontFamily: "inherit",
      fontSize: "14px",
      resize: "vertical",
    },

    error: {
      color: "red",
      marginTop: "10px",
    },
  };

  if (!session || !question) {
    return (
      <div style={styles.page}>
        <p>
          Session or question data is missing. Go back and start again.
        </p>
        <button
          style={styles.button}
          onClick={() => navigate("/behavioral-questions")}
        >
          Back to Questions
        </button>
      </div>
    );
  }

  return (
    <div style={styles.page}>
      <h2 style={styles.title}>Behavioral Session</h2>

      <div style={styles.topBar}>
        <button
          style={styles.button}
          onClick={() => navigate("/behavioral-questions")}
        >
          Back to Questions
        </button>
      </div>

      {/* Session Info */}
      <div style={styles.card}>
        <p>
          <strong>Session ID:</strong> {session.id}
        </p>
        <p>
          <strong>Time Left:</strong>{" "}
          {sessionExpired ? "Expired" : timeLeft || "Loading timer..."}
        </p>
      </div>

      {/* Expired Warning */}
      {sessionExpired && (
        <div style={{ ...styles.card, ...styles.expiredCard }}>
          <h3>Session Expired</h3>
          <p>Your time is up. You can no longer submit this response.</p>
        </div>
      )}

      {/* Question Card */}
      <div style={styles.card}>
        <h3>{question.category}</h3>
        <p>{question.questionText}</p>
        <p>
          <strong>Difficulty:</strong> {question.difficulty}
        </p>
      </div>

      {/* Form */}
      <form onSubmit={handleSubmit} style={styles.card}>
        <label>Your Response</label>

        <textarea
          style={styles.textarea}
          rows="12"
          value={responseText}
          onChange={(e) => setResponseText(e.target.value)}
          placeholder="Write your response using STAR..."
          disabled={sessionExpired}
        />

        {error && <p style={styles.error}>{error}</p>}

        <button
          style={{ ...styles.button, marginTop: "10px" }}
          type="submit"
          disabled={submitting || sessionExpired || !responseText.trim()}
        >
          {sessionExpired
            ? "Session Expired"
            : submitting
            ? "Submitting..."
            : "Submit Response"}
        </button>
      </form>

      {/* Submission */}
      {submission && (
        <div style={styles.card}>
          <h3>Submission Saved</h3>
          <p>
            <strong>Submission ID:</strong> {submission.id}
          </p>
          <p>
            <strong>Status:</strong> {submission.status}
          </p>
          <p>
            <strong>Submitted At:</strong> {submission.submittedAt}
          </p>
          <p>
            <strong>Score:</strong> {submission.score ?? "Pending"}
          </p>
        </div>
      )}

      {/* Feedback */}
      {feedback && (
        <div style={styles.card}>
          <h3>AI Feedback</h3>
          <p>
            <strong>AI Score:</strong> {feedback.aiScore}
          </p>
          <p>
            <strong>Summary:</strong> {feedback.summary}
          </p>
          <p>
            <strong>Strengths:</strong> {feedback.strengths}
          </p>
          <p>
            <strong>Weaknesses:</strong> {feedback.weaknesses}
          </p>
          <p>
            <strong>Recommendations:</strong> {feedback.recommendations}
          </p>
          <p>
            <strong>Status:</strong> {feedback.status}
          </p>
        </div>
      )}
    </div>
  );
}