package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import pl.training.module7.parameterobject.after.RolloutPlanner;
import pl.training.module7.parameterobject.after.RolloutSpec;
import pl.training.module7.parameterobject.before.LegacyRolloutPlanner;

final class ParameterObjectEquivalenceTest {
    @Test
    void preservesEstimationAndDescription() {
        var legacy = new LegacyRolloutPlanner();
        var refactored = new RolloutPlanner();
        var spec = new RolloutSpec("payments", "eu-central-1", 10, 3, 15);

        assertEquals(
                165,
                legacy.estimateSeconds("payments", "eu-central-1", 10, 3, 15));
        assertEquals(
                legacy.estimateSeconds("payments", "eu-central-1", 10, 3, 15),
                refactored.estimateSeconds(spec));

        String expected = "service=payments;region=eu-central-1;instances=10;"
                + "batchSize=3;pauseSeconds=15";
        assertEquals(
                expected,
                legacy.describe("payments", "eu-central-1", 10, 3, 15));
        assertEquals(expected, refactored.describe(spec));
    }

    @Test
    void usesOverflowSafeCeilingDivision() {
        var legacy = new LegacyRolloutPlanner();
        var refactored = new RolloutPlanner();
        var spec = new RolloutSpec(
                "search",
                "eu-west-1",
                Integer.MAX_VALUE,
                2,
                0);

        long expected = 32_212_254_720L;
        assertEquals(
                expected,
                legacy.estimateSeconds(
                        "search",
                        "eu-west-1",
                        Integer.MAX_VALUE,
                        2,
                        0));
        assertEquals(expected, refactored.estimateSeconds(spec));
    }

    @Test
    void movesValidationFromEveryOperationToParameterObjectConstruction() {
        var legacy = new LegacyRolloutPlanner();

        IllegalArgumentException legacyFailure = assertThrows(
                IllegalArgumentException.class,
                () -> legacy.describe("payments", "eu-central-1", 0, 0, -1));
        IllegalArgumentException constructionFailure = assertThrows(
                IllegalArgumentException.class,
                () -> new RolloutSpec("payments", "eu-central-1", 0, 0, -1));

        assertEquals("instances must be greater than zero", legacyFailure.getMessage());
        assertEquals(legacyFailure.getMessage(), constructionFailure.getMessage());
    }

    @Test
    void preservesValidationTypeMessageAndOrder() {
        var legacy = new LegacyRolloutPlanner();

        assertSameFailure(
                () -> legacy.describe(null, null, 0, 0, -1),
                () -> new RolloutSpec(null, null, 0, 0, -1));
        assertSameFailure(
                () -> legacy.describe(" ", null, 0, 0, -1),
                () -> new RolloutSpec(" ", null, 0, 0, -1));
        assertSameFailure(
                () -> legacy.describe("api", null, 0, 0, -1),
                () -> new RolloutSpec("api", null, 0, 0, -1));
        assertSameFailure(
                () -> legacy.describe("api", " ", 0, 0, -1),
                () -> new RolloutSpec("api", " ", 0, 0, -1));
        assertSameFailure(
                () -> legacy.describe("api", "eu", 0, 0, -1),
                () -> new RolloutSpec("api", "eu", 0, 0, -1));
        assertSameFailure(
                () -> legacy.describe("api", "eu", 1, 0, -1),
                () -> new RolloutSpec("api", "eu", 1, 0, -1));
        assertSameFailure(
                () -> legacy.describe("api", "eu", 1, 1, -1),
                () -> new RolloutSpec("api", "eu", 1, 1, -1));
    }

    @Test
    void rejectsNullParameterObjectAtTheNewApiBoundary() {
        var planner = new RolloutPlanner();

        NullPointerException estimateFailure = assertThrows(
                NullPointerException.class,
                () -> planner.estimateSeconds(null));
        NullPointerException describeFailure = assertThrows(
                NullPointerException.class,
                () -> planner.describe(null));

        assertEquals("spec", estimateFailure.getMessage());
        assertEquals("spec", describeFailure.getMessage());
    }

    private static void assertSameFailure(
            Supplier<?> legacyCall,
            Supplier<?> parameterObjectConstruction) {

        RuntimeException legacyFailure = assertThrows(
                RuntimeException.class,
                legacyCall::get);
        RuntimeException constructionFailure = assertThrows(
                RuntimeException.class,
                parameterObjectConstruction::get);

        assertEquals(legacyFailure.getClass(), constructionFailure.getClass());
        assertEquals(legacyFailure.getMessage(), constructionFailure.getMessage());
    }
}
