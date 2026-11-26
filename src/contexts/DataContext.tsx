import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  Subject,
  Task,
  Examination,
  Note,
  Goal,
  StudySession,
  ScheduleEvent,
  NotificationItem,
  UserSettings
} from '../types';
import { storageService } from '../services/storage';

interface DataContextType {
  // State
  subjects: Subject[];
  tasks: Task[];
  examinations: Examination[];
  notes: Note[];
  goals: Goal[];
  studySessions: StudySession[];
  scheduleEvents: ScheduleEvent[];
  notifications: NotificationItem[];
  settings: UserSettings;

  // Subjects CRUD
  addSubject: (data: Omit<Subject, 'id' | 'currentWeeklyMinutes' | 'totalStudyMinutes' | 'completedAssignmentsCount' | 'createdAt'>) => Subject;
  updateSubject: (id: string, updates: Partial<Subject>) => void;
  deleteSubject: (id: string) => void;
  getSubjectById: (id: string) => Subject | undefined;

  // Tasks CRUD
  addTask: (data: Omit<Task, 'id' | 'createdAt'>) => Task;
  updateTask: (id: string, updates: Partial<Task>) => void;
  deleteTask: (id: string) => void;
  toggleTaskStatus: (id: string) => void;

  // Examinations CRUD
  addExamination: (data: Omit<Examination, 'id' | 'linkedTaskIds' | 'createdAt'>) => Examination;
  updateExamination: (id: string, updates: Partial<Examination>) => void;
  deleteExamination: (id: string) => void;
  createRevisionTaskForExam: (examId: string, title: string, dueDate: string) => void;

  // Notes CRUD
  addNote: (data: Omit<Note, 'id' | 'createdAt' | 'updatedAt'>) => Note;
  updateNote: (id: string, updates: Partial<Note>) => void;
  deleteNote: (id: string) => void;
  togglePinNote: (id: string) => void;
  toggleFavouriteNote: (id: string) => void;
  toggleArchiveNote: (id: string) => void;

  // Goals CRUD
  addGoal: (data: Omit<Goal, 'id' | 'createdAt'>) => Goal;
  updateGoal: (id: string, updates: Partial<Goal>) => void;
  deleteGoal: (id: string) => void;
  incrementGoalProgress: (id: string, amount?: number) => void;
  toggleGoalComplete: (id: string) => void;

  // Study Sessions CRUD
  addStudySession: (session: Omit<StudySession, 'id' | 'createdAt'>) => StudySession;
  updateStudySession: (id: string, updates: Partial<StudySession>) => void;
  deleteStudySession: (id: string) => void;

  // Schedule Events CRUD
  addScheduleEvent: (data: Omit<ScheduleEvent, 'id' | 'createdAt'>) => ScheduleEvent;
  updateScheduleEvent: (id: string, updates: Partial<ScheduleEvent>) => void;
  deleteScheduleEvent: (id: string) => void;
  toggleScheduleEventComplete: (id: string) => void;

  // Notifications
  addNotification: (notif: Omit<NotificationItem, 'id' | 'timestamp' | 'read'>) => void;
  markNotificationAsRead: (id: string) => void;
  markAllNotificationsAsRead: () => void;
  deleteNotification: (id: string) => void;

  // Settings & Storage Maintenance
  updateSettings: (updates: Partial<UserSettings>) => void;
  toggleTheme: () => void;
  exportJSON: () => string;
  exportCSV: () => string;
  importBackup: (json: string) => { success: boolean; message: string };
  resetDemoData: () => void;
  clearAllData: () => void;
}

const DataContext = createContext<DataContextType | undefined>(undefined);

