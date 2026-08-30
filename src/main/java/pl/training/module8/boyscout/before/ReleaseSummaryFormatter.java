package pl.training.module8.boyscout.before;

import java.util.List;
import java.util.Objects;

import pl.training.module8.boyscout.DeploymentResult;
import pl.training.module8.boyscout.DeploymentStatus;

public final class ReleaseSummaryFormatter {
    public String format(
            String releaseId,
            List<DeploymentResult> results) {
        Objects.requireNonNull(releaseId, "releaseId");
        Objects.requireNonNull(results, "results");
        if (releaseId.isBlank()) {
            throw new IllegalArgumentException(
                    "releaseId must not be blank");
        }
        if (results.isEmpty()) {
            throw new IllegalArgumentException(
                    "results must not be empty");
        }

        String s = "Release " + releaseId.strip() + "\n";
        int n = 0;
        for (DeploymentResult r : results) {
            Objects.requireNonNull(r, "result");
            if (r.status() == DeploymentStatus.SUCCESS) {
                s = s + "[OK] " + r.environment() + ": "
                        + r.description() + "\n";
                n++;
            } else {
                s = s + "[ERROR] " + r.environment() + ": "
                        + r.description() + "\n";
            }
        }
        return s + "Successful: " + n + "/" + results.size();
    }
}
