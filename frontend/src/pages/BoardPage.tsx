import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { DragDropContext, Droppable, type DropResult } from '@hello-pangea/dnd';
import { getBoard } from '../api/boards';
import { createList } from '../api/lists';
import { moveCard } from '../api/cards';
import type { Board, ListItem, CardItem } from '../types';
import ListColumn from '../components/list/ListColumn';

export default function BoardPage() {
  const { boardId } = useParams<{ boardId: string }>();
  const navigate = useNavigate();

  const [board, setBoard] = useState<Board | null>(null);
  const [lists, setLists] = useState<ListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [newListTitle, setNewListTitle] = useState('');
  const [showAddList, setShowAddList] = useState(false);
  const [addingList, setAddingList] = useState(false);

  useEffect(() => {
    if (!boardId) return;
    getBoard(boardId)
      .then(({ data }) => {
        setBoard(data);
        setLists(data.lists ?? []);
      })
      .catch(() => navigate('/boards'))
      .finally(() => setLoading(false));
  }, [boardId, navigate]);

  const handleAddList = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!newListTitle.trim() || !boardId) return;
    setAddingList(true);
    try {
      const { data } = await createList(boardId, newListTitle.trim());
      setLists((prev) => [...prev, { ...data, cards: [] }]);
      setNewListTitle('');
      setShowAddList(false);
    } catch {
      // ignore
    } finally {
      setAddingList(false);
    }
  };

  const handleCardAdded = (card: CardItem, listId: string) => {
    setLists((prev) =>
      prev.map((l) =>
        l.id === listId
          ? { ...l, cards: [...l.cards, card] }
          : l
      )
    );
  };

  const handleCardDeleted = (cardId: string, listId: string) => {
    setLists((prev) =>
      prev.map((l) =>
        l.id === listId
          ? { ...l, cards: l.cards.filter((c) => c.id !== cardId) }
          : l
      )
    );
  };

  const handleListDeleted = (listId: string) => {
    setLists((prev) => prev.filter((l) => l.id !== listId));
  };

  const handleDragEnd = async (result: DropResult) => {
    const { source, destination, type } = result;
    if (!destination) return;
    if (source.droppableId === destination.droppableId && source.index === destination.index) return;

    if (type === 'LIST') {
      const reordered = Array.from(lists);
      const [moved] = reordered.splice(source.index, 1);
      reordered.splice(destination.index, 0, moved);
      setLists(reordered);
      return;
    }

    if (type === 'CARD') {
      const sourceList = lists.find((l) => l.id === source.droppableId);
      const destList = lists.find((l) => l.id === destination.droppableId);
      if (!sourceList || !destList) return;

      const card = sourceList.cards[source.index];

      // Optimistic update
      const newLists = lists.map((l) => {
        if (l.id === source.droppableId) {
          const cards = Array.from(l.cards);
          cards.splice(source.index, 1);
          return { ...l, cards };
        }
        if (l.id === destination.droppableId) {
          const cards = Array.from(l.cards);
          cards.splice(destination.index, 0, { ...card, listId: destination.droppableId });
          return { ...l, cards };
        }
        return l;
      });
      setLists(newLists);

      try {
        await moveCard(card.id, destination.droppableId, destination.index);
      } catch {
        // Revert on failure
        setLists(lists);
      }
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!board) return null;

  return (
    <div className="h-[calc(100vh-56px)] flex flex-col overflow-hidden">
      {/* Board header */}
      <div className="flex items-center gap-4 px-6 py-3 bg-indigo-700 text-white">
        <button onClick={() => navigate('/boards')} className="text-indigo-200 hover:text-white transition">
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="text-lg font-bold">{board.title}</h1>
        {board.description && (
          <span className="text-indigo-200 text-sm hidden md:block">— {board.description}</span>
        )}
        <div className="ml-auto flex items-center gap-2">
          {board.members.map((m) => (
            <div
              key={m.userId}
              title={`${m.username} (${m.role})`}
              className="w-8 h-8 rounded-full bg-indigo-500 border-2 border-indigo-300 flex items-center justify-center text-xs font-bold uppercase"
            >
              {m.username[0]}
            </div>
          ))}
        </div>
      </div>

      {/* Lists */}
      <div className="flex-1 overflow-x-auto overflow-y-hidden p-4">
        <DragDropContext onDragEnd={handleDragEnd}>
          <Droppable droppableId="board" type="LIST" direction="horizontal">
            {(provided) => (
              <div
                ref={provided.innerRef}
                {...provided.droppableProps}
                className="flex gap-3 h-full items-start"
              >
                {lists.map((list, index) => (
                  <ListColumn
                    key={list.id}
                    list={list}
                    index={index}
                    onCardAdded={handleCardAdded}
                    onCardDeleted={handleCardDeleted}
                    onListDeleted={handleListDeleted}
                  />
                ))}
                {provided.placeholder}

                {/* Add list */}
                <div className="flex-shrink-0 w-72">
                  {showAddList ? (
                    <form
                      onSubmit={handleAddList}
                      className="bg-gray-100 rounded-xl p-3 space-y-2"
                    >
                      <input
                        autoFocus
                        type="text"
                        value={newListTitle}
                        onChange={(e) => setNewListTitle(e.target.value)}
                        placeholder="List title..."
                        className="w-full px-3 py-2 text-sm border border-indigo-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        onKeyDown={(e) => e.key === 'Escape' && setShowAddList(false)}
                      />
                      <div className="flex gap-2">
                        <button
                          type="submit"
                          disabled={addingList || !newListTitle.trim()}
                          className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white text-xs font-semibold rounded-lg transition"
                        >
                          {addingList ? 'Adding...' : 'Add List'}
                        </button>
                        <button
                          type="button"
                          onClick={() => setShowAddList(false)}
                          className="px-3 py-1.5 text-gray-500 hover:text-gray-700 text-xs"
                        >
                          Cancel
                        </button>
                      </div>
                    </form>
                  ) : (
                    <button
                      onClick={() => setShowAddList(true)}
                      className="w-full flex items-center gap-2 bg-white/60 hover:bg-white text-gray-600 hover:text-indigo-700 rounded-xl px-4 py-3 text-sm font-medium transition shadow-sm"
                    >
                      <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                      Add a list
                    </button>
                  )}
                </div>
              </div>
            )}
          </Droppable>
        </DragDropContext>
      </div>
    </div>
  );
}
