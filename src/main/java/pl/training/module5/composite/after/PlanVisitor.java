package pl.training.module5.composite.after;

public interface PlanVisitor<R> {
    R visitTask(DeploymentTask task);

    R visitGroup(DeploymentGroup group);
}
