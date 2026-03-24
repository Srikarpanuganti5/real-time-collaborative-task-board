import { useState } from 'react';
import { Droppable, Draggable } from '@hello-pangea/dnd';
import type { ListItem, CardItem } from '../../types';
import CardItemComponent from '../card/CardItem';
import AddCardForm from './AddCardForm';
import { deleteList } from '../../api/lists';

interface Props {
  list: ListItem;
  index: number;
  onCardAdded: (card: CardItem, listId: string) => void;
  onCardDeleted: (cardId: string, listId: string) => void;
  onListDeleted: (listId: string) => void;
}

export default function ListColumn({ list, index, onCardAdded, onCardDeleted, onListDeleted }: Props) {
  const [showAddCard, setShowAddCard] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const handleDeleteList = async () => {
    if (!confirm(`Delete list "${list.title}" and all its cards?`)) return;
    setDeleting(true);
    try {
      await deleteList(list.id);
      onListDeleted(list.id);
    } catch {
      setDeleting(false);
    }
  };

  return (
    <Draggable draggableId={`list-${list.id}`} index={index}>
      {(provided) => (
        <div
          ref={provided.innerRef}
          {...provided.draggableProps}
          className="flex-shrink-0 w-72 bg-gray-100 rounded-xl flex flex-col max-h-full"
        >
          {/* List header */}
          <div
            {...provided.dragHandleProps}
            className="flex items-center justify-between px-3 py-2.5 cursor-grab active:cursor-grabbing"
          >
            <h3 className="font-semibold text-gray-700 text-sm truncate flex-1">
              {list.title}
            </h3>
            <div className="flex items-center gap-1 ml-2">
              <span className="text-xs text-gray-400 font-medium bg-gray-200 rounded-full px-2 py-0.5">
                {list.cards.length}
              </span>
              <button
                onClick={handleDeleteList}
                disabled={deleting}
                className="text-gray-300 hover:text-red-400 transition p-0.5 rounded"
              >
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>
            </div>
          </div>

          {/* Cards */}
          <Droppable droppableId={list.id} type="CARD">
            {(dropProvided, snapshot) => (
              <div
                ref={dropProvided.innerRef}
                {...dropProvided.droppableProps}
                className={`flex-1 overflow-y-auto px-2 pb-2 space-y-2 min-h-[8px] transition-colors ${
                  snapshot.isDraggingOver ? 'bg-indigo-50' : ''
                }`}
                style={{ maxHeight: 'calc(100vh - 220px)' }}
              >
                {list.cards.map((card, cardIndex) => (
                  <Draggable key={card.id} draggableId={card.id} index={cardIndex}>
                    {(cardProvided) => (
                      <CardItemComponent
                        card={card}
                        onDeleted={onCardDeleted}
                        innerRef={cardProvided.innerRef}
                        draggableProps={cardProvided.draggableProps as Record<string, unknown>}
                        dragHandleProps={cardProvided.dragHandleProps as Record<string, unknown>}
                      />
                    )}
                  </Draggable>
                ))}
                {dropProvided.placeholder}
              </div>
            )}
          </Droppable>

          {/* Add card */}
          <div className="px-2 pb-2">
            {showAddCard ? (
              <AddCardForm
                listId={list.id}
                onAdded={(card) => { onCardAdded(card, list.id); setShowAddCard(false); }}
                onClose={() => setShowAddCard(false)}
              />
            ) : (
              <button
                onClick={() => setShowAddCard(true)}
                className="w-full flex items-center gap-1.5 text-gray-500 hover:text-indigo-600 hover:bg-white rounded-lg px-2 py-1.5 text-xs font-medium transition"
              >
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
                Add a card
              </button>
            )}
          </div>
        </div>
      )}
    </Draggable>
  );
}
