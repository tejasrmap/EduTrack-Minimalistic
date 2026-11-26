import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Lock, Mail, User, ArrowRight, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useToast } from '../../contexts/ToastContext';

export const RegisterPage: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');

  const { register } = useAuth();
  const { toast } = useToast();
  const navigate = useNavigate();

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !email.trim()) {
      setError('Please provide your full name and student email.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setError('');
    register(name.trim(), email.trim(), password);
    toast.success('Account created! Welcome to EduTrack.');
    navigate('/onboarding');
  };

  return (
    <div className="min-h-screen bg-background dark:bg-slate-900 flex flex-col justify-center items-center px-4 py-12">
      <div className="text-center mb-8">
        <div className="w-12 h-12 rounded-xl bg-primary text-white flex items-center justify-center font-bold text-xl mx-auto shadow-sm mb-3">
          E
        </div>
        <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
          Create Academic Account
        </h1>
        <p className="text-xs sm:text-sm text-slate-secondary mt-1">
          Start your personalized student productivity workspace
        </p>
      </div>

      <div className="card-academic w-full max-w-md p-6 sm:p-8 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 shadow-modal">
        {error && (
          <div className="mb-4 p-3 rounded-academic bg-danger/10 border border-danger/20 text-danger text-xs font-medium">
            {error}
          </div>
        )}

        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
              Full Name *
            </label>
            <div className="relative">
              <User className="w-4 h-4 text-slate-secondary absolute left-3 top-3" />
              <input
                type="text"
                required
                value={name}
                onChange={e => setName(e.target.value)}
                placeholder="Julian Vance"
                className="w-full pl-9 pr-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
              University Email Address *
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
            <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
              Password (min. 6 characters) *
            </label>
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

          <div>
            <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
              Confirm Password *
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-secondary absolute left-3 top-3" />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={confirmPassword}
                onChange={e => setConfirmPassword(e.target.value)}
                placeholder="••••••••••••"
                className="w-full pl-9 pr-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-2.5 px-4 rounded-academic bg-primary text-white font-semibold text-sm hover:bg-primary-hover shadow-sm transition-colors flex items-center justify-center gap-2 mt-2"
          >
            <span>Proceed to Onboarding</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>

        <div className="mt-6 text-center text-xs text-slate-secondary">
          Already registered?{' '}
          <Link to="/login" className="text-primary font-semibold hover:underline">
            Sign in
          </Link>
        </div>
      </div>
    </div>
  );
};
