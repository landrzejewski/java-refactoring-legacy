package pl.training.module5.command.after;

@FunctionalInterface
public interface DeploymentCommand {
    String execute(String releaseId);
}
