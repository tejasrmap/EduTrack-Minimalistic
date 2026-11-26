import React, { useState } from 'react';
import { GraduationCap, ArrowRight, ShieldCheck, BookOpen } from 'lucide-react';
import { useAcademic } from '../context/AppContext';

export const AuthView: React.FC = () => {
  const { loginAsDemo, signup } = useAcademic();
  const [isSignUp, setIsSignUp] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [degree, setDegree] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (isSignUp) {
      signup(name, email, degree);
    } else {
      loginAsDemo('clara');
    }
  };

  return (
    <div className="min-h-screen bg-[#FBFBF9] flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Brand */}
        <div className="text-center mb-8">
          <div className="w-12 h-12 rounded-2xl bg-[#2A5C43] text-white flex items-center justify-center mx-auto mb-3 shadow-xs">
            <GraduationCap className="w-6 h-6" />
          </div>
          <h1 className="font-serif text-2xl font-bold text-stone-900 tracking-tight">
            EduTrack Academic
          </h1>
          <p className="text-xs text-stone-500 mt-1">
            Distraction-free workspace for scholarly inquiry & study planning
          </p>
        </div>

        {/* Paper Container */}
        <div className="paper-card rounded-2xl p-6 sm:p-8">
          {/* 1-Click Demo Profiles */}
          <div className="mb-6 pb-6 border-b border-stone-200">
            <div className="flex items-center justify-between mb-2">
              <span className="text-[11px] font-mono uppercase text-[#2A5C43] font-semibold flex items-center gap-1.5">
                <BookOpen className="w-3.5 h-3.5" />
                Select Demo Scholar
              </span>
              <span className="text-[10px] text-stone-400">Instant Access</span>
            </div>

            <div className="space-y-2">
              <button
                type="button"
                onClick={() => loginAsDemo('clara')}
                className="w-full flex items-center justify-between p-3 rounded-xl bg-stone-50 hover:bg-[#EBF3ED] border border-stone-200 hover:border-[#2A5C43] text-left transition-all group"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-serif font-bold text-stone-900 text-sm group-hover:text-[#2A5C43]">
                      Clara Beauchamp
                    </span>
                    <span className="text-[10px] px-1.5 py-0.2 rounded bg-stone-200 text-stone-700 font-mono">
                      Primary Scholar
                    </span>
                  </div>
                  <p className="text-xs text-stone-500">MArch Urban Ecology · Cambridge</p>
                </div>
                <ArrowRight className="w-4 h-4 text-stone-400 group-hover:text-[#2A5C43] group-hover:translate-x-1 transition-all" />
              </button>

              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => loginAsDemo('henry')}
                  className="p-2.5 rounded-xl bg-stone-50 hover:bg-stone-100 border border-stone-200 text-left transition-colors"
                >
                  <p className="font-serif font-bold text-xs text-stone-800">Henry Cavendish</p>
                  <p className="text-[10px] text-stone-500 truncate font-mono">Mathematics · Oxford</p>
                </button>
                <button
                  type="button"
                  onClick={() => loginAsDemo('beatrice')}
                  className="p-2.5 rounded-xl bg-stone-50 hover:bg-stone-100 border border-stone-200 text-left transition-colors"
                >
                  <p className="font-serif font-bold text-xs text-stone-800">Beatrice Webb</p>
                  <p className="text-[10px] text-stone-500 truncate font-mono">Economics · LSE</p>
                </button>
              </div>
            </div>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="flex items-center justify-between text-xs font-serif italic text-stone-600 mb-1">
              <span>{isSignUp ? 'Register New Scholar Profile' : 'Or Sign In with University Credentials'}</span>
              <button
                type="button"
                onClick={() => setIsSignUp(!isSignUp)}
                className="text-[#2A5C43] hover:underline font-sans not-italic text-[11px]"
              >
                {isSignUp ? 'Return to Sign In' : 'Create Account'}
              </button>
            </div>

            {isSignUp && (
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Full Name
                </label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Thomas Merton"
                  className="w-full px-3.5 py-2.5 rounded-xl bg-stone-50 border border-stone-200 focus:border-[#2A5C43] focus:bg-white text-xs text-stone-800 focus:outline-none"
                />
              </div>
            )}

            <div>
              <label className="block text-xs font-medium text-stone-700 mb-1">
                Institutional Email
              </label>
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="scholar@ox.ac.uk"
                className="w-full px-3.5 py-2.5 rounded-xl bg-stone-50 border border-stone-200 focus:border-[#2A5C43] focus:bg-white text-xs text-stone-800 focus:outline-none"
              />
            </div>

            {isSignUp && (
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Faculty / Degree Programme
                </label>
                <input
                  type="text"
                  required
                  value={degree}
                  onChange={(e) => setDegree(e.target.value)}
                  placeholder="e.g. Faculty of History & Philosophy"
                  className="w-full px-3.5 py-2.5 rounded-xl bg-stone-50 border border-stone-200 focus:border-[#2A5C43] focus:bg-white text-xs text-stone-800 focus:outline-none"
                />
              </div>
            )}

            {!isSignUp && (
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Passphrase
                </label>
                <input
                  type="password"
                  defaultValue="oxfordacademic2026"
                  className="w-full px-3.5 py-2.5 rounded-xl bg-stone-50 border border-stone-200 focus:border-[#2A5C43] focus:bg-white text-xs text-stone-800 focus:outline-none"
                />
              </div>
            )}

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors"
            >
              {isSignUp ? 'Complete Registration' : 'Enter Academic Workspace'}
            </button>
          </form>

          <div className="mt-6 flex items-center justify-center gap-1.5 text-[11px] text-stone-400">
            <ShieldCheck className="w-3.5 h-3.5 text-[#2A5C43]" />
            <span>Local Scholar Storage · No Remote Telemetry Required</span>
          </div>
        </div>
      </div>
    </div>
  );
};

