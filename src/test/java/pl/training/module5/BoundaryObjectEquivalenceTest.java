package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import pl.training.module5.boundaryobject.LegacyCiClient;
import pl.training.module5.boundaryobject.after.BuildReport;
import pl.training.module5.boundaryobject.after.CiBuildReportsAdapter;
import pl.training.module5.boundaryobject.after.ReleaseGate;
import pl.training.module5.boundaryobject.before.LegacyReleaseGate;

final class BoundaryObjectEquivalenceTest {
    private static final Map<String, Map<String, String>> BUILDS = Map.of(
            "build-1", payload("build-1", "SUCCESS", "82.5", "0", "A1B2C3D"),
            "build-2", payload("build-2", " success ", "80", "0", "a1b2c3d4e5"),
            "build-3", payload("build-3", "SUCCESS", "79.9", "0", "abcdef0"),
            "build-4", payload("build-4", "FAILED", "95", "3", "abcdef1"),
            "build-5", payload("build-5", "running", "0", "0", "abcdef2"));

    private final LegacyCiClient ci = BUILDS::get;

    @Test
    void domainDecisionsAreTheSameForEveryValidPayload() {
        var before = new LegacyReleaseGate(ci);
        var after = new ReleaseGate(new CiBuildReportsAdapter(ci));

        for (String buildId : BUILDS.keySet()) {
            assertEquals(before.canRelease(buildId), after.canRelease(buildId), buildId);
            assertEquals(before.summary(buildId), after.summary(buildId), buildId);
        }
        assertEquals(
                "build-1 [a1b2c3d] SUCCESS coverage=82.5% failed=0",
                after.summary("build-1"));
        assertEquals(
                List.of(true, true, false, false, false),
                BUILDS.keySet().stream().sorted().map(after::canRelease).toList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"status", "coverage", "failed", "commit", "id"})
    void payloadWithMissingFieldIsRejectedAtTheBoundary(String missing) {
        Map<String, String> broken = new HashMap<>(BUILDS.get("build-1"));
        broken.remove(missing);
        var adapter = new CiBuildReportsAdapter(ignored -> broken);

        IllegalArgumentException failure =
                assertThrows(IllegalArgumentException.class, () -> adapter.find("build-1"));

        assertEquals("invalid CI payload: missing " + missing, failure.getMessage());
    }

    @Test
    void malformedValuesAreRejectedAtTheBoundaryInsteadOfInsideTheDomain() {
        var adapter = new CiBuildReportsAdapter(ignored ->
                payload("build-9", "SUCCESS", "eighty", "0", "abcdef0"));
        var legacy = new LegacyReleaseGate(ignored ->
                payload("build-9", "SUCCESS", "eighty", "0", "abcdef0"));

        assertThrows(NumberFormatException.class, () -> legacy.canRelease("build-9"));
        assertEquals(
                "invalid CI payload: For input string: \"eighty\"",
                assertThrows(IllegalArgumentException.class, () -> adapter.find("build-9"))
                        .getMessage());
        assertEquals(
                "invalid CI payload: coverage must be between 0 and 100",
                assertThrows(IllegalArgumentException.class,
                        () -> new CiBuildReportsAdapter(ignored ->
                                payload("b", "SUCCESS", "120", "0", "abcdef0")).find("b"))
                        .getMessage());
    }

    @Test
    void boundaryObjectValidatesItselfIndependentlyOfThePayload() {
        assertThrows(IllegalArgumentException.class,
                () -> new BuildReport("b", BuildReport.Status.SUCCESS, 50, -1, "abcdef0"));
        assertThrows(IllegalArgumentException.class,
                () -> new BuildReport("b", BuildReport.Status.SUCCESS, 50, 0, "not-a-sha"));
    }

    private static Map<String, String> payload(
            String id, String status, String coverage, String failed, String commit) {
        return Map.of(
                "id", id,
                "status", status,
                "coverage", coverage,
                "failed", failed,
                "commit", commit);
    }
}
