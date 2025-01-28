package com.homerendez.controller;

import com.homerendez.entity.Listing;

public class ListingRequest {
    private Listing listing;
    private Long userId;

    // Getters and Setters
    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
