import React, { useState, useMemo } from 'react';
import { 
  Plus, 
  Search, 
  Download, 
  Trash2, 
  Edit3, 
  X
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { StudySession, SessionType } from '../../types';
import EmptyState from '../../components/common/EmptyState';
import { format, parseISO } from 'date-fns';

export const HistoryPage: React.FC = () => {
  const { studySessions, subjects, addStudySession, updateStudySession, deleteStudySession, exportCSV } = useData();
  const { toast } = useToast();

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedSubject, setSelectedSubject] = useState<string>('all');
  const [selectedType, setSelectedType] = useState<string>('all');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSession, setEditingSession] = useState<StudySession | null>(null);

  // Form State
  const [formSubjectId, setFormSubjectId] = useState('');
  const [formDurationMinutes, setFormDurationMinutes] = useState(45);
  const [formSessionType, setFormSessionType] = useState<SessionType>('deep_work');
  const [formDate, setFormDate] = useState(new Date().toISOString().split('T')[0]);
  const [formTime, setFormTime] = useState('14:00');
  const [formNotes, setFormNotes] = useState('');

  const openCreateModal = () => {
    setEditingSession(null);
    setFormSubjectId(subjects[0]?.id || '');
    setFormDurationMinutes(45);
    setFormSessionType('deep_work');
    setFormDate(new Date().toISOString().split('T')[0]);
    setFormTime('14:00');
    setFormNotes('');
    setIsModalOpen(true);
  };

  const openEditModal = (session: StudySession) => {
    setEditingSession(session);
    setFormSubjectId(session.subjectId);
    setFormDurationMinutes(session.durationMinutes);
    setFormSessionType(session.sessionType);
    setFormDate(session.startTime.split('T')[0]);
    setFormTime(session.startTime.split('T')[1]?.substring(0, 5) || '14:00');
    setFormNotes(session.notes || '');
    setIsModalOpen(true);
  };

  const handleSaveSession = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formSubjectId) {
      toast.error('Please select a subject');
      return;
    }
    if (formDurationMinutes <= 0) {
      toast.error('Duration must be greater than 0');
      return;
    }

    const startISO = new Date(`${formDate}T${formTime}:00`).toISOString();
    const endISO = new Date(new Date(`${formDate}T${formTime}:00`).getTime() + formDurationMinutes * 60000).toISOString();

    if (editingSession) {
      updateStudySession(editingSession.id, {
        subjectId: formSubjectId,
        durationMinutes: Number(formDurationMinutes),
        sessionType: formSessionType,
        notes: formNotes.trim() || undefined,
        startTime: startISO,
        endTime: endISO
      });
      toast.success('Session updated');
    } else {
      addStudySession({
        subjectId: formSubjectId,
        durationMinutes: Number(formDurationMinutes),
        sessionType: formSessionType,
        notes: formNotes.trim() || undefined,
        isManual: true,
        startTime: startISO,
        endTime: endISO
      });
      toast.success('Manual study session logged');
    }
    setIsModalOpen(false);
  };

  const handleDeleteSession = (id: string) => {
    if (window.confirm('Delete this study session from history?')) {
      deleteStudySession(id);
      toast.info('Session deleted');
    }
  };

  const handleDownloadCSV = () => {
    const csvContent = exportCSV();
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `edutrack_study_history_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    toast.success('CSV log exported');
  };

  // Filtered Sessions
  const filteredSessions = useMemo(() => {
    return studySessions.filter(s => {
      if (selectedSubject !== 'all' && s.subjectId !== selectedSubject) return false;
      if (selectedType !== 'all' && s.sessionType !== selectedType) return false;
      if (searchQuery) {
        const q = searchQuery.toLowerCase();
        const matchesNotes = s.notes?.toLowerCase().includes(q);
        const subject = subjects.find(sub => sub.id === s.subjectId);
        const matchesSub = subject?.name.toLowerCase().includes(q) || subject?.code.toLowerCase().includes(q);
        if (!matchesNotes && !matchesSub) return false;
      }
      return true;
    }).sort((a, b) => new Date(b.startTime).getTime() - new Date(a.startTime).getTime());
  }, [studySessions, selectedSubject, selectedType, searchQuery, subjects]);

  // Summary Metrics
  const totalMinutes = useMemo(() => {
    return filteredSessions.reduce((acc, s) => acc + s.durationMinutes, 0);
  }, [filteredSessions]);

  const totalHours = (totalMinutes / 60).toFixed(1);
  const avgDuration = filteredSessions.length > 0 ? Math.round(totalMinutes / filteredSessions.length) : 0;

  const getSubject = (id: string) => subjects.find(s => s.id === id);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-academic-text tracking-tight">Study Session History</h1>
          <p className="text-sm text-academic-muted mt-1">
            Complete log of automated Pomodoro sessions and manually recorded study blocks.
          </p>
        </div>

        <div className="flex items-center gap-2 self-start sm:self-auto">
          <button
            onClick={handleDownloadCSV}
            className="flex items-center gap-1.5 px-3 py-2 text-xs font-semibold rounded-lg border border-academic-border bg-academic-card hover:bg-academic-muted-bg text-academic-text transition-colors shadow-sm"
            title="Download CSV"
          >
            <Download className="w-4 h-4 text-academic-primary" />
            <span>Export CSV</span>
          </button>

          <button
            onClick={openCreateModal}
            className="flex items-center gap-2 px-4 py-2 bg-academic-primary text-white rounded-lg text-sm font-medium hover:bg-academic-primary/90 transition-colors shadow-sm"
          >
            <Plus className="w-4 h-4" />
            <span>Log Study Time</span>
          </button>
        </div>
      </div>

      {/* Summary Stat Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm">
          <span className="text-xs font-medium text-academic-muted">Total Hours Logged</span>
          <div className="text-2xl font-bold text-academic-text mt-1">{totalHours}h</div>
          <span className="text-[11px] text-academic-muted">{totalMinutes} cumulative mins</span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm">
          <span className="text-xs font-medium text-academic-muted">Total Sessions</span>
          <div className="text-2xl font-bold text-academic-text mt-1">{filteredSessions.length}</div>
          <span className="text-[11px] text-academic-muted">Logged records</span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm">
          <span className="text-xs font-medium text-academic-muted">Average Session</span>
          <div className="text-2xl font-bold text-academic-text mt-1">{avgDuration}m</div>
          <span className="text-[11px] text-academic-muted">Per session length</span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm">
          <span className="text-xs font-medium text-academic-muted">Session Source</span>
          <div className="text-base font-bold text-academic-text mt-2 flex items-center gap-2">
            <span className="text-emerald-600">
              {filteredSessions.filter(s => !s.isManual).length} Timer
            </span>
            <span>•</span>
            <span className="text-academic-primary">
              {filteredSessions.filter(s => s.isManual).length} Manual
            </span>
          </div>
          <span className="text-[11px] text-academic-muted">Automatic vs manual entry</span>
        </div>
      </div>

      {/* Filters Bar */}
      <div className="bg-academic-card p-4 rounded-xl border border-academic-border space-y-3">
        <div className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-academic-muted" />
            <input
              type="text"
              placeholder="Search session notes or subjects..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="w-full pl-9 pr-4 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            />
          </div>

          <div className="flex items-center gap-2">
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
              value={selectedType}
              onChange={e => setSelectedType(e.target.value)}
              className="text-xs bg-academic-muted-bg border border-academic-border rounded-lg px-3 py-2 text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            >
              <option value="all">All Session Types</option>
              <option value="pomodoro">Pomodoro</option>
              <option value="deep_work">Deep Work</option>
              <option value="revision">Revision</option>
              <option value="reading">Reading</option>
              <option value="assignment">Assignment</option>
            </select>
          </div>
        </div>
      </div>

      {/* Session History Table */}
      <div className="bg-academic-card rounded-xl border border-academic-border overflow-hidden">
        {filteredSessions.length === 0 ? (
          <EmptyState
            title="No study sessions recorded"
            description="Start the Pomodoro timer or log offline study hours to build your learning log."
            actionLabel="Log Study Session"
            onAction={openCreateModal}
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="border-b border-academic-border bg-academic-muted-bg/60 text-academic-muted uppercase text-[10px] font-semibold tracking-wider">
                  <th className="py-3 px-4">Date & Time</th>
                  <th className="py-3 px-4">Subject</th>
                  <th className="py-3 px-4">Type</th>
                  <th className="py-3 px-4">Duration</th>
                  <th className="py-3 px-4">Notes / Topics</th>
                  <th className="py-3 px-4">Mode</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-academic-border">
                {filteredSessions.map(session => {
                  const subject = getSubject(session.subjectId);

                  return (
                    <tr key={session.id} className="hover:bg-academic-muted-bg/40 transition-colors">
                      <td className="py-3 px-4 font-medium text-academic-text whitespace-nowrap">
                        <div className="flex flex-col">
                          <span>{format(parseISO(session.startTime), 'MMM d, yyyy')}</span>
                          <span className="text-[10px] text-academic-muted">
                            {format(parseISO(session.startTime), 'hh:mm a')}
                          </span>
                        </div>
                      </td>

                      <td className="py-3 px-4">
                        {subject ? (
                          <div className="flex items-center gap-1.5">
                            <span className="w-2 h-2 rounded-full" style={{ backgroundColor: subject.color }} />
                            <span className="font-semibold text-academic-text">{subject.code}</span>
                            <span className="text-academic-muted hidden sm:inline truncate max-w-[150px]">
                              {subject.name}
                            </span>
                          </div>
                        ) : (
                          <span className="text-academic-muted">Unknown Subject</span>
                        )}
                      </td>

                      <td className="py-3 px-4">
                        <span className="text-[11px] font-medium uppercase px-2 py-0.5 rounded bg-academic-muted-bg text-academic-muted">
                          {session.sessionType.replace('_', ' ')}
                        </span>
                      </td>

                      <td className="py-3 px-4 font-bold text-academic-text whitespace-nowrap">
                        {session.durationMinutes} min
                        <span className="text-academic-muted font-normal text-[10px] ml-1">
                          ({(session.durationMinutes / 60).toFixed(1)}h)
                        </span>
                      </td>

                      <td className="py-3 px-4 text-academic-muted max-w-xs truncate">
                        {session.notes || '—'}
                      </td>

                      <td className="py-3 px-4">
                        <span className={`text-[10px] font-medium px-2 py-0.5 rounded ${
                          session.isManual 
                            ? 'bg-blue-100 text-blue-700 dark:bg-blue-950/40 dark:text-blue-300' 
                            : 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-300'
                        }`}>
                          {session.isManual ? 'Manual' : 'Timer'}
                        </span>
                      </td>

                      <td className="py-3 px-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            onClick={() => openEditModal(session)}
                            className="p-1.5 text-academic-muted hover:text-academic-primary rounded hover:bg-academic-muted-bg transition-colors"
                            title="Edit session"
                          >
                            <Edit3 className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => handleDeleteSession(session.id)}
                            className="p-1.5 text-academic-muted hover:text-red-600 rounded hover:bg-academic-muted-bg transition-colors"
                            title="Delete session"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Manual Session Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-academic-card w-full max-w-md rounded-xl border border-academic-border shadow-xl overflow-hidden animate-slide-up">
            <div className="flex items-center justify-between p-4 border-b border-academic-border">
              <h3 className="font-semibold text-academic-text">
                {editingSession ? 'Edit Study Session' : 'Log Offline Study Time'}
              </h3>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="text-academic-muted hover:text-academic-text p-1 rounded-lg hover:bg-academic-muted-bg transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveSession} className="p-4 space-y-4">
              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Subject <span className="text-red-500">*</span>
                </label>
                <select
                  value={formSubjectId}
                  onChange={e => setFormSubjectId(e.target.value)}
                  className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary font-medium"
                >
                  {subjects.map(s => (
                    <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Duration (Minutes)
                  </label>
                  <input
                    type="number"
                    min="5"
                    step="5"
                    required
                    value={formDurationMinutes}
                    onChange={e => setFormDurationMinutes(Number(e.target.value))}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Session Type
                  </label>
                  <select
                    value={formSessionType}
                    onChange={e => setFormSessionType(e.target.value as SessionType)}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    <option value="deep_work">Deep Work</option>
                    <option value="pomodoro">Pomodoro</option>
                    <option value="revision">Revision</option>
                    <option value="reading">Reading</option>
                    <option value="assignment">Assignment</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formDate}
                    onChange={e => setFormDate(e.target.value)}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Start Time
                  </label>
                  <input
                    type="time"
                    value={formTime}
                    onChange={e => setFormTime(e.target.value)}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Session Notes / Chapters Covered
                </label>
                <textarea
                  rows={3}
                  placeholder="Completed textbook exercises, reviewed lecture slides..."
                  value={formNotes}
                  onChange={e => setFormNotes(e.target.value)}
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
                  {editingSession ? 'Update Session' : 'Save Session'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
