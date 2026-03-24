import { useState } from 'react';
import { createCard } from '../../api/cards';
import type { CardItem } from '../../types';

interface Props {
  listId: string;
  onAdded: (card: CardItem) => void;
  onClose: () => void;
}

export default function AddCardForm({ listId, onAdded, onClose }: Props) {
  const [title, setTitle] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!title.trim()) return;
    setLoading(true);
    try {
      const { data } = await createCard(listId, title.trim());
      onAdded(data);
      setTitle('');
      onClose();
    } catch {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="mt-2 space-y-2">
      <textarea
        autoFocus
        rows={2}
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="Enter card title..."
        className="w-full px-3 py-2 text-sm border border-indigo-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
        onKeyDown={(e) => {
          if (e.key === 'Escape') onClose();
          if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            e.currentTarget.form?.requestSubmit();
          }
        }}
      />
      <div className="flex gap-2">
        <button
          type="submit"
          disabled={loading || !title.trim()}
          className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white text-xs font-semibold rounded-lg transition"
        >
          {loading ? 'Adding...' : 'Add Card'}
        </button>
        <button
          type="button"
          onClick={onClose}
          className="px-3 py-1.5 text-gray-500 hover:text-gray-700 text-xs transition"
        >
          Cancel
        </button>
      </div>
    </form>
  );
}
