package com.example.shopfinder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by ofadeyi.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShopNotFoundException extends RuntimeException {
    public ShopNotFoundException(Long shopId) {
        super(String.format("Cuold not find a shop with an id matching: %d", shopId));
    }
}
