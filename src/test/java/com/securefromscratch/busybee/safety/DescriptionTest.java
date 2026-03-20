package com.securefromscratch.busybee.safety;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DescriptionTest {

    @Test
    void shouldAllowBasicFormattingTags() {
        Description description = new Description("<b>bold</b> <i>italic</i> <u>underline</u>");

        String result = description.get();

        assertTrue(result.contains("<b>bold</b>"));
        assertTrue(result.contains("<i>italic</i>"));
        assertTrue(result.contains("<u>underline</u>"));
    }

    @Test
    void shouldAllowSafeLink() {
        Description description = new Description("<a href=\"https://example.com\">link</a>");

        String result = description.get();

        assertTrue(result.contains("href=\"https://example.com\""));
        assertTrue(result.contains(">link</a>"));
        assertTrue(result.contains("rel=\"nofollow\""));
    }

    @Test
    void shouldAllowSafeImage() {
        Description description = new Description("<img src=\"https://example.com/test.png\" alt=\"img\">");

        String result = description.get();

        assertTrue(result.contains("src=\"https://example.com/test.png\""));
        assertTrue(result.contains("alt=\"img\""));
        assertTrue(result.contains("<img"));
    }

    @Test
    void shouldRemoveScriptTag() {
        Description description = new Description("<script>alert(1)</script>");

        String result = description.get();

        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("alert(1)"));
        assertEquals("", result);
    }

    @Test
    void shouldRemoveDangerousOnErrorAttribute() {
        Description description = new Description("<img src=\"x\" onerror=\"alert(1)\">");

        String result = description.get();

        assertTrue(result.contains("<img"));
        assertTrue(result.contains("src=\"x\""));
        assertFalse(result.contains("onerror"));
        assertFalse(result.contains("alert(1)"));
    }

    @Test
    void shouldBlockJavascriptProtocolInLink() {
        Description description = new Description("<a href=\"javascript:alert(1)\">click</a>");

        String result = description.get();

        assertFalse(result.contains("javascript:alert(1)"));
        assertFalse(result.contains("href=\"javascript:alert(1)\""));
        assertTrue(result.contains("click"));
    }

    @Test
    void nullDescriptionShouldBecomeEmptyString() {
        Description description = new Description(null);

        assertEquals("", description.get());
    }
}