import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './components/common/PrivateRoute';
import Navbar from './components/common/Navbar';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import BoardsPage from './pages/BoardsPage';
import BoardPage from './pages/BoardPage';

function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen flex flex-col bg-gray-100">
      <Navbar />
      <main className="flex-1">{children}</main>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/boards"
            element={
              <PrivateRoute>
                <Layout><BoardsPage /></Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/boards/:boardId"
            element={
              <PrivateRoute>
                <Layout><BoardPage /></Layout>
              </PrivateRoute>
            }
          />
          <Route path="*" element={<Navigate to="/boards" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
