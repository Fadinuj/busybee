package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonValue;

public class CommentText {
    private final String value;

    public CommentText(String value) {
        this.value = (value == null) ? "" : value.trim();
    }

    @JsonValue
    public String get() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}