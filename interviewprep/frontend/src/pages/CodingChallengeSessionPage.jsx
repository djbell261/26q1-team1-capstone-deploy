import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

export default function CodingChallengeSessionPage() {
  const { sessionId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  const passedSession = location.state?.session || null;
  const passedChallenge = location.state?.challenge || null;

  const [session] = useState(passedSession);
  const [challenge] = useState(passedChallenge);
  const [code, setCode] = useState("");
  const [feedback, setFeedback] = useState(null);
  const [submission, setSubmission] = useState(null);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [timeLeft, setTimeLeft] = useState("");

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
        `/api/feedback/coding-submission/${submissionId}`
      );
      setFeedback(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!challenge) {
      setError("Challenge data missing. Please go back and start again.");
      return;
    }

    if (sessionExpired) {
      setError("This session has expired. You can no longer submit.");
      return;
    }

    setSubmitting(true);
    setError("");

    try {
      const response = await api.post("/api/coding-submissions", {
        code,
        score: null,
        status: "SUBMITTED",
        submittedAt: new Date().toISOString(),
        timeSpentSeconds: null,
        challengeId: challenge.id,
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
          "Failed to submit solution."
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
      padding: "12px",
      borderRadius: "8px",
      border: "1px solid #d1d5db",
      fontFamily: "monospace",
      fontSize: "14px",
      minHeight: "300px",
      resize: "vertical",
    },

    error: {
      color: "red",
      marginTop: "10px",
    },
  };

  if (!session || !challenge) {
    return (
      <div style={styles.page}>
        <p>
          Session or challenge data is missing. Go back and start again.
        </p>
        <button
          style={styles.button}
          onClick={() => navigate("/coding-challenges")}
        >
          Back to Challenges
        </button>
      </div>
    );
  }

  return (
    <div style={styles.page}>
      <h2 style={styles.title}>Coding Session</h2>

      {/* Back Button */}
      <div style={styles.topBar}>
        <button
          style={styles.button}
          onClick={() => navigate("/coding-challenges")}
        >
          Back to Challenges
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
          <p>Your time is up. You can no longer submit this challenge.</p>
        </div>
      )}

      {/* Challenge Info */}
      <div style={styles.card}>
        <h3>{challenge.title}</h3>
        <p>{challenge.description || "No description"}</p>
        <p>
          <strong>Difficulty:</strong> {challenge.difficulty}
        </p>
        <p>
          <strong>Category:</strong> {challenge.category}
        </p>
        <p>
          <strong>Time Limit:</strong>{" "}
          {challenge.timeLimitMinutes || 30} minutes
        </p>
      </div>

      {/* Code Editor */}
      <form onSubmit={handleSubmit} style={styles.card}>
        <label>Your Code</label>

        <textarea
          style={styles.textarea}
          rows="18"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          placeholder="Write your coding solution here..."
          disabled={sessionExpired}
        />

        {error && <p style={styles.error}>{error}</p>}

        <button
          style={{ ...styles.button, marginTop: "10px" }}
          type="submit"
          disabled={submitting || sessionExpired || !code.trim()}
        >
          {sessionExpired
            ? "Session Expired"
            : submitting
            ? "Submitting..."
            : "Submit Solution"}
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