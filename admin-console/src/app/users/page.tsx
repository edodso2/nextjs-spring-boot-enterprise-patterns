import { connection } from 'next/server';
import { usersServiceUrl } from '@/config';
import AddUser from '@/features/users/AddUser';
import UserTable from '@/features/users/UserTable';
import { User } from '@/features/users/types';

export default async function UsersPage() {
  // Render per request: the users service isn't reachable at build time inside Docker
  await connection();
  const response = await fetch(`${usersServiceUrl}/api/users`);
  const users: User[] = await response.json();

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6 text-gray-900 dark:text-white">
        Users
      </h1>

      <UserTable users={users} />
      <AddUser />
    </div>
  );
}
