package com.ericdodson.usersservice.note;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}