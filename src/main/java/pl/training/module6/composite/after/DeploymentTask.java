package pl.training.module6.composite.after;

import java.util.Collection;
import java.util.Objects;

public record DeploymentTask(String name, long minutes) implements PlanComponent {
    public DeploymentTask {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (minutes < 0) {
            throw new IllegalArgumentException("minutes must not be negative");
        }
    }

    @Override
    public long totalMinutes() {
        return minutes;
    }

    @Override
    public void collectTasks(Collection<? super DeploymentTask> target) {
        Objects.requireNonNull(target, "target").add(this);
    }

    @Override
    public <R> R accept(PlanVisitor<R> visitor) {
        return Objects.requireNonNull(visitor, "visitor").visitTask(this);
    }
}
