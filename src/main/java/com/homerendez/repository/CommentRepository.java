package com.homerendez.repository;

import com.homerendez.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByListingId(Long listingId); // Fetch comments for a specific listing
    List<Comment> findByUserId(Long userId); // Fetch comments created by a specific user
}
