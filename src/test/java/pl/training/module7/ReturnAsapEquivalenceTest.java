package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module7.returnasap.Artifact;
import pl.training.module7.returnasap.after.ArtifactFinder;
import pl.training.module7.returnasap.before.LegacyArtifactFinder;

final class ReturnAsapEquivalenceTest {
    private final LegacyArtifactFinder before = new LegacyArtifactFinder();
    private final ArtifactFinder after = new ArtifactFinder();

    @Test
    void earlyReturnPreservesEmptyMissingAndPresentResults() {
        var first = new Artifact("api.jar", "sha-1");
        var second = new Artifact("worker.jar", "sha-2");
        var artifacts = List.of(first, second);

        assertEquals(
                before.findByChecksum(List.of(), "missing"),
                after.findByChecksum(List.of(), "missing"));
        assertEquals(
                before.findByChecksum(artifacts, "missing"),
                after.findByChecksum(artifacts, "missing"));
        assertSame(
                first,
                after.findByChecksum(artifacts, "sha-1").orElseThrow());
        assertSame(
                second,
                after.findByChecksum(artifacts, "sha-2").orElseThrow());
        assertEquals(
                before.findByChecksum(artifacts, "sha-2"),
                after.findByChecksum(artifacts, "sha-2"));
    }

    @Test
    void bothVersionsReturnTheFirstMatchingInstance() {
        var first = new Artifact("api.jar", "same");
        var second = new Artifact("worker.jar", "same");
        var artifacts = List.of(first, second);

        assertSame(
                first,
                before.findByChecksum(artifacts, "same").orElseThrow());
        assertSame(
                first,
                after.findByChecksum(artifacts, "same").orElseThrow());
    }

    @Test
    void nullElementIsReadBeforeAMatchButNotAfterIt() {
        var matching = new Artifact("api.jar", "sha-1");
        List<Artifact> nullBeforeMatch = Arrays.asList(null, matching);
        List<Artifact> nullAfterMatch = Arrays.asList(matching, null);

        assertEquals(
                assertThrows(
                        NullPointerException.class,
                        () -> before.findByChecksum(nullBeforeMatch, "sha-1"))
                        .getMessage(),
                assertThrows(
                        NullPointerException.class,
                        () -> after.findByChecksum(nullBeforeMatch, "sha-1"))
                        .getMessage());
        assertSame(
                matching,
                before.findByChecksum(nullAfterMatch, "sha-1").orElseThrow());
        assertSame(
                matching,
                after.findByChecksum(nullAfterMatch, "sha-1").orElseThrow());
    }

    @Test
    void inputValidationHasTheSameOrderAndMessages() {
        assertSameFailure(null, "sha-1");
        assertSameFailure(List.of(), null);
    }

    @Test
    void earlyReturnPreservesIndexedListAccesses() {
        List<Artifact> artifacts = List.of(
                new Artifact("api.jar", "sha-1"),
                new Artifact("worker.jar", "sha-2"));
        List<String> legacyTrace = new ArrayList<>();
        List<String> refactoredTrace = new ArrayList<>();

        before.findByChecksum(traced(artifacts, legacyTrace), "sha-1");
        after.findByChecksum(traced(artifacts, refactoredTrace), "sha-1");

        assertEquals(List.of("size", "get:0"), legacyTrace);
        assertEquals(legacyTrace, refactoredTrace);
    }

    private void assertSameFailure(List<Artifact> artifacts, String checksum) {
        assertEquals(
                assertThrows(
                        NullPointerException.class,
                        () -> before.findByChecksum(artifacts, checksum))
                        .getMessage(),
                assertThrows(
                        NullPointerException.class,
                        () -> after.findByChecksum(artifacts, checksum))
                        .getMessage());
    }

    private static List<Artifact> traced(
            List<Artifact> artifacts,
            List<String> trace) {
        return new AbstractList<>() {
            @Override
            public Artifact get(int index) {
                trace.add("get:" + index);
                return artifacts.get(index);
            }

            @Override
            public int size() {
                trace.add("size");
                return artifacts.size();
            }
        };
    }
}
