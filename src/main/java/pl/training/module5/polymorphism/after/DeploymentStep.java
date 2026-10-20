package pl.training.module5.polymorphism.after;

public sealed interface DeploymentStep permits ScriptStep, ApprovalStep {
    String execute();
}
