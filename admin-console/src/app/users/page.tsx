import { Suspense } from 'react';
import AddUser from '@/features/users/AddUser';
import UserTable from '@/features/users/UserTable';

export default async function UsersPage() {
  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6 text-gray-900 dark:text-white">
        Users
      </h1>

      <Suspense fallback={<div>Loading users...</div>}>
        <UserTable />
      </Suspense>

      <AddUser />
    </div>
  );
}
