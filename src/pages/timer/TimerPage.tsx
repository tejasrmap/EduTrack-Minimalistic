import React, { useState } from 'react';
import {
  Play,
  Pause,
  RotateCcw,
  CheckCircle2,
  Plus,
  BookOpen,
  Coffee,
  Sparkles,
  Sliders,
  X,
  Volume2
} from 'lucide-react';
import { useTimer, TimerMode } from '../../contexts/TimerContext';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { SessionType } from '../../types';

export const TimerPage: React.FC = () => {
  const {
    isRunning,
    mode,
    subjectId,
    sessionDescription,
    totalDurationSeconds,
    remainingSeconds,
    completedPomodoroCount,
    progressPercent,
    startTimer,
    pauseTimer,
    resumeTimer,
    resetTimer,
    switchMode,
    setSubjectId,
    setSessionDescription,
    setCustomDuration,
    logCompletedSession
  } = useTimer();

  const { subjects, settings, addStudySession } = useData();
  const { toast } = useToast();

  const [isManualModalOpen, setIsManualModalOpen] = useState(false);
  const [isCustomModalOpen, setIsCustomModalOpen] = useState(false);
  const [customInputMins, setCustomInputMins] = useState(45);

  // Manual logging modal form state
  const [manualSubjectId, setManualSubjectId] = useState(subjects[0]?.id || '');
  const [manualDurationMins, setManualDurationMins] = useState(60);
  const [manualDate, setManualDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [manualType, setManualType] = useState<SessionType>('deep_work');
  const [manualNotes, setManualNotes] = useState('');

  // Format MM:SS
  const mins = Math.floor(remainingSeconds / 60);
  const secs = remainingSeconds % 60;
  const timeFormatted = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

  const currentSubject = subjects.find(s => s.id === subjectId);

  const handleManualSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (manualDurationMins <= 0) return;

    addStudySession({
      subjectId: manualSubjectId || (subjects[0]?.id || ''),
      durationMinutes: Number(manualDurationMins),
      sessionType: manualType,
      notes: manualNotes.trim() || `Manual logged study session`,
      isManual: true,
      startTime: `${manualDate}T10:00:00Z`,
      endTime: `${manualDate}T11:00:00Z`,
    });

    toast.success(`Logged ${manualDurationMins} minutes to history.`);
    setIsManualModalOpen(false);
  };

  const handleCustomSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (customInputMins <= 0) return;
    switchMode('custom', customInputMins);
    setIsCustomModalOpen(false);
  };

  // Radial progress calculations
  const radius = 120;
  const stroke = 8;
  const normalizedRadius = radius - stroke * 2;
  const circumference = normalizedRadius * 2 * Math.PI;
  const strokeDashoffset = circumference - (progressPercent / 100) * circumference;

  return (
    <div className="max-w-4xl mx-auto space-y-6 sm:space-y-8 animate-fadeIn">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-slate-border dark:border-slate-800">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
            Academic Focus Timer
          </h1>
          <p className="text-xs sm:text-sm text-slate-secondary mt-0.5">
            Pomodoro pacing with zero-drift accuracy and instant session persistence
          </p>
        </div>

        <button
          onClick={() => setIsManualModalOpen(true)}
          className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white text-xs sm:text-sm font-semibold hover:bg-slate-muted dark:hover:bg-slate-700 shadow-sm transition-colors self-start sm:self-auto"
        >
          <Plus className="w-4 h-4 text-primary" />
          <span>Manual Log</span>
        </button>
      </div>

      {/* Main Distraction-Free Card */}
      <div className="card-academic p-6 sm:p-10 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 shadow-subtle flex flex-col items-center">
        {/* Mode Selector Tabs */}
        <div className="flex items-center gap-2 p-1 rounded-academic bg-slate-muted dark:bg-slate-900 border border-slate-border dark:border-slate-700 mb-8 max-w-md w-full justify-center">
          <button
            onClick={() => switchMode('focus')}
            className={`flex-1 py-1.5 px-3 text-xs font-semibold rounded-academic transition-colors flex items-center justify-center gap-1.5 ${mode === 'focus'
              ? 'bg-white dark:bg-slate-700 text-primary shadow-sm'
              : 'text-slate-secondary hover:text-slate-primary'
              }`}
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>Focus ({settings.pomodoroFocusMinutes}m)</span>
          </button>

          <button
            onClick={() => switchMode('short_break')}
            className={`flex-1 py-1.5 px-3 text-xs font-semibold rounded-academic transition-colors flex items-center justify-center gap-1.5 ${mode === 'short_break'
              ? 'bg-white dark:bg-slate-700 text-success shadow-sm'
              : 'text-slate-secondary hover:text-slate-primary'
              }`}
          >
            <Coffee className="w-3.5 h-3.5" />
            <span>Short Break ({settings.pomodoroShortBreakMinutes}m)</span>
          </button>

          <button
            onClick={() => switchMode('long_break')}
            className={`flex-1 py-1.5 px-3 text-xs font-semibold rounded-academic transition-colors flex items-center justify-center gap-1.5 ${mode === 'long_break'
              ? 'bg-white dark:bg-slate-700 text-warning shadow-sm'
              : 'text-slate-secondary hover:text-slate-primary'
              }`}
          >
            <Coffee className="w-3.5 h-3.5" />
            <span>Long Break ({settings.pomodoroLongBreakMinutes}m)</span>
          </button>

          <button
            onClick={() => setIsCustomModalOpen(true)}
            className={`p-1.5 rounded-academic transition-colors ${mode === 'custom'
              ? 'bg-white dark:bg-slate-700 text-primary shadow-sm'
              : 'text-slate-secondary hover:text-slate-primary'
              }`}
            title="Custom Interval"
          >
            <Sliders className="w-4 h-4" />
          </button>
        </div>

        {/* Circular Dial Timer */}
        <div className="relative w-64 h-64 sm:w-72 sm:h-72 flex items-center justify-center select-none mb-8">
          <svg className="w-full h-full -rotate-90">
            {/* Background Track */}
            <circle
              cx="50%"
              cy="50%"
              r={normalizedRadius}
              stroke="currentColor"
              strokeWidth={stroke}
              fill="transparent"
              className="text-slate-muted dark:text-slate-700"
            />
            {/* Animated Progress Ring */}
            <circle
              cx="50%"
              cy="50%"
              r={normalizedRadius}
              stroke="currentColor"
              strokeWidth={stroke}
              strokeDasharray={`${circumference} ${circumference}`}
              style={{ strokeDashoffset }}
              strokeLinecap="round"
              fill="transparent"
              className={`transition-all duration-300 ${mode === 'focus' ? 'text-primary' : mode === 'short_break' ? 'text-success' : 'text-warning'
                }`}
            />
          </svg>

          {/* Time & Mode Display Centered */}
          <div className="absolute flex flex-col items-center">
            <span className="font-mono text-5xl sm:text-6xl font-bold text-slate-primary dark:text-white tracking-tight">
              {timeFormatted}
            </span>
            <span className="text-xs font-semibold text-slate-secondary uppercase tracking-widest mt-1">
              {mode === 'focus' ? 'Deep Focus Session' : mode === 'short_break' ? 'Short Academic Break' : mode === 'long_break' ? 'Rejuvenation Break' : 'Custom Interval'}
            </span>
            {currentSubject && (
              <span
                className="mt-2 text-xs font-bold px-2 py-0.5 rounded-full"
                style={{ backgroundColor: `${currentSubject.color}15`, color: currentSubject.color }}
              >
                {currentSubject.code} · {currentSubject.name}
              </span>
            )}
          </div>
        </div>

        {/* Timer Control Buttons */}
        <div className="flex items-center gap-4 mb-8">
          {!isRunning ? (
            <button
              onClick={startTimer}
              className="px-8 py-3.5 rounded-academic bg-primary text-white font-bold text-sm sm:text-base hover:bg-primary-hover shadow-subtle-hover flex items-center gap-2 transition-all hover:scale-[1.02]"
            >
              <Play className="w-5 h-5 fill-current" />
              <span>{remainingSeconds < totalDurationSeconds ? 'Resume Focus' : 'Start Focus'}</span>
            </button>
          ) : (
            <button
              onClick={pauseTimer}
              className="px-8 py-3.5 rounded-academic bg-warning text-white font-bold text-sm sm:text-base hover:bg-amber-600 shadow-subtle-hover flex items-center gap-2 transition-all hover:scale-[1.02]"
            >
              <Pause className="w-5 h-5 fill-current" />
              <span>Pause Focus</span>
            </button>
          )}

          <button
            onClick={resetTimer}
            className="p-3.5 rounded-academic border border-slate-border dark:border-slate-700 text-slate-secondary hover:text-slate-primary hover:bg-slate-muted dark:hover:bg-slate-700 transition-colors"
            title="Reset Timer"
          >
            <RotateCcw className="w-5 h-5" />
          </button>

          {remainingSeconds < totalDurationSeconds && (
            <button
              onClick={() => logCompletedSession()}
              className="p-3.5 rounded-academic border border-success/30 text-success hover:bg-success/10 transition-colors"
              title="Log Early Completion"
            >
              <CheckCircle2 className="w-5 h-5" />
            </button>
          )}
        </div>

        {/* Subject & Description Assignment */}
        <div className="w-full max-w-md space-y-3 pt-6 border-t border-slate-border dark:border-slate-700">
          <div>
            <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5 flex items-center gap-1.5">
              <BookOpen className="w-3.5 h-3.5 text-primary" />
              <span>Link Academic Subject</span>
            </label>
            <select
              value={subjectId}
              onChange={e => setSubjectId(e.target.value)}
              className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-slate-muted/50 dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
            >
              {subjects.map(s => (
                <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
              Session Objective / Focus Topic
            </label>
            <input
              type="text"
              placeholder="e.g. Proving Dijkstra invariant & graph coding"
              value={sessionDescription}
              onChange={e => setSessionDescription(e.target.value)}
              className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-slate-muted/50 dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
            />
          </div>

          {/* Pomodoro Cycles Count */}
          <div className="flex items-center justify-between text-xs text-slate-secondary pt-2">
            <span>Completed Pomodoros: <strong className="text-slate-primary dark:text-white">{completedPomodoroCount}</strong></span>
            <span>Next long break in: <strong className="text-primary">{settings.longBreakInterval - (completedPomodoroCount % settings.longBreakInterval)}</strong> sessions</span>
          </div>
        </div>
      </div>

      {/* Manual Session Logging Modal */}
      {isManualModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white dark:bg-slate-900 w-full max-w-md rounded-academic-lg border border-slate-border dark:border-slate-800 shadow-modal p-6 animate-modalIn">
            <div className="flex items-center justify-between pb-3 border-b border-slate-border dark:border-slate-800">
              <h3 className="font-bold text-slate-primary dark:text-white text-base">
                Manual Study Session Entry
              </h3>
              <button
                onClick={() => setIsManualModalOpen(false)}
                className="p-1 rounded-lg text-slate-secondary hover:text-slate-primary"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleManualSubmit} className="mt-4 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Subject
                </label>
                <select
                  value={manualSubjectId}
                  onChange={e => setManualSubjectId(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                >
                  {subjects.map(s => (
                    <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Duration (Minutes) *
                  </label>
                  <input
                    type="number"
                    min="5"
                    max="600"
                    required
                    value={manualDurationMins}
                    onChange={e => setManualDurationMins(Number(e.target.value))}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Session Date
                  </label>
                  <input
                    type="date"
                    required
                    value={manualDate}
                    onChange={e => setManualDate(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Session Type
                </label>
                <select
                  value={manualType}
                  onChange={e => setManualType(e.target.value as SessionType)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                >
                  <option value="deep_work">Deep Work</option>
                  <option value="pomodoro">Pomodoro Focus</option>
                  <option value="revision">Exam Revision</option>
                  <option value="reading">Literature Reading</option>
                  <option value="assignment">Assignment Execution</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Session Notes
                </label>
                <textarea
                  rows={2}
                  placeholder="Summary of topics completed..."
                  value={manualNotes}
                  onChange={e => setManualNotes(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary resize-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-border dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsManualModalOpen(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-secondary hover:text-slate-primary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 text-xs font-semibold rounded-academic bg-primary text-white hover:bg-primary-hover shadow-sm"
                >
                  Inscribe into History
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Custom Duration Modal */}
      {isCustomModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white dark:bg-slate-900 w-full max-w-xs rounded-academic-lg border border-slate-border dark:border-slate-800 shadow-modal p-5 animate-modalIn">
            <h3 className="font-bold text-slate-primary dark:text-white text-sm mb-3">
              Set Custom Focus Interval
            </h3>
            <form onSubmit={handleCustomSubmit} className="space-y-4">
              <div>
                <label className="block text-xs text-slate-secondary mb-1">
                  Duration (Minutes)
                </label>
                <input
                  type="number"
                  min="5"
                  max="180"
                  required
                  value={customInputMins}
                  onChange={e => setCustomInputMins(Number(e.target.value))}
                  className="w-full px-3 py-2 text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div className="flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsCustomModalOpen(false)}
                  className="px-3 py-1.5 text-xs text-slate-secondary hover:text-slate-primary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 text-xs font-semibold rounded-academic bg-primary text-white hover:bg-primary-hover"
                >
                  Set Interval
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

