import React, { createContext, useContext, useState, useEffect } from 'react';
import { 
  ViewType, 
  AcademicUser, 
  AcademicSubject, 
  AcademicTask, 
  AcademicSession, 
  AcademicMilestone, 
  AcademicAward, 
  MinimalSettings 
} from '../types';
import { 
  initialAcademicUser, 
  initialAcademicSubjects, 
  initialAcademicTasks, 
  initialAcademicSessions, 
  initialAcademicMilestones, 
  initialAcademicAwards, 
  initialAcademicSettings 
} from './initialData';

const STORAGE_KEY = 'edutrack_minimal_academic_state_v1';

interface AcademicContextType {
  currentView: ViewType;
  setCurrentView: (view: ViewType) => void;
  isAuthenticated: boolean;
  user: AcademicUser;
  subjects: AcademicSubject[];
  tasks: AcademicTask[];
  sessions: AcademicSession[];
  milestones: AcademicMilestone[];
  awards: AcademicAward[];
  settings: MinimalSettings;

  // Authentication
  loginAsDemo: (scholar?: 'clara' | 'henry' | 'beatrice') => void;
  signup: (name: string, email: string, degree: string) => void;
  logout: () => void;

  // User Profile
  updateUser: (updates: Partial<AcademicUser>) => void;

  // Subjects CRUD
  addSubject: (subject: Omit<AcademicSubject, 'id'>) => void;
  updateSubject: (id: string, updates: Partial<AcademicSubject>) => void;
  deleteSubject: (id: string) => void;

  // Tasks CRUD
  addTask: (task: Omit<AcademicTask, 'id'>) => void;
  updateTask: (id: string, updates: Partial<AcademicTask>) => void;
  toggleTaskStatus: (id: string) => void;
  deleteTask: (id: string) => void;

  // Sessions
  addSession: (session: Omit<AcademicSession, 'id'>) => void;
  deleteSession: (id: string) => void;

  // Milestones CRUD
  addMilestone: (milestone: Omit<AcademicMilestone, 'id'>) => void;
  updateMilestone: (id: string, updates: Partial<AcademicMilestone>) => void;
  deleteMilestone: (id: string) => void;

  // Settings
  updateSettings: (updates: Partial<MinimalSettings>) => void;
  resetDemoData: () => void;
}

const AcademicContext = createContext<AcademicContextType | undefined>(undefined);

