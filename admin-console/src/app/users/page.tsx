import AddUser from '@/features/users/AddUser';
import UserTable from '@/features/users/UserTable';
import { User } from '@/features/users/types';

export default async function UsersPage() {
  const response = await fetch('http://localhost:8080/api/users');
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
