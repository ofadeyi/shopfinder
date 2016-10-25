package com.example.shopfinder.shop;

import com.example.shopfinder.exceptions.GeolocationException;
import com.example.shopfinder.exceptions.ShopAlreadyPresentException;
import com.example.shopfinder.exceptions.ShopNotFoundException;
import com.example.shopfinder.geo.DistanceCalculator;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by ofadeyi.
 */
@RestController
public class ShopController {
    @Value("${geolocation.baseUrl}")
    private String geolocationBaseUrl;

    @Value("${geolocation.maxRange}")
    private double maxRange;

    @Value("${geolocation.unit}")
    private String unit;


    private Map<Long, Shop> shops = Maps.newConcurrentMap();
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/shops", method = RequestMethod.DELETE)
    public ResponseEntity<?> clearShops() {
        shops.clear();
        counter.set(0l);
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }

    @RequestMapping(value = "/shops", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<Shop> retrieveShops() {
        if (shops.isEmpty()) {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(shops.values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/shops", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<?> createShop(@RequestBody Shop newShop) {

        findShops(newShop).
                ifPresent(shop -> {
                    throw new ShopAlreadyPresentException(shop);
                });

        // Call geolocation
        Pair<Double, Double> geoResponse = geoLocate(newShop)
                .orElseThrow(() -> new GeolocationException(newShop));

        Shop shopWithGeolocation = Shop
                .withGeolocation(newShop, counter.incrementAndGet(), geoResponse.getFirst(), geoResponse.getSecond());

        shops.put(shopWithGeolocation.getId(), shopWithGeolocation);

        // Return 201 Created and a location header with the link to the newly created resource
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(shopWithGeolocation.getId()).toUri());

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/shops/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<Shop> retrieveShop(@PathVariable Long id) {
        if (!shops.containsKey(id)) {
            throw new ShopNotFoundException(id);
        }
        return new ResponseEntity(shops.get(id), HttpStatus.OK);
    }
	
	 @RequestMapping(value = "/shops/search", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<Shop> findShops(@RequestParam(value = "customerLatitude") Double latitude,
                                          @RequestParam(value = "customerLongitude") Double longitude) {
        Optional<Set<Shop>> nearShops = findNearShops(latitude, longitude);
        if (nearShops.isPresent()) {
            return new ResponseEntity(nearShops.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
    }

    private Optional<Pair<Double, Double>> geoLocate(Shop newShop) {
        if (newShop == null) {
            return Optional.empty();
        }

        URI geocodingUri = null;
        try {
            geocodingUri = URI.create(geolocationBaseUrl + "?components=postal_code:" + URLEncoder
                    .encode(newShop.getAddress().getPostCode(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return Optional.empty();
        }
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(geocodingUri, String.class);

        if (response.contains("ZERO_RESULTS")) {
            return Optional.empty();
        }
        Map<String, Object> locationMap = JsonPath.parse(response).read("$.results[0].geometry.location");
        Double latitude = Double.parseDouble(locationMap.get("lat").toString());
        Double longitude = Double.parseDouble(locationMap.get("lng").toString());

        return Optional.of(Pair.of(latitude, longitude));
    }

    private Optional<Shop> findShops(Shop aShop) {
        if (shops.isEmpty()) {
            return Optional.empty();
        }

        return shops.values().stream()
                .filter(shop -> (shop.getName().equals(aShop.getName()) &&
                        shop.getAddress().equals(aShop.getAddress())))
                .findFirst();
    }

    private Optional<Set<Shop>> findNearShops(double latitude, double longitude) {
        if (shops.isEmpty()) {
            return Optional.empty();
        }

        Set<Shop> nearShops = shops.values().stream()
                .filter(shop -> isNear(latitude, longitude, shop.getLatitude(), shop.getLongitude()))
                .collect(Collectors.toSet());

        if (nearShops.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(nearShops);
    }


    private boolean isNear(double searchLatitude, double searchLongitude,
                           double shopLatitude, double shopLongitude) {
        double distance = DistanceCalculator.distance(searchLatitude, searchLongitude, shopLatitude, shopLongitude, unit);
        if (distance < maxRange) {
            return true;
        }
        return false;
    }

}
