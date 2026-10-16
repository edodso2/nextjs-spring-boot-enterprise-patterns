'use client';

interface NoteProps {
  value: string;
  onChange: (text: string) => void;
}

export function Note({ value, onChange }: NoteProps) {
  return (
    <div className="bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow duration-200">
      <textarea
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full h-48 p-4 border-none resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-inset text-gray-700 placeholder-gray-400"
        placeholder="Start writing your note..."
      />
    </div>
  );
}
