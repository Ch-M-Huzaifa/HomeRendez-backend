package com.homerendez.dto;

import com.homerendez.entity.Listing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ListingResponse {
    private Long id;
    private String title;
    private String description;
    private double price;
    private String location;
    private String images;
    private String amenities;
    private String userName;
    private Long userId;
}
