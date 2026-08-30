package pl.training.module6.composite.after;

public interface PlanVisitor<R> {
    R visitTask(DeploymentTask task);

    R visitGroup(DeploymentGroup group);
}
