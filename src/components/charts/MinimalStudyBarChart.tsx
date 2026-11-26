import React, { useState } from 'react';

interface ChartProps {
  weeklyGoalHours?: number;
}

export const MinimalStudyBarChart: React.FC<ChartProps> = ({ 
  weeklyGoalHours = 28 
}) => {
  const [hoveredIdx, setHoveredIdx] = useState<number | null>(null);

  const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  const values = [4.5, 5.2, 4.0, 5.8, 3.5, 2.0, 1.2]; // Hours studied per day

  const totalStudied = values.reduce((a, b) => a + b, 0);
  const maxVal = Math.max(...values, 7);

  const width = 500;
  const height = 160;
  const barWidth = 32;
  const spacing = (width - 60) / 7;

  return (
    <div className="w-full">
      <div className="flex items-center justify-between text-xs text-stone-500 mb-4 pb-2 border-b border-stone-200">
        <span className="font-serif italic text-sm text-stone-700">
          Weekly Study Allocation
        </span>
        <span className="font-mono text-stone-600">
          {totalStudied.toFixed(1)} / {weeklyGoalHours} hrs logged ({Math.round((totalStudied / weeklyGoalHours) * 100)}%)
        </span>
      </div>

      <div className="w-full overflow-x-auto">
        <svg viewBox={`0 0 ${width} ${height + 30}`} className="w-full h-44 select-none">
          {/* Subtle horizontal grid lines */}
          {[0, 2, 4, 6].map((h) => {
            const y = height - (h / maxVal) * height + 10;
            return (
              <g key={h}>
                <line 
                  x1="35" 
                  y1={y} 
                  x2={width - 15} 
                  y2={y} 
                  stroke="#E6E6DF" 
                  strokeDasharray="2 3"
                />
                <text 
                  x="25" 
                  y={y + 3} 
                  fill="#78716C" 
                  fontSize="10" 
                  fontFamily="system-ui" 
                  textAnchor="end"
                >
                  {h}h
                </text>
              </g>
            );
          })}

          {/* Target line at 4h/day */}
          {(() => {
            const y = height - (4 / maxVal) * height + 10;
            return (
              <line 
                x1="35" 
                y1={y} 
                x2={width - 15} 
                y2={y} 
                stroke="#4D7C0F" 
                strokeDasharray="4 4" 
                strokeWidth="1.2"
                opacity="0.4"
              />
            );
          })()}

          {/* Bars */}
          {days.map((day, idx) => {
            const val = values[idx];
            const barH = (val / maxVal) * height;
            const x = 45 + idx * spacing;
            const y = height - barH + 10;
            const isHovered = hoveredIdx === idx;

            return (
              <g 
                key={day}
                onMouseEnter={() => setHoveredIdx(idx)}
                onMouseLeave={() => setHoveredIdx(null)}
                className="cursor-pointer"
              >
                {/* Clean calm academic sage bar */}
                <rect
                  x={x}
                  y={y}
                  width={barWidth}
                  height={barH}
                  rx="3"
                  fill={isHovered ? "#2A5C43" : "#4D7C0F"}
                  opacity={isHovered ? 1 : 0.8}
                  className="transition-all duration-200"
                />

                {/* Day label */}
                <text
                  x={x + barWidth / 2}
                  y={height + 24}
                  fill={isHovered ? "#1E2522" : "#78716C"}
                  fontSize="11"
                  fontWeight={isHovered ? "600" : "400"}
                  fontFamily="system-ui"
                  textAnchor="middle"
                >
                  {day}
                </text>

                {/* Hover value indicator */}
                {isHovered && (
                  <g>
                    <rect 
                      x={x - 4} 
                      y={Math.max(y - 20, 0)} 
                      width={barWidth + 8} 
                      height="16" 
                      rx="3" 
                      fill="#1E2522" 
                    />
                    <text
                      x={x + barWidth / 2}
                      y={Math.max(y - 8, 12)}
                      fill="#FFFFFF"
                      fontSize="9"
                      fontWeight="bold"
                      fontFamily="system-ui"
                      textAnchor="middle"
                    >
                      {val}h
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

