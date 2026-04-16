import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

export default function CodingSubmissionPage() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchSubmissions();
  }, []);

  const fetchSubmissions = async () => {
    try {
      const response = await api.get("/api/coding-submissions/me");
      setSubmissions(response.data);
    } catch (err) {
      setError("Failed to load coding submissions.");
    } finally {
      setLoading(false);
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

    grid: {
      display: "grid",
      gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))",
      gap: "16px",
      marginTop: "20px",
    },

    card: {
      background: "white",
      borderRadius: "12px",
      padding: "18px",
      boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
      transition: "0.2s ease",
    },

    error: {
      color: "red",
      marginBottom: "10px",
    },

    mutedText: {
      color: "#6b7280",
    },
  };

  if (loading) {
    return (
      <div style={styles.page}>
        <p>Loading submissions...</p>
      </div>
    );
  }

  return (
    <div style={styles.page}>
      <h2 style={styles.title}>My Coding Submissions</h2>

      {/* Top Button */}
      <div style={styles.topBar}>
        <button style={styles.button} onClick={() => navigate("/dashboard")}>
          Back to Dashboard
        </button>
      </div>

      {/* Error */}
      {error && <p style={styles.error}>{error}</p>}

      {/* Empty State */}
      {submissions.length === 0 ? (
        <p>No submissions found.</p>
      ) : (
        <div style={styles.grid}>
          {submissions.map((sub) => (
            <div key={sub.id} style={styles.card}>
              <p>
                <strong>Submission ID:</strong> {sub.id}
              </p>

              <p>
                <strong>Status:</strong> {sub.status}
              </p>

              <p>
                <strong>Score:</strong> {sub.score ?? "Pending"}
              </p>

              <p style={styles.mutedText}>
                <strong>Submitted At:</strong> {sub.submittedAt}
              </p>

              <p>
                <strong>Challenge ID:</strong> {sub.challengeId}
              </p>

              <p>
                <strong>Session ID:</strong> {sub.sessionId}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}