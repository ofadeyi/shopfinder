package com.example.shopfinder.exceptions;

import com.example.shopfinder.shop.Shop;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by ofadeyi.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GeolocationException extends RuntimeException {
    public GeolocationException(Shop shop) {
        super(String.format("Error retriving geolocation for shop:", shop));
    }
}
