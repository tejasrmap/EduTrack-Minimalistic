import React from 'react';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Tooltip
} from 'recharts';

interface SubjectDonutChartProps {
  data: { name: string; hours: number; color: string }[];
}

export const SubjectDonutChart: React.FC<SubjectDonutChartProps> = ({ data }) => {
  if (data.length === 0 || data.every(d => d.hours === 0)) {
    return (
      <div className="h-56 flex items-center justify-center text-xs text-slate-secondary italic">
        No study duration recorded yet
      </div>
    );
  }

  return (
    <div className="w-full h-56 select-none flex flex-col sm:flex-row items-center justify-center gap-4">
      <div className="w-40 h-40 flex-shrink-0">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Tooltip
              contentStyle={{
                backgroundColor: '#FFFFFF',
                borderRadius: '8px',
                border: '1px solid #E8ECF2',
                boxShadow: '0 4px 12px -2px rgba(23, 32, 51, 0.08)',
                fontSize: '12px',
                padding: '8px 12px'
              }}
              formatter={(val: unknown) => {
                const num = typeof val === 'number' ? val : 0;
                return [`${num.toFixed(1)}h`, 'Study Time'];
              }}
            />
            <Pie
              data={data}
              innerRadius={45}
              outerRadius={68}
              paddingAngle={3}
              dataKey="hours"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color || '#6477D9'} />
              ))}
            </Pie>
          </PieChart>
        </ResponsiveContainer>
      </div>

      <div className="flex-1 space-y-1.5 max-w-xs w-full">
        {data.slice(0, 5).map(item => (
          <div key={item.name} className="flex items-center justify-between text-xs">
            <div className="flex items-center gap-2 truncate">
              <span className="w-2.5 h-2.5 rounded-full flex-shrink-0" style={{ backgroundColor: item.color }} />
              <span className="truncate text-slate-primary dark:text-slate-200 font-medium">{item.name}</span>
            </div>
            <span className="font-mono text-slate-secondary font-semibold ml-2">{item.hours.toFixed(1)}h</span>
          </div>
        ))}
      </div>
    </div>
  );
};

