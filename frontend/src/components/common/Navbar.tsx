import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-indigo-700 text-white px-6 py-3 flex items-center justify-between shadow-md">
      <Link to="/boards" className="text-xl font-bold tracking-tight hover:opacity-90">
        TaskBoard
      </Link>
      {user && (
        <div className="flex items-center gap-4">
          <span className="text-sm hidden sm:block opacity-80">
            {user.username}
          </span>
          <button
            onClick={handleLogout}
            className="text-sm bg-white text-indigo-700 font-semibold px-3 py-1.5 rounded hover:bg-indigo-50 transition"
          >
            Logout
          </button>
        </div>
      )}
    </nav>
  );
}
