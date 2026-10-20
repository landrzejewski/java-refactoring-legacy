package pl.training.module5.command.after;

public final class RollbackDeployment implements DeploymentCommand {
    @Override
    public String execute(String releaseId) {
        return "rolled-back:" + releaseId;
    }
}
