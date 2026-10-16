package com.ericdodson.usersservice.note;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private static final Logger log = LoggerFactory.getLogger(NoteController.class);
    
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        log.info("Creating note with text: {}", 
            note.getText() != null && note.getText().length() > 50 
                ? note.getText().substring(0, 50) + "..." 
                : note.getText());
        Note created = noteService.createNote(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Note>> getNotes(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            log.info("Searching notes with text: {}", search);
            List<Note> notes = noteService.searchNotes(search);
            return ResponseEntity.ok(notes);
        } else {
            log.info("Getting all notes");
            List<Note> notes = noteService.getNotes();
            return ResponseEntity.ok(notes);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        log.info("Getting note by id: {}", id);
        Note note = noteService.getNoteById(id);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note) {
        log.info("Updating note with id: {}", id);
        // Note updated = noteService.updateNote(id, note);

        try {
            Thread.sleep(2000); // Sleep for 1 seconds (10000 milliseconds)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            log.warn("Thread was interrupted during sleep", e);
        }

        throw new IllegalArgumentException("BITCH I JUST MIGHT");

        // return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.info("Deleting note with id: {}", id);
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}
