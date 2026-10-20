package pl.training.module5.boundaryobject.after;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Obiekt graniczny (Boundary Object): niezmienny, zwalidowany obraz wyniku buildu.
 * Jeśli istnieje, jest poprawny - domena nie musi już niczego parsować ani sprawdzać.
 */
public record BuildReport(
        String buildId,
        Status status,
        double coverage,
        int failedTests,
        String commit) {
    private static final Pattern COMMIT = Pattern.compile("[0-9a-f]{7,40}");

    public BuildReport {
        if (buildId == null || buildId.isBlank()) {
            throw new IllegalArgumentException("buildId must not be blank");
        }
        Objects.requireNonNull(status, "status");
        if (coverage < 0 || coverage > 100) {
            throw new IllegalArgumentException("coverage must be between 0 and 100");
        }
        if (failedTests < 0) {
            throw new IllegalArgumentException("failedTests must not be negative");
        }
        if (commit == null || !COMMIT.matcher(commit).matches()) {
            throw new IllegalArgumentException("commit must be a hex sha");
        }
    }

    public boolean succeeded() {
        return status == Status.SUCCESS;
    }

    public enum Status {
        SUCCESS,
        FAILED,
        RUNNING
    }
}
