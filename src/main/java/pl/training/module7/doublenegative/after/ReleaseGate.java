package pl.training.module7.doublenegative.after;

import java.util.Objects;

public final class ReleaseGate {
    public boolean canRelease(ReleaseReadiness readiness) {
        Objects.requireNonNull(readiness, "readiness");
        return readiness.approved()
                && readiness.testsPassed()
                && readiness.windowOpen();
    }
}
