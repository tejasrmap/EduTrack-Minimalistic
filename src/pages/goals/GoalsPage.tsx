import React, { useState, useMemo } from 'react';
import { 
  Plus, 
  CheckCircle2, 
  Circle, 
  Calendar, 
  Trash2, 
  Edit3, 
  X
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { Goal, GoalType } from '../../types';
import EmptyState from '../../components/common/EmptyState';
import { parseISO, differenceInDays } from 'date-fns';

export const GoalsPage: React.FC = () => {
  const { goals, subjects, addGoal, updateGoal, deleteGoal, incrementGoalProgress, toggleGoalComplete } = useData();
  const { toast } = useToast();

  const [activeTab, setActiveTab] = useState<'active' | 'completed'>('active');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingGoal, setEditingGoal] = useState<Goal | null>(null);

  // Form State
  const [formTitle, setFormTitle] = useState('');
  const [formType, setFormType] = useState<GoalType>('weekly_study');
  const [formSubjectId, setFormSubjectId] = useState('');
  const [formTargetValue, setFormTargetValue] = useState(20);
  const [formCurrentValue, setFormCurrentValue] = useState(0);
  const [formUnit, setFormUnit] = useState('hours');
  const [formTargetDate, setFormTargetDate] = useState(new Date(Date.now() + 86400000 * 14).toISOString().split('T')[0]);

  const openCreateModal = () => {
    setEditingGoal(null);
    setFormTitle('');
    setFormType('weekly_study');
    setFormSubjectId('');
    setFormTargetValue(20);
    setFormCurrentValue(0);
    setFormUnit('hours');
    setFormTargetDate(new Date(Date.now() + 86400000 * 14).toISOString().split('T')[0]);
    setIsModalOpen(true);
  };

  const openEditModal = (goal: Goal) => {
    setEditingGoal(goal);
    setFormTitle(goal.title);
    setFormType(goal.type);
    setFormSubjectId(goal.subjectId || '');
    setFormTargetValue(goal.targetValue);
    setFormCurrentValue(goal.currentValue);
    setFormUnit(goal.unit);
    setFormTargetDate(goal.targetDate);
    setIsModalOpen(true);
  };

  const handleSaveGoal = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formTitle.trim()) {
      toast.error('Goal title is required');
      return;
    }
    if (formTargetValue <= 0) {
      toast.error('Target value must be greater than 0');
      return;
    }

    if (editingGoal) {
      updateGoal(editingGoal.id, {
        title: formTitle.trim(),
        type: formType,
        subjectId: formSubjectId || undefined,
        targetValue: Number(formTargetValue),
        currentValue: Number(formCurrentValue),
        unit: formUnit.trim() || 'units',
        targetDate: formTargetDate
      });
      toast.success('Goal updated');
    } else {
      addGoal({
        title: formTitle.trim(),
        type: formType,
        subjectId: formSubjectId || undefined,
        targetValue: Number(formTargetValue),
        currentValue: Number(formCurrentValue),
        unit: formUnit.trim() || 'units',
        targetDate: formTargetDate,
        isCompleted: false
      });
      toast.success('New goal created');
    }
    setIsModalOpen(false);
  };

  const handleDeleteGoal = (id: string, title: string) => {
    if (window.confirm(`Delete goal "${title}"?`)) {
      deleteGoal(id);
      toast.info('Goal deleted');
    }
  };

  const filteredGoals = useMemo(() => {
    return goals.filter(g => activeTab === 'completed' ? g.isCompleted : !g.isCompleted);
  }, [goals, activeTab]);

  const getSubject = (id?: string) => subjects.find(s => s.id === id);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-academic-text tracking-tight">Academic Goals & Targets</h1>
          <p className="text-sm text-academic-muted mt-1">
            Set deliberate study targets, maintain discipline and track weekly milestone completions.
          </p>
        </div>

        <button
          onClick={openCreateModal}
          className="flex items-center gap-2 px-4 py-2 bg-academic-primary text-white rounded-lg text-sm font-medium hover:bg-academic-primary/90 transition-colors shadow-sm self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>Set New Goal</span>
        </button>
      </div>

      {/* Tabs */}
      <div className="flex items-center justify-between border-b border-academic-border pb-3">
        <div className="flex items-center gap-2">
          <button
            onClick={() => setActiveTab('active')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
              activeTab === 'active'
                ? 'bg-academic-primary text-white shadow-sm'
                : 'text-academic-muted hover:text-academic-text hover:bg-academic-muted-bg'
            }`}
          >
            Active Goals ({goals.filter(g => !g.isCompleted).length})
          </button>
          <button
            onClick={() => setActiveTab('completed')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
              activeTab === 'completed'
                ? 'bg-academic-primary text-white shadow-sm'
                : 'text-academic-muted hover:text-academic-text hover:bg-academic-muted-bg'
            }`}
          >
            Completed ({goals.filter(g => g.isCompleted).length})
          </button>
        </div>
      </div>

      {/* Goals Grid */}
      {filteredGoals.length === 0 ? (
        <EmptyState
          title={activeTab === 'active' ? 'No active goals' : 'No completed goals yet'}
          description={
            activeTab === 'active'
              ? 'Establish clear study targets such as weekly hours or assignment completions to stay focused.'
              : 'Complete your ongoing targets to build a strong record of academic achievement.'
          }
          actionLabel={activeTab === 'active' ? 'Set Goal' : undefined}
          onAction={activeTab === 'active' ? openCreateModal : undefined}
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredGoals.map(goal => {
            const subject = getSubject(goal.subjectId);
            const percent = Math.min(100, Math.round((goal.currentValue / goal.targetValue) * 100));
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const daysLeft = differenceInDays(parseISO(goal.targetDate), today);

            return (
              <div 
                key={goal.id}
                className="bg-academic-card p-5 rounded-xl border border-academic-border shadow-sm flex flex-col justify-between hover:border-academic-primary/40 transition-all"
              >
                <div>
                  <div className="flex items-start justify-between gap-2">
                    <span className="text-[11px] uppercase tracking-wider font-semibold px-2 py-0.5 rounded bg-academic-muted-bg text-academic-muted">
                      {goal.type.replace('_', ' ')}
                    </span>

                    <button
                      onClick={() => toggleGoalComplete(goal.id)}
                      className={`text-xs flex items-center gap-1 font-medium transition-colors ${
                        goal.isCompleted ? 'text-emerald-600' : 'text-academic-muted hover:text-academic-primary'
                      }`}
                      title={goal.isCompleted ? 'Mark incomplete' : 'Mark as complete'}
                    >
                      {goal.isCompleted ? (
                        <>
                          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                          <span>Achieved</span>
                        </>
                      ) : (
                        <>
                          <Circle className="w-4 h-4" />
                          <span>Complete</span>
                        </>
                      )}
                    </button>
                  </div>

                  <h3 className="font-semibold text-base text-academic-text mt-3">
                    {goal.title}
                  </h3>

                  {subject && (
                    <div className="flex items-center gap-1.5 mt-1 text-xs text-academic-muted">
                      <span className="w-2 h-2 rounded-full" style={{ backgroundColor: subject.color }} />
                      <span>{subject.name}</span>
                    </div>
                  )}

                  {/* Progress Bar & Numerical indicators */}
                  <div className="mt-5 space-y-2">
                    <div className="flex items-center justify-between text-xs">
                      <span className="text-academic-muted font-medium">Progress</span>
                      <span className="font-bold text-academic-text">
                        {goal.currentValue} / {goal.targetValue} {goal.unit} ({percent}%)
                      </span>
                    </div>

                    <div className="w-full h-2.5 bg-academic-muted-bg rounded-full overflow-hidden border border-academic-border/50">
                      <div 
                        className={`h-full transition-all duration-300 rounded-full ${
                          goal.isCompleted || percent >= 100
                            ? 'bg-emerald-500'
                            : 'bg-academic-primary'
                        }`}
                        style={{ width: `${percent}%` }}
                      />
                    </div>
                  </div>

                  {/* Quick Increment Controls */}
                  {!goal.isCompleted && (
                    <div className="flex items-center gap-2 mt-4 pt-3 border-t border-academic-border">
                      <span className="text-[11px] text-academic-muted font-medium">Quick Log:</span>
                      <button
                        onClick={() => incrementGoalProgress(goal.id, 1)}
                        className="px-2.5 py-1 text-xs font-semibold rounded bg-academic-muted-bg text-academic-text hover:bg-academic-primary hover:text-white transition-colors border border-academic-border"
                      >
                        +1 {goal.unit}
                      </button>
                      <button
                        onClick={() => incrementGoalProgress(goal.id, 5)}
                        className="px-2.5 py-1 text-xs font-semibold rounded bg-academic-muted-bg text-academic-text hover:bg-academic-primary hover:text-white transition-colors border border-academic-border"
                      >
                        +5 {goal.unit}
                      </button>
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-between pt-4 mt-4 border-t border-academic-border text-xs text-academic-muted">
                  <span className="flex items-center gap-1 text-[11px]">
                    <Calendar className="w-3.5 h-3.5 text-academic-primary" />
                    {daysLeft < 0 ? 'Target date passed' : daysLeft === 0 ? 'Target date is Today' : `${daysLeft} days remaining`}
                  </span>

                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => openEditModal(goal)}
                      className="p-1 hover:text-academic-primary transition-colors"
                      title="Edit goal"
                    >
                      <Edit3 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => handleDeleteGoal(goal.id, goal.title)}
                      className="p-1 hover:text-red-600 transition-colors"
                      title="Delete goal"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Goal Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-academic-card w-full max-w-md rounded-xl border border-academic-border shadow-xl overflow-hidden animate-slide-up">
            <div className="flex items-center justify-between p-4 border-b border-academic-border">
              <h3 className="font-semibold text-academic-text">
                {editingGoal ? 'Edit Goal' : 'Formulate Academic Target'}
              </h3>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="text-academic-muted hover:text-academic-text p-1 rounded-lg hover:bg-academic-muted-bg transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveGoal} className="p-4 space-y-4">
              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Goal Title <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Complete 25 hours of focused study"
                  value={formTitle}
                  onChange={e => setFormTitle(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary font-medium"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Goal Type
                  </label>
                  <select
                    value={formType}
                    onChange={e => setFormType(e.target.value as GoalType)}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    <option value="daily_study">Daily Study</option>
                    <option value="weekly_study">Weekly Study</option>
                    <option value="monthly_study">Monthly Study</option>
                    <option value="subject_target">Subject Target</option>
                    <option value="assignment_completion">Assignments</option>
                    <option value="exam_prep">Exam Prep</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Subject (Optional)
                  </label>
                  <select
                    value={formSubjectId}
                    onChange={e => setFormSubjectId(e.target.value)}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    <option value="">All Subjects</option>
                    {subjects.map(s => (
                      <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Target
                  </label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={formTargetValue}
                    onChange={e => setFormTargetValue(Number(e.target.value))}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Current
                  </label>
                  <input
                    type="number"
                    min="0"
                    value={formCurrentValue}
                    onChange={e => setFormCurrentValue(Number(e.target.value))}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Unit
                  </label>
                  <input
                    type="text"
                    placeholder="hours, tasks"
                    value={formUnit}
                    onChange={e => setFormUnit(e.target.value)}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Target Due Date
                </label>
                <input
                  type="date"
                  required
                  value={formTargetDate}
                  onChange={e => setFormTargetDate(e.target.value)}
                  className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-academic-border">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-xs font-medium text-academic-muted hover:text-academic-text rounded-lg border border-academic-border hover:bg-academic-muted-bg transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-xs font-medium bg-academic-primary text-white rounded-lg hover:bg-academic-primary/90 transition-colors shadow-sm"
                >
                  {editingGoal ? 'Save Target' : 'Create Target'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
