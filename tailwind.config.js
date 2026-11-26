/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: '#F8FAFC',
        surface: '#FFFFFF',
        primary: {
          DEFAULT: '#6477D9',
          hover: '#5063C7',
          light: '#EEF2FF',
          dark: '#4052B3',
        },
        slate: {
          primary: '#172033',
          secondary: '#64748B',
          muted: '#F1F4F8',
          border: '#E8ECF2',
        },
        success: {
          DEFAULT: '#16A37B',
          light: '#E6F7F2',
        },
        warning: {
          DEFAULT: '#E6A23C',
          light: '#FDF6EC',
        },
        danger: {
          DEFAULT: '#E06464',
          light: '#FDECEC',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      borderRadius: {
        'academic': '12px',
        'academic-sm': '8px',
        'academic-lg': '16px',
      },
      boxShadow: {
        'subtle': '0 1px 3px 0 rgba(0, 0, 0, 0.03), 0 1px 2px -1px rgba(0, 0, 0, 0.02)',
        'subtle-hover': '0 4px 14px -2px rgba(23, 32, 51, 0.06), 0 2px 6px -1px rgba(23, 32, 51, 0.03)',
        'modal': '0 20px 25px -5px rgba(0, 0, 0, 0.08), 0 8px 10px -6px rgba(0, 0, 0, 0.04)',
      }
    },
  },
  plugins: [],
}
