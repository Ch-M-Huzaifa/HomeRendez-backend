package com.homerendez.service;

import com.homerendez.entity.Listing;
import com.homerendez.entity.User;
import com.homerendez.repository.ListingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserService userService;

    public Listing createListing(Listing listing) {
        // Retrieve the currently logged-in user ID
        Long userId = userService.getLoggedInUserId()
                .orElseThrow(() -> new RuntimeException("User not logged in"));

        // Retrieve the User entity using the userId
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Associate the logged-in user with the listing
        listing.setUser(user);

        // Save and return the listing
        return listingRepository.save(listing);
    }


    public Listing updateListing(Long listingId, Listing updatedListing) {
        return listingRepository.findById(listingId).map(existingListing -> {
            existingListing.setTitle(updatedListing.getTitle());
            existingListing.setDescription(updatedListing.getDescription());
            existingListing.setPrice(updatedListing.getPrice());
            existingListing.setLocation(updatedListing.getLocation());
            existingListing.setImages(updatedListing.getImages());
            existingListing.setAmenities(updatedListing.getAmenities());
            return listingRepository.save(existingListing);
        }).orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    @Transactional
    public void deleteListing(Long listingId) {
        if (listingRepository.existsById(listingId)) {
            listingRepository.deleteById(listingId);
        } else {
            throw new RuntimeException("Listing not found");
        }
    }


    public Optional<Listing> getListingById(Long listingId) {
        Optional<Listing> listing = listingRepository.findById(listingId);

        // Ensure user details are fetched if needed (depends on JPA configuration)
        listing.ifPresent(l -> {
            User owner = l.getUser(); // Access the user to ensure it's loaded
        });

        return listing;
    }

    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    public List<Listing> getListingsByOwner(Long userId) {
        return listingRepository.findByUserId(userId);
    }
}
