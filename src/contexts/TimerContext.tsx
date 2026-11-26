import React, { createContext, useContext, useState, useEffect, useRef, useCallback } from 'react';
import { PersistedTimerState } from '../types';
import { storageService } from '../services/storage';
import { useData } from './DataContext';
import { useToast } from './ToastContext';

export type TimerMode = 'focus' | 'short_break' | 'long_break' | 'custom';

interface TimerContextType {
  isRunning: boolean;
  mode: TimerMode;
  subjectId: string;
  sessionDescription: string;
  totalDurationSeconds: number;
  remainingSeconds: number;
  completedPomodoroCount: number;
  progressPercent: number;

  startTimer: () => void;
  pauseTimer: () => void;
  resumeTimer: () => void;
  resetTimer: () => void;
  switchMode: (newMode: TimerMode, customDurationMinutes?: number) => void;
  setSubjectId: (id: string) => void;
  setSessionDescription: (desc: string) => void;
  setCustomDuration: (minutes: number) => void;
  logCompletedSession: (notes?: string) => void;
}

const TimerContext = createContext<TimerContextType | undefined>(undefined);

// Web Audio synthesizer for tranquil academic focus bell
const playAcademicChime = () => {
  try {
    const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
    if (!AudioCtx) return;
    const ctx = new AudioCtx();
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(587.33, ctx.currentTime); // D5
    osc.frequency.exponentialRampToValueAtTime(880, ctx.currentTime + 0.3); // A5

    gain.gain.setValueAtTime(0.2, ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 1.8);

    osc.connect(gain);
    gain.connect(ctx.destination);

    osc.start();
    osc.stop(ctx.currentTime + 1.8);
  } catch (e) {
    console.warn('Audio feedback not available:', e);
  }
};

