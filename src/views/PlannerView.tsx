import React, { useState } from 'react';
import { 
  Calendar, 
  Plus, 
  CheckCircle2, 
  Circle, 
  Edit2, 
  Trash2, 
  Filter, 
  X, 
  List, 
  CalendarDays 
} from 'lucide-react';
import { useAcademic } from '../context/AppContext';
import { AcademicTask, AcademicTaskPriority, AcademicTaskStatus } from '../types';

export const PlannerView: React.FC = () => {
  const { subjects, tasks, addTask, updateTask, toggleTaskStatus, deleteTask } = useAcademic();

  const [viewMode, setViewMode] = useState<'list' | 'calendar'>('list');
  const [statusFilter, setStatusFilter] = useState<'all' | AcademicTaskStatus>('all');
  const [subjectFilter, setSubjectFilter] = useState<string>('all');
  const [priorityFilter, setPriorityFilter] = useState<string>('all');

  // Modals
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<AcademicTask | null>(null);

  // Form
  const [title, setTitle] = useState('');
  const [subjectId, setSubjectId] = useState(subjects[0]?.id || '');
  const [dueDate, setDueDate] = useState(new Date().toISOString().split('T')[0]);
  const [priority, setPriority] = useState<AcademicTaskPriority>('standard');
  const [estimatedHours, setEstimatedHours] = useState(2);
  const [readingNotes, setReadingNotes] = useState('');

  const handleOpenAdd = () => {
    setEditingTask(null);
    setTitle('');
    setSubjectId(subjects[0]?.id || '');
    setDueDate(new Date().toISOString().split('T')[0]);
    setPriority('standard');
    setEstimatedHours(2);
    setReadingNotes('');
    setIsModalOpen(true);
  };

  const handleOpenEdit = (t: AcademicTask) => {
    setEditingTask(t);
    setTitle(t.title);
    setSubjectId(t.subjectId);
    setDueDate(t.dueDate);
    setPriority(t.priority);
    setEstimatedHours(t.estimatedHours);
    setReadingNotes(t.readingNotes || '');
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    if (editingTask) {
      updateTask(editingTask.id, {
        title,
        subjectId,
        dueDate,
        priority,
        estimatedHours: Number(estimatedHours),
        readingNotes,
      });
    } else {
      addTask({
        title,
        subjectId,
        dueDate,
        priority,
        status: 'pending',
        estimatedHours: Number(estimatedHours),
        readingNotes,
      });
    }
    setIsModalOpen(false);
  };

  const filtered = tasks.filter(t => {
    if (statusFilter !== 'all' && t.status !== statusFilter) return false;
    if (subjectFilter !== 'all' && t.subjectId !== subjectFilter) return false;
    if (priorityFilter !== 'all' && t.priority !== priorityFilter) return false;
    return true;
  });

  const weekDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="paper-card rounded-2xl p-5 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="font-serif text-xl font-bold text-stone-900">
            Study Planner & Timetable
          </h2>
          <p className="text-xs text-stone-500">
            {tasks.filter(t => t.status !== 'completed').length} pending assignments · {tasks.filter(t => t.status === 'completed').length} completed
          </p>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex bg-stone-100 rounded-xl p-1 border border-stone-200">
            <button
              onClick={() => setViewMode('list')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                viewMode === 'list' ? 'bg-white text-stone-900 shadow-xs' : 'text-stone-500 hover:text-stone-800'
              }`}
            >
              <List className="w-3.5 h-3.5" />
              <span>List View</span>
            </button>
            <button
              onClick={() => setViewMode('calendar')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                viewMode === 'calendar' ? 'bg-white text-stone-900 shadow-xs' : 'text-stone-500 hover:text-stone-800'
              }`}
            >
              <CalendarDays className="w-3.5 h-3.5" />
              <span>Timetable</span>
            </button>
          </div>

          <button
            onClick={handleOpenAdd}
            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors shadow-xs"
          >
            <Plus className="w-4 h-4" />
            <span>Add Task</span>
          </button>
        </div>
      </div>

      {/* Filter Toolbar */}
      <div className="paper-card rounded-xl p-3.5 flex flex-wrap items-center gap-3 text-xs">
        <div className="flex items-center gap-1.5 text-stone-500 font-mono text-[11px]">
          <Filter className="w-3.5 h-3.5" />
          <span>FILTER:</span>
        </div>

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value as unknown as AcademicTaskStatus | 'all')}
          className="bg-stone-50 border border-stone-200 rounded-lg px-2.5 py-1 text-stone-700 focus:outline-none focus:border-[#2A5C43]"
        >
          <option value="all">All Statuses</option>
          <option value="pending">Pending</option>
          <option value="in-progress">In Progress</option>
          <option value="completed">Completed</option>
        </select>

        <select
          value={subjectFilter}
          onChange={(e) => setSubjectFilter(e.target.value)}
          className="bg-stone-50 border border-stone-200 rounded-lg px-2.5 py-1 text-stone-700 focus:outline-none focus:border-[#2A5C43]"
        >
          <option value="all">All Subjects</option>
          {subjects.map(s => (
            <option key={s.id} value={s.id}>{s.code} · {s.title}</option>
          ))}
        </select>

        <select
          value={priorityFilter}
          onChange={(e) => setPriorityFilter(e.target.value)}
          className="bg-stone-50 border border-stone-200 rounded-lg px-2.5 py-1 text-stone-700 focus:outline-none focus:border-[#2A5C43]"
        >
          <option value="all">All Priorities</option>
          <option value="essential">Essential (High)</option>
          <option value="standard">Standard</option>
          <option value="supplemental">Supplemental</option>
        </select>

        {(statusFilter !== 'all' || subjectFilter !== 'all' || priorityFilter !== 'all') && (
          <button
            onClick={() => {
              setStatusFilter('all');
              setSubjectFilter('all');
              setPriorityFilter('all');
            }}
            className="text-[#2A5C43] hover:underline text-[11px] ml-auto"
          >
            Reset Filters
          </button>
        )}
      </div>

      {/* Main Content Area */}
      {viewMode === 'list' ? (
        <div className="space-y-3">
          {filtered.length === 0 ? (
            <div className="paper-card rounded-2xl p-12 text-center">
              <Calendar className="w-10 h-10 text-stone-300 mx-auto mb-2" />
              <h3 className="font-serif text-base font-bold text-stone-800">No tasks found</h3>
              <p className="text-xs text-stone-500 mt-1">Adjust filters or create a new assignment.</p>
            </div>
          ) : (
            filtered.map((t) => {
              const sub = subjects.find(s => s.id === t.subjectId);
              const isDone = t.status === 'completed';

              return (
                <div
                  key={t.id}
                  className={`paper-card rounded-xl p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 transition-colors ${
                    isDone ? 'opacity-50 bg-stone-50' : 'hover:border-stone-300'
                  }`}
                >
                  <div className="flex items-start sm:items-center gap-3.5 min-w-0 flex-1">
                    <button
                      onClick={() => toggleTaskStatus(t.id)}
                      className="mt-0.5 sm:mt-0 text-stone-400 hover:text-[#2A5C43] transition-colors flex-shrink-0"
                    >
                      {isDone ? (
                        <CheckCircle2 className="w-5 h-5 text-[#2A5C43]" />
                      ) : (
                        <Circle className="w-5 h-5" />
                      )}
                    </button>

                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2 flex-wrap">
                        <h4 className={`text-sm font-semibold text-stone-900 ${isDone ? 'line-through text-stone-400' : ''}`}>
                          {t.title}
                        </h4>
                        <span className={`text-[10px] font-mono px-2 py-0.5 rounded capitalize ${
                          t.priority === 'essential' ? 'bg-amber-100 text-amber-800' :
                          t.priority === 'standard' ? 'bg-blue-50 text-blue-800' :
                          'bg-stone-100 text-stone-600'
                        }`}>
                          {t.priority}
                        </span>
                      </div>

                      {t.readingNotes && (
                        <p className="text-xs text-stone-600 mt-1 line-clamp-1 italic">
                          {t.readingNotes}
                        </p>
                      )}

                      <div className="flex items-center gap-4 mt-2 text-[11px] text-stone-500 font-mono">
                        {sub && (
                          <span style={{ color: sub.pastelColor }} className="font-semibold">
                            {sub.code} · {sub.title}
                          </span>
                        )}
                        <span>Due {t.dueDate}</span>
                        <span>~{t.estimatedHours} hrs</span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-1 self-end sm:self-center">
                    <button
                      onClick={() => handleOpenEdit(t)}
                      className="p-1.5 rounded-md text-stone-400 hover:text-stone-800 hover:bg-stone-100"
                    >
                      <Edit2 className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => deleteTask(t.id)}
                      className="p-1.5 rounded-md text-stone-400 hover:text-rose-600 hover:bg-rose-50"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              );
            })
          )}
        </div>
      ) : (
        /* Calendar Timetable */
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-7 gap-3">
          {weekDays.map((day, idx) => (
            <div key={day} className="paper-card rounded-xl p-3 flex flex-col min-h-[260px]">
              <div className="pb-2 mb-2 border-b border-stone-200 flex items-center justify-between">
                <span className="font-serif font-bold text-xs text-stone-800">
                  {day.substring(0, 3)}
                </span>
                <span className="text-[10px] text-stone-400 font-mono">Slot {idx + 1}</span>
              </div>

              <div className="space-y-2 flex-1">
                {tasks.slice(idx, idx + 2).map((t) => {
                  const sub = subjects.find(s => s.id === t.subjectId);
                  return (
                    <div 
                      key={t.id} 
                      className="p-2 rounded-lg bg-stone-50 border border-stone-200 text-xs space-y-1"
                    >
                      <span className="text-[10px] font-mono font-bold text-[#2A5C43]">
                        {sub?.code || 'ACAD'}
                      </span>
                      <p className="text-[11px] text-stone-800 line-clamp-2">{t.title}</p>
                    </div>
                  );
                })}
              </div>

              <button
                onClick={handleOpenAdd}
                className="w-full mt-2 py-1 rounded border border-dashed border-stone-200 text-[10px] text-stone-400 hover:text-stone-700"
              >
                + Add Block
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Task Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-stone-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white w-full max-w-lg rounded-2xl p-6 shadow-xl border border-stone-200 animate-modalIn">
            <div className="flex items-center justify-between pb-3 border-b border-stone-200">
              <h3 className="font-serif text-lg font-bold text-stone-900">
                {editingTask ? 'Edit Assignment' : 'Add Study Assignment'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="p-1 text-stone-400 hover:text-stone-700">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="mt-4 space-y-4">
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Task Title *
                </label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="e.g. Read chapters 3-4 of Vitruvius Treatise"
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Academic Subject *
                  </label>
                  <select
                    value={subjectId}
                    onChange={(e) => setSubjectId(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  >
                    {subjects.map(s => (
                      <option key={s.id} value={s.id}>{s.code} · {s.title}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Due Date *
                  </label>
                  <input
                    type="date"
                    required
                    value={dueDate}
                    onChange={(e) => setDueDate(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Priority Tier
                  </label>
                  <select
                    value={priority}
                    onChange={(e) => setPriority(e.target.value as AcademicTaskPriority)}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  >
                    <option value="essential">Essential (Urgent)</option>
                    <option value="standard">Standard</option>
                    <option value="supplemental">Supplemental Reading</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Estimated Time (Hours)
                  </label>
                  <input
                    type="number"
                    step="0.5"
                    min="0.5"
                    max="40"
                    value={estimatedHours}
                    onChange={(e) => setEstimatedHours(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Reading Notes / Citations
                </label>
                <textarea
                  rows={2}
                  value={readingNotes}
                  onChange={(e) => setReadingNotes(e.target.value)}
                  placeholder="Key questions, textbook page numbers, seminar discussion points..."
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-stone-200">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-xs text-stone-500 hover:text-stone-800"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors"
                >
                  {editingTask ? 'Save Assignment' : 'Add to Schedule'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
