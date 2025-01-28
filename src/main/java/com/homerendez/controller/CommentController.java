package com.homerendez.controller;

import com.homerendez.dto.CommentResponse;
import com.homerendez.entity.Comment;
import com.homerendez.entity.CommentRequest;
import com.homerendez.entity.Listing;
import com.homerendez.entity.User;
import com.homerendez.repository.ListingRepository;
import com.homerendez.repository.UserRepository;
import com.homerendez.service.CommentService;
import com.homerendez.service.ListingService;
import com.homerendez.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    private final CommentService commentService;
    private ListingRepository listingRepository;
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    public CommentController(CommentService commentService, ListingRepository listingRepository, UserRepository userRepository) {
        this.commentService = commentService;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByListing(@PathVariable Long listingId) {
        List<Comment> comments = commentService.getCommentsByListing(listingId);

        List<CommentResponse> commentResponses = comments.stream().map(comment -> {
            // Fetch the user who created the comment
            Optional<User> userOptional = userRepository.findById(comment.getUserId());
            String userName = userOptional
                    .map(user -> user.getFirstName() + " " + user.getLastName())
                    .orElse("Unknown User");

            // Include userId in the CommentResponse
            return new CommentResponse(comment.getId(), comment.getText(), userName, comment.getUserId(), comment.getCreatedAt());
        }).toList();

        return ResponseEntity.ok(commentResponses);
    }



    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUser(@PathVariable Long userId) {
        List<Comment> comments = commentService.getCommentsByUser(userId);
        return ResponseEntity.ok(comments);
    }


    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequest request) {
        try {
            // Retrieve the Listing object from the repository
            Optional<Listing> listing = listingRepository.findById(request.getListingId());
            Optional<User> user = userRepository.findById(request.getUserId());

            // Check if Listing and User exist
            if (listing.isPresent() && user.isPresent()) {
                // Create a new Comment with the Listing object and userId
                Comment newComment = new Comment(request.getText(), listing.get(), user.get().getId());
                Comment createdComment = commentService.createComment(newComment);
                return ResponseEntity.ok(createdComment);
            } else {
                return ResponseEntity.badRequest().body(null); // Handle case where listing or user not found
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/update/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest request) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, request.getText());
            if (updatedComment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
