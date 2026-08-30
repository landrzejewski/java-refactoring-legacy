package pl.training.module8.boyscout.after;

import java.util.List;
import java.util.Objects;

import pl.training.module8.boyscout.DeploymentResult;
import pl.training.module8.boyscout.DeploymentStatus;

public final class ReleaseSummaryFormatter {
    public String format(
            String releaseId,
            List<DeploymentResult> results) {
        validate(releaseId, results);

        StringBuilder summary = new StringBuilder()
                .append("Release ")
                .append(releaseId.strip())
                .append('\n');
        int successfulDeployments = 0;
        for (DeploymentResult result : results) {
            Objects.requireNonNull(result, "result");
            summary.append(formatResult(result)).append('\n');
            if (result.status() == DeploymentStatus.SUCCESS) {
                successfulDeployments++;
            }
        }
        return summary.append("Successful: ")
                .append(successfulDeployments)
                .append('/')
                .append(results.size())
                .toString();
    }

    private static void validate(
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
    }

    private static String formatResult(DeploymentResult result) {
        String label = result.status() == DeploymentStatus.SUCCESS
                ? "[OK]"
                : "[ERROR]";
        return label + " " + result.environment() + ": "
                + result.description();
    }
}
