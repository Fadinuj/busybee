package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonValue;

public class ImageLink {
    private final String value;

    public ImageLink(String value) {
        String v = (value == null) ? "" : value.trim();

        if (!v.isEmpty() && !(v.startsWith("http://") || v.startsWith("https://"))) {
            throw new IllegalArgumentException("Image link must start with http:// or https://");
        }

        this.value = v;
    }

    @JsonValue
    public String get() {
        return value;
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }

    @Override
    public String toString() {
        return value;
    }
}