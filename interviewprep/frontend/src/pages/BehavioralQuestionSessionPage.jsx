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
      const response = await api.get(`/api/feedback/behavioral-submission/${submissionId}`);
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
    } catch (err) {
      console.error(err);
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

  if (!session || !question) {
    return (
      <div className="page-container">
        <p>Session or question data is missing. Go back and start the question again.</p>
        <button onClick={() => navigate("/behavioral-questions")}>Back to Questions</button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h2>Behavioral Session</h2>

      <div style={{ marginBottom: "16px" }}>
        <button onClick={() => navigate("/behavioral-questions")}>Back to Questions</button>
      </div>

      <div className="simple-card">
        <p><strong>Session ID:</strong> {session.id}</p>
        <p><strong>Time Left:</strong> {timeLeft || "Loading timer..."}</p>
      </div>

      <div className="simple-card">
        <h3>{question.category}</h3>
        <p>{question.questionText}</p>
        <p><strong>Difficulty:</strong> {question.difficulty}</p>
      </div>

      <form onSubmit={handleSubmit} className="simple-card">
        <label>Your Response</label>
        <textarea
          rows="12"
          value={responseText}
          onChange={(e) => setResponseText(e.target.value)}
          placeholder="Write your response using STAR..."
          disabled={sessionExpired}
        />

        {error && <p>{error}</p>}

        <button
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