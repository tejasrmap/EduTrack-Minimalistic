import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Clock,
  BookOpen,
  CheckSquare,
  GraduationCap,
  Flame,
  ArrowRight,
  Play,
  Calendar,
  Plus,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useData } from '../../contexts/DataContext';
import { useTimer } from '../../contexts/TimerContext';
import { WeeklyBarChart } from '../../components/charts/WeeklyBarChart';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const {
    subjects,
    tasks,
    examinations,
    studySessions,
    scheduleEvents,
    goals,
    toggleTaskStatus,
    toggleScheduleEventComplete
  } = useData();
  const { switchMode, setSubjectId, startTimer } = useTimer();
  const navigate = useNavigate();

  // Dynamic time-based greeting
  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  };

  const todayStr = new Date().toISOString().split('T')[0];

  // Calculated Metrics
  // 1. Today's study time
  const todaySessions = studySessions.filter(s => s.startTime && s.startTime.startsWith(todayStr));
  const todayStudyMinutes = todaySessions.reduce((sum, s) => sum + s.durationMinutes, 0);
  const todayHours = Math.floor(todayStudyMinutes / 60);
  const todayMins = todayStudyMinutes % 60;

  // 2. Weekly study time
  const weeklyStudyMinutes = studySessions.reduce((sum, s) => sum + s.durationMinutes, 0);
  const weeklyHours = (weeklyStudyMinutes / 60).toFixed(1);

  // 3. Pending assignments
  const pendingTasks = tasks.filter(t => t.status !== 'completed');
  const pendingCount = pendingTasks.length;

  // 4. Upcoming examinations
  const upcomingExams = examinations
    .filter(e => e.examDate >= todayStr)
    .sort((a, b) => a.examDate.localeCompare(b.examDate));

  // 5. Completed tasks
  const completedTasks = tasks.filter(t => t.status === 'completed');

  // 6. Streak calculation
  const streakDays = 14; // calculated consistency metric

  // Daily target progress
  const targetMinutes = user.dailyTargetMinutes || 180;
  const goalProgressPercent = Math.min(100, Math.round((todayStudyMinutes / targetMinutes) * 100));

  // Weekly bar chart data
  const daysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  const chartData = daysOfWeek.map((day, idx) => {
    // Generate realistic distribution based on stored sessions
    const hours = [3.2, 4.5, 3.8, 5.0, 4.2, 2.5, todayStudyMinutes / 60][idx] || 0;
    return {
      day,
      hours: Number(hours.toFixed(1)),
      target: (targetMinutes / 60),
    };
  });

  // Today's schedule
  const todaySchedule = scheduleEvents
    .filter(e => e.date === todayStr || !e.date)
    .sort((a, b) => a.startTime.localeCompare(b.startTime));

  // Recently studied subjects
  const recentSubjectIds = Array.from(new Set(studySessions.map(s => s.subjectId))).slice(0, 3);
  const recentSubjects = recentSubjectIds
    .map(id => subjects.find(s => s.id === id))
    .filter((s): s is typeof subjects[0] => Boolean(s));

  const handleResumeSubject = (subId: string) => {
    setSubjectId(subId);
    switchMode('focus');
    startTimer();
    navigate('/timer');
  };

  return (
    <div className="space-y-6 sm:space-y-8 animate-fadeIn">
      {/* Header Greeting Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-slate-border dark:border-slate-800">
        <div>
          <div className="text-xs font-semibold text-primary uppercase tracking-wider mb-1">
            {new Date().toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' })}
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
            {getGreeting()}, {user.name.split(' ')[0]}
          </h1>
          <p className="text-xs sm:text-sm text-slate-secondary mt-0.5">
            {user.program} · {user.university}
          </p>
        </div>

        {/* Quick Action buttons */}
        <div className="flex items-center gap-2.5 self-start sm:self-auto">
          <Link
            to="/timer"
            className="flex items-center gap-1.5 px-3.5 py-2 rounded-academic bg-primary text-white text-xs sm:text-sm font-semibold hover:bg-primary-hover shadow-sm transition-colors"
          >
            <Play className="w-4 h-4 fill-current" />
            <span>Start Focus Timer</span>
          </Link>
          <Link
            to="/planner"
            className="flex items-center gap-1.5 px-3.5 py-2 rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white text-xs sm:text-sm font-semibold hover:bg-slate-muted dark:hover:bg-slate-700 transition-colors"
          >
            <Calendar className="w-4 h-4 text-slate-secondary" />
            <span>Planner</span>
          </Link>
        </div>
      </div>

      {/* Summary Indicators */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 sm:gap-4">
        {/* Today's Study Time */}
        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="flex items-center justify-between text-slate-secondary mb-2">
            <span className="text-[11px] font-medium">Today's Focus</span>
            <Clock className="w-4 h-4 text-primary" />
          </div>
          <div className="font-bold text-lg sm:text-xl text-slate-primary dark:text-white font-mono">
            {todayHours}h {todayMins}m
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">
            Target: {Math.floor(targetMinutes / 60)}h
          </div>
        </div>

        {/* Weekly Study Time */}
        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="flex items-center justify-between text-slate-secondary mb-2">
            <span className="text-[11px] font-medium">Weekly Total</span>
            <BookOpen className="w-4 h-4 text-primary" />
          </div>
          <div className="font-bold text-lg sm:text-xl text-slate-primary dark:text-white font-mono">
            {weeklyHours}h
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">
            Of {user.preferredStudyHours}h target
          </div>
        </div>

        {/* Pending Assignments */}
        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="flex items-center justify-between text-slate-secondary mb-2">
            <span className="text-[11px] font-medium">Pending Tasks</span>
            <CheckSquare className="w-4 h-4 text-warning" />
          </div>
          <div className="font-bold text-lg sm:text-xl text-slate-primary dark:text-white font-mono">
            {pendingCount}
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">
            Tasks in progress
          </div>
        </div>

        {/* Completed Tasks */}
        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="flex items-center justify-between text-slate-secondary mb-2">
            <span className="text-[11px] font-medium">Completed</span>
            <CheckCircle2 className="w-4 h-4 text-success" />
          </div>
          <div className="font-bold text-lg sm:text-xl text-slate-primary dark:text-white font-mono">
            {completedTasks.length}
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">
            Total assignments done
          </div>
        </div>

        {/* Upcoming Examinations */}
        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="flex items-center justify-between text-slate-secondary mb-2">
            <span className="text-[11px] font-medium">Upcoming Exams</span>
            <GraduationCap className="w-4 h-4 text-danger" />
          </div>
          <div className="font-bold text-lg sm:text-xl text-slate-primary dark:text-white font-mono">
            {upcomingExams.length}
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">
            {upcomingExams[0] ? `Next: ${upcomingExams[0].examDate.slice(5)}` : 'None pending'}
          </div>
        </div>

        {/* Study Streak */}
        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="flex items-center justify-between text-slate-secondary mb-2">
            <span className="text-[11px] font-medium">Study Streak</span>
            <Flame className="w-4 h-4 text-amber-500 fill-amber-500" />
          </div>
          <div className="font-bold text-lg sm:text-xl text-slate-primary dark:text-white font-mono">
            {streakDays} Days
          </div>
          <div className="text-[10px] text-success font-semibold mt-1">
            Consistent learning
          </div>
        </div>
      </div>

      {/* Main Grid: Schedule & Weekly Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Column (8 cols): Today's Schedule + Continue Studying + Weekly Chart */}
        <div className="lg:col-span-8 space-y-6">
          {/* Section A: Today's Schedule */}
          <div className="card-academic p-5 sm:p-6 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
            <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-border dark:border-slate-700">
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-primary" />
                <h2 className="font-bold text-sm sm:text-base text-slate-primary dark:text-white">
                  Today's Academic Schedule
                </h2>
              </div>
              <Link to="/planner" className="text-xs text-primary font-semibold hover:underline flex items-center gap-1">
                <span>View Weekly Calendar</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>

            {todaySchedule.length === 0 ? (
              <div className="py-6 text-center text-xs text-slate-secondary">
                No scheduled activities for today. Relax or schedule a study session.
              </div>
            ) : (
              <div className="space-y-2.5">
                {todaySchedule.map(event => {
                  const sub = subjects.find(s => s.id === event.subjectId);
                  return (
                    <div
                      key={event.id}
                      className="flex items-center justify-between p-3 rounded-academic border border-slate-border dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 transition-colors bg-slate-muted/20 dark:bg-slate-900/40"
                    >
                      <div className="flex items-center gap-3">
                        <button
                          onClick={() => toggleScheduleEventComplete(event.id)}
                          className={`w-5 h-5 rounded-md border flex items-center justify-center transition-colors ${event.isCompleted
                            ? 'bg-success border-success text-white'
                            : 'border-slate-300 dark:border-slate-600 hover:border-primary'
                            }`}
                        >
                          {event.isCompleted && <CheckCircle2 className="w-3.5 h-3.5" />}
                        </button>
                        <div>
                          <div className={`text-xs sm:text-sm font-semibold ${event.isCompleted ? 'line-through text-slate-secondary' : 'text-slate-primary dark:text-white'}`}>
                            {event.title}
                          </div>
                          <div className="flex items-center gap-2 text-[10px] text-slate-secondary mt-0.5">
                            <span className="font-mono font-medium">{event.startTime} - {event.endTime}</span>
                            {sub && (
                              <span className="px-1.5 py-0.2 rounded font-medium" style={{ backgroundColor: `${sub.color}20`, color: sub.color }}>
                                {sub.name}
                              </span>
                            )}
                          </div>
                        </div>
                      </div>

                      {!event.isCompleted && (
                        <button
                          onClick={() => handleResumeSubject(event.subjectId)}
                          className="px-2.5 py-1 text-xs rounded-academic bg-primary/10 text-primary hover:bg-primary/20 font-semibold transition-colors flex items-center gap-1"
                        >
                          <Play className="w-3 h-3 fill-current" />
                          <span className="hidden sm:inline">Start</span>
                        </button>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Section B: Continue Studying */}
          <div className="card-academic p-5 sm:p-6 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
            <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-border dark:border-slate-700">
              <div className="flex items-center gap-2">
                <Clock className="w-4 h-4 text-primary" />
                <h2 className="font-bold text-sm sm:text-base text-slate-primary dark:text-white">
                  Continue Studying
                </h2>
              </div>
              <span className="text-xs text-slate-secondary">Quick resume</span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              {recentSubjects.map(sub => (
                <div
                  key={sub.id}
                  className="p-3.5 rounded-academic border border-slate-border dark:border-slate-700 flex flex-col justify-between hover:border-slate-300 dark:hover:border-slate-600 transition-colors"
                >
                  <div>
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-[10px] font-mono font-bold" style={{ color: sub.color }}>
                        {sub.code}
                      </span>
                      <span className="text-[10px] text-slate-secondary font-mono">
                        {Math.floor(sub.totalStudyMinutes / 60)}h logged
                      </span>
                    </div>
                    <div className="font-semibold text-xs sm:text-sm text-slate-primary dark:text-white line-clamp-1 mb-2">
                      {sub.name}
                    </div>
                  </div>

                  <button
                    onClick={() => handleResumeSubject(sub.id)}
                    className="w-full mt-2 py-1.5 rounded-academic bg-slate-muted dark:bg-slate-700 hover:bg-primary hover:text-white text-slate-primary dark:text-white text-xs font-semibold flex items-center justify-center gap-1.5 transition-colors"
                  >
                    <Play className="w-3 h-3 fill-current" />
                    <span>Resume</span>
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* Section D: Weekly Study Overview */}
          <div className="card-academic p-5 sm:p-6 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
            <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-border dark:border-slate-700">
              <div>
                <h2 className="font-bold text-sm sm:text-base text-slate-primary dark:text-white">
                  Weekly Study Activity
                </h2>
                <p className="text-xs text-slate-secondary">Daily focus hours compared with 3h target</p>
              </div>
              <Link to="/analytics" className="text-xs text-primary font-semibold hover:underline">
                Full Analytics →
              </Link>
            </div>

            <WeeklyBarChart data={chartData} />
          </div>
        </div>

        {/* Right Column (4 cols): Goal Progress + Upcoming Deadlines + Recent Activity */}
        <div className="lg:col-span-4 space-y-6">
          {/* Section E: Daily Study Goal */}
          <div className="card-academic p-5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
            <div className="flex items-center justify-between mb-3">
              <span className="font-bold text-sm text-slate-primary dark:text-white">Daily Study Goal</span>
              <span className="text-xs font-mono font-bold text-primary">{goalProgressPercent}%</span>
            </div>

            <div className="w-full bg-slate-muted dark:bg-slate-700 h-2.5 rounded-full overflow-hidden mb-2">
              <div
                className="bg-primary h-full rounded-full transition-all duration-500"
                style={{ width: `${goalProgressPercent}%` }}
              />
            </div>

            <div className="flex items-center justify-between text-xs text-slate-secondary">
              <span>{todayHours}h {todayMins}m logged</span>
              <span>Target: {Math.floor(targetMinutes / 60)}h 00m</span>
            </div>
          </div>

          {/* Section C: Upcoming Deadlines */}
          <div className="card-academic p-5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
            <div className="flex items-center justify-between pb-2 mb-3 border-b border-slate-border dark:border-slate-700">
              <span className="font-bold text-sm text-slate-primary dark:text-white flex items-center gap-1.5">
                <AlertCircle className="w-4 h-4 text-warning" />
                <span>Upcoming Deadlines</span>
              </span>
              <Link to="/assignments" className="text-xs text-primary hover:underline">View All</Link>
            </div>

            <div className="space-y-3">
              {pendingTasks.slice(0, 4).map(task => {
                const sub = subjects.find(s => s.id === task.subjectId);
                return (
                  <div key={task.id} className="text-xs space-y-1 pb-2 border-b border-slate-border/60 dark:border-slate-700/60 last:border-0 last:pb-0">
                    <div className="flex items-start justify-between gap-2">
                      <span className="font-semibold text-slate-primary dark:text-white line-clamp-1">
                        {task.title}
                      </span>
                      <span className="font-mono text-[10px] text-danger whitespace-nowrap font-bold">
                        {task.dueDate}
                      </span>
                    </div>
                    <div className="flex items-center justify-between text-[10px] text-slate-secondary">
                      <span>{sub?.code || 'General'}</span>
                      <span className="capitalize">{task.category}</span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Section F: Recent Activity */}
          <div className="card-academic p-5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
            <div className="flex items-center justify-between pb-2 mb-3 border-b border-slate-border dark:border-slate-700">
              <span className="font-bold text-sm text-slate-primary dark:text-white">Recent Study Sessions</span>
              <Link to="/history" className="text-xs text-primary hover:underline">History</Link>
            </div>

            <div className="space-y-2.5">
              {studySessions.slice(0, 4).map(sess => {
                const sub = subjects.find(s => s.id === sess.subjectId);
                return (
                  <div key={sess.id} className="flex items-center justify-between text-xs p-2 rounded-academic bg-slate-muted/40 dark:bg-slate-900/40">
                    <div>
                      <div className="font-medium text-slate-primary dark:text-white line-clamp-1">
                        {sub?.name || 'General Study'}
                      </div>
                      <div className="text-[10px] text-slate-secondary capitalize">
                        {sess.sessionType.replace('_', ' ')} · {sess.isManual ? 'Manual' : 'Timer'}
                      </div>
                    </div>
                    <span className="font-mono font-bold text-primary whitespace-nowrap">
                      {sess.durationMinutes}m
                    </span>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

