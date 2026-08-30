package pl.training.module6.composite.after;

import java.util.Collection;

public sealed interface PlanComponent permits DeploymentTask, DeploymentGroup {
    String name();

    long totalMinutes();

    void collectTasks(Collection<? super DeploymentTask> target);

    <R> R accept(PlanVisitor<R> visitor);
}
