import React, { useState, useMemo } from 'react';
import { 
  Plus, 
  Search, 
  Pin, 
  Star, 
  Archive, 
  Trash2, 
  Edit3, 
  Bold, 
  Italic, 
  Heading1, 
  Heading2, 
  List as ListIcon, 
  CheckSquare, 
  Code, 
  X,
  Clock
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { Note } from '../../types';
import EmptyState from '../../components/common/EmptyState';
import { format, parseISO } from 'date-fns';

export const NotesPage: React.FC = () => {
  const { notes, subjects, addNote, updateNote, deleteNote, togglePinNote, toggleFavouriteNote, toggleArchiveNote } = useData();
  const { toast } = useToast();

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedSubject, setSelectedSubject] = useState<string>('all');
  const [selectedTag, setSelectedTag] = useState<string>('all');
  const [tab, setTab] = useState<'all' | 'pinned' | 'favourites' | 'archived'>('all');

  // Note Modal / Editor State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingNote, setEditingNote] = useState<Note | null>(null);

  // Form
  const [formTitle, setFormTitle] = useState('');
  const [formContent, setFormContent] = useState('');
  const [formSubjectId, setFormSubjectId] = useState<string>('');
  const [formTagsString, setFormTagsString] = useState('');

  // Extract all unique tags
  const allTags = useMemo(() => {
    const set = new Set<string>();
    notes.forEach(n => n.tags?.forEach(t => set.add(t)));
    return Array.from(set);
  }, [notes]);

  const openCreateModal = () => {
    setEditingNote(null);
    setFormTitle('');
    setFormContent('');
    setFormSubjectId(subjects[0]?.id || '');
    setFormTagsString('');
    setIsModalOpen(true);
  };

  const openEditModal = (note: Note) => {
    setEditingNote(note);
    setFormTitle(note.title);
    setFormContent(note.content);
    setFormSubjectId(note.subjectId || '');
    setFormTagsString(note.tags?.join(', ') || '');
    setIsModalOpen(true);
  };

  const handleSaveNote = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formTitle.trim()) {
      toast.error('Note title is required');
      return;
    }

    const tags = formTagsString
      .split(',')
      .map(t => t.trim())
      .filter(Boolean);

    if (editingNote) {
      updateNote(editingNote.id, {
        title: formTitle.trim(),
        content: formContent.trim(),
        subjectId: formSubjectId || undefined,
        tags
      });
      toast.success('Note updated');
    } else {
      addNote({
        title: formTitle.trim(),
        content: formContent.trim(),
        subjectId: formSubjectId || undefined,
        tags,
        isPinned: false,
        isFavourite: false,
        isArchived: false
      });
      toast.success('Note created');
    }
    setIsModalOpen(false);
  };

  const handleDelete = (id: string, title: string) => {
    if (window.confirm(`Delete note "${title}"?`)) {
      deleteNote(id);
      toast.info('Note deleted');
    }
  };

  // Markdown helper
  const insertMarkdown = (prefix: string, suffix: string = '') => {
    const textarea = document.getElementById('note-textarea') as HTMLTextAreaElement;
    if (!textarea) return;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selected = formContent.substring(start, end);
    const replacement = `${prefix}${selected}${suffix}`;
    const nextContent = formContent.substring(0, start) + replacement + formContent.substring(end);
    setFormContent(nextContent);
  };

  // Filter notes
  const filteredNotes = useMemo(() => {
    return notes.filter(n => {
      if (tab === 'pinned' && !n.isPinned) return false;
      if (tab === 'favourites' && !n.isFavourite) return false;
      if (tab === 'archived' && !n.isArchived) return false;
      if (tab !== 'archived' && n.isArchived) return false;

      if (selectedSubject !== 'all' && n.subjectId !== selectedSubject) return false;
      if (selectedTag !== 'all' && !n.tags?.includes(selectedTag)) return false;

      if (searchQuery) {
        const q = searchQuery.toLowerCase();
        const matchesTitle = n.title.toLowerCase().includes(q);
        const matchesContent = n.content.toLowerCase().includes(q);
        const matchesTag = n.tags?.some(t => t.toLowerCase().includes(q));
        if (!matchesTitle && !matchesContent && !matchesTag) return false;
      }
      return true;
    }).sort((a, b) => {
      // Pinned first, then recent update
      if (a.isPinned !== b.isPinned) return a.isPinned ? -1 : 1;
      return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime();
    });
  }, [notes, tab, selectedSubject, selectedTag, searchQuery]);

  const getSubject = (id?: string) => subjects.find(s => s.id === id);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-academic-text tracking-tight">Academic Notes & Knowledge</h1>
          <p className="text-sm text-academic-muted mt-1">
            Capture lecture summaries, conceptual formulas, reading digests and revision notes.
          </p>
        </div>

        <button
          onClick={openCreateModal}
          className="flex items-center gap-2 px-4 py-2 bg-academic-primary text-white rounded-lg text-sm font-medium hover:bg-academic-primary/90 transition-colors shadow-sm self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>New Note</span>
        </button>
      </div>

      {/* Tabs and Filters */}
      <div className="bg-academic-card p-4 rounded-xl border border-academic-border space-y-3">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-3">
          {/* Tabs */}
          <div className="flex items-center gap-1 border-b lg:border-0 pb-2 lg:pb-0 border-academic-border">
            <button
              onClick={() => setTab('all')}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                tab === 'all' 
                  ? 'bg-academic-primary text-white' 
                  : 'text-academic-muted hover:text-academic-text hover:bg-academic-muted-bg'
              }`}
            >
              All Notes ({notes.filter(n => !n.isArchived).length})
            </button>
            <button
              onClick={() => setTab('pinned')}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium flex items-center gap-1.5 transition-colors ${
                tab === 'pinned' 
                  ? 'bg-academic-primary text-white' 
                  : 'text-academic-muted hover:text-academic-text hover:bg-academic-muted-bg'
              }`}
            >
              <Pin className="w-3.5 h-3.5" />
              Pinned
            </button>
            <button
              onClick={() => setTab('favourites')}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium flex items-center gap-1.5 transition-colors ${
                tab === 'favourites' 
                  ? 'bg-academic-primary text-white' 
                  : 'text-academic-muted hover:text-academic-text hover:bg-academic-muted-bg'
              }`}
            >
              <Star className="w-3.5 h-3.5" />
              Favourites
            </button>
            <button
              onClick={() => setTab('archived')}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium flex items-center gap-1.5 transition-colors ${
                tab === 'archived' 
                  ? 'bg-academic-primary text-white' 
                  : 'text-academic-muted hover:text-academic-text hover:bg-academic-muted-bg'
              }`}
            >
              <Archive className="w-3.5 h-3.5" />
              Archived
            </button>
          </div>

          {/* Search & Subject Filters */}
          <div className="flex flex-wrap items-center gap-2">
            <div className="relative flex-1 sm:w-60">
              <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-academic-muted" />
              <input
                type="text"
                placeholder="Search notes & content..."
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                className="w-full pl-8 pr-3 py-1.5 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              />
            </div>

            <select
              value={selectedSubject}
              onChange={e => setSelectedSubject(e.target.value)}
              className="text-xs bg-academic-muted-bg border border-academic-border rounded-lg px-2.5 py-1.5 text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            >
              <option value="all">All Subjects</option>
              {subjects.map(s => (
                <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
              ))}
            </select>

            {allTags.length > 0 && (
              <select
                value={selectedTag}
                onChange={e => setSelectedTag(e.target.value)}
                className="text-xs bg-academic-muted-bg border border-academic-border rounded-lg px-2.5 py-1.5 text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              >
                <option value="all">All Tags</option>
                {allTags.map(tag => (
                  <option key={tag} value={tag}>#{tag}</option>
                ))}
              </select>
            )}
          </div>
        </div>
      </div>

      {/* Notes Grid */}
      {filteredNotes.length === 0 ? (
        <EmptyState
          title="No notes found"
          description="Create structured study notes, lecture key takeaways and revision cheat-sheets."
          actionLabel="Create Note"
          onAction={openCreateModal}
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredNotes.map(note => {
            const subject = getSubject(note.subjectId);

            return (
              <div 
                key={note.id}
                className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm flex flex-col justify-between hover:border-academic-primary/40 transition-all group"
              >
                <div>
                  <div className="flex items-start justify-between gap-2">
                    {subject ? (
                      <span className="text-[11px] font-semibold px-2 py-0.5 rounded" style={{
                        backgroundColor: `${subject.color}15`,
                        color: subject.color
                      }}>
                        {subject.code}
                      </span>
                    ) : (
                      <span className="text-[11px] font-semibold px-2 py-0.5 rounded bg-academic-muted-bg text-academic-muted">
                        General Note
                      </span>
                    )}

                    <div className="flex items-center gap-1 text-academic-muted">
                      <button
                        onClick={() => togglePinNote(note.id)}
                        className={`p-1 rounded hover:bg-academic-muted-bg transition-colors ${
                          note.isPinned ? 'text-academic-primary fill-academic-primary' : ''
                        }`}
                        title={note.isPinned ? 'Unpin note' : 'Pin note to top'}
                      >
                        <Pin className="w-3.5 h-3.5" />
                      </button>

                      <button
                        onClick={() => toggleFavouriteNote(note.id)}
                        className={`p-1 rounded hover:bg-academic-muted-bg transition-colors ${
                          note.isFavourite ? 'text-amber-500 fill-amber-500' : ''
                        }`}
                        title={note.isFavourite ? 'Remove from favourites' : 'Mark as favourite'}
                      >
                        <Star className="w-3.5 h-3.5" />
                      </button>

                      <button
                        onClick={() => toggleArchiveNote(note.id)}
                        className={`p-1 rounded hover:bg-academic-muted-bg transition-colors ${
                          note.isArchived ? 'text-academic-primary' : ''
                        }`}
                        title={note.isArchived ? 'Restore from archive' : 'Archive note'}
                      >
                        <Archive className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>

                  <h3 
                    onClick={() => openEditModal(note)}
                    className="font-semibold text-sm text-academic-text mt-2.5 cursor-pointer hover:text-academic-primary transition-colors line-clamp-1"
                  >
                    {note.title}
                  </h3>

                  <p className="text-xs text-academic-muted mt-2 line-clamp-4 font-mono text-[11px] bg-academic-muted-bg/40 p-2.5 rounded-lg border border-academic-border/40 whitespace-pre-wrap">
                    {note.content || 'Empty note content...'}
                  </p>

                  {note.tags && note.tags.length > 0 && (
                    <div className="flex flex-wrap gap-1 mt-3">
                      {note.tags.map(t => (
                        <span key={t} className="text-[10px] px-1.5 py-0.5 rounded bg-academic-muted-bg text-academic-muted">
                          #{t}
                        </span>
                      ))}
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-between pt-3 mt-3 border-t border-academic-border text-xs text-academic-muted">
                  <span className="flex items-center gap-1 text-[11px]">
                    <Clock className="w-3 h-3" />
                    {format(parseISO(note.updatedAt), 'MMM d, yyyy')}
                  </span>

                  <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                    <button
                      onClick={() => openEditModal(note)}
                      className="p-1 hover:text-academic-primary"
                      title="Edit note"
                    >
                      <Edit3 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => handleDelete(note.id, note.title)}
                      className="p-1 hover:text-red-600"
                      title="Delete note"
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

      {/* Note Editor Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-academic-card w-full max-w-2xl rounded-xl border border-academic-border shadow-xl overflow-hidden flex flex-col max-h-[90vh] animate-slide-up">
            <div className="flex items-center justify-between p-4 border-b border-academic-border">
              <h3 className="font-semibold text-academic-text">
                {editingNote ? 'Edit Study Note' : 'Create New Note'}
              </h3>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="text-academic-muted hover:text-academic-text p-1 rounded-lg hover:bg-academic-muted-bg transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveNote} className="flex flex-col flex-1 overflow-hidden p-4 space-y-4">
              <div>
                <label className="block text-xs font-medium text-academic-text mb-1">
                  Note Title <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Master Theorem & Asymptotic Analysis"
                  value={formTitle}
                  onChange={e => setFormTitle(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary font-medium"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Subject (Optional)
                  </label>
                  <select
                    value={formSubjectId}
                    onChange={e => setFormSubjectId(e.target.value)}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  >
                    <option value="">No Subject (General)</option>
                    {subjects.map(s => (
                      <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-academic-text mb-1">
                    Tags (Comma separated)
                  </label>
                  <input
                    type="text"
                    placeholder="algorithms, revision, exam"
                    value={formTagsString}
                    onChange={e => setFormTagsString(e.target.value)}
                    className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
                  />
                </div>
              </div>

              {/* Markdown Toolbar */}
              <div className="flex flex-col flex-1 overflow-hidden">
                <div className="flex items-center gap-1 pb-2 border-b border-academic-border text-academic-muted">
                  <button
                    type="button"
                    onClick={() => insertMarkdown('**', '**')}
                    className="p-1.5 hover:text-academic-primary hover:bg-academic-muted-bg rounded"
                    title="Bold"
                  >
                    <Bold className="w-3.5 h-3.5" />
                  </button>
                  <button
                    type="button"
                    onClick={() => insertMarkdown('*', '*')}
                    className="p-1.5 hover:text-academic-primary hover:bg-academic-muted-bg rounded"
                    title="Italic"
                  >
                    <Italic className="w-3.5 h-3.5" />
                  </button>
                  <button
                    type="button"
                    onClick={() => insertMarkdown('# ')}
                    className="p-1.5 hover:text-academic-primary hover:bg-academic-muted-bg rounded"
                    title="Heading 1"
                  >
                    <Heading1 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    type="button"
                    onClick={() => insertMarkdown('## ')}
                    className="p-1.5 hover:text-academic-primary hover:bg-academic-muted-bg rounded"
                    title="Heading 2"
                  >
                    <Heading2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    type="button"
                    onClick={() => insertMarkdown('- ')}
                    className="p-1.5 hover:text-academic-primary hover:bg-academic-muted-bg rounded"
                    title="Bullet List"
                  >
                    <ListIcon className="w-3.5 h-3.5" />
                  </button>
                  <button
                    type="button"
                    onClick={() => insertMarkdown('- [ ] ')}
                    className="p-1.5 hover:text-academic-primary hover:bg-academic-muted-bg rounded"
                    title="Checklist item"
                  >
                    <CheckSquare className="w-3.5 h-3.5" />
                  </button>
                  <button
                    type="button"
                    onClick={() => insertMarkdown('```\n', '\n```')}
                    className="p-1.5 hover:text-academic-primary hover:bg-academic-muted-bg rounded"
                    title="Code block"
                  >
                    <Code className="w-3.5 h-3.5" />
                  </button>
                  <span className="text-[10px] text-academic-muted ml-auto">
                    Markdown supported
                  </span>
                </div>

                <textarea
                  id="note-textarea"
                  required
                  rows={12}
                  placeholder="Start taking notes, jotting down definitions or writing revision points..."
                  value={formContent}
                  onChange={e => setFormContent(e.target.value)}
                  className="w-full flex-1 p-3 text-xs font-mono bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary mt-2 resize-none"
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
                  {editingNote ? 'Save Note' : 'Create Note'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
