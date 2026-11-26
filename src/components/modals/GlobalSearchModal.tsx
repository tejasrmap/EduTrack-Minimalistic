import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, X, BookOpen, CheckSquare, FileText, GraduationCap, ArrowRight } from 'lucide-react';
import { useData } from '../../contexts/DataContext';

interface GlobalSearchModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const GlobalSearchModal: React.FC<GlobalSearchModalProps> = ({ isOpen, onClose }) => {
  const [query, setQuery] = useState('');
  const { subjects, tasks, notes, examinations } = useData();
  const navigate = useNavigate();
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => inputRef.current?.focus(), 50);
    } else {
      setQuery('');
    }
  }, [isOpen]);

  // Global hotkey Ctrl+K / Cmd+K
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault();
        if (isOpen) onClose();
        else onClose(); // trigger will be in parent
      }
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const trimmed = query.trim().toLowerCase();

  const filteredSubjects = trimmed
    ? subjects.filter(s => s.name.toLowerCase().includes(trimmed) || s.code.toLowerCase().includes(trimmed))
    : [];

  const filteredTasks = trimmed
    ? tasks.filter(t => t.title.toLowerCase().includes(trimmed) || t.description.toLowerCase().includes(trimmed))
    : [];

  const filteredNotes = trimmed
    ? notes.filter(n => n.title.toLowerCase().includes(trimmed) || n.content.toLowerCase().includes(trimmed) || n.tags.some(tag => tag.toLowerCase().includes(trimmed)))
    : [];

  const filteredExams = trimmed
    ? examinations.filter(e => e.title.toLowerCase().includes(trimmed) || e.location.toLowerCase().includes(trimmed))
    : [];

  const totalResults = filteredSubjects.length + filteredTasks.length + filteredNotes.length + filteredExams.length;

  const handleSelect = (path: string) => {
    navigate(path);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-start justify-center pt-20 px-4 bg-slate-900/40 backdrop-blur-xs animate-fadeIn">
      <div className="bg-white dark:bg-slate-900 w-full max-w-xl rounded-academic-lg border border-slate-border dark:border-slate-800 shadow-modal overflow-hidden animate-modalIn">
        {/* Search Input Bar */}
        <div className="flex items-center gap-3 px-4 py-3.5 border-b border-slate-border dark:border-slate-800">
          <Search className="w-5 h-5 text-slate-secondary" />
          <input
            ref={inputRef}
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search subjects, assignments, notes, exams..."
            className="flex-1 bg-transparent text-sm sm:text-base text-slate-primary dark:text-white placeholder:text-slate-secondary focus:outline-none"
          />
          {query && (
            <button
              onClick={() => setQuery('')}
              className="p-1 text-slate-secondary hover:text-slate-primary"
            >
              <X className="w-4 h-4" />
            </button>
          )}
          <kbd className="hidden sm:inline-block px-1.5 py-0.5 text-[10px] font-mono bg-slate-muted dark:bg-slate-800 text-slate-secondary rounded border border-slate-border dark:border-slate-700">
            ESC
          </kbd>
        </div>

        {/* Results Container */}
        <div className="max-h-96 overflow-y-auto p-3 space-y-4">
          {!trimmed && (
            <div className="p-6 text-center text-slate-secondary text-xs sm:text-sm">
              Type keywords to search across your entire academic workspace.
            </div>
          )}

          {trimmed && totalResults === 0 && (
            <div className="p-8 text-center text-slate-secondary text-xs sm:text-sm">
              No matching subjects, tasks, notes, or examinations found for "{query}".
            </div>
          )}

          {/* Subjects */}
          {filteredSubjects.length > 0 && (
            <div className="space-y-1">
              <div className="px-2 text-[10px] font-bold uppercase tracking-wider text-slate-secondary">
                Subjects
              </div>
              {filteredSubjects.map(s => (
                <div
                  key={s.id}
                  onClick={() => handleSelect(`/subjects/${s.id}`)}
                  className="flex items-center justify-between p-2.5 rounded-academic hover:bg-slate-muted dark:hover:bg-slate-800 cursor-pointer transition-colors"
                >
                  <div className="flex items-center gap-2.5">
                    <BookOpen className="w-4 h-4 text-primary" />
                    <div>
                      <span className="text-xs sm:text-sm font-semibold text-slate-primary dark:text-white">{s.name}</span>
                      <span className="ml-2 text-xs font-mono text-slate-secondary">{s.code}</span>
                    </div>
                  </div>
                  <ArrowRight className="w-3.5 h-3.5 text-slate-secondary" />
                </div>
              ))}
            </div>
          )}

          {/* Tasks */}
          {filteredTasks.length > 0 && (
            <div className="space-y-1">
              <div className="px-2 text-[10px] font-bold uppercase tracking-wider text-slate-secondary">
                Tasks & Assignments
              </div>
              {filteredTasks.map(t => (
                <div
                  key={t.id}
                  onClick={() => handleSelect('/assignments')}
                  className="flex items-center justify-between p-2.5 rounded-academic hover:bg-slate-muted dark:hover:bg-slate-800 cursor-pointer transition-colors"
                >
                  <div className="flex items-center gap-2.5">
                    <CheckSquare className="w-4 h-4 text-success" />
                    <div>
                      <span className="text-xs sm:text-sm font-medium text-slate-primary dark:text-white">{t.title}</span>
                      <span className="ml-2 text-[10px] uppercase font-bold px-1.5 py-0.5 rounded bg-slate-muted dark:bg-slate-800 text-slate-secondary">
                        {t.status.replace('_', ' ')}
                      </span>
                    </div>
                  </div>
                  <ArrowRight className="w-3.5 h-3.5 text-slate-secondary" />
                </div>
              ))}
            </div>
          )}

          {/* Notes */}
          {filteredNotes.length > 0 && (
            <div className="space-y-1">
              <div className="px-2 text-[10px] font-bold uppercase tracking-wider text-slate-secondary">
                Notes & Knowledge
              </div>
              {filteredNotes.map(n => (
                <div
                  key={n.id}
                  onClick={() => handleSelect('/notes')}
                  className="flex items-center justify-between p-2.5 rounded-academic hover:bg-slate-muted dark:hover:bg-slate-800 cursor-pointer transition-colors"
                >
                  <div className="flex items-center gap-2.5">
                    <FileText className="w-4 h-4 text-warning" />
                    <span className="text-xs sm:text-sm font-medium text-slate-primary dark:text-white">{n.title}</span>
                  </div>
                  <ArrowRight className="w-3.5 h-3.5 text-slate-secondary" />
                </div>
              ))}
            </div>
          )}

          {/* Exams */}
          {filteredExams.length > 0 && (
            <div className="space-y-1">
              <div className="px-2 text-[10px] font-bold uppercase tracking-wider text-slate-secondary">
                Examinations
              </div>
              {filteredExams.map(e => (
                <div
                  key={e.id}
                  onClick={() => handleSelect('/examinations')}
                  className="flex items-center justify-between p-2.5 rounded-academic hover:bg-slate-muted dark:hover:bg-slate-800 cursor-pointer transition-colors"
                >
                  <div className="flex items-center gap-2.5">
                    <GraduationCap className="w-4 h-4 text-danger" />
                    <div>
                      <span className="text-xs sm:text-sm font-medium text-slate-primary dark:text-white">{e.title}</span>
                      <span className="ml-2 text-xs text-slate-secondary">{e.examDate}</span>
                    </div>
                  </div>
                  <ArrowRight className="w-3.5 h-3.5 text-slate-secondary" />
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

