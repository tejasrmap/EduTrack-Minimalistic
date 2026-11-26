import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { MainLayout } from '../components/layout/MainLayout';

// Auth & Onboarding Pages
import { LoginPage } from '../pages/auth/LoginPage';
import { RegisterPage } from '../pages/auth/RegisterPage';
import { ForgotPasswordPage } from '../pages/auth/ForgotPasswordPage';
import { OnboardingPage } from '../pages/onboarding/OnboardingPage';

// Main Application Modules
import { DashboardPage } from '../pages/dashboard/DashboardPage';
import { SubjectsPage } from '../pages/subjects/SubjectsPage';
import { SubjectDetailPage } from '../pages/subjects/SubjectDetailPage';
import { PlannerPage } from '../pages/planner/PlannerPage';
import { AssignmentsPage } from '../pages/assignments/AssignmentsPage';
import { ExaminationsPage } from '../pages/examinations/ExaminationsPage';
import { NotesPage } from '../pages/notes/NotesPage';
import { TimerPage } from '../pages/timer/TimerPage';
import { GoalsPage } from '../pages/goals/GoalsPage';
import { HistoryPage } from '../pages/history/HistoryPage';
import { AnalyticsPage } from '../pages/analytics/AnalyticsPage';
import { NotificationsPage } from '../pages/notifications/NotificationsPage';
import { ProfilePage } from '../pages/profile/ProfilePage';
import { SettingsPage } from '../pages/settings/SettingsPage';

// Protected Route Guard
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // If user hasn't completed onboarding, direct them to /onboarding
  if (user && !user.isOnboarded && window.location.pathname !== '/onboarding') {
    return <Navigate to="/onboarding" replace />;
  }

  return <>{children}</>;
};

// Public Route Guard (prevents visiting login when already authenticated)
const PublicRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, user } = useAuth();

  if (isAuthenticated) {
    if (user && !user.isOnboarded) {
      return <Navigate to="/onboarding" replace />;
    }
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Authentication Routes */}
      <Route 
        path="/login" 
        element={
          <PublicRoute>
            <LoginPage />
          </PublicRoute>
        } 
      />
      <Route 
        path="/register" 
        element={
          <PublicRoute>
            <RegisterPage />
          </PublicRoute>
        } 
      />
      <Route 
        path="/forgot-password" 
        element={
          <PublicRoute>
            <ForgotPasswordPage />
          </PublicRoute>
        } 
      />

      {/* Onboarding Flow */}
      <Route 
        path="/onboarding" 
        element={
          <ProtectedRoute>
            <OnboardingPage />
          </ProtectedRoute>
        } 
      />

      {/* Main Authenticated Application Routes */}
      <Route 
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/subjects" element={<SubjectsPage />} />
        <Route path="/subjects/:id" element={<SubjectDetailPage />} />
        <Route path="/planner" element={<PlannerPage />} />
        <Route path="/assignments" element={<AssignmentsPage />} />
        <Route path="/examinations" element={<ExaminationsPage />} />
        <Route path="/notes" element={<NotesPage />} />
        <Route path="/timer" element={<TimerPage />} />
        <Route path="/goals" element={<GoalsPage />} />
        <Route path="/history" element={<HistoryPage />} />
        <Route path="/analytics" element={<AnalyticsPage />} />
        <Route path="/notifications" element={<NotificationsPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/settings" element={<SettingsPage />} />
      </Route>

      {/* Fallback route */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};
