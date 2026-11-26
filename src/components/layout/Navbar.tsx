import React from 'react';
import { Link } from 'react-router-dom';
import { Menu, Search, Plus, Bell, Timer, Moon, Sun } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useData } from '../../contexts/DataContext';
import { useTimer } from '../../contexts/TimerContext';

interface NavbarProps {
  onToggleSidebar: () => void;
  onOpenSearch: () => void;
  onOpenQuickAdd: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ 
  onToggleSidebar, 
  onOpenSearch, 
  onOpenQuickAdd 
}) => {
  const { user } = useAuth();
  const { notifications, settings, toggleTheme } = useData();
  const { isRunning, remainingSeconds, mode } = useTimer();

  const unreadCount = notifications.filter(n => !n.read).length;

  const formatTimerMinSec = (totalSecs: number) => {
    const mins = Math.floor(totalSecs / 60);
    const secs = totalSecs % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <header className="sticky top-0 z-30 h-16 bg-white/95 dark:bg-slate-900/95 backdrop-blur-sm border-b border-slate-border dark:border-slate-800 px-4 sm:px-6 lg:px-8 flex items-center justify-between">
      {/* Left: Mobile Toggle & Search Trigger */}
      <div className="flex items-center gap-3">
        <button
          onClick={onToggleSidebar}
          className="lg:hidden p-2 rounded-academic text-slate-secondary hover:text-slate-primary hover:bg-slate-muted transition-colors"
          aria-label="Toggle navigation"
        >
          <Menu className="w-5 h-5" />
        </button>

        {/* Global Search Button */}
        <button
          onClick={onOpenSearch}
          className="flex items-center gap-2 px-3 py-1.5 rounded-academic bg-slate-muted/80 dark:bg-slate-800 text-slate-secondary dark:text-slate-300 hover:text-slate-primary dark:hover:text-white border border-transparent hover:border-slate-border text-xs sm:text-sm font-medium transition-colors"
        >
          <Search className="w-4 h-4 text-slate-secondary" />
          <span className="hidden sm:inline">Search anything...</span>
          <span className="inline sm:hidden">Search</span>
          <kbd className="hidden md:inline-block ml-2 px-1.5 py-0.5 text-[10px] font-mono bg-white dark:bg-slate-700 text-slate-secondary rounded border border-slate-border dark:border-slate-600">
            ⌘K
          </kbd>
        </button>
      </div>

      {/* Right Controls */}
      <div className="flex items-center gap-2.5 sm:gap-3.5">
        {/* Active Timer Pill */}
        {isRunning && (
          <Link
            to="/timer"
            className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-primary/10 border border-primary/20 text-primary text-xs font-semibold animate-pulseSubtle hover:bg-primary/15 transition-colors"
            title="Active Focus Session"
          >
            <Timer className="w-3.5 h-3.5 animate-spin" style={{ animationDuration: '4s' }} />
            <span>{mode === 'focus' ? 'Focusing' : 'Break'}</span>
            <span className="font-mono font-bold">{formatTimerMinSec(remainingSeconds)}</span>
          </Link>
        )}

        {/* Quick Add Button */}
        <button
          onClick={onOpenQuickAdd}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-academic bg-primary text-white hover:bg-primary-hover text-xs font-semibold shadow-sm transition-colors"
        >
          <Plus className="w-4 h-4" />
          <span className="hidden sm:inline">Quick Add</span>
        </button>

        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          className="p-2 rounded-academic text-slate-secondary hover:text-slate-primary hover:bg-slate-muted dark:hover:bg-slate-800 transition-colors"
          aria-label="Toggle theme"
          title={`Switch to ${settings.theme === 'light' ? 'dark' : 'light'} theme`}
        >
          {settings.theme === 'light' ? (
            <Moon className="w-4 h-4" />
          ) : (
            <Sun className="w-4 h-4 text-warning" />
          )}
        </button>

        {/* Notifications Icon */}
        <Link
          to="/notifications"
          className="relative p-2 rounded-academic text-slate-secondary hover:text-slate-primary hover:bg-slate-muted dark:hover:bg-slate-800 transition-colors"
          aria-label="Notifications"
        >
          <Bell className="w-4 h-4" />
          {unreadCount > 0 && (
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-danger ring-2 ring-white dark:ring-slate-900" />
          )}
        </Link>

        {/* Profile Avatar */}
        <Link
          to="/profile"
          className="flex items-center gap-2 pl-1 group"
        >
          <div className="w-8 h-8 rounded-full bg-slate-muted dark:bg-slate-800 border border-slate-border dark:border-slate-700 flex items-center justify-center font-bold text-xs text-primary group-hover:border-primary transition-colors">
            {user.avatar || 'ST'}
          </div>
        </Link>
      </div>
    </header>
  );
};
