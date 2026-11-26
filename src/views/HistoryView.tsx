import React, { useState } from 'react';
import { 
  Plus, 
  Trash2, 
  Filter
} from 'lucide-react';
import { useAcademic } from '../context/AppContext';

export const HistoryView: React.FC = () => {
  const { subjects, sessions, addSession, deleteSession } = useAcademic();

  const [selectedSubjectId, setSelectedSubjectId] = useState<string>('all');
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Manual session state
  const [subjectId, setSubjectId] = useState(subjects[0]?.id || '');
  const [durationMinutes, setDurationMinutes] = useState(60);
  const [sessionType, setSessionType] = useState<'deep-work' | 'reading' | 'revision' | 'problem-set'>('reading');
  const [reflection, setReflection] = useState('');
  const [focusScore, setFocusScore] = useState(5);

  const filtered = sessions.filter(s => {
    if (selectedSubjectId !== 'all' && s.subjectId !== selectedSubjectId) return false;
    return true;
  });

  const totalMins = filtered.reduce((a, b) => a + b.durationMinutes, 0);
  const totalHrs = (totalMins / 60).toFixed(1);

  const handleManualSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const curSub = subjects.find(s => s.id === subjectId) || subjects[0];

    addSession({
      subjectId: curSub.id,
      subjectTitle: curSub.title,
      durationMinutes: Number(durationMinutes),
      timestamp: new Date().toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' }) + ' · Manual Entry',
      sessionType,
      reflection: reflection || 'Manual session recorded.',
      focusScore,
    });

    setIsModalOpen(false);
    setReflection('');
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="paper-card rounded-2xl p-5 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="font-serif text-xl font-bold text-stone-900">
            Reading Log & Study Session Archive
          </h2>
          <p className="text-xs text-stone-500">
            {sessions.length} documented study sessions · Scholarly audit trail
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors shadow-xs"
        >
          <Plus className="w-4 h-4" />
          <span>Record Offline Session</span>
        </button>
      </div>

      {/* 3 Summary Badges */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="paper-card rounded-xl p-4">
          <p className="text-xs text-stone-500 font-serif italic">Total Time Logged</p>
          <p className="text-2xl font-serif font-bold text-stone-900 mt-1">{totalHrs} Hours</p>
        </div>

        <div className="paper-card rounded-xl p-4">
          <p className="text-xs text-stone-500 font-serif italic">Archived Sessions</p>
          <p className="text-2xl font-serif font-bold text-stone-900 mt-1">{filtered.length} Blocks</p>
        </div>

        <div className="paper-card rounded-xl p-4">
          <p className="text-xs text-stone-500 font-serif italic">Average Reflection Score</p>
          <p className="text-2xl font-serif font-bold text-stone-900 mt-1">
            {filtered.length > 0 
              ? (filtered.reduce((a, b) => a + b.focusScore, 0) / filtered.length).toFixed(1) 
              : '0.0'} / 5.0
          </p>
        </div>
      </div>

      {/* Filter Toolbar */}
      <div className="paper-card rounded-xl p-3 flex items-center gap-3 text-xs">
        <Filter className="w-3.5 h-3.5 text-stone-400" />
        <span className="text-stone-500 font-serif italic">Filter by Unit:</span>
        <select
          value={selectedSubjectId}
          onChange={(e) => setSelectedSubjectId(e.target.value)}
          className="bg-stone-50 border border-stone-200 rounded-lg px-2.5 py-1 text-stone-700 focus:outline-none focus:border-[#2A5C43]"
        >
          <option value="all">All Curriculum Units</option>
          {subjects.map(s => (
            <option key={s.id} value={s.id}>{s.code} · {s.title}</option>
          ))}
        </select>
      </div>

      {/* Archive List */}
      <div className="paper-card rounded-2xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-stone-50 border-b border-stone-200 text-stone-500 font-serif italic text-xs">
              <tr>
                <th className="py-3 px-4">Date & Time</th>
                <th className="py-3 px-4">Curriculum Subject</th>
                <th className="py-3 px-4">Duration</th>
                <th className="py-3 px-4">Session Type</th>
                <th className="py-3 px-4">Score</th>
                <th className="py-3 px-4">Reflections / Citations</th>
                <th className="py-3 px-4 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-stone-100">
              {filtered.length === 0 ? (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-stone-400">
                    No sessions recorded matching this filter.
                  </td>
                </tr>
              ) : (
                filtered.map((s) => {
                  const sub = subjects.find(sub => sub.id === s.subjectId);

                  return (
                    <tr key={s.id} className="hover:bg-stone-50/50 transition-colors">
                      <td className="py-3.5 px-4 text-stone-600 whitespace-nowrap font-mono text-[11px]">
                        {s.timestamp}
                      </td>
                      <td className="py-3.5 px-4 font-semibold text-stone-900 whitespace-nowrap">
                        <span 
                          className="inline-block w-2 h-2 rounded-full mr-2"
                          style={{ backgroundColor: sub?.pastelColor || '#2A5C43' }}
                        />
                        {s.subjectTitle}
                      </td>
                      <td className="py-3.5 px-4 font-mono font-medium text-stone-800 whitespace-nowrap">
                        {s.durationMinutes} mins
                      </td>
                      <td className="py-3.5 px-4 text-stone-600 capitalize whitespace-nowrap">
                        <span className="px-2 py-0.5 rounded bg-stone-100 text-stone-700 text-[10px]">
                          {s.sessionType}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 whitespace-nowrap font-mono text-amber-700">
                        ★ {s.focusScore}.0
                      </td>
                      <td className="py-3.5 px-4 text-stone-600 max-w-xs truncate italic">
                        {s.reflection || '—'}
                      </td>
                      <td className="py-3.5 px-4 text-right whitespace-nowrap">
                        <button
                          onClick={() => deleteSession(s.id)}
                          className="p-1 rounded text-stone-400 hover:text-rose-600 hover:bg-rose-50"
                          title="Delete Session"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Manual Entry Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-stone-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white w-full max-w-md rounded-2xl p-6 shadow-xl border border-stone-200 animate-modalIn">
            <h3 className="font-serif text-lg font-bold text-stone-900 mb-2">
              Record Offline Study Block
            </h3>
            <p className="text-xs text-stone-500 mb-4">
              Add hours completed in libraries, seminars or private reading.
            </p>

            <form onSubmit={handleManualSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Subject Unit *
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

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Duration (Minutes)
                  </label>
                  <input
                    type="number"
                    min={10}
                    max={600}
                    step={10}
                    value={durationMinutes}
                    onChange={(e) => setDurationMinutes(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Activity Type
                  </label>
                  <select
                    value={sessionType}
                    onChange={(e) => setSessionType(e.target.value as 'deep-work' | 'reading' | 'revision' | 'problem-set')}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  >
                    <option value="reading">Primary Reading</option>
                    <option value="deep-work">Studio / Dissertation</option>
                    <option value="problem-set">Problem Analysis</option>
                    <option value="revision">Seminar Revision</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Engagement Score (1-5)
                </label>
                <div className="flex gap-2">
                  {[1, 2, 3, 4, 5].map((s) => (
                    <button
                      type="button"
                      key={s}
                      onClick={() => setFocusScore(s)}
                      className={`flex-1 py-1.5 rounded-lg text-xs font-mono font-semibold ${
                        focusScore >= s ? 'bg-[#2A5C43] text-white' : 'bg-stone-100 text-stone-600'
                      }`}
                    >
                      ★ {s}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Reflection / Notes
                </label>
                <textarea
                  rows={3}
                  value={reflection}
                  onChange={(e) => setReflection(e.target.value)}
                  placeholder="Record summary of arguments synthesized or texts scrutinized..."
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
                  Save Entry
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
