package pl.training.module7.breakresponsibilities;

public record DeploymentSample(
        long leadTimeMinutes,
        boolean successful) {

    public DeploymentSample {
        if (leadTimeMinutes < 0) {
            throw new IllegalArgumentException(
                    "leadTimeMinutes must not be negative");
        }
    }
}
