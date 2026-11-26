import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  BookOpen, 
  CalendarDays, 
  CheckSquare, 
  GraduationCap, 
  FileText, 
  Timer, 
  Target, 
  History, 
  BarChart3, 
  Bell, 
  User, 
  Settings,
  X
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const { tasks, notifications } = useData();

  const pendingTasksCount = tasks.filter(t => t.status !== 'completed').length;
  const unreadNotifsCount = notifications.filter(n => !n.read).length;

  const navSections = [
    {
      title: 'OVERVIEW',
      items: [
        { path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
      ],
    },
    {
      title: 'ACADEMIC',
      items: [
        { path: '/subjects', label: 'Subjects', icon: BookOpen },
        { path: '/planner', label: 'Study Planner', icon: CalendarDays },
        { path: '/assignments', label: 'Assignments & Tasks', icon: CheckSquare, badge: pendingTasksCount },
        { path: '/examinations', label: 'Examinations', icon: GraduationCap },
        { path: '/notes', label: 'Notes & Knowledge', icon: FileText },
      ],
    },
    {
      title: 'PRODUCTIVITY',
      items: [
        { path: '/timer', label: 'Study Timer', icon: Timer },
        { path: '/goals', label: 'Academic Goals', icon: Target },
        { path: '/history', label: 'Study History', icon: History },
      ],
    },
    {
      title: 'INSIGHTS',
      items: [
        { path: '/analytics', label: 'Study Analytics', icon: BarChart3 },
      ],
    },
    {
      title: 'PERSONAL',
      items: [
        { path: '/notifications', label: 'Notifications', icon: Bell, badge: unreadNotifsCount },
        { path: '/profile', label: 'Student Profile', icon: User },
        { path: '/settings', label: 'Settings', icon: Settings },
      ],
    },
  ];

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div 
          onClick={onClose}
          className="fixed inset-0 bg-slate-900/30 backdrop-blur-xs z-40 lg:hidden animate-fadeIn"
        />
      )}

      {/* Sidebar Container */}
      <aside 
        className={`fixed top-0 left-0 bottom-0 z-50 w-64 bg-white dark:bg-slate-900 border-r border-slate-border dark:border-slate-800 flex flex-col transition-transform duration-250 ease-in-out lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Brand Header */}
        <div className="h-16 px-6 border-b border-slate-border dark:border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-primary text-white flex items-center justify-center font-bold text-sm shadow-sm">
              E
            </div>
            <div>
              <span className="font-bold text-slate-primary dark:text-white tracking-tight text-base">
                EduTrack
              </span>
              <span className="block text-[10px] text-slate-secondary uppercase tracking-wider font-semibold">
                Minimal Academic
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="lg:hidden p-1.5 rounded-lg text-slate-secondary hover:text-slate-primary hover:bg-slate-muted transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Navigation List */}
        <div className="flex-1 overflow-y-auto px-4 py-4 space-y-6">
          {navSections.map((sec) => (
            <div key={sec.title} className="space-y-1">
              <div className="px-3 text-[10px] font-bold text-slate-secondary dark:text-slate-400 tracking-wider uppercase mb-1.5">
                {sec.title}
              </div>
              {sec.items.map((item) => {
                const Icon = item.icon;
                return (
                  <NavLink
                    key={item.path}
                    to={item.path}
                    onClick={onClose}
                    className={({ isActive }) => `flex items-center justify-between px-3 py-2 rounded-academic text-xs sm:text-sm font-medium transition-colors ${
                      isActive 
                        ? 'bg-primary text-white shadow-sm font-semibold' 
                        : 'text-slate-secondary dark:text-slate-300 hover:text-slate-primary dark:hover:text-white hover:bg-slate-muted/80 dark:hover:bg-slate-800'
                    }`}
                  >
                    <div className="flex items-center gap-2.5">
                      <Icon className="w-4 h-4" />
                      <span>{item.label}</span>
                    </div>
                    {typeof item.badge === 'number' && item.badge > 0 && (
                      <span className="px-1.5 py-0.5 text-[10px] font-bold rounded-full bg-slate-muted dark:bg-slate-800 text-slate-primary dark:text-white">
                        {item.badge}
                      </span>
                    )}
                  </NavLink>
                );
              })}
            </div>
          ))}
        </div>

        {/* Minimal Footer Stamp */}
        <div className="p-4 border-t border-slate-border dark:border-slate-800 text-center">
          <span className="text-[11px] text-slate-secondary dark:text-slate-400">
            Academic Edition v2.0
          </span>
        </div>
      </aside>
    </>
  );
};
