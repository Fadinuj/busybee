package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ValidTime {
    private final LocalTime value;

    public ValidTime(String timeString) {
        if (timeString == null) throw new IllegalArgumentException("Time is required");
        this.value = LocalTime.parse(timeString);
    }

    public LocalTime getTime() {
        return value;
    }

    public boolean isPastWhen(LocalDate date) {
        if (date == null) return false;
        return LocalDateTime.of(date, value).isBefore(LocalDateTime.now());
    }

    @JsonValue
    @Override
    public String toString() {
        return value.toString();
    }
}