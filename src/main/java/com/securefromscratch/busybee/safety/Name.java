package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.regex.Pattern;

public class Name {
    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 100;
    private static final Pattern SAFE_PATTERN = Pattern.compile("^[a-zA-Z0-9 \\-_.!?#א-ת]*$");

    private final String value;

    public Name(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Invalid name length");
        }
        if (!SAFE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Name contains illegal characters");
        }
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}