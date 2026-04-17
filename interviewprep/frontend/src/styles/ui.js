export const ui = {
  colors: {
    bg: "#f5f7fb",
    panel: "#ffffff",
    panelAlt: "#f8fafc",
    text: "#0f172a",
    muted: "#64748b",
    border: "#e2e8f0",
    primary: "#0f172a",
    primarySoft: "#e2e8f0",
    successBg: "#ecfdf5",
    successText: "#166534",
    warningBg: "#fff7ed",
    warningText: "#c2410c",
    dangerBg: "#fef2f2",
    dangerText: "#b91c1c",
    shadow: "0 10px 30px rgba(15, 23, 42, 0.08)",
  },

  page: {
    minHeight: "100vh",
    background: "#f5f7fb",
    padding: "32px 24px 48px",
    fontFamily: "Inter, Arial, sans-serif",
    color: "#0f172a",
  },

  container: {
    maxWidth: "1400px",
    margin: "0 auto",
  },

  hero: {
    background:
      "linear-gradient(135deg, #0f172a 0%, #1e293b 55%, #334155 100%)",
    color: "#fff",
    borderRadius: "24px",
    padding: "32px",
    boxShadow: "0 10px 30px rgba(15, 23, 42, 0.18)",
    marginBottom: "24px",
  },

  heroTitle: {
    margin: 0,
    fontSize: "36px",
    fontWeight: 700,
    letterSpacing: "-0.02em",
  },

  heroSubtitle: {
    margin: "10px 0 0",
    fontSize: "15px",
    color: "rgba(255,255,255,0.82)",
    lineHeight: 1.6,
  },

  sectionTitle: {
    margin: 0,
    fontSize: "22px",
    fontWeight: 700,
    letterSpacing: "-0.02em",
  },

  sectionSubtitle: {
    margin: "6px 0 0",
    color: "#64748b",
    fontSize: "14px",
  },

  topRow: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: "16px",
    flexWrap: "wrap",
    marginBottom: "20px",
  },

  statGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    gap: "18px",
    marginBottom: "24px",
  },

  grid2: {
    display: "grid",
    gridTemplateColumns: "minmax(0, 2fr) minmax(320px, 1fr)",
    gap: "20px",
    alignItems: "start",
  },

  grid3: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
    gap: "18px",
  },

  card: {
    background: "#ffffff",
    border: "1px solid #e2e8f0",
    borderRadius: "20px",
    padding: "22px",
    boxShadow: "0 10px 30px rgba(15, 23, 42, 0.08)",
  },

  statCard: {
    background: "#ffffff",
    border: "1px solid #e2e8f0",
    borderRadius: "20px",
    padding: "22px",
    boxShadow: "0 10px 30px rgba(15, 23, 42, 0.08)",
  },

  statLabel: {
    margin: 0,
    color: "#64748b",
    fontSize: "13px",
    fontWeight: 600,
    textTransform: "uppercase",
    letterSpacing: "0.04em",
  },

  statValue: {
    margin: "10px 0 0",
    fontSize: "34px",
    fontWeight: 700,
    letterSpacing: "-0.03em",
    color: "#0f172a",
  },

  button: {
    border: "none",
    background: "#0f172a",
    color: "#fff",
    padding: "12px 16px",
    borderRadius: "12px",
    cursor: "pointer",
    fontWeight: 600,
    fontSize: "14px",
  },

  secondaryButton: {
    border: "1px solid #cbd5e1",
    background: "#fff",
    color: "#0f172a",
    padding: "12px 16px",
    borderRadius: "12px",
    cursor: "pointer",
    fontWeight: 600,
    fontSize: "14px",
  },

  input: {
    width: "100%",
    border: "1px solid #cbd5e1",
    borderRadius: "14px",
    padding: "12px 14px",
    fontSize: "14px",
    outline: "none",
    boxSizing: "border-box",
    background: "#fff",
  },

  textarea: {
    width: "100%",
    border: "1px solid #cbd5e1",
    borderRadius: "16px",
    padding: "14px 16px",
    fontSize: "14px",
    outline: "none",
    boxSizing: "border-box",
    background: "#fff",
    resize: "vertical",
  },

  badge: {
    display: "inline-flex",
    alignItems: "center",
    padding: "6px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: 700,
    background: "#e2e8f0",
    color: "#334155",
  },

  muted: {
    color: "#64748b",
  },

  error: {
    marginTop: "12px",
    color: "#b91c1c",
    background: "#fef2f2",
    border: "1px solid #fecaca",
    borderRadius: "12px",
    padding: "12px 14px",
  },

  infoBox: {
    background: "#f8fafc",
    border: "1px solid #e2e8f0",
    borderRadius: "16px",
    padding: "14px",
  },
};