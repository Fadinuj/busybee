package com.securefromscratch.busybee.safety;

import com.fasterxml.jackson.annotation.JsonValue;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class Description {
    private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
        .allowElements("a", "b", "i", "u", "img", "p", "br", "div")
        .allowAttributes("href").onElements("a")
        .allowStandardUrlProtocols()
        .allowAttributes("src", "alt").onElements("img")
        .requireRelNofollowOnLinks()
        .toFactory();

    private final String value;

    public Description(String value) {
        if (value == null) {
            this.value = "";
            return;
        }
        this.value = POLICY.sanitize(value);
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