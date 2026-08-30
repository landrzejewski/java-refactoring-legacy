package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import pl.training.module7.breakmethod.ManifestEntry;
import pl.training.module7.breakmethod.after.ReleaseManifestBuilder;
import pl.training.module7.breakmethod.before.LegacyReleaseManifestBuilder;

final class BreakMethodEquivalenceTest {
    @Test
    void preservesOrderingRenderingAndTheCallersCollection() {
        var entries = new ArrayList<>(List.of(
                new ManifestEntry("web", "sha-web", 20),
                new ManifestEntry("worker", "sha-worker", 10),
                new ManifestEntry("api", "sha-api", 10)));
        List<ManifestEntry> originalOrder = List.copyOf(entries);

        var legacy = new LegacyReleaseManifestBuilder();
        var refactored = new ReleaseManifestBuilder();

        String expected = "10|api|sha-api\n"
                + "10|worker|sha-worker\n"
                + "20|web|sha-web";

        assertEquals(expected, legacy.build(entries));
        assertEquals(expected, refactored.build(entries));
        assertEquals(originalOrder, entries);
        assertEquals("", legacy.build(List.of()));
        assertEquals("", refactored.build(List.of()));
    }

    @Test
    void preservesValidationTypeMessageAndEncounterOrder() {
        var legacy = new LegacyReleaseManifestBuilder();
        var refactored = new ReleaseManifestBuilder();

        assertSameFailure(
                () -> legacy.build(null),
                () -> refactored.build(null));
        assertSameFailure(
                () -> legacy.build(listContainingNull()),
                () -> refactored.build(listContainingNull()));
        assertSameFailure(
                () -> legacy.build(List.of(new ManifestEntry(null, null, -1))),
                () -> refactored.build(List.of(new ManifestEntry(null, null, -1))));
        assertSameFailure(
                () -> legacy.build(List.of(new ManifestEntry(" ", null, -1))),
                () -> refactored.build(List.of(new ManifestEntry(" ", null, -1))));
        assertSameFailure(
                () -> legacy.build(List.of(new ManifestEntry("api", null, -1))),
                () -> refactored.build(List.of(new ManifestEntry("api", null, -1))));
        assertSameFailure(
                () -> legacy.build(List.of(new ManifestEntry("api", " ", -1))),
                () -> refactored.build(List.of(new ManifestEntry("api", " ", -1))));
        assertSameFailure(
                () -> legacy.build(List.of(new ManifestEntry("api", "sha", -1))),
                () -> refactored.build(List.of(new ManifestEntry("api", "sha", -1))));
    }

    @Test
    void preservesInputOrderWhenAllSortKeysAreEqual() {
        List<ManifestEntry> entries = List.of(
                new ManifestEntry("api", "sha-first", 10),
                new ManifestEntry("api", "sha-second", 10));
        String expected = "10|api|sha-first\n10|api|sha-second";

        assertEquals(expected, new LegacyReleaseManifestBuilder().build(entries));
        assertEquals(expected, new ReleaseManifestBuilder().build(entries));
    }

    private static List<ManifestEntry> listContainingNull() {
        var entries = new ArrayList<ManifestEntry>();
        entries.add(null);
        return entries;
    }

    private static void assertSameFailure(
            Supplier<String> legacyCall,
            Supplier<String> refactoredCall) {

        RuntimeException legacyFailure = assertThrows(
                RuntimeException.class,
                legacyCall::get);
        RuntimeException refactoredFailure = assertThrows(
                RuntimeException.class,
                refactoredCall::get);

        assertEquals(legacyFailure.getClass(), refactoredFailure.getClass());
        assertEquals(legacyFailure.getMessage(), refactoredFailure.getMessage());
    }
}
