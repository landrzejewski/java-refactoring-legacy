package pl.training.module5.decorator.after;

@FunctionalInterface
public interface DeploymentRunner {
    String run(String releaseId);
}
