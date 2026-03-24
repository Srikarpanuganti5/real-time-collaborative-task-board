import { useNavigate } from 'react-router-dom';
import type { Board } from '../../types';

const BOARD_COLORS = [
  'from-indigo-500 to-purple-600',
  'from-blue-500 to-cyan-600',
  'from-emerald-500 to-teal-600',
  'from-orange-500 to-red-500',
  'from-pink-500 to-rose-600',
  'from-violet-500 to-indigo-600',
];

function getBoardColor(id: string) {
  const index = id.charCodeAt(0) % BOARD_COLORS.length;
  return BOARD_COLORS[index];
}

interface Props {
  board: Board;
}

export default function BoardCard({ board }: Props) {
  const navigate = useNavigate();
  const color = getBoardColor(board.id);

  return (
    <div
      onClick={() => navigate(`/boards/${board.id}`)}
      className="cursor-pointer group rounded-xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-200 hover:-translate-y-1"
    >
      {/* Color header */}
      <div className={`bg-gradient-to-br ${color} h-24 p-4 flex items-start`}>
        <span className="text-white font-bold text-lg leading-tight line-clamp-2">
          {board.title}
        </span>
      </div>

      {/* Info footer */}
      <div className="bg-white px-4 py-3">
        {board.description && (
          <p className="text-gray-500 text-xs mb-2 line-clamp-1">{board.description}</p>
        )}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1.5">
            <svg className="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            <span className="text-xs text-gray-500">{board.members.length} member{board.members.length !== 1 ? 's' : ''}</span>
          </div>
          <span className="text-xs text-indigo-600 font-medium opacity-0 group-hover:opacity-100 transition">
            Open →
          </span>
        </div>
      </div>
    </div>
  );
}
