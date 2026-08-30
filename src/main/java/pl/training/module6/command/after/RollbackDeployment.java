package pl.training.module6.command.after;

public final class RollbackDeployment implements DeploymentCommand {
    @Override
    public String execute(String releaseId) {
        return "rolled-back:" + releaseId;
    }
}
