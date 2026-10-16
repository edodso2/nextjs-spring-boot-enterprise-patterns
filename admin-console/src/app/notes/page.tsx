import { Notes } from "@/features/notes/Notes";

export default function UsersPage() {

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6 text-gray-900 dark:text-white">
        Notes
      </h1>

      <Notes />
    </div>
  );
}
