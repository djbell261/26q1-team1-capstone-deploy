import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuthContext } from "../context/AuthContext";

function EmptyState({ text }) {
  return <p style={{ margin: 0, color: "#6b7280" }}>{text}</p>;
}

export default function SessionsPage() {
  const navigate = useNavigate();
  const { logout } = useAuthContext();

  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadSessions() {
      try {
        setLoading(true);
        setError("");

        const response = await api.get("/api/sessions/me");

        if (isMounted) {
          setSessions(response.data ?? []);
        }
      } catch (err) {
        console.error("Failed to load sessions:", err);

        if (err.response?.status === 401 || err.response?.status === 403) {
          logout();
          navigate("/login", { replace: true });
          return;
        }

        if (isMounted) {
          setError("Failed to load sessions.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    loadSessions();

    return () => {
      isMounted = false;
    };
  }, [logout, navigate]);

  return (
    <div style={pageStyle}>
      <div style={containerStyle}>
        <div style={headerStyle}>
          <div>
            <h1 style={titleStyle}>Sessions</h1>
            <p style={subtitleStyle}>
              Track your interview practice sessions.
            </p>
          </div>

          <button onClick={() => navigate("/dashboard")} style={topButtonStyle}>
            Back to Dashboard
          </button>
        </div>

        {loading && (
          <div style={messageCard}>
            <p>Loading sessions...</p>
          </div>
        )}

        {!loading && error && (
          <div style={messageCard}>
            <p style={{ color: "crimson" }}>{error}</p>
          </div>
        )}

        {!loading && !error && sessions.length === 0 && (
          <div style={messageCard}>
            <EmptyState text="No sessions yet." />
          </div>
        )}

        {!loading && !error && sessions.length > 0 && (
          <div style={gridStyle}>
            {sessions.map((session, index) => (
              <div key={session.id ?? index} style={cardStyle}>
                <div style={cardHeader}>
                  <h2 style={cardTitle}>
                    Session #{session.id ?? index + 1}
                  </h2>

                  <span style={badgeStyle}>
                    {session.status || "ACTIVE"}
                  </span>
                </div>

                <div style={infoGrid}>
                  <div style={infoBox}>
                    <p style={label}>Type</p>
                    <p style={value}>
                      {session.type || "N/A"}
                    </p>
                  </div>

                  <div style={infoBox}>
                    <p style={label}>Started</p>
                    <p style={value}>
                      {session.startedAt
                        ? new Date(session.startedAt).toLocaleString()
                        : "N/A"}
                    </p>
                  </div>

                  <div style={infoBox}>
                    <p style={label}>Expires</p>
                    <p style={value}>
                      {session.expiresAt
                        ? new Date(session.expiresAt).toLocaleString()
                        : "N/A"}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

const pageStyle = {
  minHeight: "100vh",
  background: "#f5f7fb",
  padding: "2rem",
};

const containerStyle = {
  maxWidth: "1100px",
  margin: "0 auto",
};

const headerStyle = {
  display: "flex",
  justifyContent: "space-between",
  alignItems: "center",
  marginBottom: "2rem",
  flexWrap: "wrap",
};

const titleStyle = {
  margin: 0,
  fontSize: "2.5rem",
  color: "#111827",
};

const subtitleStyle = {
  margin: "0.5rem 0 0",
  color: "#6b7280",
};

const topButtonStyle = {
  border: "none",
  background: "#111827",
  color: "#fff",
  padding: "0.85rem 1.1rem",
  borderRadius: "12px",
  cursor: "pointer",
  fontWeight: 600,
};

const messageCard = {
  background: "#fff",
  borderRadius: "16px",
  padding: "1rem",
  border: "1px solid #ececec",
};

const gridStyle = {
  display: "grid",
  gap: "1rem",
};

const cardStyle = {
  background: "#fff",
  borderRadius: "18px",
  padding: "1.25rem",
  border: "1px solid #ececec",
  boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
};

const cardHeader = {
  display: "flex",
  justifyContent: "space-between",
  marginBottom: "1rem",
  alignItems: "center",
  gap: "1rem",
  flexWrap: "wrap",
};

const cardTitle = {
  margin: 0,
  fontSize: "1.2rem",
};

const badgeStyle = {
  background: "#e0f2fe",
  color: "#0369a1",
  padding: "0.3rem 0.6rem",
  borderRadius: "999px",
  fontSize: "0.8rem",
  fontWeight: 600,
};

const infoGrid = {
  display: "grid",
  gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
  gap: "0.75rem",
};

const infoBox = {
  background: "#f9fafb",
  borderRadius: "12px",
  padding: "0.75rem",
  border: "1px solid #e5e7eb",
};

const label = {
  margin: 0,
  fontSize: "0.8rem",
  color: "#6b7280",
};

const value = {
  margin: "0.25rem 0 0",
  fontWeight: 600,
};