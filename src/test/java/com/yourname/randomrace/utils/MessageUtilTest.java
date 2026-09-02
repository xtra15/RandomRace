package com.yourname.randomrace.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageUtilTest {
    @Test
    void colorsAmpersandCodes() {
        assertEquals("\u00A7aHello", MessageUtil.color("&aHello"));
    }

    @Test
    void replacesPlaceholders() {
        assertEquals("You are an Elf!", MessageUtil.replace("{race}", "Elf", "You are an {race}!"));
    }

    @Test
    void leavesPlainTextUntouched() {
        assertEquals("plain text", MessageUtil.color("plain text"));
    }
}
