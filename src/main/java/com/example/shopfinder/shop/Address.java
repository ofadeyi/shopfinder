package com.example.shopfinder.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created by ofadeyi.
 */
public class Address {

    private final String number;
    private final String postCode;

    @JsonCreator
    public Address(@JsonProperty("number") String number, @JsonProperty("postCode") String postCode) {
        this.number = Objects.requireNonNull(number);
        this.postCode = Objects.requireNonNull(postCode);
    }


    public String getNumber() {
        return number;
    }


    public String getPostCode() {
        return postCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return Objects.equals(getNumber(), address.getNumber()) &&
                Objects.equals(StringUtils.trimAllWhitespace(getPostCode()),
                        StringUtils.trimAllWhitespace(address.getPostCode()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), StringUtils.trimAllWhitespace(getPostCode()));
    }

    @Override
    public String toString() {
        return String.format("Address{ number='%s', postCode='%s' }", number, postCode);
    }
}
