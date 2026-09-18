package com.yourname.randomrace.managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerDataManagerTest {

    @TempDir
    Path tempDir;

    private PlayerDataManager manager() {
        return new PlayerDataManager(new File(tempDir.toFile(), "playerdata.yml"));
    }

    @Test
    void defaultRerollsAndSlots() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        assertEquals(0, m.getRaceRerolls(id));
        assertEquals(0, m.getClassRerolls(id));
        assertEquals(-1, m.getClassSlots(id));
    }

    @Test
    void addAndSetRerollsPersist() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        m.addRaceRerolls(id, 2);
        m.addClassRerolls(id, 3);
        assertEquals(2, m.getRaceRerolls(id));
        assertEquals(3, m.getClassRerolls(id));
        m.setRaceRerolls(id, 1);
        m.setClassRerolls(id, 0);
        assertEquals(1, m.getRaceRerolls(id));
        assertEquals(0, m.getClassRerolls(id));
    }

    @Test
    void setClassSlotsPersists() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        m.setClassSlots(id, 5);
        assertEquals(5, m.getClassSlots(id));
    }

    @Test
    void clearClaimPreservesRerollsSlotsAndRecord() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        m.markJoined(id);
        m.setRace(id, "dragonkin", "Steve");
        m.addRaceRerolls(id, 2);
        m.setClassSlots(id, 6);
        m.clearClaim(id);
        assertNull(m.getRace(id));
        assertEquals(2, m.getRaceRerolls(id));
        assertEquals(6, m.getClassSlots(id));
        assertTrue(m.hasRecord(id));
    }

    @Test
    void markJoinedCreatesRecord() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        assertFalse(m.hasRecord(id));
        m.markJoined(id);
        assertTrue(m.hasRecord(id));
    }

    @Test
    void resolveOfflineFindsByName() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        m.setRace(id, "elf", "Alex");
        assertEquals(id, m.resolveOffline("ALEX"));
    }

    @Test
    void migrateMovesPhantomRerollsToRealUuid() {
        PlayerDataManager m = manager();
        UUID phantom = UUID.randomUUID();
        UUID real = UUID.randomUUID();
        m.setRace(phantom, "elf", "Steve");
        m.addRaceRerolls(phantom, 2);
        m.addClassRerolls(phantom, 3);
        m.setClassSlots(phantom, 5);
        m.migrateByName(real, "Steve");
        assertEquals(2, m.getRaceRerolls(real));
        assertEquals(3, m.getClassRerolls(real));
        assertEquals(5, m.getClassSlots(real));
        assertFalse(m.hasRecord(phantom));
    }

    @Test
    void migrateIsNoOpWhenRealRecordExists() {
        PlayerDataManager m = manager();
        UUID phantom = UUID.randomUUID();
        UUID real = UUID.randomUUID();
        m.setRace(phantom, "elf", "Steve");
        m.addRaceRerolls(phantom, 2);
        m.markJoined(real);
        m.addRaceRerolls(real, 1);
        m.migrateByName(real, "Steve");
        assertEquals(1, m.getRaceRerolls(real));
        assertTrue(m.hasRecord(phantom));
    }

    @Test
    void migrateDoesNotCopyDefaultSlots() {
        PlayerDataManager m = manager();
        UUID phantom = UUID.randomUUID();
        UUID real = UUID.randomUUID();
        m.setRace(phantom, "elf", "Steve");
        m.addRaceRerolls(phantom, 2);
        m.setClassSlots(phantom, -1);
        m.migrateByName(real, "Steve");
        assertEquals(2, m.getRaceRerolls(real));
        assertEquals(-1, m.getClassSlots(real));
        assertFalse(m.hasRecord(phantom));
    }

    @Test
    void raceSlotsDefaultEmpty() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        assertNull(m.getRaceSlot(id, 1));
        assertTrue(m.getRaceSlots(id).isEmpty());
    }

    @Test
    void setRaceSlotPersists() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        m.setRaceSlot(id, 3, "dragonkin");
        assertEquals("dragonkin", m.getRaceSlot(id, 3));
        assertEquals("dragonkin", m.getRaceSlots(id).get(3));
    }

    @Test
    void clearRaceSlotRemovesIt() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        m.setRaceSlot(id, 2, "elf");
        m.setRaceSlot(id, 2, null);
        assertNull(m.getRaceSlot(id, 2));
        assertTrue(m.getRaceSlots(id).isEmpty());
    }

    @Test
    void loadCooldownDefaultZero() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        assertEquals(0L, m.getLoadCooldown(id));
    }

    @Test
    void setLoadCooldownPersists() {
        PlayerDataManager m = manager();
        UUID id = UUID.randomUUID();
        m.setLoadCooldown(id, 123456789L);
        assertEquals(123456789L, m.getLoadCooldown(id));
    }
}