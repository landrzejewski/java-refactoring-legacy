package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import pl.training.module5.inlinesingleton.after.FeatureFlags;
import pl.training.module5.inlinesingleton.after.RolloutCompositionRoot;
import pl.training.module5.inlinesingleton.after.RolloutService;
import pl.training.module5.inlinesingleton.before.LegacyFeatureFlags;
import pl.training.module5.inlinesingleton.before.LegacyRolloutService;

final class InlineSingletonEquivalenceTest {
    private static final List<Set<String>> COMBINATIONS = List.of(
            Set.of(),
            Set.of("canary"),
            Set.of("dark-launch"),
            Set.of("canary", "dark-launch"),
            Set.of("unrelated"));

    @Test
    @ResourceLock("LegacyFeatureFlags")
    void planIsTheSameForEveryFlagCombination() {
        var legacy = new LegacyRolloutService();
        try {
            for (Set<String> combination : COMBINATIONS) {
                // stary kod: trzeba zmienić globalny stan i pamiętać o sprzątaniu
                LegacyFeatureFlags.getInstance().reset();
                combination.forEach(LegacyFeatureFlags.getInstance()::enable);

                assertEquals(
                        legacy.plan("rel-42"),
                        new RolloutService(new FeatureFlags(combination)).plan("rel-42"),
                        combination.toString());
            }
        } finally {
            LegacyFeatureFlags.getInstance().reset();
        }
    }

    @Test
    void servicesWithDifferentFlagsCanCoexistWithoutSharedState() {
        var canary = new RolloutService(FeatureFlags.of("canary"));
        var plain = new RolloutService(FeatureFlags.of());

        assertEquals("rel-1: 10% -> 50% -> 100%", canary.plan("rel-1"));
        assertEquals("rel-1: 100%", plain.plan("rel-1"));
        assertEquals("rel-1: 10% -> 50% -> 100%", canary.plan("rel-1"));
    }

    @Test
    @ResourceLock("LegacyFeatureFlags")
    void legacySingletonLeaksStateBetweenUsers() {
        var first = new LegacyRolloutService();
        var second = new LegacyRolloutService();
        try {
            LegacyFeatureFlags.getInstance().enable("canary");

            assertEquals("rel-1: 10% -> 50% -> 100%", second.plan("rel-1"));
            assertEquals(first.plan("rel-1"), second.plan("rel-1"));
        } finally {
            LegacyFeatureFlags.getInstance().reset();
        }
    }

    @Test
    void compositionRootBuildsTheServiceFromConfiguration() {
        assertEquals(
                "rel-7: 10% -> 50% -> 100% (dark launch)",
                RolloutCompositionRoot.create(" canary , dark-launch ").plan("rel-7"));
        assertEquals("rel-7: 100%", RolloutCompositionRoot.create("").plan("rel-7"));
    }

    @Test
    void flagsAreImmutableValues() {
        var source = new java.util.HashSet<>(Set.of("canary"));
        var flags = new FeatureFlags(source);
        source.clear();

        assertEquals(FeatureFlags.of("canary"), flags);
        assertThrows(UnsupportedOperationException.class, () -> flags.enabled().add("x"));
        assertEquals(
                assertThrows(IllegalArgumentException.class,
                        () -> new LegacyRolloutService().plan(" ")).getMessage(),
                assertThrows(IllegalArgumentException.class,
                        () -> new RolloutService(FeatureFlags.of()).plan(" ")).getMessage());
    }
}
