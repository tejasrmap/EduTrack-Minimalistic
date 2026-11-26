// Re-export all modern types from types/index
export * from './types/index';

export type ViewType = 
  | 'dashboard' 
  | 'subjects' 
  | 'planner' 
  | 'timer' 
  | 'history' 
  | 'goals' 
  | 'analytics' 
  | 'settings';

export interface AcademicUser {
  id: string;
  name: string;
  email: string;
  avatar: string;
  degree: string;
  institution: string;
  term: string;
  targetGpa: number;
  currentGpa: number;
  weeklyHourGoal: number;
  streakDays: number;
}

export interface AcademicSubject {
  id: string;
  title: string;
  code: string;
  pastelColor: string; // gentle calm pastel hex
  badgeBg: string;
  badgeText: string;
  credits: number;
  gradeTarget: string;
  currentGrade: string;
  attendancePercentage: number;
  weeklyTargetHours: number;
  currentWeekHours: number;
  instructor: string;
  location: string;
  syllabusOverview?: string;
}

export type AcademicTaskPriority = 'essential' | 'standard' | 'supplemental';
export type AcademicTaskStatus = 'pending' | 'in-progress' | 'completed';

export interface AcademicTask {
  id: string;
  title: string;
  subjectId: string;
  dueDate: string;
  dueTime?: string;
  priority: AcademicTaskPriority;
  status: AcademicTaskStatus;
  type?: 'assignment' | 'exam' | 'reading' | 'revision';
  estimatedHours: number;
  notes?: string;
  readingNotes?: string;
  completedAt?: string;
}

export interface AcademicSession {
  id: string;
  subjectId: string;
  subjectTitle?: string;
  date?: string;
  timestamp?: string;
  durationMinutes: number;
  type?: 'pomodoro' | 'deep-focus' | 'review' | 'assignment-prep';
  sessionType?: string;
  notes?: string;
  reflection?: string;
  focusScore: number;
  completedAt?: string;
}

export type AcademicStudySession = AcademicSession;

export interface AcademicMilestone {
  id: string;
  title: string;
  targetDate?: string;
  deadline?: string;
  isCompleted?: boolean;
  completed?: boolean;
  type?: string;
  targetMetric?: string;
  targetValue: number;
  currentValue: number;
  unit?: string;
}

export interface AcademicAward {
  id: string;
  title: string;
  description?: string;
  dateEarned?: string;
  icon?: string;
  criteria?: string;
  earned?: boolean;
}

export interface MinimalSettings {
  theme?: 'light' | 'dark';
  pomodoroMinutes?: number;
  focusSessionMinutes: number;
  shortBreakMinutes?: number;
  breakMinutes: number;
  longBreakMinutes?: number;
  soundEnabled?: boolean;
  soundAlerts: boolean;
  distractionFreeMode?: boolean;
}

export interface AcademicScheduleSlot {
  id: string;
  subjectId: string;
  dayOfWeek: number; // 0 = Sun, 1 = Mon ...
  startTime: string; // "09:00"
  endTime: string;   // "10:30"
  location: string;
  type: 'lecture' | 'lab' | 'seminar' | 'tutorial' | 'office-hours';
}
