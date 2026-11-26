import React, { useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Clock,
  CheckSquare,
  FileText,
  GraduationCap,
  Play,
  Plus,
  User,
  CheckCircle2,
  Calendar,
  Layers
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useTimer } from '../../contexts/TimerContext';
import { EmptyState } from '../../components/common/EmptyState';

export const SubjectDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { subjects, tasks, notes, studySessions, examinations, toggleTaskStatus } = useData();
  const { switchMode, setSubjectId, startTimer } = useTimer();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState<'overview' | 'assignments' | 'notes' | 'history'>('overview');

  const subject = subjects.find(s => s.id === id);

  if (!subject) {
    return (
      <div className="p-8 text-center space-y-4">
        <h2 className="text-xl font-bold text-slate-primary dark:text-white">Subject Not Found</h2>
        <p className="text-sm text-slate-secondary">The requested academic subject could not be located.</p>
        <Link to="/subjects" className="inline-flex items-center gap-2 text-primary font-semibold text-sm">
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Subjects</span>
        </Link>
      </div>
    );
  }

  // Related data
  const relatedTasks = tasks.filter(t => t.subjectId === subject.id);
  const relatedNotes = notes.filter(n => n.subjectId === subject.id);
  const relatedSessions = studySessions.filter(s => s.subjectId === subject.id);
  const relatedExams = examinations.filter(e => e.subjectId === subject.id);

  const completedTasksCount = relatedTasks.filter(t => t.status === 'completed').length;
  const pendingTasksCount = relatedTasks.length - completedTasksCount;
  const totalStudyHours = (subject.totalStudyMinutes / 60).toFixed(1);
  const weeklyStudyHours = (subject.currentWeeklyMinutes / 60).toFixed(1);
  const weeklyProgress = Math.min(100, Math.round((subject.currentWeeklyMinutes / (subject.weeklyTargetHours * 60)) * 100));

  const handleStartFocus = () => {
    setSubjectId(subject.id);
    switchMode('focus');
    startTimer();
    navigate('/timer');
  };

  return (
    <div className="space-y-6 sm:space-y-8 animate-fadeIn">
      {/* Breadcrumb Back Link */}
      <div>
        <Link
          to="/subjects"
          className="inline-flex items-center gap-1.5 text-xs text-slate-secondary hover:text-slate-primary dark:hover:text-white font-medium mb-3 transition-colors"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Back to all subjects</span>
        </Link>

        {/* Hero Header */}
        <div className="card-academic p-6 sm:p-8 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="space-y-2">
              <div className="flex items-center gap-2.5">
                <span
                  className="px-2.5 py-1 rounded text-xs font-mono font-bold"
                  style={{ backgroundColor: `${subject.color}15`, color: subject.color }}
                >
                  {subject.code}
                </span>
                <span className="text-xs text-slate-secondary font-medium">
                  {subject.semester}
                </span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
                {subject.name}
              </h1>
              <p className="text-xs sm:text-sm text-slate-secondary max-w-2xl leading-relaxed">
                {subject.description || 'Dedicated academic tracking and syllabus review workspace.'}
              </p>
              <div className="flex items-center gap-4 text-xs text-slate-secondary pt-1">
                <span className="flex items-center gap-1.5">
                  <User className="w-3.5 h-3.5" />
                  <span>{subject.instructor}</span>
                </span>
                <span>·</span>
                <span className="text-success font-medium">
                  Attendance: {subject.attendanceRate}%
                </span>
              </div>
            </div>

            <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
              <button
                onClick={handleStartFocus}
                className="px-5 py-2.5 rounded-academic bg-primary text-white font-semibold text-xs sm:text-sm hover:bg-primary-hover shadow-sm transition-colors flex items-center justify-center gap-2"
              >
                <Play className="w-4 h-4 fill-current" />
                <span>Start Focus Session</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Quantitative Subject Indicators */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="text-[11px] text-slate-secondary mb-1">Total Time Invested</div>
          <div className="font-mono text-xl sm:text-2xl font-bold text-slate-primary dark:text-white">
            {totalStudyHours}h
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">Across all logged sessions</div>
        </div>

        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="text-[11px] text-slate-secondary mb-1">Weekly Target Pace</div>
          <div className="font-mono text-xl sm:text-2xl font-bold text-primary">
            {weeklyStudyHours}h <span className="text-xs font-normal text-slate-secondary">/ {subject.weeklyTargetHours}h</span>
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">{weeklyProgress}% of weekly quota</div>
        </div>

        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="text-[11px] text-slate-secondary mb-1">Assignments</div>
          <div className="font-mono text-xl sm:text-2xl font-bold text-success">
            {completedTasksCount} <span className="text-xs font-normal text-slate-secondary">/ {relatedTasks.length}</span>
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">{pendingTasksCount} remaining to submit</div>
        </div>

        <div className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="text-[11px] text-slate-secondary mb-1">Upcoming Exams</div>
          <div className="font-mono text-xl sm:text-2xl font-bold text-danger">
            {relatedExams.length}
          </div>
          <div className="text-[10px] text-slate-secondary mt-1">
            {relatedExams[0] ? `Next on ${relatedExams[0].examDate}` : 'No exams pending'}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-slate-border dark:border-slate-800 gap-6">
        {[
          { id: 'overview', label: 'Overview & Syllabi', count: undefined },
          { id: 'assignments', label: 'Assignments', count: relatedTasks.length },
          { id: 'notes', label: 'Study Notes', count: relatedNotes.length },
          { id: 'history', label: 'Session History', count: relatedSessions.length },
        ].map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id as typeof activeTab)}
            className={`pb-3 text-xs sm:text-sm font-semibold border-b-2 flex items-center gap-1.5 transition-colors ${activeTab === tab.id
              ? 'border-primary text-primary'
              : 'border-transparent text-slate-secondary hover:text-slate-primary dark:hover:text-white'
              }`}
          >
            <span>{tab.label}</span>
            {typeof tab.count === 'number' && (
              <span className="px-1.5 py-0.2 rounded-full text-[10px] bg-slate-muted dark:bg-slate-800 text-slate-secondary font-mono">
                {tab.count}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* Tab Panels */}
      {activeTab === 'overview' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 animate-fadeIn">
          {/* Syllabus Details */}
          <div className="card-academic p-5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 space-y-4">
            <h3 className="font-bold text-sm text-slate-primary dark:text-white flex items-center gap-2">
              <Layers className="w-4 h-4 text-primary" />
              <span>Course Syllabus & Directives</span>
            </h3>
            <p className="text-xs sm:text-sm text-slate-secondary leading-relaxed">
              {subject.description || 'Comprehensive syllabus modules covering core theory, recitation sessions, laboratory exercises, and research projects.'}
            </p>
            <div className="pt-3 border-t border-slate-border dark:border-slate-700 text-xs space-y-2">
              <div className="flex justify-between">
                <span className="text-slate-secondary">Primary Faculty:</span>
                <span className="font-semibold text-slate-primary dark:text-white">{subject.instructor}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-secondary">Attendance Benchmark:</span>
                <span className="font-semibold text-success">{subject.attendanceRate}% Attested</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-secondary">Allocated Study Target:</span>
                <span className="font-semibold text-slate-primary dark:text-white">{subject.weeklyTargetHours} Hours / Week</span>
              </div>
            </div>
          </div>

          {/* Upcoming Milestones */}
          <div className="card-academic p-5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 space-y-4">
            <h3 className="font-bold text-sm text-slate-primary dark:text-white flex items-center gap-2">
              <Calendar className="w-4 h-4 text-warning" />
              <span>Upcoming Deliverables</span>
            </h3>
            {relatedTasks.filter(t => t.status !== 'completed').length === 0 ? (
              <div className="py-6 text-center text-xs text-slate-secondary">
                No active assignments pending for this course.
              </div>
            ) : (
              <div className="space-y-2.5">
                {relatedTasks.filter(t => t.status !== 'completed').map(task => (
                  <div key={task.id} className="p-3 rounded-academic border border-slate-border dark:border-slate-700 bg-slate-muted/30 dark:bg-slate-900/30 flex items-center justify-between text-xs">
                    <div>
                      <div className="font-semibold text-slate-primary dark:text-white">{task.title}</div>
                      <div className="text-[10px] text-slate-secondary mt-0.5">Due: {task.dueDate}</div>
                    </div>
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold uppercase bg-warning/10 text-warning">
                      {task.priority}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {activeTab === 'assignments' && (
        <div className="space-y-3 animate-fadeIn">
          {relatedTasks.length === 0 ? (
            <EmptyState
              icon={CheckSquare}
              title="No Assignments Added"
              description="No tasks or problem sets have been associated with this course yet."
              actionLabel="Create Task"
              onAction={() => navigate('/assignments')}
            />
          ) : (
            relatedTasks.map(task => (
              <div
                key={task.id}
                className="card-academic p-4 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 flex items-center justify-between"
              >
                <div className="flex items-center gap-3">
                  <button
                    onClick={() => toggleTaskStatus(task.id)}
                    className={`w-5 h-5 rounded-md border flex items-center justify-center transition-colors ${task.status === 'completed'
                      ? 'bg-success border-success text-white'
                      : 'border-slate-300 dark:border-slate-600 hover:border-primary'
                      }`}
                  >
                    {task.status === 'completed' && <CheckCircle2 className="w-3.5 h-3.5" />}
                  </button>
                  <div>
                    <h4 className={`text-xs sm:text-sm font-semibold ${task.status === 'completed' ? 'line-through text-slate-secondary' : 'text-slate-primary dark:text-white'}`}>
                      {task.title}
                    </h4>
                    <p className="text-[11px] text-slate-secondary mt-0.5">
                      Due: {task.dueDate} · Category: <span className="capitalize">{task.category}</span>
                    </p>
                  </div>
                </div>

                <span className={`px-2 py-0.5 text-[10px] font-bold uppercase rounded ${task.priority === 'high' ? 'bg-danger/10 text-danger' : task.priority === 'medium' ? 'bg-warning/10 text-warning' : 'bg-slate-muted text-slate-secondary'
                  }`}>
                  {task.priority}
                </span>
              </div>
            ))
          )}
        </div>
      )}

      {activeTab === 'notes' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 animate-fadeIn">
          {relatedNotes.length === 0 ? (
            <div className="col-span-2">
              <EmptyState
                icon={FileText}
                title="No Course Notes Yet"
                description="Write lecture summaries, proof outlines, and research takeaways for this subject."
                actionLabel="Write New Note"
                onAction={() => navigate('/notes')}
              />
            </div>
          ) : (
            relatedNotes.map(n => (
              <div key={n.id} className="card-academic p-5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 space-y-2">
                <h4 className="font-bold text-sm text-slate-primary dark:text-white">{n.title}</h4>
                <p className="text-xs text-slate-secondary line-clamp-3 leading-relaxed">{n.content.replace(/#/g, '')}</p>
                <div className="flex flex-wrap gap-1.5 pt-2">
                  {n.tags.map(t => (
                    <span key={t} className="px-2 py-0.5 text-[10px] rounded bg-slate-muted dark:bg-slate-700 text-slate-secondary">
                      #{t}
                    </span>
                  ))}
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {activeTab === 'history' && (
        <div className="space-y-2.5 animate-fadeIn">
          {relatedSessions.length === 0 ? (
            <EmptyState
              icon={Clock}
              title="No Study Sessions Logged"
              description="Start a focus timer to log your study sessions for this subject."
              actionLabel="Launch Timer"
              onAction={handleStartFocus}
            />
          ) : (
            relatedSessions.map(sess => (
              <div key={sess.id} className="card-academic p-3.5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 flex items-center justify-between text-xs">
                <div>
                  <div className="font-semibold text-slate-primary dark:text-white">
                    {sess.notes || `${subject.name} Focus Session`}
                  </div>
                  <div className="text-[10px] text-slate-secondary mt-0.5">
                    {sess.startTime ? sess.startTime.split('T')[0] : 'Recorded'} · <span className="capitalize">{sess.sessionType.replace('_', ' ')}</span> ({sess.isManual ? 'Manual' : 'Timer'})
                  </div>
                </div>
                <span className="font-mono font-bold text-primary text-sm">
                  {sess.durationMinutes}m
                </span>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};

