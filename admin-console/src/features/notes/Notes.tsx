'use client';

import { useRef, useState } from 'react';
import useSWR from 'swr';
import { Note } from './Note';
import { Note as NoteType } from './types';
import { getNotes, updateNote } from './actions';

/**
 * Instead of retry message we should do three auto retires with backoff
 * after three retries show "trouble saving messages". Then only retry every few seconds.
 *
 * If user goes offline show a "offline enabled" message. Same logic as retry but only
 * sync when back online.
 *
 * This will use the OfflineProvider that stores all mutations. It will override
 * previous mutations. The args used to call the mutation will be the key.
 *
 * Bonus: store in localstorage, when back online try to sync. OR, every once in
 * a while try to sync.
 */

export function Notes() {
  const { data, error, isLoading, mutate } = useSWR<NoteType[]>(
    'notes',
    getNotes
  );
  const [showSaveError, setShowSaveError] = useState(false);

  const timeoutRef = useRef<NodeJS.Timeout>(undefined);
  const pendingSaveRef = useRef<{ id: string; text: string } | null>(null);

  const handleNoteChange = async (id: string, text: string) => {
    setShowSaveError(false);

    // Update UI immediately (optimistic update)
    const optimisticData = data?.map((note) =>
      note.id === id ? { ...note, text } : note
    );

    mutate(optimisticData, { revalidate: false });

    // Clear previous timeout
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }

    // Set new timeout for server update
    timeoutRef.current = setTimeout(async () => {
      try {
        await updateNote(id, { text });

        pendingSaveRef.current = null;
      } catch (error) {
        pendingSaveRef.current = { id, text };
        console.log('Error saving note');
        setShowSaveError(true);
      }
    }, 500);
  };

  const handleRetry = async () => {
    const pending = pendingSaveRef.current;
    if (!pending) return;

    setShowSaveError(false);
    try {
      await updateNote(pending.id, { text: pending.text });
      pendingSaveRef.current = null;
    } catch (error) {
      setShowSaveError(true);
    }
  };

  if (error) return <div>failed to load</div>;
  if (isLoading) return <div>loading...</div>;
  if (!data) return <div>no data</div>;

  return (
    <>
      {showSaveError && (
        <div className="fixed top-4 left-1/2 -translate-x-1/2 z-50 transition-opacity duration-300">
          <div className="bg-green-500 text-white text-sm font-medium px-4 py-2 rounded shadow-lg">
            Error saving note <button onClick={handleRetry}>retry</button>
          </div>
        </div>
      )}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {data.map(({ id, text }) => (
          <Note
            key={id}
            value={text}
            onChange={(text) => handleNoteChange(id, text)}
          />
        ))}
      </div>
    </>
  );
}
