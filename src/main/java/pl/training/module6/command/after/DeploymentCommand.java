package pl.training.module6.command.after;

@FunctionalInterface
public interface DeploymentCommand {
    String execute(String releaseId);
}
