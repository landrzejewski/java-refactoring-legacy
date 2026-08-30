package pl.training.module6.command.after;

public final class PauseDeployment implements DeploymentCommand {
    @Override
    public String execute(String releaseId) {
        return "paused:" + releaseId;
    }
}
