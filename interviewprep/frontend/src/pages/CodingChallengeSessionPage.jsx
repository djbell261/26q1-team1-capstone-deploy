import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import api from "../services/api";
import { ui } from "../styles/ui";

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

  if (!session || !challenge) {
    return (
      <div style={ui.page}>
        <div style={ui.container}>
          <div style={ui.card}>
            <p>Session or challenge data is missing. Go back and start again.</p>
            <button style={ui.button} onClick={() => navigate("/coding-challenges")}>
              Back to Challenges
            </button>
          </div>
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
              <h1 style={ui.heroTitle}>Coding Session</h1>
              <p style={ui.heroSubtitle}>
                Solve the problem in a timed environment and submit your solution for feedback.
              </p>
            </div>

            <button style={ui.secondaryButton} onClick={() => navigate("/coding-challenges")}>
              Back to Challenges
            </button>
          </div>
        </section>

        <section style={ui.grid2}>
          <div style={{ display: "grid", gap: "20px" }}>
            <div style={ui.card}>
              <div style={{ display: "flex", justifyContent: "space-between", gap: "12px", alignItems: "center", flexWrap: "wrap" }}>
                <h2 style={ui.sectionTitle}>{challenge.title}</h2>
                <span style={ui.badge}>{challenge.difficulty}</span>
              </div>

              <p style={{ ...ui.muted, margin: "14px 0 18px", lineHeight: 1.7 }}>
                {challenge.description || "No description"}
              </p>

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
                  gap: "12px",
                }}
              >
                <div style={ui.infoBox}>
                  <p style={ui.statLabel}>Category</p>
                  <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                    {challenge.category}
                  </p>
                </div>

                <div style={ui.infoBox}>
                  <p style={ui.statLabel}>Time Limit</p>
                  <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                    {challenge.timeLimitMinutes || 30} minutes
                  </p>
                </div>

                <div style={ui.infoBox}>
                  <p style={ui.statLabel}>Session ID</p>
                  <p style={{ margin: "6px 0 0", fontWeight: 700 }}>{session.id}</p>
                </div>
              </div>
            </div>

            <form onSubmit={handleSubmit} style={ui.card}>
              <label style={ui.label}>Your Code</label>

              <textarea
                style={ui.codeTextarea}
                rows="20"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder="Write your coding solution here..."
                disabled={sessionExpired}
              />

              {error && <div style={ui.error}>{error}</div>}

              <div style={{ marginTop: "16px", display: "flex", gap: "12px", flexWrap: "wrap" }}>
                <button
                  style={ui.button}
                  type="submit"
                  disabled={submitting || sessionExpired || !code.trim()}
                >
                  {sessionExpired
                    ? "Session Expired"
                    : submitting
                    ? "Submitting..."
                    : "Submit Solution"}
                </button>
              </div>
            </form>
          </div>

          <aside style={{ display: "grid", gap: "20px" }}>
            <div style={ui.card}>
              <h2 style={ui.sectionTitle}>Session Status</h2>
              <div style={{ display: "grid", gap: "12px", marginTop: "16px" }}>
                <div style={ui.infoBox}>
                  <p style={ui.statLabel}>Time Left</p>
                  <p style={{ margin: "6px 0 0", fontWeight: 700, fontSize: "20px" }}>
                    {sessionExpired ? "Expired" : timeLeft || "Loading timer..."}
                  </p>
                </div>

                {sessionExpired && (
                  <div style={ui.warning}>
                    Your time is up. You can no longer submit this challenge.
                  </div>
                )}
              </div>
            </div>

            {submission && (
              <div style={ui.card}>
                <h2 style={ui.sectionTitle}>Submission Saved</h2>
                <div style={{ display: "grid", gap: "12px", marginTop: "16px" }}>
                  <div style={ui.infoBox}>
                    <strong>Submission ID:</strong> {submission.id}
                  </div>
                  <div style={ui.infoBox}>
                    <strong>Status:</strong> {submission.status}
                  </div>
                  <div style={ui.infoBox}>
                    <strong>Submitted At:</strong> {submission.submittedAt}
                  </div>
                  <div style={ui.infoBox}>
                    <strong>Score:</strong> {submission.score ?? "Pending"}
                  </div>
                </div>
              </div>
            )}

            {feedback && (
              <div style={ui.card}>
                <h2 style={ui.sectionTitle}>AI Feedback</h2>
                <div style={{ display: "grid", gap: "12px", marginTop: "16px" }}>
                  <div style={ui.infoBox}>
                    <strong>AI Score:</strong> {feedback.aiScore}
                  </div>
                  <div style={ui.infoBox}>
                    <strong>Summary:</strong> {feedback.summary}
                  </div>
                  <div style={ui.infoBox}>
                    <strong>Strengths:</strong> {feedback.strengths}
                  </div>
                  <div style={ui.infoBox}>
                    <strong>Weaknesses:</strong> {feedback.weaknesses}
                  </div>
                  <div style={ui.infoBox}>
                    <strong>Recommendations:</strong> {feedback.recommendations}
                  </div>
                  <div style={ui.infoBox}>
                    <strong>Status:</strong> {feedback.status}
                  </div>
                </div>
              </div>
            )}
          </aside>
        </section>
      </div>
    </div>
  );
}