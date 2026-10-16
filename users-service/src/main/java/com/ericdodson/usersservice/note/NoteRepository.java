package com.ericdodson.usersservice.note;

import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NoteRepository {
    private final Map<Long, Note> notes = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @PostConstruct
    private void initializeDefaultNotes() {
        LocalDateTime now = LocalDateTime.now();
        
        // Default Note 1
        Note note1 = new Note(
            1L,
            "Welcome to the note-taking app! This is your first note.",
            now,
            now
        );
        
        // Default Note 2
        Note note2 = new Note(
            2L,
            "Remember to buy groceries: milk, bread, eggs, and coffee.",
            now,
            now
        );
        
        // Default Note 3
        Note note3 = new Note(
            3L,
            "Meeting with team tomorrow at 10 AM to discuss project timeline.",
            now,
            now
        );
        
        notes.put(1L, note1);
        notes.put(2L, note2);
        notes.put(3L, note3);
        
        // Set the counter to start after the default notes
        idCounter.set(4);
    }

    // CREATE
    public Note save(Note note) {
        if (note.getId() == null) {
            // New note - generate ID and set timestamps
            note.setId(idCounter.getAndIncrement());
            LocalDateTime now = LocalDateTime.now();
            note.setCreatedAt(now);
            note.setUpdatedAt(now);
        } else {
            // Existing note - only update the updatedAt timestamp
            note.setUpdatedAt(LocalDateTime.now());
        }
        notes.put(note.getId(), note);
        return note;
    }

    // READ - find by ID
    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(notes.get(id));
    }

    // READ - find all notes
    public List<Note> findAll() {
        return new ArrayList<>(notes.values());
    }

    // UPDATE (same as save - will overwrite existing)
    public Note update(Note note) {
        if (note.getId() == null || !notes.containsKey(note.getId())) {
            throw new IllegalArgumentException("Note does not exist");
        }
        // Update the updatedAt timestamp
        note.setUpdatedAt(LocalDateTime.now());
        notes.put(note.getId(), note);
        return note;
    }

    // DELETE
    public void deleteById(Long id) {
        notes.remove(id);
    }

    // Find notes containing specific text (case-insensitive)
    public List<Note> findByTextContaining(String searchText) {
        return notes.values().stream()
            .filter(note -> note.getText().toLowerCase().contains(searchText.toLowerCase()))
            .collect(java.util.stream.Collectors.toList());
    }

    // Check if a note with exact text already exists
    public boolean existsByText(String text) {
        return notes.values().stream()
            .anyMatch(note -> note.getText().equals(text));
    }
}
