import React, { useState, useMemo } from 'react';
import { 
  Clock, 
  Award, 
  Sparkles, 
  CheckCircle2, 
  Flame
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { WeeklyBarChart } from '../../components/charts/WeeklyBarChart';
import { SubjectDonutChart } from '../../components/charts/SubjectDonutChart';
import { MonthlyTrendChart } from '../../components/charts/MonthlyTrendChart';
import { format, subDays } from 'date-fns';

type DateRange = '7d' | '30d' | '90d' | 'all';

export const AnalyticsPage: React.FC = () => {
  const { studySessions, subjects, tasks, goals, settings } = useData();
  const [dateRange, setDateRange] = useState<DateRange>('30d');

  // Filter study sessions by selected date range
  const filteredSessions = useMemo(() => {
    if (dateRange === 'all') return studySessions;
    const now = new Date();
    const days = dateRange === '7d' ? 7 : dateRange === '30d' ? 30 : 90;
    const cutoff = subDays(now, days);
    return studySessions.filter(s => new Date(s.startTime) >= cutoff);
  }, [studySessions, dateRange]);

  // Total Study Minutes & Hours
  const totalMinutes = useMemo(() => {
    return filteredSessions.reduce((acc, s) => acc + s.durationMinutes, 0);
  }, [filteredSessions]);

  const totalHours = (totalMinutes / 60).toFixed(1);

  // Subject Study Distribution
  const subjectDistribution = useMemo(() => {
    const map = new Map<string, number>();
    filteredSessions.forEach(s => {
      map.set(s.subjectId, (map.get(s.subjectId) || 0) + s.durationMinutes);
    });

    return subjects.map(sub => {
      const mins = map.get(sub.id) || 0;
      const hours = Math.round((mins / 60) * 10) / 10;
      return {
        name: sub.name,
        code: sub.code,
        color: sub.color,
        hours,
        percentage: totalMinutes > 0 ? Math.round((mins / totalMinutes) * 100) : 0
      };
    }).sort((a, b) => b.hours - a.hours);
  }, [filteredSessions, subjects, totalMinutes]);

  // Daily Consistency & Streak
  const uniqueStudyDays = useMemo(() => {
    const daysSet = new Set<string>();
    filteredSessions.forEach(s => {
      daysSet.add(s.startTime.split('T')[0]);
    });
    return daysSet.size;
  }, [filteredSessions]);

  const totalDaysInRange = dateRange === '7d' ? 7 : dateRange === '30d' ? 30 : dateRange === '90d' ? 90 : Math.max(1, uniqueStudyDays);
  const consistencyScore = Math.min(100, Math.round((uniqueStudyDays / totalDaysInRange) * 100));

  // Goal & Task metrics
  const completedTasksCount = tasks.filter(t => t.status === 'completed').length;
  const completedGoalsCount = goals.filter(g => g.isCompleted).length;
  const goalCompletionRate = goals.length > 0 ? Math.round((completedGoalsCount / goals.length) * 100) : 0;

  // Most focused subject
  const topSubject = subjectDistribution[0];

  // Daily Chart Data for Recharts
  const dailyChartData = useMemo(() => {
    const numDays = dateRange === '7d' ? 7 : 14;
    const result = [];
    for (let i = numDays - 1; i >= 0; i--) {
      const date = subDays(new Date(), i);
      const dateKey = format(date, 'yyyy-MM-dd');
      const label = format(date, 'EEE d');

      const dayMins = filteredSessions
        .filter(s => s.startTime.startsWith(dateKey))
        .reduce((sum, s) => sum + s.durationMinutes, 0);

      result.push({
        day: label,
        hours: Math.round((dayMins / 60) * 10) / 10,
        target: Math.round((settings.dailyStudyTargetMinutes / 60) * 10) / 10
      });
    }
    return result;
  }, [filteredSessions, dateRange, settings.dailyStudyTargetMinutes]);

  // Rule-based Academic Insights
  const insights = useMemo(() => {
    const list: { title: string; desc: string; type: 'success' | 'warning' | 'info' }[] = [];

    if (consistencyScore >= 60) {
      list.push({
        title: 'High Academic Consistency',
        desc: `You have active study logs on ${uniqueStudyDays} out of ${totalDaysInRange} days (${consistencyScore}% consistency rate). Regular short sessions yield 40% higher retention than cramming.`,
        type: 'success'
      });
    } else {
      list.push({
        title: 'Consistency Opportunity',
        desc: `Your active study frequency is currently ${consistencyScore}%. Spreading study time across 4-5 days each week reduces exam anxiety.`,
        type: 'warning'
      });
    }

    if (topSubject && topSubject.hours > 0) {
      list.push({
        title: `Primary Focus: ${topSubject.code}`,
        desc: `${topSubject.name} accounts for ${topSubject.percentage}% of your study time (${topSubject.hours} hrs). Ensure other core modules remain balanced.`,
        type: 'info'
      });
    }

    const underTargetSubjects = subjects.filter(s => {
      const logged = (s.totalStudyMinutes / 60);
      return logged < s.weeklyTargetHours * 0.5;
    });

    if (underTargetSubjects.length > 0) {
      list.push({
        title: 'Subject Attention Required',
        desc: `${underTargetSubjects[0].name} has fallen behind weekly recommended study allocation. Consider scheduling a Pomodoro focus block.`,
        type: 'warning'
      });
    }

    return list;
  }, [consistencyScore, uniqueStudyDays, totalDaysInRange, topSubject, subjects]);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-academic-text tracking-tight">Academic Analytics & Insights</h1>
          <p className="text-sm text-academic-muted mt-1">
            Data-driven evaluation of study habits, subject distributions and focus consistency.
          </p>
        </div>

        {/* Date Range Selector */}
        <div className="flex items-center bg-academic-muted-bg p-1 rounded-lg border border-academic-border self-start sm:self-auto">
          <button
            onClick={() => setDateRange('7d')}
            className={`px-3 py-1.5 text-xs font-medium rounded-md transition-colors ${
              dateRange === '7d' ? 'bg-academic-card text-academic-primary shadow-sm font-semibold' : 'text-academic-muted hover:text-academic-text'
            }`}
          >
            Last 7 Days
          </button>
          <button
            onClick={() => setDateRange('30d')}
            className={`px-3 py-1.5 text-xs font-medium rounded-md transition-colors ${
              dateRange === '30d' ? 'bg-academic-card text-academic-primary shadow-sm font-semibold' : 'text-academic-muted hover:text-academic-text'
            }`}
          >
            Last 30 Days
          </button>
          <button
            onClick={() => setDateRange('90d')}
            className={`px-3 py-1.5 text-xs font-medium rounded-md transition-colors ${
              dateRange === '90d' ? 'bg-academic-card text-academic-primary shadow-sm font-semibold' : 'text-academic-muted hover:text-academic-text'
            }`}
          >
            Last 90 Days
          </button>
          <button
            onClick={() => setDateRange('all')}
            className={`px-3 py-1.5 text-xs font-medium rounded-md transition-colors ${
              dateRange === 'all' ? 'bg-academic-card text-academic-primary shadow-sm font-semibold' : 'text-academic-muted hover:text-academic-text'
            }`}
          >
            All Time
          </button>
        </div>
      </div>

      {/* Primary KPI Metrics */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-academic-muted">Total Study Time</span>
            <Clock className="w-4 h-4 text-academic-primary" />
          </div>
          <div className="text-2xl font-bold text-academic-text mt-2">{totalHours}h</div>
          <span className="text-[11px] text-academic-muted mt-1 block">
            {filteredSessions.length} total sessions logged
          </span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-academic-muted">Consistency Score</span>
            <Flame className="w-4 h-4 text-amber-500" />
          </div>
          <div className="text-2xl font-bold text-academic-text mt-2">{consistencyScore}%</div>
          <span className="text-[11px] text-academic-muted mt-1 block">
            {uniqueStudyDays} active days in period
          </span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-academic-muted">Tasks Completed</span>
            <CheckCircle2 className="w-4 h-4 text-emerald-500" />
          </div>
          <div className="text-2xl font-bold text-academic-text mt-2">{completedTasksCount}</div>
          <span className="text-[11px] text-academic-muted mt-1 block">
            Across {subjects.length} enrolled subjects
          </span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-academic-muted">Target Completion</span>
            <Award className="w-4 h-4 text-indigo-500" />
          </div>
          <div className="text-2xl font-bold text-academic-text mt-2">{goalCompletionRate}%</div>
          <span className="text-[11px] text-academic-muted mt-1 block">
            {completedGoalsCount} / {goals.length} academic goals met
          </span>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Daily Study Bar Chart */}
        <div className="lg:col-span-2 bg-academic-card p-5 rounded-xl border border-academic-border shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div>
                <h3 className="font-semibold text-academic-text text-sm">Study Time Overview</h3>
                <p className="text-xs text-academic-muted">Daily recorded study hours vs daily target</p>
              </div>
              <span className="text-xs px-2.5 py-1 rounded-md bg-academic-muted-bg text-academic-muted border border-academic-border">
                Target: {(settings.dailyStudyTargetMinutes / 60).toFixed(1)}h/day
              </span>
            </div>

            <div className="h-64 w-full">
              <WeeklyBarChart data={dailyChartData} />
            </div>
          </div>
        </div>

        {/* Subject Donut Chart */}
        <div className="bg-academic-card p-5 rounded-xl border border-academic-border shadow-sm flex flex-col justify-between">
          <div>
            <div className="mb-4">
              <h3 className="font-semibold text-academic-text text-sm">Subject Distribution</h3>
              <p className="text-xs text-academic-muted">Proportion of study hours by subject</p>
            </div>

            <div className="h-64 w-full">
              <SubjectDonutChart data={subjectDistribution} />
            </div>
          </div>
        </div>
      </div>

      {/* Monthly Trend & Detailed Subject Breakdown */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Trend Area Chart */}
        <div className="bg-academic-card p-5 rounded-xl border border-academic-border shadow-sm">
          <div className="mb-4">
            <h3 className="font-semibold text-academic-text text-sm">Cumulative Focus Trend</h3>
            <p className="text-xs text-academic-muted">Monthly progression of study intensity</p>
          </div>
          <div className="h-60 w-full">
            <MonthlyTrendChart />
          </div>
        </div>

        {/* Subject Breakdown List */}
        <div className="bg-academic-card p-5 rounded-xl border border-academic-border shadow-sm flex flex-col">
          <h3 className="font-semibold text-academic-text text-sm mb-4">Subject Hours Breakdown</h3>
          <div className="space-y-3.5 flex-1 overflow-y-auto">
            {subjectDistribution.map(item => (
              <div key={item.code} className="space-y-1.5">
                <div className="flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2">
                    <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: item.color }} />
                    <span className="font-medium text-academic-text">{item.name}</span>
                    <span className="text-academic-muted">({item.code})</span>
                  </div>
                  <span className="font-bold text-academic-text">{item.hours}h ({item.percentage}%)</span>
                </div>

                <div className="w-full h-2 bg-academic-muted-bg rounded-full overflow-hidden">
                  <div 
                    className="h-full rounded-full transition-all duration-300"
                    style={{ width: `${item.percentage}%`, backgroundColor: item.color }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Rule-based Academic Insights */}
      <div className="bg-academic-card p-5 rounded-xl border border-academic-border shadow-sm">
        <div className="flex items-center gap-2 mb-4">
          <Sparkles className="w-4 h-4 text-academic-primary" />
          <h3 className="font-semibold text-academic-text text-sm">Algorithmic Study Recommendations</h3>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {insights.map((insight, idx) => (
            <div 
              key={idx}
              className={`p-4 rounded-xl border text-xs ${
                insight.type === 'success' 
                  ? 'bg-emerald-50/50 border-emerald-200 dark:bg-emerald-950/20 dark:border-emerald-800/40 text-emerald-900 dark:text-emerald-300'
                  : insight.type === 'warning'
                  ? 'bg-amber-50/50 border-amber-200 dark:bg-amber-950/20 dark:border-amber-800/40 text-amber-900 dark:text-amber-300'
                  : 'bg-blue-50/50 border-blue-200 dark:bg-blue-950/20 dark:border-blue-800/40 text-blue-900 dark:text-blue-300'
              }`}
            >
              <h4 className="font-semibold text-sm mb-1">{insight.title}</h4>
              <p className="leading-relaxed opacity-90">{insight.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
