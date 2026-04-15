import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import DashboardPage from "../pages/DashboardPage";
import SessionsPage from "../pages/SessionsPage";
import CodingSubmissionPage from "../pages/CodingSubmissionPage";
import BehaviorSubmission from "../pages/BehaviorSubmission";
import RecommendationPage from "../pages/RecommendationPage";
import ProtectedRoute from "../components/ProtectedRoute.jsx";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/sessions"
          element={
            <ProtectedRoute>
              <SessionsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/coding-submissions"
          element={
            <ProtectedRoute>
              <CodingSubmissionPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/behavioral-submissions"
          element={
            <ProtectedRoute>
              <BehaviorSubmission />
            </ProtectedRoute>
          }
        />

        <Route
          path="/recommendations"
          element={
            <ProtectedRoute>
              <RecommendationPage />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}