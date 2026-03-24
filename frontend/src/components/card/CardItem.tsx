import { useState } from 'react';
import type { CardItem as CardType } from '../../types';
import { deleteCard } from '../../api/cards';

interface Props {
  card: CardType;
  onDeleted: (cardId: string, listId: string) => void;
  dragHandleProps?: Record<string, unknown>;
  draggableProps?: Record<string, unknown>;
  innerRef?: (el: HTMLDivElement | null) => void;
}

export default function CardItem({ card, onDeleted, dragHandleProps, draggableProps, innerRef }: Props) {
  const [deleting, setDeleting] = useState(false);
  const [showActions, setShowActions] = useState(false);

  const handleDelete = async (e: React.MouseEvent) => {
    e.stopPropagation();
    setDeleting(true);
    try {
      await deleteCard(card.id);
      onDeleted(card.id, card.listId);
    } catch {
      setDeleting(false);
    }
  };

  return (
    <div
      ref={innerRef}
      {...draggableProps}
      className="bg-white rounded-lg shadow-sm border border-gray-200 px-3 py-2.5 group hover:shadow-md transition-shadow cursor-pointer"
      onMouseEnter={() => setShowActions(true)}
      onMouseLeave={() => setShowActions(false)}
    >
      <div className="flex items-start justify-between gap-2">
        <div {...dragHandleProps} className="flex-1 min-w-0">
          <p className="text-sm text-gray-800 font-medium leading-snug break-words">{card.title}</p>
          {card.description && (
            <p className="text-xs text-gray-400 mt-1 line-clamp-2">{card.description}</p>
          )}
        </div>
        {showActions && (
          <button
            onClick={handleDelete}
            disabled={deleting}
            className="flex-shrink-0 text-gray-300 hover:text-red-400 transition"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        )}
      </div>

      {/* Badges */}
      <div className="flex flex-wrap gap-1.5 mt-2">
        {card.assigneeUsername && (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-indigo-50 text-indigo-600 rounded-full text-xs font-medium">
            <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
            </svg>
            {card.assigneeUsername}
          </span>
        )}
        {card.dueDate && (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-amber-50 text-amber-600 rounded-full text-xs font-medium">
            <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            {new Date(card.dueDate).toLocaleDateString()}
          </span>
        )}
      </div>
    </div>
  );
}
