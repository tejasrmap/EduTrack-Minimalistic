import { 
  UserProfile, 
  Subject, 
  Task, 
  Examination, 
  Note, 
  Goal, 
  StudySession, 
  ScheduleEvent, 
  NotificationItem, 
  UserSettings,
  PersistedTimerState
} from '../../types';
import { 
  initialUserProfile, 
  initialSubjects, 
  initialTasks, 
  initialExaminations, 
  initialNotes, 
  initialGoals, 
  initialStudySessions, 
  initialScheduleEvents, 
  initialNotifications, 
  initialUserSettings 
} from './initialSeed';

const PREFIX = 'edutrack_minimal_academic_v2';

export const STORAGE_KEYS = {
  USER: `${PREFIX}_user`,
  SUBJECTS: `${PREFIX}_subjects`,
  TASKS: `${PREFIX}_tasks`,
  EXAMS: `${PREFIX}_exams`,
  NOTES: `${PREFIX}_notes`,
  GOALS: `${PREFIX}_goals`,
  SESSIONS: `${PREFIX}_sessions`,
  SCHEDULE: `${PREFIX}_schedule`,
  NOTIFICATIONS: `${PREFIX}_notifications`,
  SETTINGS: `${PREFIX}_settings`,
  TIMER_STATE: `${PREFIX}_timer_state`,
  AUTH_SESSION: `${PREFIX}_auth_session`,
};

class StorageService {
  private getItem<T>(key: string, defaultValue: T): T {
    try {
      const data = localStorage.getItem(key);
      if (!data) return defaultValue;
      return JSON.parse(data) as T;
    } catch (err) {
      console.warn(`[StorageService] Failed to parse item for key "${key}":`, err);
      return defaultValue;
    }
  }

  private setItem<T>(key: string, value: T): void {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch (err) {
      console.error(`[StorageService] Failed to set item for key "${key}":`, err);
    }
  }

  // --- Auth & User ---
  getUser(): UserProfile {
    return this.getItem<UserProfile>(STORAGE_KEYS.USER, initialUserProfile);
  }

  saveUser(user: UserProfile): void {
    this.setItem(STORAGE_KEYS.USER, user);
  }

  getIsAuthenticated(): boolean {
    return this.getItem<boolean>(STORAGE_KEYS.AUTH_SESSION, true);
  }

  setIsAuthenticated(status: boolean): void {
    this.setItem(STORAGE_KEYS.AUTH_SESSION, status);
  }

  // --- Subjects ---
  getSubjects(): Subject[] {
    return this.getItem<Subject[]>(STORAGE_KEYS.SUBJECTS, initialSubjects);
  }

  saveSubjects(subjects: Subject[]): void {
    this.setItem(STORAGE_KEYS.SUBJECTS, subjects);
  }

  // --- Tasks ---
  getTasks(): Task[] {
    return this.getItem<Task[]>(STORAGE_KEYS.TASKS, initialTasks);
  }

  saveTasks(tasks: Task[]): void {
    this.setItem(STORAGE_KEYS.TASKS, tasks);
  }

  // --- Examinations ---
  getExaminations(): Examination[] {
    return this.getItem<Examination[]>(STORAGE_KEYS.EXAMS, initialExaminations);
  }

  saveExaminations(exams: Examination[]): void {
    this.setItem(STORAGE_KEYS.EXAMS, exams);
  }

  // --- Notes ---
  getNotes(): Note[] {
    return this.getItem<Note[]>(STORAGE_KEYS.NOTES, initialNotes);
  }

  saveNotes(notes: Note[]): void {
    this.setItem(STORAGE_KEYS.NOTES, notes);
  }

  // --- Goals ---
  getGoals(): Goal[] {
    return this.getItem<Goal[]>(STORAGE_KEYS.GOALS, initialGoals);
  }

  saveGoals(goals: Goal[]): void {
    this.setItem(STORAGE_KEYS.GOALS, goals);
  }

  // --- Study Sessions (History) ---
  getStudySessions(): StudySession[] {
    return this.getItem<StudySession[]>(STORAGE_KEYS.SESSIONS, initialStudySessions);
  }

  saveStudySessions(sessions: StudySession[]): void {
    this.setItem(STORAGE_KEYS.SESSIONS, sessions);
  }

  // --- Schedule Events ---
  getScheduleEvents(): ScheduleEvent[] {
    return this.getItem<ScheduleEvent[]>(STORAGE_KEYS.SCHEDULE, initialScheduleEvents);
  }

  saveScheduleEvents(events: ScheduleEvent[]): void {
    this.setItem(STORAGE_KEYS.SCHEDULE, events);
  }

