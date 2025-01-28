package com.homerendez.entity;


import lombok.Data;

@Data
public class CommentRequest {
    private String text;
    private Long listingId;
    private Long userId;
}
