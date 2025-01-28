package com.homerendez.service;

import com.homerendez.entity.Comment;
import com.homerendez.entity.Listing;
import com.homerendez.entity.User;
import com.homerendez.repository.CommentRepository;
import com.homerendez.repository.ListingRepository;
import com.homerendez.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    public Comment createComment(Comment comment) {
        // Retrieve the Listing and User from the comment object
        Optional<Listing> listing = listingRepository.findById(comment.getListing().getId());
        Optional<User> user = userRepository.findById(comment.getUserId());

        if (listing.isPresent() && user.isPresent()) {
            comment.setListing(listing.get());
            return commentRepository.save(comment);
        } else {
            throw new RuntimeException("Listing or User not found");
        }
    }



    public List<Comment> getCommentsByListing(Long listingId) {
        return commentRepository.findByListingId(listingId);
    }

    public List<Comment> getCommentsByUser(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment updateComment(Long commentId, String newText) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            Comment existingComment = comment.get();
            existingComment.setText(newText);
            return commentRepository.save(existingComment);
        } else {
            throw new RuntimeException("Comment not found");
        }
    }
}
