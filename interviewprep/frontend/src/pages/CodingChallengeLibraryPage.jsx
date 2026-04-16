import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

export default function CodingChallengeLibraryPage() {
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchChallenges();
  }, []);

  const fetchChallenges = async () => {
    try {
      const response = await api.get("/api/coding-challenges");
      setChallenges(response.data);
    } catch (err) {
      setError("Failed to load coding challenges.");
    } finally {
      setLoading(false);
    }
  };

  const startSession = async (challenge) => {
    try {
      const now = new Date();
      const expires = new Date(
        now.getTime() + (challenge.timeLimitMinutes || 30) * 60 * 1000
      );

      const response = await api.post("/api/sessions", {
        type: "CODING",
        startedAt: now.toISOString(),
        expiresAt: expires.toISOString(),
        status: "ACTIVE",
      });

      const session = response.data;

      navigate(`/coding-session/${session.id}`, {
        state: {
          session,
          challenge,
        },
      });
    } catch (err) {
      setError("Failed to start coding session.");
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
        <p>Loading coding challenges...</p>
      </div>
    );
  }

  return (
    <div style={styles.page}>
      <h2 style={styles.title}>Coding Challenge Library</h2>

      {/* Top Button */}
      <div style={styles.topBar}>
        <button style={styles.button} onClick={() => navigate("/dashboard")}>
          Back to Dashboard
        </button>
      </div>

      {/* Error */}
      {error && <p style={styles.error}>{error}</p>}

      {/* Empty State */}
      {challenges.length === 0 ? (
        <p>No coding challenges found.</p>
      ) : (
        <div style={styles.grid}>
          {challenges.map((challenge) => (
            <div key={challenge.id} style={styles.card}>
              <h3>{challenge.title}</h3>

              <p style={styles.mutedText}>
                {challenge.description || "No description"}
              </p>

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

              <button
                style={{ ...styles.button, marginTop: "10px" }}
                onClick={() => startSession(challenge)}
              >
                Start Challenge
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}