import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Lock, Mail, ArrowRight, UserCheck } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useToast } from '../../contexts/ToastContext';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('julian.vance@university.edu');
  const [password, setPassword] = useState('academic2026');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const { login, loginAsDemo } = useAuth();
  const { toast } = useToast();
  const navigate = useNavigate();

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password.trim()) {
      setError('Please provide your student email and password.');
      return;
    }
    setError('');
    login(email);
    toast.success('Signed in successfully.');
    navigate('/dashboard');
  };

  const handleDemoAccess = (persona: 'julian' | 'elena' | 'marcus') => {
    loginAsDemo(persona);
    toast.success(`Demo profile loaded: ${persona === 'julian' ? 'Julian Vance' : persona === 'elena' ? 'Elena Rostova' : 'Marcus Holloway'}`);
    navigate('/dashboard');
  };

  return (
    <div className="min-h-screen bg-background dark:bg-slate-900 flex flex-col justify-center items-center px-4 py-12">
      {/* Brand Header */}
      <div className="text-center mb-8">
        <div className="w-12 h-12 rounded-xl bg-primary text-white flex items-center justify-center font-bold text-xl mx-auto shadow-sm mb-3">
          E
        </div>
        <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
          Welcome back to EduTrack
        </h1>
        <p className="text-xs sm:text-sm text-slate-secondary mt-1">
          Minimal Academic Workspace for University Students
        </p>
      </div>

      {/* Main Card */}
      <div className="card-academic w-full max-w-md p-6 sm:p-8 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 shadow-modal">
        {error && (
          <div className="mb-4 p-3 rounded-academic bg-danger/10 border border-danger/20 text-danger text-xs font-medium">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
              University Email Address
            </label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-secondary absolute left-3 top-3" />
              <input
                type="email"
                required
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="student@university.edu"
                className="w-full pl-9 pr-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
              />
            </div>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1.5">
              <label className="block text-xs font-semibold text-slate-primary dark:text-white">
                Password
              </label>
              <Link to="/forgot-password" className="text-xs text-primary hover:underline">
                Forgot password?
              </Link>
            </div>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-secondary absolute left-3 top-3" />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="••••••••••••"
                className="w-full pl-9 pr-10 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-2.5 text-slate-secondary hover:text-slate-primary"
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-2.5 px-4 rounded-academic bg-primary text-white font-semibold text-sm hover:bg-primary-hover shadow-sm transition-colors flex items-center justify-center gap-2 mt-2"
          >
            <span>Sign In</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>

        {/* Divider */}
        <div className="relative my-6 text-center">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-slate-border dark:border-slate-700" />
          </div>
          <span className="relative px-3 bg-white dark:bg-slate-800 text-[11px] font-bold tracking-wider uppercase text-slate-secondary">
            Instant Demo Profiles
          </span>
        </div>

        {/* Demo Personas */}
        <div className="grid grid-cols-3 gap-2">
          <button
            type="button"
            onClick={() => handleDemoAccess('julian')}
            className="p-2 rounded-academic border border-slate-border dark:border-slate-700 hover:border-primary text-center text-xs transition-colors hover:bg-primary/5"
          >
            <div className="font-bold text-slate-primary dark:text-white">Julian</div>
            <div className="text-[10px] text-slate-secondary truncate">CS & Math</div>
          </button>

          <button
            type="button"
            onClick={() => handleDemoAccess('elena')}
            className="p-2 rounded-academic border border-slate-border dark:border-slate-700 hover:border-primary text-center text-xs transition-colors hover:bg-primary/5"
          >
            <div className="font-bold text-slate-primary dark:text-white">Elena</div>
            <div className="text-[10px] text-slate-secondary truncate">AI & CogSci</div>
          </button>

          <button
            type="button"
            onClick={() => handleDemoAccess('marcus')}
            className="p-2 rounded-academic border border-slate-border dark:border-slate-700 hover:border-primary text-center text-xs transition-colors hover:bg-primary/5"
          >
            <div className="font-bold text-slate-primary dark:text-white">Marcus</div>
            <div className="text-[10px] text-slate-secondary truncate">Elec Eng</div>
          </button>
        </div>

        {/* Footer Link */}
        <div className="mt-6 text-center text-xs text-slate-secondary">
          Don't have an account yet?{' '}
          <Link to="/register" className="text-primary font-semibold hover:underline">
            Register new workspace
          </Link>
        </div>
      </div>
    </div>
  );
};

