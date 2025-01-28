package com.homerendez.repository;

import com.homerendez.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByUserId(Long userId); // Fetch listings by the owner (user)
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.listing.id = :listingId")
    void deleteCommentsByListingId(@Param("listingId") Long listingId);

}