export const TimerProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { settings, subjects, addStudySession, addNotification } = useData();
  const { toast } = useToast();

  const defaultFocusSecs = settings.pomodoroFocusMinutes * 60;
  const defaultSubjectId = subjects[0]?.id || '';

  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [mode, setMode] = useState<TimerMode>('focus');
  const [subjectId, setSubjectId] = useState<string>(defaultSubjectId);
  const [sessionDescription, setSessionDescription] = useState<string>('');
  const [totalDurationSeconds, setTotalDurationSeconds] = useState<number>(defaultFocusSecs);
  const [remainingSeconds, setRemainingSeconds] = useState<number>(defaultFocusSecs);
  const [accumulatedSeconds, setAccumulatedSeconds] = useState<number>(0);
  const [startTimestamp, setStartTimestamp] = useState<number | null>(null);
  const [completedPomodoroCount, setCompletedPomodoroCount] = useState<number>(0);

  // Restore persisted timer state on mount
  useEffect(() => {
    const saved = storageService.getTimerState();
    if (saved) {
      setMode(saved.mode);
      setSubjectId(saved.subjectId || defaultSubjectId);
      setSessionDescription(saved.sessionDescription || '');
      setTotalDurationSeconds(saved.totalDurationSeconds);
      setCompletedPomodoroCount(saved.completedPomodoroCount);

      if (saved.isRunning && saved.startTimestamp) {
        const elapsedSinceStart = Math.floor((Date.now() - saved.startTimestamp) / 1000);
        const actualRemaining = Math.max(0, saved.totalDurationSeconds - (saved.accumulatedSeconds + elapsedSinceStart));

        setRemainingSeconds(actualRemaining);
        setAccumulatedSeconds(saved.accumulatedSeconds + elapsedSinceStart);
        setStartTimestamp(Date.now());
        setIsRunning(actualRemaining > 0);
      } else {
        setRemainingSeconds(saved.remainingSeconds);
        setAccumulatedSeconds(saved.accumulatedSeconds);
        setStartTimestamp(null);
        setIsRunning(false);
      }
    }
  }, []);

  // Save timer state to localStorage whenever values change
  useEffect(() => {
    const state: PersistedTimerState = {
      isRunning,
      mode,
      subjectId,
      sessionDescription,
      totalDurationSeconds,
      remainingSeconds,
      startTimestamp,
      accumulatedSeconds,
      completedPomodoroCount,
    };
    storageService.saveTimerState(state);
  }, [isRunning, mode, subjectId, sessionDescription, totalDurationSeconds, remainingSeconds, startTimestamp, accumulatedSeconds, completedPomodoroCount]);

  // Log session callback
  const handleSessionComplete = useCallback(() => {
    setIsRunning(false);
    setStartTimestamp(null);
    playAcademicChime();

    if (mode === 'focus' || mode === 'custom') {
      const durationMins = Math.max(1, Math.round(totalDurationSeconds / 60));
      const subjName = subjects.find(s => s.id === subjectId)?.name || 'General Academic Focus';

      addStudySession({
        subjectId,
        durationMinutes: durationMins,
        sessionType: mode === 'focus' ? 'pomodoro' : 'deep_work',
        notes: sessionDescription || `Completed ${mode === 'focus' ? 'Pomodoro' : 'Study'} block for ${subjName}`,
        isManual: false,
        startTime: new Date(Date.now() - totalDurationSeconds * 1000).toISOString(),
        endTime: new Date().toISOString(),
      });

      const nextPomodoroCount = completedPomodoroCount + 1;
      setCompletedPomodoroCount(nextPomodoroCount);

      addNotification({
        title: 'Study Session Concluded ⏱️',
        message: `Great job! You logged ${durationMins} minutes of focus on ${subjName}.`,
        type: 'reminder',
        linkTo: '/history',
      });

      toast.success(`Completed ${durationMins}m focus session on ${subjName}!`);

      // Switch to break
      if (nextPomodoroCount % settings.longBreakInterval === 0) {
        const breakSecs = settings.pomodoroLongBreakMinutes * 60;
        setMode('long_break');
        setTotalDurationSeconds(breakSecs);
        setRemainingSeconds(breakSecs);
        setAccumulatedSeconds(0);
      } else {
        const breakSecs = settings.pomodoroShortBreakMinutes * 60;
        setMode('short_break');
        setTotalDurationSeconds(breakSecs);
        setRemainingSeconds(breakSecs);
        setAccumulatedSeconds(0);
      }
    } else {
      // Break finished
      toast.info('Break time ended. Ready for the next focus session!');
      const focusSecs = settings.pomodoroFocusMinutes * 60;
      setMode('focus');
      setTotalDurationSeconds(focusSecs);
      setRemainingSeconds(focusSecs);
      setAccumulatedSeconds(0);
    }
  }, [mode, totalDurationSeconds, subjects, subjectId, sessionDescription, completedPomodoroCount, settings, addStudySession, addNotification, toast]);

  // Timestamp-based accurate interval timer
  useEffect(() => {
    if (!isRunning || startTimestamp === null) return;

    const interval = setInterval(() => {
      const elapsedSinceStart = Math.floor((Date.now() - startTimestamp) / 1000);
      const currentRemaining = Math.max(0, totalDurationSeconds - (accumulatedSeconds + elapsedSinceStart));

      setRemainingSeconds(currentRemaining);

      if (currentRemaining <= 0) {
        clearInterval(interval);
        handleSessionComplete();
      }
    }, 200);

    return () => clearInterval(interval);
  }, [isRunning, startTimestamp, totalDurationSeconds, accumulatedSeconds, handleSessionComplete]);

  // Actions
  const startTimer = () => {
    if (remainingSeconds <= 0) {
      setRemainingSeconds(totalDurationSeconds);
      setAccumulatedSeconds(0);
    }
    setStartTimestamp(Date.now());
    setIsRunning(true);
  };

  const pauseTimer = () => {
    if (!isRunning || startTimestamp === null) return;
    const elapsedSinceStart = Math.floor((Date.now() - startTimestamp) / 1000);
    setAccumulatedSeconds(prev => prev + elapsedSinceStart);
    setStartTimestamp(null);
    setIsRunning(false);
  };

  const resumeTimer = () => {
    setStartTimestamp(Date.now());
    setIsRunning(true);
  };

  const resetTimer = () => {
    setIsRunning(false);
    setStartTimestamp(null);
    setAccumulatedSeconds(0);
    setRemainingSeconds(totalDurationSeconds);
  };

  const switchMode = (newMode: TimerMode, customDurationMinutes?: number) => {
    setIsRunning(false);
    setStartTimestamp(null);
    setAccumulatedSeconds(0);
    setMode(newMode);

    let secs = defaultFocusSecs;
    if (newMode === 'focus') {
      secs = settings.pomodoroFocusMinutes * 60;
    } else if (newMode === 'short_break') {
      secs = settings.pomodoroShortBreakMinutes * 60;
    } else if (newMode === 'long_break') {
      secs = settings.pomodoroLongBreakMinutes * 60;
    } else if (newMode === 'custom') {
      secs = (customDurationMinutes || 45) * 60;
    }

    setTotalDurationSeconds(secs);
    setRemainingSeconds(secs);
  };

  const setCustomDuration = (minutes: number) => {
    const secs = Math.max(1, minutes) * 60;
    setTotalDurationSeconds(secs);
    setRemainingSeconds(secs);
    setAccumulatedSeconds(0);
    setStartTimestamp(null);
    setIsRunning(false);
  };

  const logCompletedSession = (notes?: string) => {
    const elapsedSeconds = totalDurationSeconds - remainingSeconds;
    const durationMins = Math.max(1, Math.round(elapsedSeconds / 60));
    const subjName = subjects.find(s => s.id === subjectId)?.name || 'General Study';

    addStudySession({
      subjectId,
      durationMinutes: durationMins,
      sessionType: mode === 'focus' ? 'pomodoro' : 'deep_work',
      notes: notes || sessionDescription || `Study session for ${subjName}`,
      isManual: false,
      startTime: new Date(Date.now() - elapsedSeconds * 1000).toISOString(),
      endTime: new Date().toISOString(),
    });

    toast.success(`Logged ${durationMins} minutes to study history.`);
    resetTimer();
  };

  const progressPercent = totalDurationSeconds > 0
    ? Math.min(100, Math.round(((totalDurationSeconds - remainingSeconds) / totalDurationSeconds) * 100))
    : 0;

  return (
    <TimerContext.Provider
      value={{
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
        logCompletedSession,
      }}
    >
      {children}
    </TimerContext.Provider>
  );
};

export const useTimer = (): TimerContextType => {
  const context = useContext(TimerContext);
  if (!context) {
    throw new Error('useTimer must be used within a TimerProvider');
  }
  return context;
};

