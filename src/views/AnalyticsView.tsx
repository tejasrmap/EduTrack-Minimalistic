import React from 'react';
import { useAcademic } from '../context/AppContext';
import { MinimalStudyBarChart } from '../components/charts/MinimalStudyBarChart';
import { MinimalSubjectDonut } from '../components/charts/MinimalSubjectDonut';
import { FocusTrendChart } from '../components/charts/FocusTrendChart';

export const AnalyticsView: React.FC = () => {
  const { user, subjects, sessions, tasks } = useAcademic();

  const totalHours = subjects.reduce((sum, s) => sum + s.currentWeekHours, 0);
  const completedTasks = tasks.filter(t => t.status === 'completed').length;
  const completionRate = tasks.length > 0 ? Math.round((completedTasks / tasks.length) * 100) : 0;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="paper-card rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="font-serif text-xl font-bold text-stone-900">
            Academic Analytics & Study Allocation
          </h2>
          <p className="text-xs text-stone-500">
            Quantified overview of studio, seminar and private study hours
          </p>
        </div>

        <span className="text-xs font-mono text-[#2A5C43] bg-[#EBF3ED] px-3 py-1.5 rounded-lg">
          Lent Term Status: Active
        </span>
      </div>

      {/* 4 Quiet Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="paper-card rounded-xl p-4">
          <span className="text-xs text-stone-500 font-serif italic">Total Term Hours</span>
          <p className="text-2xl font-serif font-bold text-stone-900 mt-1">{totalHours.toFixed(1)} hrs</p>
          <span className="text-[11px] text-[#2A5C43] mt-1 block">Weekly Target: {user.weeklyHourGoal} hrs</span>
        </div>

        <div className="paper-card rounded-xl p-4">
          <span className="text-xs text-stone-500 font-serif italic">Reading Completion</span>
          <p className="text-2xl font-serif font-bold text-stone-900 mt-1">{completionRate}%</p>
          <span className="text-[11px] text-stone-500 mt-1 block">{completedTasks} of {tasks.length} assignments</span>
        </div>

        <div className="paper-card rounded-xl p-4">
          <span className="text-xs text-stone-500 font-serif italic">Average Session Span</span>
          <p className="text-2xl font-serif font-bold text-stone-900 mt-1">74 Mins</p>
          <span className="text-[11px] text-stone-500 mt-1 block">{sessions.length} documented sessions</span>
        </div>

        <div className="paper-card rounded-xl p-4">
          <span className="text-xs text-stone-500 font-serif italic">Academic GPA Trend</span>
          <p className="text-2xl font-serif font-bold text-stone-900 mt-1">{user.currentGpa.toFixed(2)}</p>
          <span className="text-[11px] text-stone-500 mt-1 block">Dean's Honors List candidate</span>
        </div>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="paper-card rounded-2xl p-5">
          <h3 className="font-serif text-base font-bold text-stone-900 mb-2">
            Weekly Studio & Seminar Hours
          </h3>
          <MinimalStudyBarChart weeklyGoalHours={user.weeklyHourGoal} />
        </div>

        <div className="paper-card rounded-2xl p-5">
          <h3 className="font-serif text-base font-bold text-stone-900 mb-2">
            Study Consistency Trend
          </h3>
          <FocusTrendChart />
        </div>
      </div>

      <div className="paper-card rounded-2xl p-5">
        <h3 className="font-serif text-base font-bold text-stone-900 mb-2">
          Curriculum Allocation Distribution
        </h3>
        <MinimalSubjectDonut subjects={subjects} />
      </div>
    </div>
  );
};
