'use server';

import { revalidatePath } from 'next/cache';

export async function addUserAction(formData: {
  firstName: string;
  lastName: string;
  email: string;
}) {
  try {
    const response = await fetch('http://localhost:8080/api/users', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData),
    });

    // Call revalidatePath to reload the users page so the updated set
    // of users is fetched from the server
    revalidatePath('/users');

    return { success: response.ok };
  } catch (error) {
    console.error(
      error instanceof Error ? error.message : 'Unknown error occurred'
    );
    return { success: false };
  }
}
