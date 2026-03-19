package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;

public class FutureDate {
    private final LocalDate value;

    public FutureDate(String dateString) {
        if (dateString == null) throw new IllegalArgumentException("Date is required");
        LocalDate parsed = LocalDate.parse(dateString);
        if (parsed.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the past");
        }
        this.value = parsed;
    }

    public LocalDate getDate() {
        return value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value.toString();
    }
}