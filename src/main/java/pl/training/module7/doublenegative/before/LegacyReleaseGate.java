package pl.training.module7.doublenegative.before;

import java.util.Objects;

public final class LegacyReleaseGate {
    public boolean canRelease(LegacyReleaseReadiness readiness) {
        Objects.requireNonNull(readiness, "readiness");
        return !readiness.notApproved()
                && !readiness.testsNotPassed()
                && !readiness.windowNotOpen();
    }
}
