package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public class ResponsibilityOf {
    private final List<String> value;

    @JsonCreator // זה השורה שחסרה לך כדי ש-Spring יבין שהמערך ב-JSON שייך לכאן
    public ResponsibilityOf(List<String> usernames) {
        this.value = (usernames == null) ? List.of() : usernames;
    }

    public String[] toArray() {
        return value.toArray(new String[0]);
    }

    @JsonValue
    public List<String> getResponsibilities() {
        return value;
    }
}