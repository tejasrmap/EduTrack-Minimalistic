import React, { useState, useMemo } from 'react';
import { 
  Plus, 
  Search, 
  Kanban, 
  List, 
  Calendar, 
  CheckCircle2, 
  Circle, 
  Clock, 
  Trash2, 
  Edit3, 
  ArrowUpDown,
  X,
  CalendarDays
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { Task, TaskCategory, TaskPriority, TaskStatus } from '../../types';
import EmptyState from '../../components/common/EmptyState';
import { format, isPast, isToday, parseISO } from 'date-fns';

type ViewMode = 'list' | 'kanban' | 'calendar';

export const AssignmentsPage: React.FC = () => {
  const { tasks, subjects, addTask, updateTask, deleteTask, toggleTaskStatus } = useData();
  const { toast } = useToast();

  const [viewMode, setViewMode] = useState<ViewMode>('list');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedSubject, setSelectedSubject] = useState<string>('all');
  const [selectedPriority, setSelectedPriority] = useState<string>('all');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedStatus, setSelectedStatus] = useState<string>('all');
  const [sortBy, setSortBy] = useState<'dueDate' | 'priority' | 'title'>('dueDate');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);

  // Form State
  const [formTitle, setFormTitle] = useState('');
  const [formDescription, setFormDescription] = useState('');
  const [formSubjectId, setFormSubjectId] = useState('');
  const [formCategory, setFormCategory] = useState<TaskCategory>('assignment');
  const [formPriority, setFormPriority] = useState<TaskPriority>('medium');
  const [formDueDate, setFormDueDate] = useState(new Date().toISOString().split('T')[0]);
  const [formDueTime, setFormDueTime] = useState('23:59');
  const [formEstimatedMinutes, setFormEstimatedMinutes] = useState(60);

  const openCreateModal = () => {
    setEditingTask(null);
    setFormTitle('');
    setFormDescription('');
    setFormSubjectId(subjects[0]?.id || '');
    setFormCategory('assignment');
    setFormPriority('medium');
    setFormDueDate(new Date().toISOString().split('T')[0]);
    setFormDueTime('23:59');
    setFormEstimatedMinutes(60);
    setIsModalOpen(true);
  };

  const openEditModal = (task: Task) => {
    setEditingTask(task);
    setFormTitle(task.title);
    setFormDescription(task.description);
    setFormSubjectId(task.subjectId);
    setFormCategory(task.category);
    setFormPriority(task.priority);
    setFormDueDate(task.dueDate);
    setFormDueTime(task.dueTime || '23:59');
    setFormEstimatedMinutes(task.estimatedMinutes || 60);
    setIsModalOpen(true);
  };

  const handleSaveTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formTitle.trim()) {
      toast.error('Task title is required');
      return;
    }
    if (!formSubjectId) {
      toast.error('Please assign a subject');
      return;
    }

    if (editingTask) {
      updateTask(editingTask.id, {
        title: formTitle.trim(),
        description: formDescription.trim(),
        subjectId: formSubjectId,
        category: formCategory,
        priority: formPriority,
        dueDate: formDueDate,
        dueTime: formDueTime,
        estimatedMinutes: Number(formEstimatedMinutes) || 60
      });
      toast.success('Assignment updated successfully');
    } else {
      addTask({
        title: formTitle.trim(),
        description: formDescription.trim(),
        subjectId: formSubjectId,
        category: formCategory,
        priority: formPriority,
        status: 'not_started',
        dueDate: formDueDate,
        dueTime: formDueTime,
        estimatedMinutes: Number(formEstimatedMinutes) || 60
      });
      toast.success('Assignment created successfully');
    }
    setIsModalOpen(false);
  };

  const handleDeleteTask = (id: string, title: string) => {
    if (window.confirm(`Delete task "${title}"?`)) {
      deleteTask(id);
      toast.info('Task removed');
    }
  };

  // Filtered & Sorted Tasks
  const filteredTasks = useMemo(() => {
    return tasks.filter(t => {
      if (searchQuery) {
        const query = searchQuery.toLowerCase();
        const matchesTitle = t.title.toLowerCase().includes(query);
        const matchesDesc = t.description.toLowerCase().includes(query);
        if (!matchesTitle && !matchesDesc) return false;
      }
      if (selectedSubject !== 'all' && t.subjectId !== selectedSubject) return false;
      if (selectedPriority !== 'all' && t.priority !== selectedPriority) return false;
      if (selectedCategory !== 'all' && t.category !== selectedCategory) return false;
      if (selectedStatus !== 'all' && t.status !== selectedStatus) return false;
      return true;
    }).sort((a, b) => {
      if (sortBy === 'dueDate') {
        return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
      }
      if (sortBy === 'priority') {
        const pOrder: Record<TaskPriority, number> = { high: 3, medium: 2, low: 1 };
        return pOrder[b.priority] - pOrder[a.priority];
      }
      return a.title.localeCompare(b.title);
    });
  }, [tasks, searchQuery, selectedSubject, selectedPriority, selectedCategory, selectedStatus, sortBy]);

  const priorityBadge = (priority: TaskPriority) => {
    switch (priority) {
      case 'high':
        return <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-red-100 text-red-700 dark:bg-red-950/40 dark:text-red-400">High</span>;
      case 'medium':
        return <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-amber-100 text-amber-700 dark:bg-amber-950/40 dark:text-amber-400">Medium</span>;
      case 'low':
        return <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300">Low</span>;
    }
  };

  const getSubject = (id: string) => subjects.find(s => s.id === id);

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-academic-text tracking-tight">Assignments & Tasks</h1>
          <p className="text-sm text-academic-muted mt-1">
            Organise course deliverables, homework, readings and examination preparations.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {/* View Mode Toggle */}
          <div className="flex items-center bg-academic-muted-bg p-1 rounded-lg border border-academic-border">
            <button
              onClick={() => setViewMode('list')}
              className={`p-1.5 rounded-md text-xs font-medium transition-colors ${
                viewMode === 'list' 
                  ? 'bg-academic-card text-academic-primary shadow-sm' 
                  : 'text-academic-muted hover:text-academic-text'
              }`}
              title="List View"
            >
              <List className="w-4 h-4" />
            </button>
            <button
              onClick={() => setViewMode('kanban')}
              className={`p-1.5 rounded-md text-xs font-medium transition-colors ${
                viewMode === 'kanban' 
                  ? 'bg-academic-card text-academic-primary shadow-sm' 
                  : 'text-academic-muted hover:text-academic-text'
              }`}
              title="Kanban Board"
            >
              <Kanban className="w-4 h-4" />
            </button>
            <button
              onClick={() => setViewMode('calendar')}
              className={`p-1.5 rounded-md text-xs font-medium transition-colors ${
                viewMode === 'calendar' 
                  ? 'bg-academic-card text-academic-primary shadow-sm' 
                  : 'text-academic-muted hover:text-academic-text'
              }`}
              title="Deadline Calendar"
            >
              <Calendar className="w-4 h-4" />
            </button>
          </div>

          <button
            onClick={openCreateModal}
            className="flex items-center gap-2 px-4 py-2 bg-academic-primary text-white rounded-lg text-sm font-medium hover:bg-academic-primary/90 transition-colors shadow-sm"
          >
            <Plus className="w-4 h-4" />
            <span>Add Task</span>
          </button>
        </div>
      </div>

      {/* Filter & Search Bar */}
      <div className="bg-academic-card p-4 rounded-xl border border-academic-border space-y-3">
        <div className="flex flex-col md:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-academic-muted" />
            <input
              type="text"
              placeholder="Search tasks, deliverables, descriptions..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="w-full pl-9 pr-4 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            />
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <select
              value={selectedSubject}
              onChange={e => setSelectedSubject(e.target.value)}
              className="text-xs bg-academic-muted-bg border border-academic-border rounded-lg px-3 py-2 text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            >
              <option value="all">All Subjects</option>
              {subjects.map(s => (
                <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
              ))}
            </select>

            <select
              value={selectedPriority}
              onChange={e => setSelectedPriority(e.target.value)}
              className="text-xs bg-academic-muted-bg border border-academic-border rounded-lg px-3 py-2 text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            >
              <option value="all">All Priorities</option>
              <option value="high">High Priority</option>
              <option value="medium">Medium Priority</option>
              <option value="low">Low Priority</option>
            </select>

            <select
              value={selectedCategory}
              onChange={e => setSelectedCategory(e.target.value)}
              className="text-xs bg-academic-muted-bg border border-academic-border rounded-lg px-3 py-2 text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            >
              <option value="all">All Categories</option>
              <option value="assignment">Assignment</option>
              <option value="homework">Homework</option>
              <option value="revision">Revision</option>
              <option value="project">Project</option>
              <option value="reading">Reading</option>
              <option value="exam_prep">Exam Prep</option>
              <option value="other">Other</option>
            </select>

            {viewMode === 'list' && (
              <select
                value={selectedStatus}
                onChange={e => setSelectedStatus(e.target.value)}
                className="text-xs bg-academic-muted-bg border border-academic-border rounded-lg px-3 py-2 text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              >
                <option value="all">All Statuses</option>
                <option value="not_started">To Do</option>
                <option value="in_progress">In Progress</option>
                <option value="completed">Completed</option>
              </select>
            )}

            <div className="flex items-center gap-1.5 ml-auto text-xs text-academic-muted">
              <ArrowUpDown className="w-3.5 h-3.5" />
              <select
                value={sortBy}
                onChange={e => setSortBy(e.target.value as any)}
                className="text-xs bg-transparent border-0 text-academic-text font-medium focus:outline-none cursor-pointer"
              >
                <option value="dueDate">Due Date</option>
                <option value="priority">Priority</option>
                <option value="title">Title</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content by View Mode */}
      {viewMode === 'list' && (
        <div className="bg-academic-card rounded-xl border border-academic-border overflow-hidden">
          {filteredTasks.length === 0 ? (
            <EmptyState
              title="No tasks found"
              description="No tasks match your current filters. Add a new assignment to keep track of deadlines."
              actionLabel="Add Task"
              onAction={openCreateModal}
            />
          ) : (
            <div className="divide-y divide-academic-border">
              {filteredTasks.map(task => {
                const subject = getSubject(task.subjectId);
                const isOverdue = !task.completedAt && isPast(parseISO(task.dueDate)) && !isToday(parseISO(task.dueDate));
                const isDueToday = isToday(parseISO(task.dueDate));

                return (
                  <div 
                    key={task.id}
                    className="p-4 flex items-center justify-between gap-4 hover:bg-academic-muted-bg/50 transition-colors"
                  >
                    <div className="flex items-start gap-3 min-w-0">
                      <button
                        onClick={() => toggleTaskStatus(task.id)}
                        className="mt-0.5 text-academic-muted hover:text-academic-primary transition-colors flex-shrink-0"
                      >
                        {task.status === 'completed' ? (
                          <CheckCircle2 className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
                        ) : (
                          <Circle className="w-5 h-5" />
                        )}
                      </button>

                      <div className="min-w-0">
                        <div className="flex items-center gap-2 flex-wrap">
                          <span className={`text-sm font-medium ${
                            task.status === 'completed' 
                              ? 'line-through text-academic-muted' 
                              : 'text-academic-text'
                          }`}>
                            {task.title}
                          </span>
                          {priorityBadge(task.priority)}
                          <span className="text-[11px] uppercase tracking-wider px-2 py-0.5 rounded bg-academic-muted-bg text-academic-muted font-medium">
                            {task.category.replace('_', ' ')}
                          </span>
                        </div>

                        {task.description && (
                          <p className="text-xs text-academic-muted mt-1 truncate max-w-xl">
                            {task.description}
                          </p>
                        )}

                        <div className="flex items-center gap-3 mt-2 text-xs text-academic-muted">
                          {subject && (
                            <span className="flex items-center gap-1 font-medium text-academic-text">
                              <span 
                                className="w-2 h-2 rounded-full" 
                                style={{ backgroundColor: subject.color }}
                              />
                              {subject.code}
                            </span>
                          )}

                          <span className={`flex items-center gap-1 ${
                            isOverdue 
                              ? 'text-red-600 dark:text-red-400 font-semibold' 
                              : isDueToday 
                              ? 'text-amber-600 dark:text-amber-400 font-medium' 
                              : ''
                          }`}>
                            <CalendarDays className="w-3.5 h-3.5" />
                            {isDueToday ? 'Due Today' : isOverdue ? `Overdue (${format(parseISO(task.dueDate), 'MMM d')})` : format(parseISO(task.dueDate), 'MMM d, yyyy')}
                          </span>

                          {task.estimatedMinutes && (
                            <span className="flex items-center gap-1">
                              <Clock className="w-3.5 h-3.5" />
                              {task.estimatedMinutes}m est.
                            </span>
                          )}
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center gap-1 flex-shrink-0">
                      <button
                        onClick={() => openEditModal(task)}
                        className="p-1.5 text-academic-muted hover:text-academic-primary rounded-lg hover:bg-academic-muted-bg transition-colors"
                        title="Edit task"
                      >
                        <Edit3 className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleDeleteTask(task.id, task.title)}
                        className="p-1.5 text-academic-muted hover:text-red-600 rounded-lg hover:bg-academic-muted-bg transition-colors"
                        title="Delete task"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* Kanban Board View */}
      {viewMode === 'kanban' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          {(['not_started', 'in_progress', 'completed'] as TaskStatus[]).map(columnStatus => {
            const columnTasks = filteredTasks.filter(t => t.status === columnStatus);
            const columnTitle = columnStatus === 'not_started' ? 'To Do' : columnStatus === 'in_progress' ? 'In Progress' : 'Completed';

            return (
              <div key={columnStatus} className="bg-academic-muted-bg/60 p-4 rounded-xl border border-academic-border flex flex-col min-h-[500px]">
                <div className="flex items-center justify-between mb-3 pb-2 border-b border-academic-border">
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-sm text-academic-text">{columnTitle}</span>
                    <span className="text-xs px-2 py-0.5 rounded-full bg-academic-card font-medium text-academic-muted border border-academic-border">
                      {columnTasks.length}
                    </span>
                  </div>
                  {columnStatus === 'not_started' && (
                    <button 
                      onClick={openCreateModal}
                      className="p-1 text-academic-muted hover:text-academic-primary rounded hover:bg-academic-card transition-colors"
                    >
                      <Plus className="w-4 h-4" />
                    </button>
                  )}
                </div>

                <div className="space-y-3 flex-1 overflow-y-auto">
                  {columnTasks.map(task => {
                    const subject = getSubject(task.subjectId);
                    return (
                      <div 
                        key={task.id}
                        className="bg-academic-card p-3.5 rounded-xl border border-academic-border shadow-sm hover:border-academic-primary/50 transition-all group"
                      >
                        <div className="flex items-start justify-between gap-2">
                          <span className="text-xs font-semibold px-2 py-0.5 rounded" style={{
                            backgroundColor: subject ? `${subject.color}15` : undefined,
                            color: subject?.color
                          }}>
                            {subject?.code || 'General'}
                          </span>
                          {priorityBadge(task.priority)}
                        </div>

                        <h4 className="text-sm font-medium text-academic-text mt-2 leading-snug">
                          {task.title}
                        </h4>

                        {task.description && (
                          <p className="text-xs text-academic-muted mt-1 line-clamp-2">
                            {task.description}
                          </p>
                        )}

                        <div className="flex items-center justify-between mt-3 pt-2 border-t border-academic-border/60 text-xs text-academic-muted">
                          <span className="flex items-center gap-1">
                            <Clock className="w-3.5 h-3.5" />
                            {format(parseISO(task.dueDate), 'MMM d')}
                          </span>

                          <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                            <button
                              onClick={() => openEditModal(task)}
                              className="p-1 hover:text-academic-primary"
                            >
                              <Edit3 className="w-3.5 h-3.5" />
                            </button>
                            <button
                              onClick={() => toggleTaskStatus(task.id)}
                              className="p-1 hover:text-emerald-600"
                              title="Toggle status"
                            >
                              <CheckCircle2 className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        </div>
                      </div>
                    );
                  })}

                  {columnTasks.length === 0 && (
                    <div className="h-32 flex items-center justify-center border-2 border-dashed border-academic-border/60 rounded-xl text-xs text-academic-muted">
                      No tasks
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Calendar Deadline View */}
      {viewMode === 'calendar' && (
        <div className="bg-academic-card p-5 rounded-xl border border-academic-border">
          <h3 className="font-semibold text-sm text-academic-text mb-4 flex items-center gap-2">
            <Calendar className="w-4 h-4 text-academic-primary" />
            Upcoming Deliverables Schedule
          </h3>
          <div className="space-y-4">
            {filteredTasks.map(task => {
              const subject = getSubject(task.subjectId);
              return (
                <div 
                  key={task.id}
                  className="flex items-center gap-4 p-3 rounded-lg border border-academic-border hover:bg-academic-muted-bg/50 transition-colors"
                >
                  <div className="text-center w-14 flex-shrink-0 py-1 bg-academic-muted-bg rounded-lg border border-academic-border">
                    <span className="block text-[10px] font-bold uppercase text-academic-primary">
                      {format(parseISO(task.dueDate), 'MMM')}
                    </span>
                    <span className="block text-base font-bold text-academic-text">
                      {format(parseISO(task.dueDate), 'd')}
                    </span>
                  </div>

                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-medium text-academic-text truncate">
                        {task.title}
                      </span>
                      {priorityBadge(task.priority)}
                    </div>
                    <p className="text-xs text-academic-muted mt-0.5">
                      {subject?.name} • Due at {task.dueTime || '23:59'}
                    </p>
                  </div>

                  <button
                    onClick={() => toggleTaskStatus(task.id)}
                    className="flex-shrink-0 px-3 py-1.5 rounded-lg border border-academic-border text-xs font-medium hover:bg-academic-muted-bg transition-colors"
                  >
                    {task.status === 'completed' ? 'Completed' : 'Mark Done'}
                  </button>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Create / Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-academic-card w-full max-w-lg rounded-xl border border-academic-border shadow-xl overflow-hidden animate-slide-up">
            <div className="flex items-center justify-between p-4 border-b border-academic-border">
              <h3 className="font-semibold text-academic-text">
                {editingTask ? 'Edit Task' : 'Create New Assignment'}
              </h3>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="text-academic-muted hover:text-academic-text p-1 rounded-lg hover:bg-academic-muted-bg transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveTask} className="p-4 space-y-4">
              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Title <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Lab 4: Binary Search Trees"
                  value={formTitle}
                  onChange={e => setFormTitle(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Subject <span className="text-red-500">*</span>
                  </label>
                  <select
                    value={formSubjectId}
                    onChange={e => setFormSubjectId(e.target.value)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    {subjects.map(s => (
                      <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Category
                  </label>
                  <select
                    value={formCategory}
                    onChange={e => setFormCategory(e.target.value as TaskCategory)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    <option value="assignment">Assignment</option>
                    <option value="homework">Homework</option>
                    <option value="revision">Revision</option>
                    <option value="project">Project</option>
                    <option value="reading">Reading</option>
                    <option value="exam_prep">Exam Prep</option>
                    <option value="other">Other</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Priority
                  </label>
                  <select
                    value={formPriority}
                    onChange={e => setFormPriority(e.target.value as TaskPriority)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    <option value="low">Low</option>
                    <option value="medium">Medium</option>
                    <option value="high">High</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Due Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formDueDate}
                    onChange={e => setFormDueDate(e.target.value)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Due Time
                  </label>
                  <input
                    type="time"
                    value={formDueTime}
                    onChange={e => setFormDueTime(e.target.value)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Estimated Minutes
                </label>
                <input
                  type="number"
                  min="15"
                  step="15"
                  value={formEstimatedMinutes}
                  onChange={e => setFormEstimatedMinutes(Number(e.target.value))}
                  className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Description / Notes
                </label>
                <textarea
                  rows={3}
                  placeholder="Additional instructions, reference links or grading criteria..."
                  value={formDescription}
                  onChange={e => setFormDescription(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
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
                  {editingTask ? 'Save Changes' : 'Create Task'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
