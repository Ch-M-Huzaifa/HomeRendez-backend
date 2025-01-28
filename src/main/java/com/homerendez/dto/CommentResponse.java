package com.homerendez.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String text;
    private String userName;
    private Long userId;
    private LocalDateTime createdAt; // Add this field
}