package com.yourname.randomrace.managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RerollServiceTest {

    @TempDir
    Path tempDir;

    private RerollService service(int defaultSlots) {
        File file = new File(tempDir.toFile(), "d" + System.nanoTime() + ".yml");
        return new RerollService(new PlayerDataManager(file), defaultSlots);
    }

    @Test
    void spendRaceRerollWithNoPointsFails() {
        RerollService s = service(3);
        UUID id = UUID.randomUUID();
        assertFalse(s.trySpendRaceReroll(id));
        assertEquals(0, s.raceRerolls(id));
    }

    @Test
    void spendRaceRerollWithPointsSucceedsAndDecrements() {
        RerollService s = service(3);
        UUID id = UUID.randomUUID();
        s.addRaceRerolls(id, 2);
        assertTrue(s.trySpendRaceReroll(id));
        assertEquals(1, s.raceRerolls(id));
        assertTrue(s.trySpendRaceReroll(id));
        assertEquals(0, s.raceRerolls(id));
        assertFalse(s.trySpendRaceReroll(id));
    }

    @Test
    void spendClassRerollUsesSeparatePool() {
        RerollService s = service(3);
        UUID id = UUID.randomUUID();
        s.addClassRerolls(id, 1);
        assertTrue(s.trySpendClassReroll(id));
        assertEquals(0, s.classRerolls(id));
        assertFalse(s.trySpendRaceReroll(id));
    }

    @Test
    void classSlotsFallBackToDefaultWhenUnset() {
        RerollService s = service(4);
        UUID id = UUID.randomUUID();
        assertEquals(4, s.classSlots(id));
        s.setClassSlots(id, 7);
        assertEquals(7, s.classSlots(id));
    }

    @Test
    void setterClampsRanges() {
        RerollService s = service(3);
        UUID id = UUID.randomUUID();
        s.setClassSlots(id, 99);
        assertEquals(10, s.classSlots(id));
        s.setClassSlots(id, 0);
        assertEquals(1, s.classSlots(id));
        s.setRaceRerolls(id, -5);
        assertEquals(0, s.raceRerolls(id));
    }

    @Test
    void hasAnyRerollsTracksBothTypes() {
        RerollService s = service(3);
        UUID id = UUID.randomUUID();
        assertFalse(s.hasAnyRerolls(id));
        s.addClassRerolls(id, 1);
        assertTrue(s.hasAnyRerolls(id));
        s.setClassRerolls(id, 0);
        assertFalse(s.hasAnyRerolls(id));
    }
}