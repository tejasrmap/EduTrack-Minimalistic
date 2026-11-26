import React, { useState } from 'react';
import { 
  Play, 
  CheckCircle2, 
  Circle, 
  Plus
} from 'lucide-react';
import { useAcademic } from '../context/AppContext';
import { MinimalStudyBarChart } from '../components/charts/MinimalStudyBarChart';
import { MinimalSubjectDonut } from '../components/charts/MinimalSubjectDonut';

export const DashboardView: React.FC = () => {
  const { 
    user, 
    subjects, 
    tasks, 
    sessions, 
    addTask, 
    toggleTaskStatus, 
    setCurrentView 
  } = useAcademic();

  const [quickTitle, setQuickTitle] = useState('');

  const handleQuickAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!quickTitle.trim()) return;

    addTask({
      title: quickTitle.trim(),
      subjectId: subjects[0]?.id || 'subj-1',
      dueDate: new Date().toISOString().split('T')[0],
      priority: 'standard',
      status: 'pending',
      estimatedHours: 2,
    });
    setQuickTitle('');
  };

  const totalStudied = subjects.reduce((sum, s) => sum + s.currentWeekHours, 0);
  const pendingTasks = tasks.filter(t => t.status !== 'completed');

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="paper-card rounded-2xl p-6 sm:p-8 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <span className="text-[11px] font-mono text-[#2A5C43] bg-[#EBF3ED] px-2.5 py-1 rounded-md uppercase font-semibold">
            {user.term} · Academic Schedule
          </span>
          <h2 className="font-serif text-2xl sm:text-3xl font-bold text-stone-900 mt-2 tracking-tight">
            Good day, {user.name}
          </h2>
          <p className="text-xs sm:text-sm text-stone-600 mt-1 max-w-xl leading-relaxed">
            {user.degree}. Currently enrolled across {subjects.length} reading units. Term average stands at <strong className="text-stone-900">{user.currentGpa.toFixed(2)} GPA</strong>.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setCurrentView('timer')}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors shadow-xs"
          >
            <Play className="w-3.5 h-3.5 fill-white" />
            <span>Enter Focus Chamber</span>
          </button>
        </div>
      </div>

      {/* 4 Quiet Metric Tiles */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="paper-card rounded-xl p-4 flex flex-col justify-between">
          <span className="text-xs text-stone-500 font-serif italic">Weekly Study Time</span>
          <div className="my-2">
            <span className="text-2xl font-serif font-bold text-stone-900">{totalStudied.toFixed(1)} hrs</span>
            <span className="text-xs text-stone-500 ml-1.5 font-mono">/ {user.weeklyHourGoal}h goal</span>
          </div>
          <span className="text-[11px] text-[#2A5C43] font-medium">
            {Math.round((totalStudied / user.weeklyHourGoal) * 100)}% weekly target met
          </span>
        </div>

        <div className="paper-card rounded-xl p-4 flex flex-col justify-between">
          <span className="text-xs text-stone-500 font-serif italic">Academic Standing</span>
          <div className="my-2">
            <span className="text-2xl font-serif font-bold text-stone-900">{user.currentGpa.toFixed(2)}</span>
            <span className="text-xs text-stone-500 ml-1.5 font-mono">First Class</span>
          </div>
          <span className="text-[11px] text-stone-600">
            Target: {user.targetGpa.toFixed(2)} GPA
          </span>
        </div>

        <div className="paper-card rounded-xl p-4 flex flex-col justify-between">
          <span className="text-xs text-stone-500 font-serif italic">Outstanding Readings</span>
          <div className="my-2">
            <span className="text-2xl font-serif font-bold text-stone-900">{pendingTasks.length}</span>
            <span className="text-xs text-stone-500 ml-1.5 font-mono">assignments</span>
          </div>
          <span className="text-[11px] text-amber-700 font-medium">
            {tasks.filter(t => t.priority === 'essential' && t.status !== 'completed').length} essential items
          </span>
        </div>

        <div className="paper-card rounded-xl p-4 flex flex-col justify-between">
          <span className="text-xs text-stone-500 font-serif italic">Unbroken Focus Streak</span>
          <div className="my-2">
            <span className="text-2xl font-serif font-bold text-stone-900">{user.streakDays}</span>
            <span className="text-xs text-stone-500 ml-1.5 font-mono">days active</span>
          </div>
          <span className="text-[11px] text-stone-600">
            Lent Term Consistency
          </span>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 paper-card rounded-2xl p-5">
          <div className="flex items-center justify-between mb-2">
            <h3 className="font-serif text-base font-bold text-stone-900">
              Hours Logged This Week
            </h3>
            <button
              onClick={() => setCurrentView('analytics')}
              className="text-xs text-[#2A5C43] hover:underline font-mono"
            >
              Full Analytics →
            </button>
          </div>
          <MinimalStudyBarChart weeklyGoalHours={user.weeklyHourGoal} />
        </div>

        <div className="paper-card rounded-2xl p-5">
          <div className="flex items-center justify-between mb-2">
            <h3 className="font-serif text-base font-bold text-stone-900">
              Curriculum Allocation
            </h3>
            <button
              onClick={() => setCurrentView('subjects')}
              className="text-xs text-[#2A5C43] hover:underline font-mono"
            >
              Units →
            </button>
          </div>
          <MinimalSubjectDonut subjects={subjects} />
        </div>
      </div>

      {/* Task Queue & Recent Reading Reflections */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Task Queue (2 cols) */}
        <div className="lg:col-span-2 paper-card rounded-2xl p-5 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="font-serif text-base font-bold text-stone-900">
              Reading & Assignment Queue
            </h3>
            <button
              onClick={() => setCurrentView('planner')}
              className="text-xs text-[#2A5C43] hover:underline font-mono"
            >
              View All Tasks ({tasks.length})
            </button>
          </div>

          <form onSubmit={handleQuickAdd} className="flex gap-2">
            <input
              type="text"
              value={quickTitle}
              onChange={(e) => setQuickTitle(e.target.value)}
              placeholder="Add reading or seminar assignment..."
              className="flex-1 px-3.5 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 placeholder-stone-400 focus:outline-none focus:border-[#2A5C43] focus:bg-white"
            />
            <button
              type="submit"
              className="px-3.5 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium flex items-center gap-1 transition-colors"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Add</span>
            </button>
          </form>

          <div className="space-y-2">
            {tasks.slice(0, 5).map((t) => {
              const sub = subjects.find(s => s.id === t.subjectId);
              const isDone = t.status === 'completed';

              return (
                <div
                  key={t.id}
                  className={`flex items-center justify-between p-3 rounded-xl border transition-colors ${
                    isDone ? 'bg-stone-50 border-stone-100 opacity-60' : 'bg-white border-stone-200 hover:border-stone-300'
                  }`}
                >
                  <div className="flex items-center gap-3 min-w-0 pr-3">
                    <button
                      onClick={() => toggleTaskStatus(t.id)}
                      className="text-stone-400 hover:text-[#2A5C43] transition-colors flex-shrink-0"
                    >
                      {isDone ? (
                        <CheckCircle2 className="w-4 h-4 text-[#2A5C43]" />
                      ) : (
                        <Circle className="w-4 h-4" />
                      )}
                    </button>
                    <div className="min-w-0">
                      <p className={`text-xs font-medium text-stone-800 truncate ${isDone ? 'line-through text-stone-400' : ''}`}>
                        {t.title}
                      </p>
                      <div className="flex items-center gap-2 mt-0.5 text-[10px] text-stone-500 font-mono">
                        {sub && (
                          <span style={{ color: sub.pastelColor }} className="font-semibold">
                            {sub.code}
                          </span>
                        )}
                        <span>Due {t.dueDate}</span>
                        <span className="capitalize">{t.priority}</span>
                      </div>
                    </div>
                  </div>

                  <span className="text-[11px] font-mono text-stone-500 flex-shrink-0">
                    ~{t.estimatedHours} hrs
                  </span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Recent Reading Log */}
        <div className="paper-card rounded-2xl p-5 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="font-serif text-base font-bold text-stone-900">
              Recent Study Sessions
            </h3>
            <button
              onClick={() => setCurrentView('history')}
              className="text-xs text-[#2A5C43] hover:underline font-mono"
            >
              Archive
            </button>
          </div>

          <div className="space-y-3">
            {sessions.slice(0, 3).map((sess) => (
              <div 
                key={sess.id}
                className="p-3 rounded-xl bg-stone-50 border border-stone-200 space-y-1.5"
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-stone-800 truncate max-w-[170px]">
                    {sess.subjectTitle}
                  </span>
                  <span className="text-xs font-mono text-[#2A5C43] font-bold">
                    {sess.durationMinutes}m
                  </span>
                </div>
                <div className="flex items-center justify-between text-[10px] text-stone-500 font-mono">
                  <span>{sess.timestamp}</span>
                  <span className="capitalize">{sess.sessionType}</span>
                </div>
                {sess.reflection && (
                  <p className="text-[11px] text-stone-600 italic line-clamp-2 leading-relaxed">
                    "{sess.reflection}"
                  </p>
                )}
              </div>
            ))}
          </div>

          <button
            onClick={() => setCurrentView('timer')}
            className="w-full py-2 rounded-xl bg-stone-100 hover:bg-stone-200 text-stone-700 text-xs font-medium transition-colors"
          >
            Start New Session
          </button>
        </div>
      </div>
    </div>
  );
};
