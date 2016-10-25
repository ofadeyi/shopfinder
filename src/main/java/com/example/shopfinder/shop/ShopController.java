package com.example.shopfinder.shop;

import com.google.common.collect.Maps;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Created by ofadeyi.
 */
@RestController
public class ShopController {

    private Map<Long, Shop> shops = Maps.newConcurrentMap();


    @RequestMapping(value = "/shops", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<Shop> retrieveShops() {
        if (shops.isEmpty()) {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(shops.values(), HttpStatus.OK);
    }

}
