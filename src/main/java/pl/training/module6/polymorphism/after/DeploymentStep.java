package pl.training.module6.polymorphism.after;

public sealed interface DeploymentStep permits ScriptStep, ApprovalStep {
    String execute();
}
