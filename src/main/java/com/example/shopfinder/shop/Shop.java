package com.example.shopfinder.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created by ofadeyi.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shop {

    @JsonIgnore
    private final long id;
    @JsonProperty("shopName")
    private final String name;
    @JsonProperty("shopAddress")
    private final Address address;
    @JsonProperty("shopLatitude")
    private final Double latitude;
    @JsonProperty("shopLongitude")
    private final Double longitude;

    @JsonCreator
    public Shop(@JsonProperty("shopName") String name, @JsonProperty("shopAddress") Address address) {
        this(0, name, address, 0.0, 0.0);
    }

    public Shop(long id, String name, Address address, Double latitude, Double longitude) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.address = Objects.requireNonNull(address);
        this.latitude = Objects.requireNonNull(latitude);
        this.longitude = Objects.requireNonNull(longitude);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }


    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shop)) return false;
        Shop shop = (Shop) o;
        return Objects.equals(getName(), shop.getName()) &&
                Objects.equals(address, shop.address) &&
                Objects.equals(getLatitude(), shop.getLatitude()) &&
                Objects.equals(getLongitude(), shop.getLongitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), address, getLatitude(), getLongitude());
    }

    @Override
    public String toString() {
        return String.format("Shop{ id=%d, name='%s', address=%s, latitude=%s, longitude=%s }",
                id, name, address, latitude, longitude);
    }

    public static Shop withGeolocation(Shop aShop, long id, double latitude, double longitude){
        return new Shop(id, aShop.name, aShop.address, latitude, longitude);
    }
}
