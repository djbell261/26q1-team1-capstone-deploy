import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../api/authApi";
import { useAuthContext } from "../context/AuthContext";

export default function RegisterPage() {
  const navigate = useNavigate();
  const { login } = useAuthContext();

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
      const data = await registerUser(formData);

      login({
        token: data.token,
        user: {
          id: data.id,
          name: data.name,
          email: data.email,
          role: data.role,
        },
      });

      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Registration failed. Please try again."
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
          Create Account
        </h1>

        <p
          style={{
            marginTop: 0,
            marginBottom: "1.5rem",
            color: "#6b7280",
          }}
        >
          Join the platform and start practicing interviews.
        </p>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: "1rem" }}>
            <label style={labelStyle}>Full Name</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </div>

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

          <div style={{ marginBottom: "1rem" }}>
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

          <div style={{ marginBottom: "1.25rem" }}>
            <label style={labelStyle}>Role</label>
            <select
              name="role"
              value={formData.role}
              onChange={handleChange}
              style={inputStyle}
            >
              <option value="USER">USER</option>
            </select>
          </div>

          {error && (
            <p style={{ color: "crimson", marginBottom: "1rem" }}>{error}</p>
          )}

          <button type="submit" disabled={loading} style={primaryButtonStyle}>
            {loading ? "Creating account..." : "Register"}
          </button>
        </form>

        <p
          style={{
            marginTop: "1.25rem",
            marginBottom: 0,
            color: "#6b7280",
          }}
        >
          Already have an account?{" "}
          <Link to="/login" style={linkStyle}>
            Sign in
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