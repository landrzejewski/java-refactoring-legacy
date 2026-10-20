package pl.training.module5.boundaryobject.after;

import java.util.Objects;

/**
 * Usługa domenowa operująca wyłącznie na {@link BuildReport}.
 */
public final class ReleaseGate {
    private final BuildReports reports;

    public ReleaseGate(BuildReports reports) {
        this.reports = Objects.requireNonNull(reports, "reports");
    }

    public boolean canRelease(String buildId) {
        BuildReport report = reports.find(buildId);
        return report.succeeded() && report.failedTests() == 0 && report.coverage() >= 80.0;
    }

    public String summary(String buildId) {
        BuildReport report = reports.find(buildId);
        return report.buildId()
                + " [" + report.commit() + "] "
                + report.status()
                + " coverage=" + report.coverage() + "%"
                + " failed=" + report.failedTests();
    }
}
