export interface UserProfile {
  id: string;
  name: string;
  email: string;
  university: string;
  program: string;
  academicYear: string; // e.g. "Year 2", "Sophomore"
  semester: string;     // e.g. "Semester 1", "Fall 2026"
  preferredStudyHours: number; // e.g. 25 hours/week
  dailyTargetMinutes: number;  // e.g. 180 mins
  avatar?: string;
  isOnboarded: boolean;
  createdAt: string;
}

export interface Subject {
  id: string;
  name: string;
  code: string; // e.g. "CS201", "MATH102"
  color: string; // Hex color for chips and charts
  description: string;
  semester: string;
  instructor: string;
  weeklyTargetHours: number;
  currentWeeklyMinutes: number;
  totalStudyMinutes: number;
  completedAssignmentsCount: number;
  attendanceRate: number; // e.g. 92%
  createdAt: string;
}

export type TaskCategory = 
  | 'assignment' 
  | 'homework' 
  | 'revision' 
  | 'project' 
  | 'reading' 
  | 'exam_prep' 
  | 'other';

export type TaskPriority = 'low' | 'medium' | 'high';

export type TaskStatus = 'not_started' | 'in_progress' | 'completed';

export interface Task {
  id: string;
  title: string;
  description: string;
  subjectId: string;
  category: TaskCategory;
  priority: TaskPriority;
  status: TaskStatus;
  dueDate: string;      // YYYY-MM-DD
  dueTime?: string;     // HH:mm
  examId?: string;      // Optional linkage to an examination
  estimatedMinutes?: number;
  completedAt?: string;
  createdAt: string;
}

export type ExamType = 'midterm' | 'final' | 'quiz' | 'practical' | 'presentation' | 'other';
export type ExamPrepStatus = 'not_prepared' | 'in_progress' | 'ready';

export interface Examination {
  id: string;
  title: string;
  subjectId: string;
  examDate: string;     // YYYY-MM-DD
  examTime: string;     // HH:mm
  location: string;
  examType: ExamType;
  preparationStatus: ExamPrepStatus;
  targetGrade?: string;
  notes?: string;
  linkedTaskIds: string[];
  createdAt: string;
}

export interface Note {
  id: string;
  title: string;
  content: string; // Markdown / rich text
  subjectId?: string;
  tags: string[];
  isPinned: boolean;
  isFavourite: boolean;
  isArchived: boolean;
  createdAt: string;
  updatedAt: string;
}

export type GoalType = 
  | 'daily_study' 
  | 'weekly_study' 
  | 'monthly_study' 
  | 'subject_target' 
  | 'assignment_completion' 
  | 'exam_prep';

export interface Goal {
  id: string;
  title: string;
  type: GoalType;
  subjectId?: string;
  targetValue: number;
  currentValue: number;
  unit: string; // e.g. "hours", "tasks", "sessions"
  targetDate: string; // YYYY-MM-DD
  isCompleted: boolean;
  completedAt?: string;
  createdAt: string;
}

export type SessionType = 'pomodoro' | 'deep_work' | 'revision' | 'reading' | 'assignment';

export interface StudySession {
  id: string;
  subjectId: string;
  durationMinutes: number;
  sessionType: SessionType;
  notes?: string;
  isManual: boolean;
  startTime: string; // ISO
  endTime: string;   // ISO
  createdAt: string;
}

export interface ScheduleEvent {
  id: string;
  title: string;
  subjectId: string;
  date: string;       // YYYY-MM-DD
  startTime: string;  // HH:mm
  endTime: string;    // HH:mm
  description?: string;
  reminder: boolean;
  isCompleted: boolean;
  createdAt: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: 'deadline' | 'exam' | 'reminder' | 'goal' | 'streak';
  read: boolean;
  timestamp: string; // ISO
  linkTo?: string;   // internal route link e.g. "/assignments"
}

export interface UserSettings {
  theme: 'light' | 'dark';
  pomodoroFocusMinutes: number;
  pomodoroShortBreakMinutes: number;
  pomodoroLongBreakMinutes: number;
  longBreakInterval: number; // e.g. every 4 sessions
  weekStartDay: 'monday' | 'sunday';
  timeFormat: '12h' | '24h';
  soundEnabled: boolean;
  browserNotifications: boolean;
  dailyStudyTargetMinutes: number;
}

// Active Timer state saved to localStorage for persistence across reloads
export interface PersistedTimerState {
  isRunning: boolean;
  mode: 'focus' | 'short_break' | 'long_break' | 'custom';
  subjectId: string;
  sessionDescription: string;
  totalDurationSeconds: number;
  remainingSeconds: number;
  startTimestamp: number | null; // Date.now() when started/resumed
  accumulatedSeconds: number;     // Seconds spent before current running segment
  completedPomodoroCount: number;
}
