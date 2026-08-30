package pl.training.module6.decorator.after;

public final class BasicDeploymentRunner implements DeploymentRunner {
    @Override
    public String run(String releaseId) {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        return "deployed:" + releaseId;
    }
}
