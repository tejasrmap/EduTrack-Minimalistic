import React, { useState, useEffect, useRef } from 'react';
import { 
  Play, 
  Pause, 
  RotateCcw, 
  CheckCircle2 
} from 'lucide-react';
import { useAcademic } from '../context/AppContext';

export const TimerView: React.FC = () => {
  const { subjects, settings, addSession } = useAcademic();

  const [mode, setMode] = useState<'study' | 'break' | 'stopwatch'>('study');
  const [selectedSubjectId, setSelectedSubjectId] = useState<string>(subjects[0]?.id || '');

  // Timer states
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [secondsLeft, setSecondsLeft] = useState<number>(settings.focusSessionMinutes * 60);
  const [stopwatchSeconds, setStopwatchSeconds] = useState<number>(0);
  const [initialSeconds, setInitialSeconds] = useState<number>(settings.focusSessionMinutes * 60);

  // Reflection Modal
  const [isLogOpen, setIsLogOpen] = useState<boolean>(false);
  const [reflection, setReflection] = useState<string>('');
  const [focusScore, setFocusScore] = useState<number>(5);
  const [sessionType, setSessionType] = useState<'deep-work' | 'reading' | 'revision' | 'problem-set'>('reading');

  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const handleModeChange = (newMode: 'study' | 'break' | 'stopwatch') => {
    setIsRunning(false);
    setMode(newMode);

    if (newMode === 'study') {
      const dur = settings.focusSessionMinutes * 60;
      setSecondsLeft(dur);
      setInitialSeconds(dur);
    } else if (newMode === 'break') {
      const dur = settings.breakMinutes * 60;
      setSecondsLeft(dur);
      setInitialSeconds(dur);
    } else {
      setStopwatchSeconds(0);
    }
  };

  useEffect(() => {
    if (isRunning) {
      timerRef.current = setInterval(() => {
        if (mode === 'stopwatch') {
          setStopwatchSeconds(prev => prev + 1);
        } else {
          setSecondsLeft(prev => {
            if (prev <= 1) {
              clearInterval(timerRef.current!);
              setIsRunning(false);
              setIsLogOpen(true);
              return 0;
            }
            return prev - 1;
          });
        }
      }, 1000);
    } else {
      if (timerRef.current) clearInterval(timerRef.current);
    }

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [isRunning, mode]);

  const toggleRun = () => setIsRunning(!isRunning);

  const resetTimer = () => {
    setIsRunning(false);
    if (mode === 'stopwatch') {
      setStopwatchSeconds(0);
    } else {
      setSecondsLeft(initialSeconds);
    }
  };

  const handleFinishEarly = () => {
    setIsRunning(false);
    setIsLogOpen(true);
  };

  const handleSaveSession = (e: React.FormEvent) => {
    e.preventDefault();
    const curSubj = subjects.find(s => s.id === selectedSubjectId) || subjects[0];

    let durationMins = 0;
    if (mode === 'stopwatch') {
      durationMins = Math.max(1, Math.round(stopwatchSeconds / 60));
    } else {
      durationMins = Math.max(1, Math.round((initialSeconds - secondsLeft) / 60));
    }

    addSession({
      subjectId: curSubj.id,
      subjectTitle: curSubj.title,
      durationMinutes: durationMins,
      timestamp: new Date().toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' }) + ' · ' + new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      sessionType,
      reflection: reflection || 'Concluded reading and problem analysis block.',
      focusScore,
    });

    setIsLogOpen(false);
    setReflection('');
    resetTimer();
  };

  const displaySecs = mode === 'stopwatch' ? stopwatchSeconds : secondsLeft;
  const mins = Math.floor(displaySecs / 60);
  const secs = displaySecs % 60;
  const timeFormatted = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

  const currentSubject = subjects.find(s => s.id === selectedSubjectId) || subjects[0];
  const progressPercent = mode === 'stopwatch' 
    ? ((stopwatchSeconds % 3600) / 3600) * 100 
    : ((initialSeconds - secondsLeft) / initialSeconds) * 100;

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Top Banner */}
      <div className="paper-card rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="font-serif text-xl font-bold text-stone-900">
            Focus Chamber & Reading Room
          </h2>
          <p className="text-xs text-stone-500">
            Serene study environment with low cognitive friction
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="text-xs text-stone-500 font-serif italic">Subject:</span>
          <select
            value={selectedSubjectId}
            onChange={(e) => setSelectedSubjectId(e.target.value)}
            className="px-3 py-1.5 rounded-lg bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
          >
            {subjects.map(s => (
              <option key={s.id} value={s.id}>{s.code} · {s.title}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Main Focus Chamber */}
      <div className="paper-card rounded-3xl p-8 sm:p-12 flex flex-col items-center justify-center text-center">
        {/* Mode selector */}
        <div className="flex rounded-xl bg-stone-100 p-1 mb-8 gap-1">
          <button
            onClick={() => handleModeChange('study')}
            className={`px-4 py-1.5 rounded-lg text-xs font-medium transition-colors ${
              mode === 'study' ? 'bg-white text-stone-900 shadow-xs' : 'text-stone-500 hover:text-stone-800'
            }`}
          >
            Reading Block ({settings.focusSessionMinutes}m)
          </button>
          <button
            onClick={() => handleModeChange('break')}
            className={`px-4 py-1.5 rounded-lg text-xs font-medium transition-colors ${
              mode === 'break' ? 'bg-white text-stone-900 shadow-xs' : 'text-stone-500 hover:text-stone-800'
            }`}
          >
            Intermission ({settings.breakMinutes}m)
          </button>
          <button
            onClick={() => handleModeChange('stopwatch')}
            className={`px-4 py-1.5 rounded-lg text-xs font-medium transition-colors ${
              mode === 'stopwatch' ? 'bg-white text-stone-900 shadow-xs' : 'text-stone-500 hover:text-stone-800'
            }`}
          >
            Open Reading Clock
          </button>
        </div>

        {/* Circular Display */}
        <div className="relative w-64 h-64 sm:w-72 sm:h-72 flex items-center justify-center my-4">
          <svg className="w-full h-full -rotate-90" viewBox="0 0 240 240">
            <circle
              cx="120"
              cy="120"
              r="105"
              fill="none"
              stroke="#F4F4F0"
              strokeWidth="5"
            />
            <circle
              cx="120"
              cy="120"
              r="105"
              fill="none"
              stroke={currentSubject ? currentSubject.pastelColor : '#2A5C43'}
              strokeWidth="6"
              strokeDasharray={2 * Math.PI * 105}
              strokeDashoffset={2 * Math.PI * 105 * (1 - progressPercent / 100)}
              strokeLinecap="round"
              className="transition-all duration-300"
            />
          </svg>

          <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
            <span className="text-[11px] font-mono text-stone-500 mb-1">
              {currentSubject?.code || 'STUDY'}
            </span>
            <span className="font-serif text-5xl sm:text-6xl font-bold text-stone-900 tracking-tight">
              {timeFormatted}
            </span>
            <span className="text-xs text-stone-500 mt-2">
              {isRunning ? 'Session in progress' : 'Paused'}
            </span>
          </div>
        </div>

        {/* Controls */}
        <div className="flex items-center gap-3 mt-6">
          <button
            onClick={resetTimer}
            className="p-3 rounded-xl bg-stone-100 hover:bg-stone-200 text-stone-600 transition-colors"
            title="Reset"
          >
            <RotateCcw className="w-4 h-4" />
          </button>

          <button
            onClick={toggleRun}
            className="px-6 py-3 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-semibold flex items-center gap-2 transition-colors shadow-xs"
          >
            {isRunning ? (
              <>
                <Pause className="w-4 h-4 fill-white" />
                <span>Pause Session</span>
              </>
            ) : (
              <>
                <Play className="w-4 h-4 fill-white" />
                <span>Begin Session</span>
              </>
            )}
          </button>

          <button
            onClick={handleFinishEarly}
            className="p-3 rounded-xl bg-[#EBF3ED] hover:bg-[#d8e9dc] text-[#2A5C43] transition-colors"
            title="Conclude & Record Reflection"
          >
            <CheckCircle2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Reflection Log Modal */}
      {isLogOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-stone-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white w-full max-w-md rounded-2xl p-6 shadow-xl border border-stone-200 animate-modalIn">
            <h3 className="font-serif text-lg font-bold text-stone-900 mb-2">
              Record Study Reflection
            </h3>
            <p className="text-xs text-stone-500 mb-4">
              Log insights or monograph citations from this focus period.
            </p>

            <form onSubmit={handleSaveSession} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Session Classification
                </label>
                <select
                  value={sessionType}
                  onChange={(e) => setSessionType(e.target.value as 'deep-work' | 'reading' | 'revision' | 'problem-set')}
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                >
                  <option value="reading">Primary Reading / Monograph</option>
                  <option value="deep-work">Studio Writing / Design</option>
                  <option value="problem-set">Problem Set Analysis</option>
                  <option value="revision">Seminar Revision</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Cognitive Engagement Rating (1 - 5)
                </label>
                <div className="flex gap-2">
                  {[1, 2, 3, 4, 5].map((s) => (
                    <button
                      type="button"
                      key={s}
                      onClick={() => setFocusScore(s)}
                      className={`flex-1 py-1.5 rounded-lg text-xs font-mono font-semibold ${
                        focusScore >= s ? 'bg-[#2A5C43] text-white' : 'bg-stone-100 text-stone-600'
                      }`}
                    >
                      ★ {s}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Scholarly Reflection / Notes
                </label>
                <textarea
                  rows={3}
                  value={reflection}
                  onChange={(e) => setReflection(e.target.value)}
                  placeholder="Summarize key conceptual breakthroughs, primary thesis points, or arguments investigated..."
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-stone-200">
                <button
                  type="button"
                  onClick={() => setIsLogOpen(false)}
                  className="px-4 py-2 text-xs text-stone-500 hover:text-stone-800"
                >
                  Discard
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors"
                >
                  Commit to Archive
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
