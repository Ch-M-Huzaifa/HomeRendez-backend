package com.homerendez.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    @JsonIgnoreProperties("comments")
    private Listing listing;

    @Column(nullable = false)
    private Long userId;

    private LocalDateTime createdAt;

    public Comment(String text, Listing listing, Long userId) {
        this.text = text;
        this.listing = listing;
        this.userId = userId;
        this.createdAt = LocalDateTime.now(); // Consider setting this in the constructor
    }

}
