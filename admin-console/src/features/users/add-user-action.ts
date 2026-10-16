'use server';

import { revalidatePath } from 'next/cache';
import { usersServiceUrl } from '@/config';

export async function addUserAction(formData: {
  firstName: string;
  lastName: string;
  email: string;
}) {
  try {
    const response = await fetch(`${usersServiceUrl}/api/users`, {
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
