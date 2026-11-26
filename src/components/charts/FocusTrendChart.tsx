import React, { useState } from 'react';

export const FocusTrendChart: React.FC = () => {
  const [activePt, setActivePt] = useState<number | null>(null);

  const points = [
    { day: 'Mon', score: 85 },
    { day: 'Tue', score: 92 },
    { day: 'Wed', score: 78 },
    { day: 'Thu', score: 94 },
    { day: 'Fri', score: 86 },
    { day: 'Sat', score: 70 },
    { day: 'Sun', score: 65 },
  ];

  const width = 480;
  const height = 130;
  const padX = 30;
  const padY = 20;

  const getXY = (idx: number, score: number) => {
    const x = padX + (idx * (width - 2 * padX)) / (points.length - 1);
    const y = height - padY - ((score - 50) / 50) * (height - 2 * padY);
    return { x, y };
  };

  const pathD = points.reduce((acc, curr, idx) => {
    const { x, y } = getXY(idx, curr.score);
    return `${acc} ${idx === 0 ? 'M' : 'L'} ${x} ${y}`;
  }, '');

  return (
    <div className="w-full">
      <div className="flex items-center justify-between text-xs text-stone-500 mb-2">
        <span className="font-serif italic text-stone-700">Daily Focus Consistency</span>
        <span className="font-mono text-stone-600">Average: 81.4%</span>
      </div>

      <div className="w-full overflow-x-auto">
        <svg viewBox={`0 0 ${width} ${height + 25}`} className="w-full h-36 select-none">
          {/* Subtle grid */}
          {[60, 80, 100].map(s => {
            const y = height - padY - ((s - 50) / 50) * (height - 2 * padY);
            return (
              <g key={s}>
                <line x1={padX} y1={y} x2={width - padX} y2={y} stroke="#E6E6DF" strokeDasharray="2 3" />
                <text x={padX - 8} y={y + 3} fill="#A8A29E" fontSize="9" textAnchor="end">{s}%</text>
              </g>
            );
          })}

          {/* Line */}
          <path d={pathD} fill="none" stroke="#2A5C43" strokeWidth="2" strokeLinecap="round" />

          {/* Points */}
          {points.map((pt, idx) => {
            const { x, y } = getXY(idx, pt.score);
            const isHov = activePt === idx;

            return (
              <g 
                key={pt.day}
                onMouseEnter={() => setActivePt(idx)}
                onMouseLeave={() => setActivePt(null)}
                className="cursor-pointer"
              >
                <circle
                  cx={x}
                  cy={y}
                  r={isHov ? 5 : 3.5}
                  fill="#FFFFFF"
                  stroke="#2A5C43"
                  strokeWidth="2"
                />
                <text x={x} y={height + 15} fill="#78716C" fontSize="10" textAnchor="middle">
                  {pt.day}
                </text>
                {isHov && (
                  <g>
                    <rect x={x - 18} y={y - 22} width="36" height="16" rx="3" fill="#1E2522" />
                    <text x={x} y={y - 11} fill="#FFFFFF" fontSize="9" fontWeight="bold" textAnchor="middle">
                      {pt.score}%
                    </text>
                  </g>
                )}
              </g>
            );
          })}
        </svg>
      </div>
    </div>
  );
};