export const DataProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [subjects, setSubjects] = useState<Subject[]>(() => storageService.getSubjects());
  const [tasks, setTasks] = useState<Task[]>(() => storageService.getTasks());
  const [examinations, setExaminations] = useState<Examination[]>(() => storageService.getExaminations());
  const [notes, setNotes] = useState<Note[]>(() => storageService.getNotes());
  const [goals, setGoals] = useState<Goal[]>(() => storageService.getGoals());
  const [studySessions, setStudySessions] = useState<StudySession[]>(() => storageService.getStudySessions());
  const [scheduleEvents, setScheduleEvents] = useState<ScheduleEvent[]>(() => storageService.getScheduleEvents());
  const [notifications, setNotifications] = useState<NotificationItem[]>(() => storageService.getNotifications());
  const [settings, setSettings] = useState<UserSettings>(() => storageService.getSettings());

  // Auto-sync state to localStorage
  useEffect(() => { storageService.saveSubjects(subjects); }, [subjects]);
  useEffect(() => { storageService.saveTasks(tasks); }, [tasks]);
  useEffect(() => { storageService.saveExaminations(examinations); }, [examinations]);
  useEffect(() => { storageService.saveNotes(notes); }, [notes]);
  useEffect(() => { storageService.saveGoals(goals); }, [goals]);
  useEffect(() => { storageService.saveStudySessions(studySessions); }, [studySessions]);
  useEffect(() => { storageService.saveScheduleEvents(scheduleEvents); }, [scheduleEvents]);
  useEffect(() => { storageService.saveNotifications(notifications); }, [notifications]);
  useEffect(() => {
    storageService.saveSettings(settings);
    if (settings.theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [settings]);

  // --- Subjects CRUD ---
  const addSubject = (data: Omit<Subject, 'id' | 'currentWeeklyMinutes' | 'totalStudyMinutes' | 'completedAssignmentsCount' | 'createdAt'>): Subject => {
    const newSubj: Subject = {
      ...data,
      id: `subj-${Date.now()}`,
      currentWeeklyMinutes: 0,
      totalStudyMinutes: 0,
      completedAssignmentsCount: 0,
      createdAt: new Date().toISOString(),
    };
    setSubjects(prev => [newSubj, ...prev]);
    return newSubj;
  };

  const updateSubject = (id: string, updates: Partial<Subject>) => {
    setSubjects(prev => prev.map(s => s.id === id ? { ...s, ...updates } : s));
  };

  const deleteSubject = (id: string) => {
    setSubjects(prev => prev.filter(s => s.id !== id));
    // Optional: unlink or keep tasks
  };

  const getSubjectById = (id: string) => subjects.find(s => s.id === id);

  // --- Tasks CRUD ---
  const addTask = (data: Omit<Task, 'id' | 'createdAt'>): Task => {
    const newTask: Task = {
      ...data,
      id: `task-${Date.now()}`,
      createdAt: new Date().toISOString(),
    };
    setTasks(prev => [newTask, ...prev]);
    return newTask;
  };

  const updateTask = (id: string, updates: Partial<Task>) => {
    setTasks(prev => prev.map(t => t.id === id ? { ...t, ...updates } : t));
  };

  const deleteTask = (id: string) => {
    setTasks(prev => prev.filter(t => t.id !== id));
  };

  const toggleTaskStatus = (id: string) => {
    setTasks(prev => prev.map(t => {
      if (t.id === id) {
        const nextStatus = t.status === 'completed' ? 'in_progress' : 'completed';
        const completedAt = nextStatus === 'completed' ? new Date().toISOString() : undefined;

        // Update subject completedAssignmentsCount if completed
        if (nextStatus === 'completed' && t.subjectId) {
          setSubjects(sPrev => sPrev.map(s =>
            s.id === t.subjectId
              ? { ...s, completedAssignmentsCount: s.completedAssignmentsCount + 1 }
              : s
          ));
        }

        return { ...t, status: nextStatus, completedAt };
      }
      return t;
    }));
  };

  // --- Examinations CRUD ---
  const addExamination = (data: Omit<Examination, 'id' | 'linkedTaskIds' | 'createdAt'>): Examination => {
    const newExam: Examination = {
      ...data,
      id: `exam-${Date.now()}`,
      linkedTaskIds: [],
      createdAt: new Date().toISOString(),
    };
    setExaminations(prev => [newExam, ...prev]);
    return newExam;
  };

  const updateExamination = (id: string, updates: Partial<Examination>) => {
    setExaminations(prev => prev.map(e => e.id === id ? { ...e, ...updates } : e));
  };

  const deleteExamination = (id: string) => {
    setExaminations(prev => prev.filter(e => e.id !== id));
  };

  const createRevisionTaskForExam = (examId: string, title: string, dueDate: string) => {
    const exam = examinations.find(e => e.id === examId);
    if (!exam) return;

    const newTask = addTask({
      title,
      description: `Targeted revision block for ${exam.title}`,
      subjectId: exam.subjectId,
      category: 'revision',
      priority: 'high',
      status: 'not_started',
      dueDate,
      dueTime: '20:00',
      examId,
      estimatedMinutes: 90,
    });

    updateExamination(examId, {
      linkedTaskIds: [...exam.linkedTaskIds, newTask.id],
      preparationStatus: 'in_progress',
    });
  };

  // --- Notes CRUD ---
  const addNote = (data: Omit<Note, 'id' | 'createdAt' | 'updatedAt'>): Note => {
    const newNote: Note = {
      ...data,
      id: `note-${Date.now()}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    setNotes(prev => [newNote, ...prev]);
    return newNote;
  };

  const updateNote = (id: string, updates: Partial<Note>) => {
    setNotes(prev => prev.map(n => n.id === id ? { ...n, ...updates, updatedAt: new Date().toISOString() } : n));
  };

  const deleteNote = (id: string) => {
    setNotes(prev => prev.filter(n => n.id !== id));
  };

  const togglePinNote = (id: string) => {
    setNotes(prev => prev.map(n => n.id === id ? { ...n, isPinned: !n.isPinned } : n));
  };

  const toggleFavouriteNote = (id: string) => {
    setNotes(prev => prev.map(n => n.id === id ? { ...n, isFavourite: !n.isFavourite } : n));
  };

  const toggleArchiveNote = (id: string) => {
    setNotes(prev => prev.map(n => n.id === id ? { ...n, isArchived: !n.isArchived } : n));
  };

  // --- Goals CRUD ---
  const addGoal = (data: Omit<Goal, 'id' | 'createdAt'>): Goal => {
    const newGoal: Goal = {
      ...data,
      id: `goal-${Date.now()}`,
      createdAt: new Date().toISOString(),
    };
    setGoals(prev => [newGoal, ...prev]);
    return newGoal;
  };

  const updateGoal = (id: string, updates: Partial<Goal>) => {
    setGoals(prev => prev.map(g => g.id === id ? { ...g, ...updates } : g));
  };

  const deleteGoal = (id: string) => {
    setGoals(prev => prev.filter(g => g.id !== id));
  };

  const incrementGoalProgress = (id: string, amount: number = 1) => {
    setGoals(prev => prev.map(g => {
      if (g.id === id) {
        const nextVal = Math.min(g.targetValue, g.currentValue + amount);
        const isCompleted = nextVal >= g.targetValue;
        return {
          ...g,
          currentValue: nextVal,
          isCompleted,
          completedAt: isCompleted ? new Date().toISOString() : undefined,
        };
      }
      return g;
    }));
  };

  const toggleGoalComplete = (id: string) => {
    setGoals(prev => prev.map(g => {
      if (g.id === id) {
        const isCompleted = !g.isCompleted;
        return {
          ...g,
          isCompleted,
          completedAt: isCompleted ? new Date().toISOString() : undefined,
          currentValue: isCompleted ? g.targetValue : g.currentValue,
        };
      }
      return g;
    }));
  };

  // --- Study Sessions CRUD ---
  const addStudySession = (data: Omit<StudySession, 'id' | 'createdAt'>): StudySession => {
    const newSession: StudySession = {
      ...data,
      id: `sess-${Date.now()}`,
      createdAt: new Date().toISOString(),
    };
    setStudySessions(prev => [newSession, ...prev]);

    // Recalculate subject total study time and current weekly minutes
    if (newSession.subjectId) {
      setSubjects(sPrev => sPrev.map(s => {
        if (s.id === newSession.subjectId) {
          return {
            ...s,
            totalStudyMinutes: s.totalStudyMinutes + newSession.durationMinutes,
            currentWeeklyMinutes: s.currentWeeklyMinutes + newSession.durationMinutes,
          };
        }
        return s;
      }));
    }

    return newSession;
  };

  const updateStudySession = (id: string, updates: Partial<StudySession>) => {
    setStudySessions(prev => prev.map(s => s.id === id ? { ...s, ...updates } : s));
  };

  const deleteStudySession = (id: string) => {
    const toDelete = studySessions.find(s => s.id === id);
    if (toDelete && toDelete.subjectId) {
      setSubjects(sPrev => sPrev.map(s => {
        if (s.id === toDelete.subjectId) {
          return {
            ...s,
            totalStudyMinutes: Math.max(0, s.totalStudyMinutes - toDelete.durationMinutes),
            currentWeeklyMinutes: Math.max(0, s.currentWeeklyMinutes - toDelete.durationMinutes),
          };
        }
        return s;
      }));
    }
    setStudySessions(prev => prev.filter(s => s.id !== id));
  };

  // --- Schedule Events CRUD ---
  const addScheduleEvent = (data: Omit<ScheduleEvent, 'id' | 'createdAt'>): ScheduleEvent => {
    const newEvent: ScheduleEvent = {
      ...data,
      id: `sched-${Date.now()}`,
      createdAt: new Date().toISOString(),
    };
    setScheduleEvents(prev => [...prev, newEvent].sort((a, b) => a.startTime.localeCompare(b.startTime)));
    return newEvent;
  };

  const updateScheduleEvent = (id: string, updates: Partial<ScheduleEvent>) => {
    setScheduleEvents(prev => prev.map(e => e.id === id ? { ...e, ...updates } : e));
  };

  const deleteScheduleEvent = (id: string) => {
    setScheduleEvents(prev => prev.filter(e => e.id !== id));
  };

  const toggleScheduleEventComplete = (id: string) => {
    setScheduleEvents(prev => prev.map(e => e.id === id ? { ...e, isCompleted: !e.isCompleted } : e));
  };

  // --- Notifications ---
  const addNotification = (notif: Omit<NotificationItem, 'id' | 'timestamp' | 'read'>) => {
    const newNotif: NotificationItem = {
      ...notif,
      id: `notif-${Date.now()}`,
      timestamp: new Date().toISOString(),
      read: false,
    };
    setNotifications(prev => [newNotif, ...prev]);
  };

  const markNotificationAsRead = (id: string) => {
    setNotifications(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
  };

  const markAllNotificationsAsRead = () => {
    setNotifications(prev => prev.map(n => ({ ...n, read: true })));
  };

  const deleteNotification = (id: string) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  };

  // --- Settings ---
  const updateSettings = (updates: Partial<UserSettings>) => {
    setSettings(prev => ({ ...prev, ...updates }));
  };

  const toggleTheme = () => {
    setSettings(prev => ({ ...prev, theme: prev.theme === 'light' ? 'dark' : 'light' }));
  };

  // --- Data maintenance ---
  const exportJSON = () => storageService.exportAllDataJSON();
  const exportCSV = () => storageService.exportStudyHistoryCSV();

  const importBackup = (jsonString: string) => {
    const res = storageService.importBackupJSON(jsonString);
    if (res.success) {
      setSubjects(storageService.getSubjects());
      setTasks(storageService.getTasks());
      setExaminations(storageService.getExaminations());
      setNotes(storageService.getNotes());
      setGoals(storageService.getGoals());
      setStudySessions(storageService.getStudySessions());
      setScheduleEvents(storageService.getScheduleEvents());
      setNotifications(storageService.getNotifications());
      setSettings(storageService.getSettings());
    }
    return res;
  };

  const resetDemoData = () => {
    storageService.resetToDemoData();
    setSubjects(storageService.getSubjects());
    setTasks(storageService.getTasks());
    setExaminations(storageService.getExaminations());
    setNotes(storageService.getNotes());
    setGoals(storageService.getGoals());
    setStudySessions(storageService.getStudySessions());
    setScheduleEvents(storageService.getScheduleEvents());
    setNotifications(storageService.getNotifications());
    setSettings(storageService.getSettings());
  };

  const clearAllData = () => {
    storageService.clearAllData();
    setSubjects([]);
    setTasks([]);
    setExaminations([]);
    setNotes([]);
    setGoals([]);
    setStudySessions([]);
    setScheduleEvents([]);
    setNotifications([]);
  };

  return (
    <DataContext.Provider
      value={{
        subjects,
        tasks,
        examinations,
        notes,
        goals,
        studySessions,
        scheduleEvents,
        notifications,
        settings,

        addSubject,
        updateSubject,
        deleteSubject,
        getSubjectById,

        addTask,
        updateTask,
        deleteTask,
        toggleTaskStatus,

        addExamination,
        updateExamination,
        deleteExamination,
        createRevisionTaskForExam,

        addNote,
        updateNote,
        deleteNote,
        togglePinNote,
        toggleFavouriteNote,
        toggleArchiveNote,

        addGoal,
        updateGoal,
        deleteGoal,
        incrementGoalProgress,
        toggleGoalComplete,

        addStudySession,
        updateStudySession,
        deleteStudySession,

        addScheduleEvent,
        updateScheduleEvent,
        deleteScheduleEvent,
        toggleScheduleEventComplete,

        addNotification,
        markNotificationAsRead,
        markAllNotificationsAsRead,
        deleteNotification,

        updateSettings,
        toggleTheme,
        exportJSON,
        exportCSV,
        importBackup,
        resetDemoData,
        clearAllData,
      }}
    >
      {children}
    </DataContext.Provider>
  );
};

export const useData = (): DataContextType => {
  const context = useContext(DataContext);
  if (!context) {
    throw new Error('useData must be used within a DataProvider');
  }
  return context;
};

