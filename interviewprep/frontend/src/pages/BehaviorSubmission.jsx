import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

export default function BehaviorSubmission() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchSubmissions();
  }, []);

  const fetchSubmissions = async () => {
    try {
      const response = await api.get("/api/behavioral-submissions/me");
      setSubmissions(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load behavioral submissions.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <p>Loading submissions...</p>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h2>My Behavioral Submissions</h2>

      <div style={{ marginBottom: "16px" }}>
        <button onClick={() => navigate("/dashboard")}>Back to Dashboard</button>
      </div>

      {error && <p>{error}</p>}

      {submissions.length === 0 ? (
        <p>No submissions found.</p>
      ) : (
        <div className="card-list">
          {submissions.map((sub) => (
            <div key={sub.id} className="simple-card">
              <p><strong>Submission ID:</strong> {sub.id}</p>
              <p><strong>Status:</strong> {sub.status}</p>
              <p><strong>Score:</strong> {sub.score ?? "Pending"}</p>
              <p><strong>Submitted At:</strong> {sub.submittedAt}</p>
              <p><strong>Question ID:</strong> {sub.questionId}</p>
              <p><strong>Session ID:</strong> {sub.sessionId}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}