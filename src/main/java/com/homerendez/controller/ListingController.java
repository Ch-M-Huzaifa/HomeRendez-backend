package com.homerendez.controller;

import com.homerendez.dto.ListingResponse;
import com.homerendez.entity.Listing;
import com.homerendez.entity.User;
import com.homerendez.service.ListingService;
import com.homerendez.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ListingController.class);

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ListingResponse>> getAllListings() {
        List<Listing> listings = listingService.getAllListings();

        // Convert the listings to ListingResponse DTOs
        List<ListingResponse> responseList = new ArrayList<>();
        for (Listing listing : listings) {
            User owner = listing.getUser();  // Get the listing's owner
            String userName = owner.getFirstName() + " " + owner.getLastName();  // Combine first and last name
            Long userId=listing.getUser().getId();

            // Create ListingResponse and add to the response list
            ListingResponse response = new ListingResponse(
                    listing.getId(),
                    listing.getTitle(),
                    listing.getDescription(),
                    listing.getPrice(),
                    listing.getLocation(),
                    String.join(", ", listing.getImages()),  // Join images list if you want to return it as a string
                    String.join(", ", listing.getAmenities()), // Join amenities list if you want to return it as a string
                    userName,
                    userId
            );

            responseList.add(response);
        }

        return ResponseEntity.ok(responseList);  // Return the list of ListingResponse objects
    }



    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getListingById(@PathVariable Long id) {
        return listingService.getListingById(id)
                .map(listing -> {
                    User owner = listing.getUser();  // Get the listing's owner
                    String userName = owner.getFirstName() + " " + owner.getLastName(); // Combine first and last name
                    Long userId=listing.getUser().getId();
                    // Create the ListingResponse object with the necessary details
                    ListingResponse response = new ListingResponse(
                            listing.getId(),
                            listing.getTitle(),
                            listing.getDescription(),
                            listing.getPrice(),
                            listing.getLocation(),
                            String.join(", ", listing.getImages()),  // Join images list if you want to return it as a string
                            String.join(", ", listing.getAmenities()), // Join amenities list if you want to return it as a string
                            userName,
                            userId

                    );

                    // Return the response
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Listing> createListing(@RequestBody Listing listing) {
        try {
            Listing createdListing = listingService.createListing(listing);
            return new ResponseEntity<>(createdListing, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (e.getMessage().equals("User not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Listing> updateListing(@PathVariable Long id, @RequestBody Listing listing) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Get the username from the authentication principal
        String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        // Retrieve the authenticated user entity from the database
        User user = userService.getUserByEmail(username);

        // Fetch the listing by id
        Listing existingListing = listingService.getListingById(id).orElseThrow(() -> new EntityNotFoundException("Listing not found"));

        // Check if the authenticated user is the owner of the listing
        if (!existingListing.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Update the listing
        Listing updatedListing = listingService.updateListing(id, listing);
        return ResponseEntity.ok(updatedListing);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteListing(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }

        // Get the username from the authentication principal
        String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        // Retrieve the authenticated user entity from the database
        User user = userService.getUserByEmail(username);

        // Fetch the listing by id
        Optional<Listing> optionalListing = listingService.getListingById(id);
        if (optionalListing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
        }

        Listing existingListing = optionalListing.get();

        // Check if the authenticated user is the owner of the listing
        if (!existingListing.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this listing");
        }

        // Delete the listing
        listingService.deleteListing(id);
        return ResponseEntity.ok("Listing deleted successfully");
    }



}
