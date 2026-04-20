import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuthContext } from "../context/AuthContext";
import { ui } from "../styles/ui";

function EmptyState({ text }) {
  return <p style={{ margin: 0, color: "#64748b" }}>{text}</p>;
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

  return (
    <div style={ui.page}>
      <div style={ui.container}>
        <section style={ui.hero}>
          <div style={ui.topRow}>
            <div>
              <h1 style={ui.heroTitle}>Sessions</h1>
              <p style={ui.heroSubtitle}>
                Track your practice sessions, expiration times, and current statuses.
              </p>
            </div>

            <button style={ui.secondaryButton} onClick={() => navigate("/dashboard")}>
              Back to Dashboard
            </button>
          </div>
        </section>

        {loading && <div style={ui.card}>Loading sessions...</div>}

        {!loading && error && <div style={ui.error}>{error}</div>}

        {!loading && !error && sessions.length === 0 && (
          <div style={ui.card}>
            <EmptyState text="No sessions yet." />
          </div>
        )}

        {!loading && !error && sessions.length > 0 && (
          <section style={ui.gridCards}>
            {sessions.map((session, index) => (
              <article key={session.id ?? index} style={ui.card}>
                <div style={{ display: "flex", justifyContent: "space-between", gap: "12px", alignItems: "center", flexWrap: "wrap" }}>
                  <h2 style={{ margin: 0, fontSize: "22px" }}>
                    Session #{session.id ?? index + 1}
                  </h2>

                  <span style={ui.badge}>{session.status || "ACTIVE"}</span>
                </div>

                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
                    gap: "12px",
                    marginTop: "16px",
                  }}
                >
                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Type</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                      {session.type || "N/A"}
                    </p>
                  </div>

                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Started</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                      {session.startedAt
                        ? new Date(session.startedAt).toLocaleString()
                        : "N/A"}
                    </p>
                  </div>

                  <div style={ui.infoBox}>
                    <p style={ui.statLabel}>Expires</p>
                    <p style={{ margin: "6px 0 0", fontWeight: 700 }}>
                      {session.expiresAt
                        ? new Date(session.expiresAt).toLocaleString()
                        : "N/A"}
                    </p>
                  </div>
                </div>
              </article>
            ))}
          </section>
        )}
      </div>
    </div>
  );
}