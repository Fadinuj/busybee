package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonValue;

public class Times {
    public static final int MIN_TIMES = 1;
    public static final int MAX_TIMES = 100;

    private final int value;

    public Times(String valueStr) {
        try {
            int parsed = Integer.parseInt(valueStr);
            validate(parsed);
            this.value = parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Times must be a valid integer");
        }
    }

    public Times(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int val) {
        if (val < MIN_TIMES || val > MAX_TIMES) {
            throw new IllegalArgumentException("Times must be between " + MIN_TIMES + " and " + MAX_TIMES);
        }
    }

    @JsonValue
    public int get() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}