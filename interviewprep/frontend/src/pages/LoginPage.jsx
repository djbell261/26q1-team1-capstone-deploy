import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { loginUser } from "../api/authApi";
import { useAuthContext } from "../context/AuthContext";

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuthContext();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const from = location.state?.from?.pathname || "/dashboard";

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
      const data = await loginUser(formData);

      login({
        token: data.token,
        user: {
          id: data.id,
          name: data.name,
          email: data.email,
          role: data.role,
        },
      });

      navigate(from, { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Login failed. Please check your credentials."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        background: "#f5f7fb",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "2rem",
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: "460px",
          background: "#ffffff",
          borderRadius: "20px",
          padding: "2rem",
          boxShadow: "0 12px 30px rgba(0,0,0,0.08)",
          border: "1px solid #ececec",
        }}
      >
        <h1
          style={{
            marginTop: 0,
            marginBottom: "0.5rem",
            fontSize: "2rem",
            color: "#111827",
          }}
        >
          Login
        </h1>

        <p
          style={{
            marginTop: 0,
            marginBottom: "1.5rem",
            color: "#6b7280",
          }}
        >
          Sign in to continue your interview prep.
        </p>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: "1rem" }}>
            <label style={labelStyle}>Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </div>

          <div style={{ marginBottom: "1.25rem" }}>
            <label style={labelStyle}>Password</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </div>

          {error && (
            <p style={{ color: "crimson", marginBottom: "1rem" }}>{error}</p>
          )}

          <button type="submit" disabled={loading} style={primaryButtonStyle}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <p style={{ marginTop: "1rem", color: "#6b7280" }}>
          Don’t have an account?{" "}
          <Link to="/register" style={linkStyle}>
            Create one
          </Link>
        </p>
      </div>
    </div>
  );
}

const labelStyle = {
  display: "block",
  marginBottom: "0.45rem",
  fontWeight: 600,
  color: "#111827",
};

const inputStyle = {
  width: "100%",
  padding: "0.85rem 1rem",
  borderRadius: "12px",
  border: "1px solid #d1d5db",
  outline: "none",
  fontSize: "1rem",
  boxSizing: "border-box",
};

const primaryButtonStyle = {
  width: "100%",
  border: "none",
  background: "#111827",
  color: "#ffffff",
  padding: "0.95rem 1rem",
  borderRadius: "12px",
  cursor: "pointer",
  fontWeight: 600,
  fontSize: "1rem",
};

const linkStyle = {
  color: "#111827",
  fontWeight: 600,
  textDecoration: "none",
};