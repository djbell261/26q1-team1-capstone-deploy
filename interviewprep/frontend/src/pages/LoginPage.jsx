import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";

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

  // ===== INLINE STYLES (consistent SaaS auth theme) =====
  const styles = {
    page: {
      minHeight: "100vh",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      background: "#f9fafb",
      fontFamily: "Arial, sans-serif",
      padding: "20px",
    },

    card: {
      width: "100%",
      maxWidth: "420px",
      background: "white",
      borderRadius: "12px",
      padding: "24px",
      boxShadow: "0 6px 20px rgba(0,0,0,0.08)",
    },

    title: {
      fontSize: "26px",
      marginBottom: "16px",
      textAlign: "center",
      color: "#111827",
    },

    label: {
      display: "block",
      marginTop: "12px",
      marginBottom: "6px",
      fontWeight: "bold",
      fontSize: "14px",
    },

    input: {
      width: "100%",
      padding: "10px 12px",
      borderRadius: "8px",
      border: "1px solid #d1d5db",
      outline: "none",
      fontSize: "14px",
    },

    button: {
      width: "100%",
      marginTop: "16px",
      padding: "10px",
      borderRadius: "8px",
      border: "none",
      cursor: "pointer",
      backgroundColor: "#111827",
      color: "white",
      fontWeight: "bold",
    },

    error: {
      color: "red",
      marginTop: "10px",
      fontSize: "14px",
    },

    linkText: {
      marginTop: "16px",
      textAlign: "center",
      fontSize: "14px",
    },
  };

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h2 style={styles.title}>Login</h2>

        <form onSubmit={handleSubmit}>
          <label style={styles.label}>Email</label>
          <input
            style={styles.input}
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <label style={styles.label}>Password</label>
          <input
            style={styles.input}
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          {error && <p style={styles.error}>{error}</p>}

          <button style={styles.button} type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <p style={styles.linkText}>
          Don’t have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}