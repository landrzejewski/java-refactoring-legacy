package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.module7.doublenegative.after.ReleaseGate;
import pl.training.module7.doublenegative.after.ReleaseReadiness;
import pl.training.module7.doublenegative.before.LegacyReleaseGate;
import pl.training.module7.doublenegative.before.LegacyReleaseReadiness;

final class DoubleNegativeEquivalenceTest {
    @Test
    void positiveNamesPreserveAllEightTruthTableRows() {
        var before = new LegacyReleaseGate();
        var after = new ReleaseGate();
        boolean[] values = {false, true};

        for (boolean notApproved : values) {
            for (boolean testsNotPassed : values) {
                for (boolean windowNotOpen : values) {
                    boolean legacyResult = before.canRelease(
                            new LegacyReleaseReadiness(
                                    notApproved,
                                    testsNotPassed,
                                    windowNotOpen));
                    boolean refactoredResult = after.canRelease(
                            new ReleaseReadiness(
                                    !notApproved,
                                    !testsNotPassed,
                                    !windowNotOpen));

                    assertEquals(legacyResult, refactoredResult);
                    assertEquals(
                            !notApproved
                                    && !testsNotPassed
                                    && !windowNotOpen,
                            refactoredResult);
                }
            }
        }
    }

    @Test
    void bothGatesRejectMissingReadinessWithTheSameContract() {
        RuntimeException legacyFailure = assertThrows(
                RuntimeException.class,
                () -> new LegacyReleaseGate().canRelease(null));
        RuntimeException refactoredFailure = assertThrows(
                RuntimeException.class,
                () -> new ReleaseGate().canRelease(null));

        assertEquals(legacyFailure.getClass(), refactoredFailure.getClass());
        assertEquals(legacyFailure.getMessage(), refactoredFailure.getMessage());
    }
}
