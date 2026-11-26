import React from 'react';
import { Menu, Play, Calendar as CalendarIcon, BookMarked } from 'lucide-react';
import { useAcademic } from '../context/AppContext';

interface HeaderProps {
  onToggleSidebar: () => void;
}

export const AcademicHeader: React.FC<HeaderProps> = ({ onToggleSidebar }) => {
  const { currentView, setCurrentView, user } = useAcademic();

  const titles: Record<string, string> = {
    dashboard: 'Academic Overview',
    subjects: 'Curriculum & Units of Study',
    planner: 'Study Planner & Timetable',
    timer: 'Distraction-Free Focus Chamber',
    history: 'Reading & Seminar Log',
    goals: 'Academic Honors & Milestones',
    analytics: 'Scholarly Time Allocation',
    settings: 'Workspace Preferences',
  };

  return (
    <header className="sticky top-0 z-30 h-16 bg-white/90 backdrop-blur-sm border-b border-[#E6E6DF] px-4 lg:px-8 flex items-center justify-between">
      <div className="flex items-center gap-4">
        <button
          onClick={onToggleSidebar}
          className="lg:hidden p-2 rounded-lg text-stone-600 hover:text-stone-900 hover:bg-stone-100"
          aria-label="Toggle navigation"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div>
          <h2 className="font-serif text-lg font-bold text-stone-900 tracking-tight">
            {titles[currentView] || 'Academic Workspace'}
          </h2>
          <p className="text-[11px] text-stone-500 hidden sm:block">
            {user.institution} · {user.term}
          </p>
        </div>
      </div>

      <div className="flex items-center gap-3">
        {/* Streak tag */}
        <div className="hidden sm:flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-stone-100 text-stone-700 text-xs font-mono">
          <BookMarked className="w-3.5 h-3.5 text-[#2A5C43]" />
          <span>{user.streakDays} Day Term Streak</span>
        </div>

        {/* Start Focus Session */}
        {currentView !== 'timer' && (
          <button
            onClick={() => setCurrentView('timer')}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors"
          >
            <Play className="w-3.5 h-3.5 fill-white" />
            <span>Begin Focus</span>
          </button>
        )}

        <div className="text-xs text-stone-500 font-mono hidden md:flex items-center gap-1">
          <CalendarIcon className="w-3.5 h-3.5" />
          <span>{new Date().toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}</span>
        </div>
      </div>
    </header>
  );
};