export const AcademicProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentView, setCurrentView] = useState<ViewType>('dashboard');
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(true);

  const [user, setUser] = useState<AcademicUser>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_user`);
    return saved ? JSON.parse(saved) : initialAcademicUser;
  });

  const [subjects, setSubjects] = useState<AcademicSubject[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_subjects`);
    return saved ? JSON.parse(saved) : initialAcademicSubjects;
  });

  const [tasks, setTasks] = useState<AcademicTask[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_tasks`);
    return saved ? JSON.parse(saved) : initialAcademicTasks;
  });

  const [sessions, setSessions] = useState<AcademicSession[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_sessions`);
    return saved ? JSON.parse(saved) : initialAcademicSessions;
  });

  const [milestones, setMilestones] = useState<AcademicMilestone[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_milestones`);
    return saved ? JSON.parse(saved) : initialAcademicMilestones;
  });

  const [awards, setAwards] = useState<AcademicAward[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_awards`);
    return saved ? JSON.parse(saved) : initialAcademicAwards;
  });

  const [settings, setSettings] = useState<MinimalSettings>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_settings`);
    return saved ? JSON.parse(saved) : initialAcademicSettings;
  });

  // LocalStorage syncing
  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_user`, JSON.stringify(user));
  }, [user]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_subjects`, JSON.stringify(subjects));
  }, [subjects]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_tasks`, JSON.stringify(tasks));
  }, [tasks]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_sessions`, JSON.stringify(sessions));
  }, [sessions]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_milestones`, JSON.stringify(milestones));
  }, [milestones]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_awards`, JSON.stringify(awards));
  }, [awards]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_settings`, JSON.stringify(settings));
  }, [settings]);

  // Auth
  const loginAsDemo = (scholar: 'clara' | 'henry' | 'beatrice' = 'clara') => {
    if (scholar === 'henry') {
      setUser({
        ...initialAcademicUser,
        name: 'Henry Cavendish',
        email: 'henry.cavendish@ox.ac.uk',
        degree: 'MSc Pure Mathematics & Cryptography',
        institution: 'University of Oxford · Mathematical Institute',
        currentGpa: 3.96,
      });
    } else if (scholar === 'beatrice') {
      setUser({
        ...initialAcademicUser,
        name: 'Beatrice Webb',
        email: 'b.webb@lse.ac.uk',
        degree: 'MSc Economic History & Political Philosophy',
        institution: 'London School of Economics',
        currentGpa: 3.88,
      });
    } else {
      setUser(initialAcademicUser);
    }
    setIsAuthenticated(true);
    setCurrentView('dashboard');
  };

  const signup = (name: string, email: string, degree: string) => {
    setUser({
      ...initialAcademicUser,
      id: `usr_acad_${Date.now()}`,
      name: name || 'Academic Scholar',
      email: email || 'scholar@university.ac.uk',
      degree: degree || 'Graduate Studies',
    });
    setIsAuthenticated(true);
    setCurrentView('dashboard');
  };

  const logout = () => {
    setIsAuthenticated(false);
  };

  const updateUser = (updates: Partial<AcademicUser>) => {
    setUser(prev => ({ ...prev, ...updates }));
  };

  // Subjects CRUD
  const addSubject = (subj: Omit<AcademicSubject, 'id'>) => {
    const newSubj: AcademicSubject = {
      ...subj,
      id: `subj-${Date.now()}`,
    };
    setSubjects(prev => [newSubj, ...prev]);
  };

  const updateSubject = (id: string, updates: Partial<AcademicSubject>) => {
    setSubjects(prev => prev.map(s => s.id === id ? { ...s, ...updates } : s));
  };

  const deleteSubject = (id: string) => {
    setSubjects(prev => prev.filter(s => s.id !== id));
  };

  // Tasks CRUD
  const addTask = (t: Omit<AcademicTask, 'id'>) => {
    const newTask: AcademicTask = {
      ...t,
      id: `atask-${Date.now()}`,
    };
    setTasks(prev => [newTask, ...prev]);
  };

  const updateTask = (id: string, updates: Partial<AcademicTask>) => {
    setTasks(prev => prev.map(t => t.id === id ? { ...t, ...updates } : t));
  };

  const toggleTaskStatus = (id: string) => {
    setTasks(prev => prev.map(t => {
      if (t.id === id) {
        const nextStatus = t.status === 'completed' ? 'pending' : 'completed';
        return {
          ...t,
          status: nextStatus,
          completedAt: nextStatus === 'completed' ? new Date().toISOString().split('T')[0] : undefined,
        };
      }
      return t;
    }));
  };

  const deleteTask = (id: string) => {
    setTasks(prev => prev.filter(t => t.id !== id));
  };

  // Sessions CRUD
  const addSession = (sess: Omit<AcademicSession, 'id'>) => {
    const newSession: AcademicSession = {
      ...sess,
      id: `asess-${Date.now()}`,
    };
    setSessions(prev => [newSession, ...prev]);
  };

  const deleteSession = (id: string) => {
    setSessions(prev => prev.filter(s => s.id !== id));
  };

  // Milestones CRUD
  const addMilestone = (m: Omit<AcademicMilestone, 'id'>) => {
    const newMilestone: AcademicMilestone = {
      ...m,
      id: `milestone-${Date.now()}`,
    };
    setMilestones(prev => [newMilestone, ...prev]);
  };

  const updateMilestone = (id: string, updates: Partial<AcademicMilestone>) => {
    setMilestones(prev => prev.map(m => m.id === id ? { ...m, ...updates } : m));
  };

  const deleteMilestone = (id: string) => {
    setMilestones(prev => prev.filter(m => m.id !== id));
  };

  // Settings
  const updateSettings = (updates: Partial<MinimalSettings>) => {
    setSettings((prev: MinimalSettings) => ({ ...prev, ...updates }));
  };

  const resetDemoData = () => {
    setUser(initialAcademicUser);
    setSubjects(initialAcademicSubjects);
    setTasks(initialAcademicTasks);
    setSessions(initialAcademicSessions);
    setMilestones(initialAcademicMilestones);
    setAwards(initialAcademicAwards);
    setSettings(initialAcademicSettings);
  };

  return (
    <AcademicContext.Provider
      value={{
        currentView,
        setCurrentView,
        isAuthenticated,
        user,
        subjects,
        tasks,
        sessions,
        milestones,
        awards,
        settings,
        loginAsDemo,
        signup,
        logout,
        updateUser,
        addSubject,
        updateSubject,
        deleteSubject,
        addTask,
        updateTask,
        toggleTaskStatus,
        deleteTask,
        addSession,
        deleteSession,
        addMilestone,
        updateMilestone,
        deleteMilestone,
        updateSettings,
        resetDemoData,
      }}
    >
      {children}
    </AcademicContext.Provider>
  );
};

export const useAcademic = () => {
  const context = useContext(AcademicContext);
  if (!context) {
    throw new Error('useAcademic must be used within an AcademicProvider');
  }
  return context;
};