  // --- Notifications ---
  getNotifications(): NotificationItem[] {
    return this.getItem<NotificationItem[]>(STORAGE_KEYS.NOTIFICATIONS, initialNotifications);
  }

  saveNotifications(notifs: NotificationItem[]): void {
    this.setItem(STORAGE_KEYS.NOTIFICATIONS, notifs);
  }

  // --- User Settings ---
  getSettings(): UserSettings {
    return this.getItem<UserSettings>(STORAGE_KEYS.SETTINGS, initialUserSettings);
  }

  saveSettings(settings: UserSettings): void {
    this.setItem(STORAGE_KEYS.SETTINGS, settings);
  }

  // --- Persisted Timer State ---
  getTimerState(): PersistedTimerState | null {
    return this.getItem<PersistedTimerState | null>(STORAGE_KEYS.TIMER_STATE, null);
  }

  saveTimerState(state: PersistedTimerState | null): void {
    if (state === null) {
      localStorage.removeItem(STORAGE_KEYS.TIMER_STATE);
    } else {
      this.setItem(STORAGE_KEYS.TIMER_STATE, state);
    }
  }

  // --- Backup & Data Management ---
  exportAllDataJSON(): string {
    const backup = {
      version: '2.0.0',
      exportedAt: new Date().toISOString(),
      user: this.getUser(),
      subjects: this.getSubjects(),
      tasks: this.getTasks(),
      examinations: this.getExaminations(),
      notes: this.getNotes(),
      goals: this.getGoals(),
      studySessions: this.getStudySessions(),
      scheduleEvents: this.getScheduleEvents(),
      notifications: this.getNotifications(),
      settings: this.getSettings(),
    };
    return JSON.stringify(backup, null, 2);
  }

  exportStudyHistoryCSV(): string {
    const sessions = this.getStudySessions();
    const subjects = this.getSubjects();
    const subjectMap = new Map(subjects.map(s => [s.id, s.name]));

    const headers = ['ID', 'Date', 'Subject', 'Duration (Minutes)', 'Session Type', 'Logged As', 'Notes'];
    const rows = sessions.map(s => [
      `"${s.id}"`,
      `"${s.startTime ? s.startTime.split('T')[0] : ''}"`,
      `"${subjectMap.get(s.subjectId) || 'General'}"`,
      s.durationMinutes,
      `"${s.sessionType}"`,
      s.isManual ? '"Manual"' : '"Timer"',
      `"${(s.notes || '').replace(/"/g, '""')}"`
    ]);

    return [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
  }

  importBackupJSON(jsonString: string): { success: boolean; message: string } {
    try {
      const parsed = JSON.parse(jsonString);
      if (!parsed || typeof parsed !== 'object') {
        return { success: false, message: 'Invalid JSON file structure.' };
      }

      if (parsed.user) this.saveUser(parsed.user);
      if (Array.isArray(parsed.subjects)) this.saveSubjects(parsed.subjects);
      if (Array.isArray(parsed.tasks)) this.saveTasks(parsed.tasks);
      if (Array.isArray(parsed.examinations)) this.saveExaminations(parsed.examinations);
      if (Array.isArray(parsed.notes)) this.saveNotes(parsed.notes);
      if (Array.isArray(parsed.goals)) this.saveGoals(parsed.goals);
      if (Array.isArray(parsed.studySessions)) this.saveStudySessions(parsed.studySessions);
      if (Array.isArray(parsed.scheduleEvents)) this.saveScheduleEvents(parsed.scheduleEvents);
      if (Array.isArray(parsed.notifications)) this.saveNotifications(parsed.notifications);
      if (parsed.settings) this.saveSettings(parsed.settings);

      return { success: true, message: 'All academic records restored successfully.' };
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Unknown parsing error';
      return { success: false, message: `Backup import failed: ${msg}` };
    }
  }

  resetToDemoData(): void {
    this.saveUser(initialUserProfile);
    this.saveSubjects(initialSubjects);
    this.saveTasks(initialTasks);
    this.saveExaminations(initialExaminations);
    this.saveNotes(initialNotes);
    this.saveGoals(initialGoals);
    this.saveStudySessions(initialStudySessions);
    this.saveScheduleEvents(initialScheduleEvents);
    this.saveNotifications(initialNotifications);
    this.saveSettings(initialUserSettings);
    this.saveTimerState(null);
    this.setIsAuthenticated(true);
  }

  clearAllData(): void {
    Object.values(STORAGE_KEYS).forEach(k => localStorage.removeItem(k));
  }
}

export const storageService = new StorageService();
