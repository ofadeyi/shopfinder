package com.example.shopfinder.exceptions;

import com.example.shopfinder.shop.Shop;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by ofadeyi.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ShopAlreadyPresentException extends RuntimeException {
    public ShopAlreadyPresentException(Shop shop) {
        super(String.format("The shop: %s is already defined", shop));
    }
}
