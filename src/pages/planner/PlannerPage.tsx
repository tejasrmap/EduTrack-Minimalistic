import React, { useState } from 'react';
import {
  CalendarDays,
  ChevronLeft,
  ChevronRight,
  Plus,
  Clock,
  CheckCircle2,
  AlertTriangle,
  Trash2,
  Edit3,
  X,
  Bell,
  Play
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useTimer } from '../../contexts/TimerContext';
import { useToast } from '../../contexts/ToastContext';
import { ScheduleEvent } from '../../types';

export const PlannerPage: React.FC = () => {
  const { subjects, scheduleEvents, addScheduleEvent, updateScheduleEvent, deleteScheduleEvent, toggleScheduleEventComplete } = useData();
  const { switchMode, setSubjectId, startTimer } = useTimer();
  const { toast } = useToast();

  const [currentView, setCurrentView] = useState<'daily' | 'weekly' | 'monthly'>('weekly');
  const [selectedDate, setSelectedDate] = useState(() => new Date());
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingEvent, setEditingEvent] = useState<ScheduleEvent | null>(null);

  // Form states
  const [title, setTitle] = useState('');
  const [subjectId, setSubjectIdState] = useState(subjects[0]?.id || '');
  const [date, setDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [startTime, setStartTime] = useState('10:00');
  const [endTime, setEndTime] = useState('12:00');
  const [description, setDescription] = useState('');
  const [reminder, setReminder] = useState(true);
  const [overlapWarning, setOverlapWarning] = useState<string | null>(null);

  // Date navigation helpers
  const handlePrev = () => {
    const next = new Date(selectedDate);
    if (currentView === 'daily') next.setDate(next.getDate() - 1);
    else if (currentView === 'weekly') next.setDate(next.getDate() - 7);
    else next.setMonth(next.getMonth() - 1);
    setSelectedDate(next);
  };

  const handleNext = () => {
    const next = new Date(selectedDate);
    if (currentView === 'daily') next.setDate(next.getDate() + 1);
    else if (currentView === 'weekly') next.setDate(next.getDate() + 7);
    else next.setMonth(next.getMonth() + 1);
    setSelectedDate(next);
  };

  const handleToday = () => setSelectedDate(new Date());

  // Check overlap helper
  const checkOverlap = (checkDate: string, start: string, end: string, currentId?: string): boolean => {
    const sameDayEvents = scheduleEvents.filter(e => e.date === checkDate && e.id !== currentId);
    return sameDayEvents.some(e => {
      return (start < e.endTime && e.startTime < end);
    });
  };

  const handleOpenAdd = () => {
    setEditingEvent(null);
    setTitle('');
    setSubjectIdState(subjects[0]?.id || '');
    setDate(selectedDate.toISOString().split('T')[0]);
    setStartTime('10:00');
    setEndTime('11:30');
    setDescription('');
    setReminder(true);
    setOverlapWarning(null);
    setIsModalOpen(true);
  };

  const handleOpenEdit = (evt: ScheduleEvent) => {
    setEditingEvent(evt);
    setTitle(evt.title);
    setSubjectIdState(evt.subjectId);
    setDate(evt.date);
    setStartTime(evt.startTime);
    setEndTime(evt.endTime);
    setDescription(evt.description || '');
    setReminder(evt.reminder);
    setOverlapWarning(null);
    setIsModalOpen(true);
  };

  const handleTimeChange = (newStart: string, newEnd: string, newDate: string) => {
    setStartTime(newStart);
    setEndTime(newEnd);
    setDate(newDate);

    if (newStart >= newEnd) {
      setOverlapWarning('End time must be after start time.');
      return;
    }

    const hasOverlap = checkOverlap(newDate, newStart, newEnd, editingEvent?.id);
    if (hasOverlap) {
      setOverlapWarning('Notice: This block overlaps with another scheduled session.');
    } else {
      setOverlapWarning(null);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !startTime || !endTime) return;
    if (startTime >= endTime) {
      toast.error('End time must be after start time.');
      return;
    }

    if (editingEvent) {
      updateScheduleEvent(editingEvent.id, {
        title: title.trim(),
        subjectId,
        date,
        startTime,
        endTime,
        description: description.trim(),
        reminder,
      });
      toast.success('Schedule updated.');
    } else {
      addScheduleEvent({
        title: title.trim(),
        subjectId,
        date,
        startTime,
        endTime,
        description: description.trim(),
        reminder,
        isCompleted: false,
      });
      toast.success('Study session scheduled.');
    }

    setIsModalOpen(false);
  };

  // Week days calculation
  const getWeekDates = (baseDate: Date) => {
    const current = new Date(baseDate);
    const day = current.getDay();
    const diff = current.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
    const monday = new Date(current.setDate(diff));

    const week = [];
    for (let i = 0; i < 7; i++) {
      const d = new Date(monday);
      d.setDate(monday.getDate() + i);
      week.push(d);
    }
    return week;
  };

  const weekDays = getWeekDates(selectedDate);
  const selectedDateStr = selectedDate.toISOString().split('T')[0];

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Title & Calendar Controls */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-slate-border dark:border-slate-800">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
            Study Planner
          </h1>
          <p className="text-xs sm:text-sm text-slate-secondary mt-0.5">
            Organize daily focus blocks, lectures, tutorials, and detect overlapping schedules
          </p>
        </div>

        <div className="flex items-center gap-2.5 self-start sm:self-auto">
          {/* View Toggles */}
          <div className="flex bg-slate-muted dark:bg-slate-800 p-0.5 rounded-academic border border-slate-border dark:border-slate-700 text-xs">
            {(['daily', 'weekly', 'monthly'] as const).map(v => (
              <button
                key={v}
                onClick={() => setCurrentView(v)}
                className={`px-3 py-1.5 rounded-academic capitalize font-medium transition-colors ${currentView === v
                  ? 'bg-white dark:bg-slate-700 text-primary font-bold shadow-sm'
                  : 'text-slate-secondary hover:text-slate-primary dark:hover:text-white'
                  }`}
              >
                {v}
              </button>
            ))}
          </div>

          <button
            onClick={handleOpenAdd}
            className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-academic bg-primary text-white text-xs sm:text-sm font-semibold hover:bg-primary-hover shadow-sm transition-colors"
          >
            <Plus className="w-4 h-4" />
            <span>Schedule</span>
          </button>
        </div>
      </div>

      {/* Date Header Bar */}
      <div className="card-academic p-3.5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <button
            onClick={handlePrev}
            className="p-1.5 rounded-academic border border-slate-border dark:border-slate-700 hover:bg-slate-muted dark:hover:bg-slate-700 text-slate-secondary"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          <button
            onClick={handleToday}
            className="px-2.5 py-1 text-xs rounded-academic border border-slate-border dark:border-slate-700 font-semibold text-slate-primary dark:text-white hover:bg-slate-muted dark:hover:bg-slate-700"
          >
            Today
          </button>
          <button
            onClick={handleNext}
            className="p-1.5 rounded-academic border border-slate-border dark:border-slate-700 hover:bg-slate-muted dark:hover:bg-slate-700 text-slate-secondary"
          >
            <ChevronRight className="w-4 h-4" />
          </button>

          <span className="text-xs sm:text-sm font-bold text-slate-primary dark:text-white ml-2">
            {selectedDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
          </span>
        </div>

        <span className="text-xs text-slate-secondary hidden sm:inline">
          {scheduleEvents.filter(e => !e.isCompleted).length} active scheduled sessions
        </span>
      </div>

      {/* View Renderers */}
      {currentView === 'weekly' && (
        <div className="grid grid-cols-1 md:grid-cols-7 gap-3 select-none">
          {weekDays.map(dayObj => {
            const dayStr = dayObj.toISOString().split('T')[0];
            const isToday = dayStr === new Date().toISOString().split('T')[0];
            const dayEvents = scheduleEvents
              .filter(e => e.date === dayStr)
              .sort((a, b) => a.startTime.localeCompare(b.startTime));

            return (
              <div
                key={dayStr}
                className={`card-academic p-3 min-h-[360px] flex flex-col bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 ${isToday ? 'ring-2 ring-primary/40' : ''
                  }`}
              >
                {/* Column Day Header */}
                <div className="pb-2 mb-2 border-b border-slate-border dark:border-slate-700 text-center">
                  <div className="text-[10px] uppercase font-bold text-slate-secondary">
                    {dayObj.toLocaleDateString('en-US', { weekday: 'short' })}
                  </div>
                  <div className={`text-base font-bold font-mono mt-0.5 ${isToday ? 'text-primary' : 'text-slate-primary dark:text-white'}`}>
                    {dayObj.getDate()}
                  </div>
                </div>

                {/* Day events stack */}
                <div className="flex-1 space-y-2 overflow-y-auto">
                  {dayEvents.length === 0 ? (
                    <div className="h-full flex items-center justify-center text-[10px] text-slate-secondary italic">
                      No blocks
                    </div>
                  ) : (
                    dayEvents.map(evt => {
                      const sub = subjects.find(s => s.id === evt.subjectId);
                      return (
                        <div
                          key={evt.id}
                          className={`p-2.5 rounded-academic border text-xs space-y-1.5 transition-all ${evt.isCompleted
                            ? 'bg-slate-muted/50 border-slate-200 dark:border-slate-700 opacity-60'
                            : 'bg-white dark:bg-slate-700 border-slate-border dark:border-slate-600 hover:shadow-subtle'
                            }`}
                        >
                          <div className="flex items-center justify-between">
                            <span
                              className="px-1.5 py-0.2 rounded text-[9px] font-mono font-bold"
                              style={{ backgroundColor: `${sub?.color || '#6477D9'}20`, color: sub?.color || '#6477D9' }}
                            >
                              {sub?.code || 'GEN'}
                            </span>
                            <div className="flex items-center gap-1">
                              <button
                                onClick={() => handleOpenEdit(evt)}
                                className="text-slate-secondary hover:text-slate-primary p-0.5"
                              >
                                <Edit3 className="w-3 h-3" />
                              </button>
                              <button
                                onClick={() => deleteScheduleEvent(evt.id)}
                                className="text-slate-secondary hover:text-danger p-0.5"
                              >
                                <Trash2 className="w-3 h-3" />
                              </button>
                            </div>
                          </div>

                          <div className={`font-semibold line-clamp-2 ${evt.isCompleted ? 'line-through text-slate-secondary' : 'text-slate-primary dark:text-white'}`}>
                            {evt.title}
                          </div>

                          <div className="flex items-center justify-between text-[10px] text-slate-secondary font-mono pt-1">
                            <span>{evt.startTime} - {evt.endTime}</span>
                            <button
                              onClick={() => toggleScheduleEventComplete(evt.id)}
                              title={evt.isCompleted ? 'Mark Pending' : 'Mark Completed'}
                              className={`p-0.5 rounded ${evt.isCompleted ? 'text-success' : 'text-slate-400 hover:text-success'}`}
                            >
                              <CheckCircle2 className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        </div>
                      );
                    })
                  )}
                </div>

                {/* Quick Add Slot */}
                <button
                  onClick={() => {
                    setDate(dayStr);
                    handleOpenAdd();
                  }}
                  className="w-full mt-2 py-1 text-[10px] rounded-academic border border-dashed border-slate-border dark:border-slate-700 text-slate-secondary hover:text-primary hover:border-primary transition-colors text-center"
                >
                  + Add Session
                </button>
              </div>
            );
          })}
        </div>
      )}

      {currentView === 'daily' && (
        <div className="card-academic p-6 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 space-y-4">
          <div className="flex items-center justify-between border-b border-slate-border dark:border-slate-700 pb-3">
            <div>
              <h2 className="text-lg font-bold text-slate-primary dark:text-white">
                Schedule for {selectedDate.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric' })}
              </h2>
              <span className="text-xs text-slate-secondary">Chronological academic roadmap</span>
            </div>
            <button
              onClick={handleOpenAdd}
              className="px-3 py-1.5 rounded-academic bg-primary text-white text-xs font-semibold hover:bg-primary-hover"
            >
              + Add Session
            </button>
          </div>

          <div className="space-y-3">
            {scheduleEvents.filter(e => e.date === selectedDateStr).length === 0 ? (
              <div className="py-12 text-center text-xs text-slate-secondary">
                No sessions scheduled for this day. Click "+ Add Session" to create your study block.
              </div>
            ) : (
              scheduleEvents
                .filter(e => e.date === selectedDateStr)
                .sort((a, b) => a.startTime.localeCompare(b.startTime))
                .map(evt => {
                  const sub = subjects.find(s => s.id === evt.subjectId);
                  return (
                    <div
                      key={evt.id}
                      className="p-4 rounded-academic border border-slate-border dark:border-slate-700 bg-slate-muted/20 dark:bg-slate-900/40 flex items-center justify-between"
                    >
                      <div className="flex items-center gap-4">
                        <button
                          onClick={() => toggleScheduleEventComplete(evt.id)}
                          className={`w-6 h-6 rounded-md border flex items-center justify-center transition-colors ${evt.isCompleted ? 'bg-success border-success text-white' : 'border-slate-300 dark:border-slate-600 hover:border-primary'
                            }`}
                        >
                          {evt.isCompleted && <CheckCircle2 className="w-4 h-4" />}
                        </button>
                        <div>
                          <div className={`text-sm font-bold ${evt.isCompleted ? 'line-through text-slate-secondary' : 'text-slate-primary dark:text-white'}`}>
                            {evt.title}
                          </div>
                          <div className="text-xs text-slate-secondary flex items-center gap-2 mt-0.5">
                            <span className="font-mono">{evt.startTime} – {evt.endTime}</span>
                            {sub && <span className="font-semibold" style={{ color: sub.color }}>· {sub.name}</span>}
                            {evt.description && <span>· {evt.description}</span>}
                          </div>
                        </div>
                      </div>

                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => {
                            setSubjectId(evt.subjectId);
                            switchMode('focus');
                            startTimer();
                          }}
                          className="px-3 py-1.5 rounded-academic bg-primary/10 text-primary hover:bg-primary/20 text-xs font-semibold flex items-center gap-1.5"
                        >
                          <Play className="w-3.5 h-3.5 fill-current" />
                          <span>Focus</span>
                        </button>
                        <button
                          onClick={() => handleOpenEdit(evt)}
                          className="p-1.5 text-slate-secondary hover:text-slate-primary"
                        >
                          <Edit3 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => deleteScheduleEvent(evt.id)}
                          className="p-1.5 text-slate-secondary hover:text-danger"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  );
                })
            )}
          </div>
        </div>
      )}

      {currentView === 'monthly' && (
        <div className="card-academic p-6 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700">
          <div className="text-sm font-bold text-slate-primary dark:text-white mb-4">
            Monthly Schedule Grid ({selectedDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })})
          </div>
          <div className="grid grid-cols-7 gap-2 text-center text-xs">
            {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map(d => (
              <div key={d} className="font-bold text-slate-secondary py-1 text-[11px] uppercase">{d}</div>
            ))}
            {/* Generate 35 days */}
            {Array.from({ length: 35 }).map((_, i) => {
              const dayNum = (i % 31) + 1;
              const hasEvents = scheduleEvents.some(e => e.date.endsWith(`-${dayNum.toString().padStart(2, '0')}`));
              return (
                <div
                  key={i}
                  className={`p-2.5 rounded-academic border border-slate-border dark:border-slate-700 min-h-[50px] flex flex-col justify-between hover:bg-slate-muted/50 cursor-pointer ${dayNum === new Date().getDate() ? 'bg-primary/10 border-primary' : ''
                    }`}
                  onClick={() => {
                    const d = new Date(selectedDate);
                    d.setDate(dayNum);
                    setSelectedDate(d);
                    setCurrentView('daily');
                  }}
                >
                  <span className="font-mono text-xs font-semibold">{dayNum}</span>
                  {hasEvents && <span className="w-1.5 h-1.5 rounded-full bg-primary mx-auto" />}
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Schedule Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white dark:bg-slate-900 w-full max-w-lg rounded-academic-lg border border-slate-border dark:border-slate-800 shadow-modal p-6 animate-modalIn">
            <div className="flex items-center justify-between pb-4 border-b border-slate-border dark:border-slate-800">
              <h3 className="font-bold text-slate-primary dark:text-white text-base">
                {editingEvent ? 'Edit Study Session' : 'Schedule Study Session'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-1 rounded-lg text-slate-secondary hover:text-slate-primary"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Overlap alert */}
            {overlapWarning && (
              <div className="mt-3 p-3 rounded-academic bg-warning/10 border border-warning/20 text-warning text-xs flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 flex-shrink-0" />
                <span>{overlapWarning}</span>
              </div>
            )}

            <form onSubmit={handleSubmit} className="mt-4 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Session Title *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Graph Traversal Recitation"
                  value={title}
                  onChange={e => setTitle(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Subject
                  </label>
                  <select
                    value={subjectId}
                    onChange={e => setSubjectIdState(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  >
                    {subjects.map(s => (
                      <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Date
                  </label>
                  <input
                    type="date"
                    required
                    value={date}
                    onChange={e => handleTimeChange(startTime, endTime, e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Start Time
                  </label>
                  <input
                    type="time"
                    required
                    value={startTime}
                    onChange={e => handleTimeChange(e.target.value, endTime, date)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    End Time
                  </label>
                  <input
                    type="time"
                    required
                    value={endTime}
                    onChange={e => handleTimeChange(startTime, e.target.value, date)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Description / Topic Notes
                </label>
                <textarea
                  rows={3}
                  placeholder="Notes, reading pages, or room location..."
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary resize-none"
                />
              </div>

              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  id="reminderCheck"
                  checked={reminder}
                  onChange={e => setReminder(e.target.checked)}
                  className="rounded text-primary focus:ring-0"
                />
                <label htmlFor="reminderCheck" className="text-xs text-slate-primary dark:text-white flex items-center gap-1.5 cursor-pointer">
                  <Bell className="w-3.5 h-3.5 text-slate-secondary" />
                  <span>Send in-app reminder notification</span>
                </label>
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-border dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-secondary hover:text-slate-primary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 text-xs font-semibold rounded-academic bg-primary text-white hover:bg-primary-hover shadow-sm"
                >
                  {editingEvent ? 'Save Changes' : 'Schedule Session'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

