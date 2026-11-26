import React, { createContext, useContext, useState, useEffect } from 'react';
import { UserProfile } from '../types';
import { storageService } from '../services/storage';

interface AuthContextType {
  user: UserProfile;
  isAuthenticated: boolean;
  login: (email: string, password?: string) => boolean;
  loginAsDemo: (persona?: 'julian' | 'elena' | 'marcus') => void;
  register: (name: string, email: string, password?: string) => void;
  logout: () => void;
  completeOnboarding: (details: Partial<UserProfile>) => void;
  updateUser: (updates: Partial<UserProfile>) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserProfile>(() => storageService.getUser());
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() => storageService.getIsAuthenticated());

  useEffect(() => {
    storageService.saveUser(user);
  }, [user]);

  useEffect(() => {
    storageService.setIsAuthenticated(isAuthenticated);
  }, [isAuthenticated]);

  const login = (email: string): boolean => {
    // Demo-friendly authentication
    setUser(prev => ({ ...prev, email }));
    setIsAuthenticated(true);
    return true;
  };

  const loginAsDemo = (persona: 'julian' | 'elena' | 'marcus' = 'julian') => {
    if (persona === 'julian') {
      setUser({
        id: 'user-julian-1',
        name: 'Julian Vance',
        email: 'julian.vance@university.edu',
        university: 'Oxford University / Institute of Technology',
        program: 'B.Sc. Computer Science & Mathematics',
        academicYear: 'Year 2 (Sophomore)',
        semester: 'Fall Semester 2026',
        preferredStudyHours: 25,
        dailyTargetMinutes: 180,
        avatar: 'JV',
        isOnboarded: true,
        createdAt: '2026-09-01T08:00:00Z',
      });
    } else if (persona === 'elena') {
      setUser({
        id: 'user-elena-2',
        name: 'Elena Rostova',
        email: 'elena.rostova@cambridge.edu',
        university: 'Cambridge University',
        program: 'M.Sc. Artificial Intelligence & Cognitive Science',
        academicYear: 'Graduate Year 1',
        semester: 'Michaelmas 2026',
        preferredStudyHours: 30,
        dailyTargetMinutes: 240,
        avatar: 'ER',
        isOnboarded: true,
        createdAt: '2026-09-01T08:00:00Z',
      });
    } else {
      setUser({
        id: 'user-marcus-3',
        name: 'Marcus Holloway',
        email: 'm.holloway@stanford.edu',
        university: 'Stanford School of Engineering',
        program: 'B.S. Electrical & Computer Engineering',
        academicYear: 'Year 3 (Junior)',
        semester: 'Autumn Quarter 2026',
        preferredStudyHours: 28,
        dailyTargetMinutes: 200,
        avatar: 'MH',
        isOnboarded: true,
        createdAt: '2026-09-01T08:00:00Z',
      });
    }
    setIsAuthenticated(true);
  };

  const register = (name: string, email: string) => {
    const initials = name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2) || 'ST';

    setUser({
      id: `user-${Date.now()}`,
      name,
      email,
      university: 'University Academy',
      program: 'Undergraduate Studies',
      academicYear: 'Year 1',
      semester: 'Semester 1',
      preferredStudyHours: 20,
      dailyTargetMinutes: 150,
      avatar: initials,
      isOnboarded: false, // will trigger onboarding flow
      createdAt: new Date().toISOString(),
    });
    setIsAuthenticated(true);
  };

  const logout = () => {
    setIsAuthenticated(false);
  };

  const completeOnboarding = (details: Partial<UserProfile>) => {
    setUser(prev => ({
      ...prev,
      ...details,
      isOnboarded: true,
    }));
  };

  const updateUser = (updates: Partial<UserProfile>) => {
    setUser(prev => ({
      ...prev,
      ...updates,
    }));
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        login,
        loginAsDemo,
        register,
        logout,
        completeOnboarding,
        updateUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

