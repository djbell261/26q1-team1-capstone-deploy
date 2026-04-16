import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import DashboardPage from "../pages/DashboardPage";
import SessionsPage from "../pages/SessionsPage";
import CodingSubmissionPage from "../pages/CodingSubmissionPage";
import BehaviorSubmission from "../pages/BehaviorSubmission";
import RecommendationPage from "../pages/RecommendationPage";
import CodingChallengeLibraryPage from "../pages/CodingChallengeLibraryPage";
import CodingChallengeSessionPage from "../pages/CodingChallengeSessionPage";
import BehavioralQuestionLibraryPage from "../pages/BehavioralQuestionLibraryPage";
import BehavioralQuestionSessionPage from "../pages/BehavioralQuestionSessionPage";
import ProtectedRoute from "../components/ProtectedRoute";

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

        <Route
          path="/coding-challenges"
          element={
            <ProtectedRoute>
              <CodingChallengeLibraryPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/coding-session/:sessionId"
          element={
            <ProtectedRoute>
              <CodingChallengeSessionPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/behavioral-questions"
          element={
            <ProtectedRoute>
              <BehavioralQuestionLibraryPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/behavioral-session/:sessionId"
          element={
            <ProtectedRoute>
              <BehavioralQuestionSessionPage />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}