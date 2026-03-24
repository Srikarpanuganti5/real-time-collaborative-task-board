import type { BoardEvent, ListItem, CardItem } from '../types';

type SetLists = React.Dispatch<React.SetStateAction<ListItem[]>>;

export function handleBoardEvent(event: BoardEvent, setLists: SetLists) {
  const { type, payload } = event;

  switch (type) {
    case 'LIST_CREATED': {
      const list = payload as ListItem;
      setLists((prev) => {
        if (prev.find((l) => l.id === list.id)) return prev;
        return [...prev, { ...list, cards: list.cards ?? [] }];
      });
      break;
    }

    case 'LIST_UPDATED': {
      const updated = payload as ListItem;
      setLists((prev) =>
        prev.map((l) => (l.id === updated.id ? { ...l, title: updated.title } : l))
      );
      break;
    }

    case 'LIST_DELETED': {
      const { listId } = payload as { listId: string };
      setLists((prev) => prev.filter((l) => l.id !== listId));
      break;
    }

    case 'LISTS_REORDERED': {
      const reordered = payload as ListItem[];
      setLists((prev) => {
        const cardMap = Object.fromEntries(prev.map((l) => [l.id, l.cards]));
        return reordered.map((l) => ({ ...l, cards: cardMap[l.id] ?? [] }));
      });
      break;
    }

    case 'CARD_CREATED': {
      const card = payload as CardItem;
      setLists((prev) =>
        prev.map((l) =>
          l.id === card.listId && !l.cards.find((c) => c.id === card.id)
            ? { ...l, cards: [...l.cards, card] }
            : l
        )
      );
      break;
    }

    case 'CARD_UPDATED': {
      const card = payload as CardItem;
      setLists((prev) =>
        prev.map((l) =>
          l.id === card.listId
            ? { ...l, cards: l.cards.map((c) => (c.id === card.id ? card : c)) }
            : l
        )
      );
      break;
    }

    case 'CARD_DELETED': {
      const { cardId, listId } = payload as { cardId: string; listId: string };
      setLists((prev) =>
        prev.map((l) =>
          l.id === listId ? { ...l, cards: l.cards.filter((c) => c.id !== cardId) } : l
        )
      );
      break;
    }

    case 'CARD_MOVED': {
      const card = payload as CardItem;
      setLists((prev) => {
        // Remove from all lists first, then insert at new position
        const withoutCard = prev.map((l) => ({
          ...l,
          cards: l.cards.filter((c) => c.id !== card.id),
        }));
        return withoutCard.map((l) => {
          if (l.id !== card.listId) return l;
          const cards = [...l.cards];
          cards.splice(card.position, 0, card);
          return { ...l, cards };
        });
      });
      break;
    }

    default:
      break;
  }
}
