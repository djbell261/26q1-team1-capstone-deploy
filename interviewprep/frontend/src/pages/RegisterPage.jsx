import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";
import { ui } from "../styles/ui";

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

  return (
    <div style={ui.authPage}>
      <section style={ui.authHero}>
        <div>
          <h1 style={ui.authTitle}>Create Your Interview Prep Account</h1>
          <p style={ui.authText}>
            Build your practice history, review personalized recommendations,
            and improve across coding and behavioral interviews.
          </p>
        </div>
      </section>

      <section style={ui.authPanelWrap}>
        <div style={ui.authCard}>
          <h2 style={{ ...ui.sectionTitle, fontSize: "28px" }}>Create Account</h2>
          <p style={ui.sectionSubtitle}>Get started with your personalized dashboard.</p>

          <form onSubmit={handleSubmit} style={{ ...ui.formStack, marginTop: "22px" }}>
            <div style={ui.formField}>
              <label style={ui.label}>Name</label>
              <input
                style={ui.input}
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>

            <div style={ui.formField}>
              <label style={ui.label}>Email</label>
              <input
                style={ui.input}
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div style={ui.formField}>
              <label style={ui.label}>Password</label>
              <input
                style={ui.input}
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            {error && <div style={ui.error}>{error}</div>}

            <button style={ui.button} type="submit" disabled={loading}>
              {loading ? "Creating Account..." : "Register"}
            </button>
          </form>

          <p style={{ marginTop: "18px", ...ui.muted }}>
            Already have an account? <Link to="/login">Login</Link>
          </p>
        </div>
      </section>
    </div>
  );
}