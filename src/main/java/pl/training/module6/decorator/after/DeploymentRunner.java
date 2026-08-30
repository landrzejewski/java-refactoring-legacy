package pl.training.module6.decorator.after;

@FunctionalInterface
public interface DeploymentRunner {
    String run(String releaseId);
}
