import React, { useState } from 'react';
import { 
  Plus, 
  Calendar, 
  Clock, 
  MapPin, 
  Award, 
  Sparkles, 
  Edit3, 
  Trash2, 
  X,
  FileText
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { Examination, ExamPrepStatus, ExamType } from '../../types';
import EmptyState from '../../components/common/EmptyState';
import { format, differenceInDays, parseISO } from 'date-fns';

export const ExaminationsPage: React.FC = () => {
  const { examinations, subjects, tasks, addExamination, updateExamination, deleteExamination, createRevisionTaskForExam } = useData();
  const { toast } = useToast();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingExam, setEditingExam] = useState<Examination | null>(null);

  // Form State
  const [formTitle, setFormTitle] = useState('');
  const [formSubjectId, setFormSubjectId] = useState('');
  const [formExamDate, setFormExamDate] = useState('');
  const [formExamTime, setFormExamTime] = useState('09:00');
  const [formLocation, setFormLocation] = useState('');
  const [formExamType, setFormExamType] = useState<ExamType>('final');
  const [formPrepStatus, setFormPrepStatus] = useState<ExamPrepStatus>('not_prepared');
  const [formTargetGrade, setFormTargetGrade] = useState('A');
  const [formNotes, setFormNotes] = useState('');

  // Revision Modal State
  const [revisionExam, setRevisionExam] = useState<Examination | null>(null);
  const [revisionTopic, setRevisionTopic] = useState('');
  const [revisionDaysBefore, setRevisionDaysBefore] = useState(3);

  const openCreateModal = () => {
    setEditingExam(null);
    setFormTitle('');
    setFormSubjectId(subjects[0]?.id || '');
    setFormExamDate(new Date(Date.now() + 86400000 * 7).toISOString().split('T')[0]);
    setFormExamTime('09:00');
    setFormLocation('Hall B');
    setFormExamType('midterm');
    setFormPrepStatus('not_prepared');
    setFormTargetGrade('A');
    setFormNotes('');
    setIsModalOpen(true);
  };

  const openEditModal = (exam: Examination) => {
    setEditingExam(exam);
    setFormTitle(exam.title);
    setFormSubjectId(exam.subjectId);
    setFormExamDate(exam.examDate);
    setFormExamTime(exam.examTime);
    setFormLocation(exam.location);
    setFormExamType(exam.examType);
    setFormPrepStatus(exam.preparationStatus);
    setFormTargetGrade(exam.targetGrade || 'A');
    setFormNotes(exam.notes || '');
    setIsModalOpen(true);
  };

  const handleSaveExam = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formTitle.trim()) {
      toast.error('Exam title is required');
      return;
    }
    if (!formSubjectId) {
      toast.error('Please assign a subject');
      return;
    }

    if (editingExam) {
      updateExamination(editingExam.id, {
        title: formTitle.trim(),
        subjectId: formSubjectId,
        examDate: formExamDate,
        examTime: formExamTime,
        location: formLocation.trim(),
        examType: formExamType,
        preparationStatus: formPrepStatus,
        targetGrade: formTargetGrade.trim(),
        notes: formNotes.trim()
      });
      toast.success('Examination updated');
    } else {
      addExamination({
        title: formTitle.trim(),
        subjectId: formSubjectId,
        examDate: formExamDate,
        examTime: formExamTime,
        location: formLocation.trim(),
        examType: formExamType,
        preparationStatus: formPrepStatus,
        targetGrade: formTargetGrade.trim(),
        notes: formNotes.trim()
      });
      toast.success('Examination added');
    }
    setIsModalOpen(false);
  };

  const handleDeleteExam = (id: string, title: string) => {
    if (window.confirm(`Delete examination "${title}"?`)) {
      deleteExamination(id);
      toast.info('Examination deleted');
    }
  };

  const handleCreateRevisionPlan = (e: React.FormEvent) => {
    e.preventDefault();
    if (!revisionExam) return;

    const topic = revisionTopic.trim() || `Revision: ${revisionExam.title}`;
    const examDate = new Date(revisionExam.examDate);
    const dueDateObj = new Date(examDate.getTime() - revisionDaysBefore * 86400000);
    const dueDateStr = dueDateObj.toISOString().split('T')[0];

    createRevisionTaskForExam(revisionExam.id, topic, dueDateStr);
    toast.success(`Generated linked revision task: "${topic}"`);
    setRevisionExam(null);
    setRevisionTopic('');
  };

  const getSubject = (id: string) => subjects.find(s => s.id === id);

  const getCountdown = (dateStr: string) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const examDate = parseISO(dateStr);
    const diff = differenceInDays(examDate, today);

    if (diff < 0) return { text: 'Completed', color: 'text-academic-muted bg-academic-muted-bg' };
    if (diff === 0) return { text: 'Today!', color: 'text-red-600 bg-red-100 dark:bg-red-950/40 font-bold' };
    if (diff === 1) return { text: 'Tomorrow', color: 'text-amber-600 bg-amber-100 dark:bg-amber-950/40 font-semibold' };
    return { text: `In ${diff} days`, color: 'text-academic-primary bg-academic-primary/10 font-medium' };
  };

  const prepBadge = (status: ExamPrepStatus) => {
    switch (status) {
      case 'ready':
        return <span className="px-2.5 py-0.5 text-xs font-medium rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-400">Exam Ready</span>;
      case 'in_progress':
        return <span className="px-2.5 py-0.5 text-xs font-medium rounded-full bg-amber-100 text-amber-700 dark:bg-amber-950/40 dark:text-amber-400">Review in Progress</span>;
      case 'not_prepared':
        return <span className="px-2.5 py-0.5 text-xs font-medium rounded-full bg-red-100 text-red-700 dark:bg-red-950/40 dark:text-red-400">Not Started</span>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-academic-text tracking-tight">Examinations & Tests</h1>
          <p className="text-sm text-academic-muted mt-1">
            Track midterm, final and practical assessments with automated revision scheduling.
          </p>
        </div>

        <button
          onClick={openCreateModal}
          className="flex items-center gap-2 px-4 py-2 bg-academic-primary text-white rounded-lg text-sm font-medium hover:bg-academic-primary/90 transition-colors shadow-sm self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>Add Examination</span>
        </button>
      </div>

      {/* Examinations List */}
      {examinations.length === 0 ? (
        <EmptyState
          title="No examinations scheduled"
          description="Keep your semester stress-free by tracking your upcoming tests and exam milestones early."
          actionLabel="Add Examination"
          onAction={openCreateModal}
        />
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
          {examinations.map(exam => {
            const subject = getSubject(exam.subjectId);
            const countdown = getCountdown(exam.examDate);
            const linkedTasks = tasks.filter(t => exam.linkedTaskIds?.includes(t.id));

            return (
              <div 
                key={exam.id}
                className="bg-academic-card p-5 rounded-xl border border-academic-border shadow-sm flex flex-col justify-between hover:border-academic-primary/40 transition-all"
              >
                <div>
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="text-xs font-bold uppercase tracking-wider px-2 py-0.5 rounded" style={{
                        backgroundColor: subject ? `${subject.color}15` : undefined,
                        color: subject?.color
                      }}>
                        {subject?.code || 'Exam'}
                      </span>
                      <span className="text-xs px-2 py-0.5 rounded uppercase font-medium bg-academic-muted-bg text-academic-muted">
                        {exam.examType}
                      </span>
                      {prepBadge(exam.preparationStatus)}
                    </div>

                    <span className={`text-xs px-2.5 py-0.5 rounded-full ${countdown.color}`}>
                      {countdown.text}
                    </span>
                  </div>

                  <h3 className="text-base font-semibold text-academic-text mt-3">
                    {exam.title}
                  </h3>

                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 mt-4 text-xs text-academic-muted bg-academic-muted-bg/50 p-3 rounded-lg border border-academic-border">
                    <div className="flex items-center gap-2">
                      <Calendar className="w-4 h-4 text-academic-primary" />
                      <div>
                        <span className="block text-[10px] text-academic-muted uppercase font-medium">Date</span>
                        <span className="font-semibold text-academic-text">{format(parseISO(exam.examDate), 'MMM d, yyyy')}</span>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <Clock className="w-4 h-4 text-academic-primary" />
                      <div>
                        <span className="block text-[10px] text-academic-muted uppercase font-medium">Time</span>
                        <span className="font-semibold text-academic-text">{exam.examTime}</span>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <MapPin className="w-4 h-4 text-academic-primary" />
                      <div>
                        <span className="block text-[10px] text-academic-muted uppercase font-medium">Location</span>
                        <span className="font-semibold text-academic-text">{exam.location || 'TBA'}</span>
                      </div>
                    </div>
                  </div>

                  {exam.notes && (
                    <p className="text-xs text-academic-muted mt-3 italic bg-academic-muted-bg/30 p-2 rounded border border-academic-border/50">
                      "{exam.notes}"
                    </p>
                  )}

                  {/* Linked Revision Tasks */}
                  <div className="mt-4 pt-3 border-t border-academic-border">
                    <div className="flex items-center justify-between text-xs mb-2">
                      <span className="font-medium text-academic-text flex items-center gap-1.5">
                        <FileText className="w-3.5 h-3.5 text-academic-primary" />
                        Linked Revision Tasks ({linkedTasks.length})
                      </span>
                      <button
                        onClick={() => {
                          setRevisionExam(exam);
                          setRevisionTopic(`Review ${exam.title}`);
                        }}
                        className="text-academic-primary hover:underline font-medium flex items-center gap-1"
                      >
                        <Sparkles className="w-3 h-3" />
                        Plan Revision Task
                      </button>
                    </div>

                    {linkedTasks.length > 0 ? (
                      <div className="space-y-1.5">
                        {linkedTasks.map(t => (
                          <div 
                            key={t.id} 
                            className="flex items-center justify-between text-xs p-2 rounded bg-academic-muted-bg border border-academic-border"
                          >
                            <span className={t.status === 'completed' ? 'line-through text-academic-muted' : 'text-academic-text'}>
                              {t.title}
                            </span>
                            <span className="text-[11px] text-academic-muted">
                              Due {format(parseISO(t.dueDate), 'MMM d')}
                            </span>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p className="text-xs text-academic-muted">
                        No revision tasks planned yet. Generate study milestones before the exam.
                      </p>
                    )}
                  </div>
                </div>

                <div className="flex items-center justify-between pt-4 mt-4 border-t border-academic-border">
                  <div className="flex items-center gap-1 text-xs text-academic-muted">
                    <Award className="w-4 h-4 text-amber-500" />
                    <span>Target Grade: <strong className="text-academic-text">{exam.targetGrade || 'A'}</strong></span>
                  </div>

                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => openEditModal(exam)}
                      className="p-1.5 text-academic-muted hover:text-academic-primary rounded-lg hover:bg-academic-muted-bg transition-colors"
                      title="Edit examination"
                    >
                      <Edit3 className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDeleteExam(exam.id, exam.title)}
                      className="p-1.5 text-academic-muted hover:text-red-600 rounded-lg hover:bg-academic-muted-bg transition-colors"
                      title="Delete examination"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Create / Edit Exam Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-academic-card w-full max-w-lg rounded-xl border border-academic-border shadow-xl overflow-hidden animate-slide-up">
            <div className="flex items-center justify-between p-4 border-b border-academic-border">
              <h3 className="font-semibold text-academic-text">
                {editingExam ? 'Edit Examination' : 'Schedule Examination'}
              </h3>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="text-academic-muted hover:text-academic-text p-1 rounded-lg hover:bg-academic-muted-bg transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveExam} className="p-4 space-y-4">
              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Examination Title <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Midterm Examination: Computer Systems"
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
                    Exam Type
                  </label>
                  <select
                    value={formExamType}
                    onChange={e => setFormExamType(e.target.value as ExamType)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    <option value="midterm">Midterm Exam</option>
                    <option value="final">Final Exam</option>
                    <option value="quiz">Quiz / Assessment</option>
                    <option value="practical">Lab / Practical</option>
                    <option value="presentation">Presentation</option>
                    <option value="other">Other</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formExamDate}
                    onChange={e => setFormExamDate(e.target.value)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Time
                  </label>
                  <input
                    type="time"
                    value={formExamTime}
                    onChange={e => setFormExamTime(e.target.value)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Location
                  </label>
                  <input
                    type="text"
                    placeholder="Hall 4B"
                    value={formLocation}
                    onChange={e => setFormLocation(e.target.value)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Preparation Status
                  </label>
                  <select
                    value={formPrepStatus}
                    onChange={e => setFormPrepStatus(e.target.value as ExamPrepStatus)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    <option value="not_prepared">Not Started</option>
                    <option value="in_progress">Review in Progress</option>
                    <option value="ready">Exam Ready</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Target Grade
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. A, 90%"
                    value={formTargetGrade}
                    onChange={e => setFormTargetGrade(e.target.value)}
                    className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Preparation Notes / Syllabus Chapters
                </label>
                <textarea
                  rows={3}
                  placeholder="Chapters 1-7, practice past papers 2024..."
                  value={formNotes}
                  onChange={e => setFormNotes(e.target.value)}
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
                  {editingExam ? 'Save Changes' : 'Schedule Exam'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Revision Plan Modal */}
      {revisionExam && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-academic-card w-full max-w-md rounded-xl border border-academic-border shadow-xl overflow-hidden animate-slide-up">
            <div className="flex items-center justify-between p-4 border-b border-academic-border">
              <h3 className="font-semibold text-academic-text flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-academic-primary" />
                Plan Revision Milestone
              </h3>
              <button 
                onClick={() => setRevisionExam(null)}
                className="text-academic-muted hover:text-academic-text p-1 rounded-lg hover:bg-academic-muted-bg transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleCreateRevisionPlan} className="p-4 space-y-4">
              <p className="text-xs text-academic-muted">
                Create a targeted study task linked to <strong>{revisionExam.title}</strong> so it appears on your daily schedule.
              </p>

              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Revision Task Name
                </label>
                <input
                  type="text"
                  required
                  value={revisionTopic}
                  onChange={e => setRevisionTopic(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Schedule Task (Days before exam)
                </label>
                <select
                  value={revisionDaysBefore}
                  onChange={e => setRevisionDaysBefore(Number(e.target.value))}
                  className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                >
                  <option value={1}>1 Day before exam</option>
                  <option value={2}>2 Days before exam</option>
                  <option value={3}>3 Days before exam</option>
                  <option value={5}>5 Days before exam</option>
                  <option value={7}>1 Week before exam</option>
                </select>
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-academic-border">
                <button
                  type="button"
                  onClick={() => setRevisionExam(null)}
                  className="px-4 py-2 text-xs font-medium text-academic-muted hover:text-academic-text rounded-lg border border-academic-border hover:bg-academic-muted-bg transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-xs font-medium bg-academic-primary text-white rounded-lg hover:bg-academic-primary/90 transition-colors shadow-sm"
                >
                  Create Task
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
