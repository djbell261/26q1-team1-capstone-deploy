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
        if (err.response?.status === 401 || err.response?.status === 403) {
          logout();
          navigate("/login", { replace: true });
          return;
        }

        if (isMounted) {
          setError("Failed to load sessions.");
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    }

    loadSessions();

    return () => {
      isMounted = false;
    };
  }, [logout, navigate]);

  // ===== CONSISTENT THEME =====
  const styles = {
    page: {
      maxWidth: "1100px",
      margin: "40px auto",
      padding: "20px",
      fontFamily: "Arial, sans-serif",
      color: "#1f2937",
    },

    header: {
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      flexWrap: "wrap",
      gap: "16px",
      marginBottom: "24px",
    },

    title: {
      margin: 0,
      fontSize: "32px",
    },

    subtitle: {
      margin: "6px 0 0",
      color: "#6b7280",
    },

    button: {
      border: "none",
      background: "#111827",
      color: "#fff",
      padding: "10px 14px",
      borderRadius: "8px",
      cursor: "pointer",
    },

    grid: {
      display: "grid",
      gap: "16px",
    },

    card: {
      background: "#fff",
      borderRadius: "12px",
      padding: "18px",
      boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    },

    cardHeader: {
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      marginBottom: "12px",
      flexWrap: "wrap",
      gap: "10px",
    },

    cardTitle: {
      margin: 0,
      fontSize: "18px",
    },

    badge: {
      padding: "6px 10px",
      borderRadius: "999px",
      background: "#ecfeff",
      color: "#155e75",
      fontWeight: 600,
      fontSize: "12px",
    },

    infoGrid: {
      display: "grid",
      gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
      gap: "12px",
    },

    box: {
      background: "#f9fafb",
      border: "1px solid #e5e7eb",
      borderRadius: "10px",
      padding: "12px",
    },

    label: {
      margin: 0,
      fontSize: "12px",
      color: "#6b7280",
      fontWeight: 600,
    },

    value: {
      margin: "4px 0 0",
      fontSize: "14px",
      fontWeight: 500,
      color: "#111827",
    },

    message: {
      background: "#fff",
      borderRadius: "12px",
      padding: "14px",
      border: "1px solid #ececec",
    },

    error: {
      color: "crimson",
    },
  };

  return (
    <div style={styles.page}>
      {/* HEADER */}
      <div style={styles.header}>
        <div>
          <h1 style={styles.title}>Sessions</h1>
          <p style={styles.subtitle}>
            Track your interview practice sessions.
          </p>
        </div>

        <button style={styles.button} onClick={() => navigate("/dashboard")}>
          Back to Dashboard
        </button>
      </div>

      {/* LOADING */}
      {loading && (
        <div style={styles.message}>
          <p style={{ margin: 0 }}>Loading sessions...</p>
        </div>
      )}

      {/* ERROR */}
      {!loading && error && (
        <div style={styles.message}>
          <p style={styles.error}>{error}</p>
        </div>
      )}

      {/* EMPTY */}
      {!loading && !error && sessions.length === 0 && (
        <div style={styles.message}>
          <EmptyState text="No sessions yet." />
        </div>
      )}

      {/* LIST */}
      {!loading && !error && sessions.length > 0 && (
        <div style={styles.grid}>
          {sessions.map((session, index) => (
            <div key={session.id ?? index} style={styles.card}>
              {/* HEADER ROW */}
              <div style={styles.cardHeader}>
                <h2 style={styles.cardTitle}>
                  Session #{session.id ?? index + 1}
                </h2>

                <span style={styles.badge}>
                  {session.status || "ACTIVE"}
                </span>
              </div>

              {/* INFO GRID */}
              <div style={styles.infoGrid}>
                <div style={styles.box}>
                  <p style={styles.label}>Type</p>
                  <p style={styles.value}>{session.type || "N/A"}</p>
                </div>

                <div style={styles.box}>
                  <p style={styles.label}>Started</p>
                  <p style={styles.value}>
                    {session.startedAt
                      ? new Date(session.startedAt).toLocaleString()
                      : "N/A"}
                  </p>
                </div>

                <div style={styles.box}>
                  <p style={styles.label}>Expires</p>
                  <p style={styles.value}>
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
  );
}