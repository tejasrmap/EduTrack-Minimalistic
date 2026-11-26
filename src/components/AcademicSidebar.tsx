import React from 'react';
import { 
  Compass, 
  BookOpen, 
  Calendar, 
  Clock, 
  Archive, 
  Award, 
  BarChart2, 
  Settings, 
  LogOut,
  GraduationCap
} from 'lucide-react';
import { useAcademic } from '../context/AppContext';
import { ViewType } from '../types';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AcademicSidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const { currentView, setCurrentView, user, tasks, logout } = useAcademic();

  const pendingCount = tasks.filter(t => t.status !== 'completed').length;

  const links: { id: ViewType; label: string; icon: React.ElementType; count?: number }[] = [
    { id: 'dashboard', label: 'Overview', icon: Compass },
    { id: 'subjects', label: 'Curriculum & Units', icon: BookOpen },
    { id: 'planner', label: 'Study Planner', icon: Calendar, count: pendingCount },
    { id: 'timer', label: 'Focus Chamber', icon: Clock },
    { id: 'history', label: 'Reading Log & Archive', icon: Archive },
    { id: 'goals', label: 'Milestones & Honors', icon: Award },
    { id: 'analytics', label: 'Academic Analytics', icon: BarChart2 },
    { id: 'settings', label: 'Preferences', icon: Settings },
  ];

  const handleNav = (v: ViewType) => {
    setCurrentView(v);
    onClose();
  };

  return (
    <>
      {isOpen && (
        <div 
          onClick={onClose}
          className="fixed inset-0 bg-stone-900/30 backdrop-blur-xs z-40 lg:hidden"
        />
      )}

      <aside 
        className={`fixed top-0 bottom-0 left-0 w-64 bg-[#FBFBF9] border-r border-[#E6E6DF] z-50 flex flex-col justify-between transition-transform duration-200 lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div>
          {/* Brand */}
          <div className="h-16 flex items-center gap-3 px-6 border-b border-[#E6E6DF]">
            <div className="w-8 h-8 rounded-lg bg-[#2A5C43] text-white flex items-center justify-center">
              <GraduationCap className="w-5 h-5" />
            </div>
            <div>
              <h1 className="font-serif text-base font-bold text-[#1E2522] tracking-tight">
                EduTrack
              </h1>
              <p className="text-[10px] uppercase font-mono tracking-wider text-stone-500">
                Minimal Academic
              </p>
            </div>
          </div>

          {/* User Profile Snippet */}
          <div className="p-4 mx-3 mt-3 rounded-xl bg-white border border-[#E6E6DF] flex items-center gap-3">
            <img 
              src={user.avatar} 
              alt={user.name} 
              className="w-9 h-9 rounded-full object-cover ring-1 ring-stone-200"
            />
            <div className="min-w-0 flex-1">
              <h3 className="text-xs font-semibold text-stone-800 truncate">{user.name}</h3>
              <p className="text-[10px] text-stone-500 truncate">{user.institution.split('·')[0]}</p>
            </div>
          </div>

          {/* Nav List */}
          <nav className="p-3 space-y-0.5 mt-2">
            {links.map((link) => {
              const Icon = link.icon;
              const isActive = currentView === link.id;

              return (
                <button
                  key={link.id}
                  onClick={() => handleNav(link.id)}
                  className={`w-full flex items-center justify-between px-3.5 py-2 rounded-lg text-xs font-medium transition-colors ${
                    isActive 
                      ? 'bg-[#EBF3ED] text-[#2A5C43] font-semibold' 
                      : 'text-stone-600 hover:text-stone-900 hover:bg-stone-100'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <Icon className={`w-4 h-4 ${isActive ? 'text-[#2A5C43]' : 'text-stone-500'}`} />
                    <span>{link.label}</span>
                  </div>

                  {link.count !== undefined && link.count > 0 && (
                    <span className="text-[10px] px-1.5 py-0.2 rounded-full bg-stone-200 text-stone-700 font-mono">
                      {link.count}
                    </span>
                  )}
                </button>
              );
            })}
          </nav>
        </div>

        {/* Footer */}
        <div className="p-4 border-t border-[#E6E6DF]">
          <button
            onClick={logout}
            className="w-full flex items-center justify-center gap-2 px-3 py-2 rounded-lg text-xs text-stone-600 hover:text-stone-900 hover:bg-stone-100 transition-colors"
          >
            <LogOut className="w-3.5 h-3.5 text-stone-400" />
            <span>Sign Out</span>
          </button>
        </div>
      </aside>
    </>
  );
};

