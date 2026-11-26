import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, CalendarDays, Timer, CheckSquare, MoreHorizontal } from 'lucide-react';

interface MobileNavProps {
  onOpenMore: () => void;
}

export const MobileNav: React.FC<MobileNavProps> = ({ onOpenMore }) => {
  const tabs = [
    { path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { path: '/planner', label: 'Planner', icon: CalendarDays },
    { path: '/timer', label: 'Timer', icon: Timer },
    { path: '/assignments', label: 'Tasks', icon: CheckSquare },
  ];

  return (
    <nav className="lg:hidden fixed bottom-0 left-0 right-0 z-40 bg-white/95 dark:bg-slate-900/95 backdrop-blur-sm border-t border-slate-border dark:border-slate-800 px-2 py-1.5 flex items-center justify-around">
      {tabs.map((t) => {
        const Icon = t.icon;
        return (
          <NavLink
            key={t.path}
            to={t.path}
            className={({ isActive }) => `flex flex-col items-center py-1 px-3 rounded-lg text-[10px] font-medium transition-colors ${
              isActive 
                ? 'text-primary font-bold' 
                : 'text-slate-secondary hover:text-slate-primary dark:hover:text-white'
            }`}
          >
            <Icon className="w-5 h-5 mb-0.5" />
            <span>{t.label}</span>
          </NavLink>
        );
      })}

      <button
        onClick={onOpenMore}
        className="flex flex-col items-center py-1 px-3 rounded-lg text-[10px] font-medium text-slate-secondary hover:text-slate-primary dark:hover:text-white transition-colors"
      >
        <MoreHorizontal className="w-5 h-5 mb-0.5" />
        <span>More</span>
      </button>
    </nav>
  );
};
