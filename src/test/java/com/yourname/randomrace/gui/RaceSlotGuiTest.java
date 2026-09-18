package com.yourname.randomrace.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RaceSlotGuiTest {

    @Test
    void remainingSecondsIsCeiling() {
        assertEquals(3L, RaceSlotGui.secondsRemaining(5000L, 2000L));
        assertEquals(1L, RaceSlotGui.secondsRemaining(5000L, 4999L));
        assertEquals(0L, RaceSlotGui.secondsRemaining(5000L, 5000L));
        assertEquals(0L, RaceSlotGui.secondsRemaining(5000L, 6000L));
    }
}