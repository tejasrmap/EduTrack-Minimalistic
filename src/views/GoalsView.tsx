import React, { useState } from 'react';
import { 
  Award, 
  Plus, 
  CheckCircle2, 
  Trash2
} from 'lucide-react';
import { useAcademic } from '../context/AppContext';

export const GoalsView: React.FC = () => {
  const { milestones, awards, addMilestone, updateMilestone, deleteMilestone } = useAcademic();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [title, setTitle] = useState('');
  const [targetMetric, setTargetMetric] = useState('Studio Hours');
  const [currentValue, setCurrentValue] = useState(0);
  const [targetValue, setTargetValue] = useState(25);
  const [unit, setUnit] = useState('hours');
  const [deadline, setDeadline] = useState(new Date().toISOString().split('T')[0]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    addMilestone({
      title,
      targetMetric,
      currentValue: Number(currentValue),
      targetValue: Number(targetValue),
      unit,
      deadline,
      completed: Number(currentValue) >= Number(targetValue),
    });
    setIsModalOpen(false);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="paper-card rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="font-serif text-xl font-bold text-stone-900">
            Academic Milestones & Commendations
          </h2>
          <p className="text-xs text-stone-500">
            {milestones.filter(m => m.completed).length} / {milestones.length} milestones accomplished
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors shadow-xs"
        >
          <Plus className="w-4 h-4" />
          <span>Add Academic Target</span>
        </button>
      </div>

      {/* Milestones */}
      <div>
        <h3 className="font-serif text-base font-bold text-stone-900 mb-3">
          Term Milestones
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {milestones.map((m) => {
            const pct = Math.min(100, Math.round((m.currentValue / m.targetValue) * 100));

            return (
              <div key={m.id} className="paper-card rounded-xl p-5 flex flex-col justify-between">
                <div>
                  <div className="flex items-start justify-between gap-2 mb-2">
                    <span className="text-[10px] font-mono uppercase bg-stone-100 text-stone-700 px-2 py-0.5 rounded">
                      {m.targetMetric}
                    </span>
                    <button
                      onClick={() => deleteMilestone(m.id)}
                      className="p-1 rounded text-stone-400 hover:text-rose-600 hover:bg-rose-50"
                      title="Delete Milestone"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>

                  <h4 className="font-serif text-sm font-bold text-stone-900 mb-2">
                    {m.title}
                  </h4>

                  <div className="flex items-baseline justify-between mb-2 text-xs font-mono">
                    <span className="text-stone-500">
                      Progress: <strong className="text-stone-900">{m.currentValue}</strong> / {m.targetValue} {m.unit}
                    </span>
                    <span className="font-bold text-[#2A5C43]">{pct}%</span>
                  </div>

                  <div className="w-full bg-stone-100 h-1.5 rounded-full overflow-hidden mb-3">
                    <div 
                      className="h-full bg-[#2A5C43] rounded-full transition-all duration-300"
                      style={{ width: `${pct}%` }}
                    />
                  </div>
                </div>

                <div className="flex items-center justify-between pt-3 border-t border-stone-200 text-xs">
                  <span className="text-stone-500 font-mono text-[11px]">Due: {m.deadline}</span>
                  {pct >= 100 ? (
                    <span className="text-[#2A5C43] font-medium flex items-center gap-1">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      Completed
                    </span>
                  ) : (
                    <button
                      onClick={() => {
                        const next = Math.min(m.targetValue, m.currentValue + 1);
                        updateMilestone(m.id, {
                          currentValue: next,
                          completed: next >= m.targetValue,
                        });
                      }}
                      className="text-[#2A5C43] hover:underline"
                    >
                      + Advance Progress
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Scholarly Commendations */}
      <div className="pt-2">
        <h3 className="font-serif text-base font-bold text-stone-900 mb-3">
          Scholarly Commendations & Fellowships
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          {awards.map((a) => (
            <div 
              key={a.id} 
              className={`paper-card rounded-xl p-4 ${
                a.earned ? 'bg-white' : 'opacity-60 bg-stone-50'
              }`}
            >
              <div className="flex items-center justify-between mb-2">
                <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${
                  a.earned ? 'bg-[#EBF3ED] text-[#2A5C43]' : 'bg-stone-200 text-stone-500'
                }`}>
                  <Award className="w-4 h-4" />
                </div>
                <span className="text-[10px] font-mono text-stone-500">
                  {a.earned ? 'Awarded' : 'Candidate'}
                </span>
              </div>

              <h4 className="font-serif font-bold text-xs text-stone-900 mb-1">
                {a.title}
              </h4>
              <p className="text-[11px] text-stone-600 leading-relaxed mb-3">
                {a.criteria}
              </p>

              {a.earned && a.dateEarned && (
                <p className="text-[10px] text-stone-400 font-mono pt-2 border-t border-stone-100">
                  Conferred: {a.dateEarned}
                </p>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Milestone Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-stone-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white w-full max-w-md rounded-2xl p-6 shadow-xl border border-stone-200 animate-modalIn">
            <h3 className="font-serif text-lg font-bold text-stone-900 mb-2">
              Add Academic Target
            </h3>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Objective Title *
                </label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="e.g. Complete Cambridge Studio Thesis Defense"
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Target Metric
                  </label>
                  <input
                    type="text"
                    value={targetMetric}
                    onChange={(e) => setTargetMetric(e.target.value)}
                    placeholder="e.g. Reading Hours"
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  />
                </div>
              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Current
                  </label>
                  <input
                    type="number"
                    min={0}
                    value={currentValue}
                    onChange={(e) => setCurrentValue(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Target Value
                  </label>
                  <input
                    type="number"
                    min={1}
                    value={targetValue}
                    onChange={(e) => setTargetValue(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Unit
                  </label>
                  <input
                    type="text"
                    value={unit}
                    onChange={(e) => setUnit(e.target.value)}
                    placeholder="hours, texts, chapters"
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  />
                </div>
              </div>
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Target Deadline
                  </label>
                  <input
                    type="date"
                    required
                    value={deadline}
                    onChange={(e) => setDeadline(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
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
                  Save Target
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
