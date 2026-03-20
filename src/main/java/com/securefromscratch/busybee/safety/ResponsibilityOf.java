package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ResponsibilityOf {
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-zא-ת][A-Za-zא-ת0-9 ]*$");

    private final List<String> value;

    @JsonCreator
    public ResponsibilityOf(List<String> usernames) {
        if (usernames == null) {
            this.value = List.of();
            return;
        }

        Set<String> cleaned = new LinkedHashSet<>();

        for (String username : usernames) {
            if (username == null) {
                throw new IllegalArgumentException("responsibilityOf contains null username");
            }

            String trimmed = username.trim();

            if (trimmed.isEmpty()) {
                throw new IllegalArgumentException("responsibilityOf contains empty username");
            }

            if (!USERNAME_PATTERN.matcher(trimmed).matches()) {
                throw new IllegalArgumentException("Invalid username format: " + trimmed);
            }

            cleaned.add(trimmed);
        }

        this.value = new ArrayList<>(cleaned);
    }

    public String[] toArray() {
        return value.toArray(new String[0]);
    }

    @JsonValue
    public List<String> getResponsibilities() {
        return value;
    }
}