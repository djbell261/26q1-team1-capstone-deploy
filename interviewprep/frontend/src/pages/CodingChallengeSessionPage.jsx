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
        setTimeLeft("Time expired");
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
      const response = await api.get(`/api/feedback/coding-submission/${submissionId}`);
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
    } catch (err) {
      console.error(err);
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

  if (!session || !challenge) {
    return (
      <div className="page-container">
        <p>Session or challenge data is missing. Go back and start the challenge again.</p>
        <button onClick={() => navigate("/coding-challenges")}>Back to Challenges</button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h2>Coding Session</h2>

      <div style={{ marginBottom: "16px" }}>
        <button onClick={() => navigate("/coding-challenges")}>Back to Challenges</button>
      </div>

      <div className="simple-card">
        <p><strong>Session ID:</strong> {session.id}</p>
        <p><strong>Time Left:</strong> {timeLeft || "Loading timer..."}</p>
      </div>

      <div className="simple-card">
        <h3>{challenge.title}</h3>
        <p>{challenge.description || "No description"}</p>
        <p><strong>Difficulty:</strong> {challenge.difficulty}</p>
        <p><strong>Category:</strong> {challenge.category}</p>
        <p><strong>Time Limit:</strong> {challenge.timeLimitMinutes || 30} minutes</p>
      </div>

      <form onSubmit={handleSubmit} className="simple-card">
        <label>Your Code</label>
        <textarea
          rows="18"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          placeholder="Write your coding solution here..."
          disabled={sessionExpired}
        />

        {error && <p>{error}</p>}

        <button
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

      {submission && (
        <div className="simple-card">
          <h3>Submission Saved</h3>
          <p><strong>Submission ID:</strong> {submission.id}</p>
          <p><strong>Status:</strong> {submission.status}</p>
          <p><strong>Submitted At:</strong> {submission.submittedAt}</p>
          <p><strong>Score:</strong> {submission.score ?? "Pending"}</p>
        </div>
      )}

      {feedback && (
        <div className="simple-card">
          <h3>AI Feedback</h3>
          <p><strong>AI Score:</strong> {feedback.aiScore}</p>
          <p><strong>Summary:</strong> {feedback.summary}</p>
          <p><strong>Strengths:</strong> {feedback.strengths}</p>
          <p><strong>Weaknesses:</strong> {feedback.weaknesses}</p>
          <p><strong>Recommendations:</strong> {feedback.recommendations}</p>
          <p><strong>Status:</strong> {feedback.status}</p>
        </div>
      )}
    </div>
  );
}