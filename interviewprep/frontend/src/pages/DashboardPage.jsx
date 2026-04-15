export default function DashboardPage() {
  const user = JSON.parse(localStorage.getItem("user"));

  return (
    <div style={{ padding: "24px" }}>
      <h1>Dashboard</h1>
      <p>Welcome, {user?.name}.</p>
      <p>You are logged in.</p>
    </div>
  );
}