package pl.training.module6.composite.after;

public final class TotalMinutesVisitor implements PlanVisitor<Long> {
    @Override
    public Long visitTask(DeploymentTask task) {
        return task.minutes();
    }

    @Override
    public Long visitGroup(DeploymentGroup group) {
        long total = 0;
        for (PlanComponent component : group.components()) {
            total = Math.addExact(total, component.accept(this));
        }
        return total;
    }
}
