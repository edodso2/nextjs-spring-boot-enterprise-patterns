package com.ericdodson.usersservice.note;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(Note note) {
        if (note.getText() == null || note.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Note text cannot be empty");
        }
        return noteRepository.save(note);
    }

    public List<Note> getNotes() {
        return noteRepository.findAll();
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + id));
    }

    public Note updateNote(Long id, Note updatedNote) {
        Note existingNote = getNoteById(id); // This will throw if not found
        
        if (updatedNote.getText() == null || updatedNote.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Note text cannot be empty");
        }
        
        // Update the existing note with new data
        existingNote.setText(updatedNote.getText());
        return noteRepository.update(existingNote);
    }

    public void deleteNote(Long id) {
        if (!noteRepository.findById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + id);
        }
        noteRepository.deleteById(id);
    }

    public List<Note> searchNotes(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getNotes(); // Return all notes if search text is empty
        }
        return noteRepository.findByTextContaining(searchText);
    }
}
