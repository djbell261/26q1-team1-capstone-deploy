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
      console.error(err);
      setError("Failed to load coding challenges.");
    } finally {
      setLoading(false);
    }
  };

  const startSession = async (challenge) => {
    try {
      const now = new Date();
      const expires = new Date(now.getTime() + (challenge.timeLimitMinutes || 30) * 60 * 1000);

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
      console.error(err);
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          err.message ||
          "Failed to start coding session."
      );
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <p>Loading coding challenges...</p>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h2>Coding Challenge Library</h2>

      <div style={{ marginBottom: "16px" }}>
        <button onClick={() => navigate("/dashboard")}>Back to Dashboard</button>
      </div>

      {error && <p>{error}</p>}

      {challenges.length === 0 ? (
        <p>No coding challenges found.</p>
      ) : (
        <div className="card-list">
          {challenges.map((challenge) => (
            <div key={challenge.id} className="simple-card">
              <h3>{challenge.title}</h3>
              <p>{challenge.description || "No description"}</p>
              <p><strong>Difficulty:</strong> {challenge.difficulty}</p>
              <p><strong>Category:</strong> {challenge.category}</p>
              <p><strong>Time Limit:</strong> {challenge.timeLimitMinutes || 30} minutes</p>

              <button onClick={() => startSession(challenge)}>
                Start Challenge
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}