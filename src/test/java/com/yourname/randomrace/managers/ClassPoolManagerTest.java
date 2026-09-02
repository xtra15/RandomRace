package com.yourname.randomrace.managers;

import me.athlaeos.valhallaraces.Class;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ClassPoolManagerTest {

    private Class cls(String id, int group, String perm, List<String> limited) {
        return new Class(id, "&c" + id, null, group, perm, limited);
    }

    @Test
    void emptyRaceFilterIncludesClass() {
        Class c = cls("a", 1, null, Collections.emptyList());
        assertTrue(ClassPoolManager.passRaceFilter(c.getLimitedToRaces(), "elf"));
        assertTrue(ClassPoolManager.passRaceFilter(c.getLimitedToRaces(), null));
    }

    @Test
    void nonEmptyRaceFilterExcludesUnlistedRace() {
        Class c = cls("a", 1, null, Arrays.asList("elf", "human"));
        assertTrue(ClassPoolManager.passRaceFilter(c.getLimitedToRaces(), "elf"));
        assertFalse(ClassPoolManager.passRaceFilter(c.getLimitedToRaces(), "dragon"));
    }

    @Test
    void nullRaceExcludedWhenFilterNonEmpty() {
        Class c = cls("a", 1, null, Arrays.asList("elf"));
        assertFalse(ClassPoolManager.passRaceFilter(c.getLimitedToRaces(), null));
    }

    @Test
    void candidatesFilteredByGroupPermAndRace() {
        ClassPoolManager m = new ClassPoolManager(null);
        List<Class> all = Arrays.asList(
                cls("one", 1, null, Collections.emptyList()),
                cls("elf-only", 1, null, Arrays.asList("elf")),
                cls("other-group", 2, null, Collections.emptyList()),
                cls("locked", 1, "some.perm", Collections.emptyList()));
        m.poolForTest(all);
        List<Class> g1 = m.candidatesFor(1, null, "elf");
        List<String> ids = new java.util.ArrayList<>();
        for (Class c : g1) ids.add(c.getName());
        assertTrue(ids.contains("one"));
        assertTrue(ids.contains("elf-only"));
        assertFalse(ids.contains("other-group"));
        assertFalse(ids.contains("locked"));
    }

    @Test
    void pickForGroupReturnsClassInThatGroup() {
        ClassPoolManager m = new ClassPoolManager(null);
        m.poolForTest(Arrays.asList(
                cls("one", 1, null, Collections.emptyList()),
                cls("two", 1, null, Collections.emptyList()),
                cls("three", 2, null, Collections.emptyList())));
        Class picked = m.pickForGroup(new Random(5L), 1, null, null);
        assertNotNull(picked);
        assertEquals(1, picked.getGroup());
    }

    @Test
    void eligibleGroupsOnlyIncludesGroupsWithAvailableClasses() {
        ClassPoolManager m = new ClassPoolManager(null);
        m.poolForTest(Arrays.asList(
                cls("a", 1, null, Collections.emptyList()),
                cls("b", 3, null, Collections.emptyList())));
        List<Integer> eligible = m.eligibleGroups(null, null, null);
        assertEquals(Arrays.asList(1, 3), eligible);
    }

    @Test
    void eligibleGroupsRespectsSkip() {
        ClassPoolManager m = new ClassPoolManager(null);
        m.poolForTest(Arrays.asList(
                cls("a", 1, null, Collections.emptyList()),
                cls("b", 3, null, Collections.emptyList())));
        List<Integer> eligible = m.eligibleGroups(null, null, java.util.Collections.singleton(1));
        assertEquals(Arrays.asList(3), eligible);
    }

    @Test
    void pickRandomGroupsReturnsDistinctEligibleGroups() {
        ClassPoolManager m = new ClassPoolManager(null);
        List<me.athlaeos.valhallaraces.Class> all = new java.util.ArrayList<>();
        for (int g = 1; g <= 5; g++) all.add(cls("g" + g, g, null, Collections.emptyList()));
        m.poolForTest(all);
        List<Integer> picked = m.pickRandomGroups(new Random(5L), 3, null, null, null);
        assertEquals(new java.util.HashSet<>(Arrays.asList(1, 2, 3, 4, 5)),
                new java.util.HashSet<>(m.eligibleGroups(null, null, null)));
        assertEquals(3, picked.size());
        assertEquals(3, new java.util.HashSet<>(picked).size());
        for (int g : picked) assertTrue(g >= 1 && g <= 5);
    }

    @Test
    void pickRandomGroupsCappedByEligibleCount() {
        ClassPoolManager m = new ClassPoolManager(null);
        m.poolForTest(Arrays.asList(cls("a", 1, null, Collections.emptyList())));
        List<Integer> picked = m.pickRandomGroups(new Random(5L), 5, null, null, null);
        assertEquals(Arrays.asList(1), picked);
    }
}
