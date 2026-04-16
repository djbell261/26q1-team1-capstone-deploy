import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    role: "USER",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await api.post("/api/auth/register", formData);
      const data = response.data;

      if (data?.token) {
        localStorage.setItem("token", data.token);
      }

      navigate("/dashboard");
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          "Registration failed."
      );
    } finally {
      setLoading(false);
    }
  };

  // ===== CONSISTENT AUTH THEME (matches LoginPage) =====
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
      maxWidth: "450px",
      background: "#fff",
      borderRadius: "12px",
      padding: "24px",
      boxShadow: "0 6px 20px rgba(0,0,0,0.08)",
    },

    title: {
      fontSize: "26px",
      textAlign: "center",
      marginBottom: "16px",
      color: "#111827",
    },

    label: {
      display: "block",
      marginTop: "12px",
      marginBottom: "6px",
      fontWeight: "bold",
      fontSize: "14px",
      color: "#111827",
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

    footerText: {
      marginTop: "16px",
      textAlign: "center",
      fontSize: "14px",
      color: "#6b7280",
    },
  };

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h2 style={styles.title}>Create Account</h2>

        <form onSubmit={handleSubmit}>
          <label style={styles.label}>Name</label>
          <input
            style={styles.input}
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />

          <label style={styles.label}>Email</label>
          <input
            style={styles.input}
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />

          <label style={styles.label}>Password</label>
          <input
            style={styles.input}
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
          />

          {error && <p style={styles.error}>{error}</p>}

          <button style={styles.button} type="submit" disabled={loading}>
            {loading ? "Creating Account..." : "Register"}
          </button>
        </form>

        <p style={styles.footerText}>
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}