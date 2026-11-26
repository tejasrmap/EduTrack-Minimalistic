import React, { useState } from 'react';
import { AcademicSubject } from '../../types';

interface DonutProps {
  subjects: AcademicSubject[];
}

export const MinimalSubjectDonut: React.FC<DonutProps> = ({ subjects }) => {
  const [hovered, setHovered] = useState<AcademicSubject | null>(null);

  const total = subjects.reduce((sum, s) => sum + s.currentWeekHours, 0) || 1;

  const size = 160;
  const stroke = 20;
  const radius = (size - stroke) / 2;
  const circ = 2 * Math.PI * radius;

  let cumulative = 0;

  return (
    <div className="flex flex-col sm:flex-row items-center gap-6">
      <div className="relative w-40 h-40 flex-shrink-0 flex items-center justify-center">
        <svg viewBox={`0 0 ${size} ${size}`} className="w-full h-full -rotate-90">
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            fill="none"
            stroke="#F4F4F0"
            strokeWidth={stroke}
          />

          {subjects.map((sub) => {
            const pct = sub.currentWeekHours / total;
            const dash = `${pct * circ} ${circ}`;
            const offset = -cumulative * circ;
            cumulative += pct;
            const isHov = hovered?.id === sub.id;

            return (
              <circle
                key={sub.id}
                cx={size / 2}
                cy={size / 2}
                r={radius}
                fill="none"
                stroke={sub.pastelColor}
                strokeWidth={isHov ? stroke + 3 : stroke}
                strokeDasharray={dash}
                strokeDashoffset={offset}
                strokeLinecap="round"
                className="transition-all duration-200 cursor-pointer"
                opacity={hovered && !isHov ? 0.35 : 0.9}
                onMouseEnter={() => setHovered(sub)}
                onMouseLeave={() => setHovered(null)}
              />
            );
          })}
        </svg>

        <div className="absolute inset-0 flex flex-col items-center justify-center text-center pointer-events-none">
          <span className="text-[11px] font-mono text-stone-500">
            {hovered ? hovered.code : 'TOTAL'}
          </span>
          <span className="text-lg font-serif font-bold text-stone-800">
            {hovered ? `${hovered.currentWeekHours}h` : `${total.toFixed(1)}h`}
          </span>
        </div>
      </div>

      <div className="flex-1 space-y-1.5 w-full">
        {subjects.slice(0, 5).map((sub) => {
          const pct = Math.round((sub.currentWeekHours / total) * 100);
          const isHov = hovered?.id === sub.id;

          return (
            <div
              key={sub.id}
              className={`flex items-center justify-between p-1.5 rounded-lg text-xs cursor-pointer transition-colors ${
                isHov ? 'bg-stone-100' : 'hover:bg-stone-50'
              }`}
              onMouseEnter={() => setHovered(sub)}
              onMouseLeave={() => setHovered(null)}
            >
              <div className="flex items-center gap-2 truncate pr-2">
                <span 
                  className="w-2.5 h-2.5 rounded-full flex-shrink-0"
                  style={{ backgroundColor: sub.pastelColor }}
                />
                <span className="text-stone-700 truncate">{sub.title}</span>
              </div>
              <div className="flex items-center gap-2 font-mono flex-shrink-0 text-stone-500">
                <span>{sub.currentWeekHours}h</span>
                <span className="w-8 text-right font-semibold text-stone-800">{pct}%</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

