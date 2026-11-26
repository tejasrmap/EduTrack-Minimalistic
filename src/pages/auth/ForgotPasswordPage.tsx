import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail, ArrowLeft, CheckCircle2 } from 'lucide-react';
import { useToast } from '../../contexts/ToastContext';

export const ForgotPasswordPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const { toast } = useToast();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim()) return;
    setSubmitted(true);
    toast.info('Simulated password reset instruction sent.');
  };

  return (
    <div className="min-h-screen bg-background dark:bg-slate-900 flex flex-col justify-center items-center px-4 py-12">
      <div className="text-center mb-8">
        <div className="w-12 h-12 rounded-xl bg-primary text-white flex items-center justify-center font-bold text-xl mx-auto shadow-sm mb-3">
          E
        </div>
        <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
          Reset Password
        </h1>
        <p className="text-xs sm:text-sm text-slate-secondary mt-1">
          Recover access to your academic workspace
        </p>
      </div>

      <div className="card-academic w-full max-w-md p-6 sm:p-8 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 shadow-modal">
        {submitted ? (
          <div className="text-center py-4 space-y-4">
            <CheckCircle2 className="w-12 h-12 text-success mx-auto" />
            <h3 className="font-bold text-slate-primary dark:text-white text-base">
              Reset Link Dispatched
            </h3>
            <p className="text-xs sm:text-sm text-slate-secondary leading-relaxed">
              If an account with <strong className="text-slate-primary dark:text-white">{email}</strong> exists, password reset instructions have been simulated. In demo mode, you can sign in directly.
            </p>
            <div className="pt-2">
              <Link
                to="/login"
                className="inline-flex items-center gap-1.5 text-xs sm:text-sm font-semibold text-primary hover:underline"
              >
                <ArrowLeft className="w-4 h-4" />
                <span>Return to Sign In</span>
              </Link>
            </div>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <p className="text-xs text-slate-secondary leading-relaxed">
              Enter your student email address and we'll send a link to reset your account password.
            </p>
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

            <button
              type="submit"
              className="w-full py-2.5 px-4 rounded-academic bg-primary text-white font-semibold text-sm hover:bg-primary-hover shadow-sm transition-colors flex items-center justify-center gap-2 mt-2"
            >
              Send Reset Instructions
            </button>

            <div className="text-center pt-2">
              <Link
                to="/login"
                className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-secondary hover:text-slate-primary"
              >
                <ArrowLeft className="w-3.5 h-3.5" />
                <span>Back to Sign In</span>
              </Link>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
