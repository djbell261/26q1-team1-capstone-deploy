import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";
import { ui } from "../styles/ui";

export default function LoginPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await api.post("/api/auth/login", {
        email,
        password,
      });

      const data = response.data;

      if (data?.token) {
        localStorage.setItem("token", data.token);
      }

      navigate("/dashboard");
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          "Login failed."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={ui.authPage}>
      <section style={ui.authHero}>
        <div>
          <h1 style={ui.authTitle}>AI-Powered Interview Prep Platform</h1>
          <p style={ui.authText}>
            Practice coding and behavioral interviews, get AI feedback, and track
            your progress in one place.
          </p>
        </div>
      </section>

      <section style={ui.authPanelWrap}>
        <div style={ui.authCard}>
          <h2 style={{ ...ui.sectionTitle, fontSize: "28px" }}>Login</h2>
          <p style={ui.sectionSubtitle}>Sign in to continue your interview prep.</p>

          <form onSubmit={handleSubmit} style={{ ...ui.formStack, marginTop: "22px" }}>
            <div style={ui.formField}>
              <label style={ui.label}>Email</label>
              <input
                style={ui.input}
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div style={ui.formField}>
              <label style={ui.label}>Password</label>
              <input
                style={ui.input}
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            {error && <div style={ui.error}>{error}</div>}

            <button style={ui.button} type="submit" disabled={loading}>
              {loading ? "Logging in..." : "Login"}
            </button>
          </form>

          <p style={{ marginTop: "18px", ...ui.muted }}>
            Don’t have an account? <Link to="/register">Register</Link>
          </p>
        </div>
      </section>
    </div>
  );
}