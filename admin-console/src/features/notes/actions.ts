'use server';

import { Note as NoteType } from './types';

export async function getNotes(): Promise<NoteType[]> {
  try {
    const response = await fetch('http://localhost:8080/api/notes');

    if (!response.ok) {
      throw new Error();
    }

    const data: NoteType[] = await response.json();
    return data;
  } catch (error) {
    console.error('Failed to fetch notes:', error);
    throw new Error('Failed to fetch notes');
  }
}

export async function updateNote(
  id: string,
  noteData: Partial<NoteType>
): Promise<NoteType> {
  try {
    const response = await fetch(`http://localhost:8080/api/notes/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(noteData),
    });

    if (!response.ok) {
      throw new Error();
    }

    // Randomly throw error for testing (50% chance)
    // if (Math.random() < 0.5) {
    //   throw new Error('Random test error');
    // }

    const data: NoteType = await response.json();
    return data;
  } catch (error) {
    console.error('Failed to update note:', error);
    throw new Error('Failed to update note');
  }
}
