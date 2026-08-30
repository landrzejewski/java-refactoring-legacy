package pl.training.module6.composite.after;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public record DeploymentGroup(
        String name,
        List<PlanComponent> components) implements PlanComponent {
    public DeploymentGroup {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        components = List.copyOf(Objects.requireNonNull(components, "components"));
    }

    @Override
    public long totalMinutes() {
        long total = 0;
        for (PlanComponent component : components) {
            total = Math.addExact(total, component.totalMinutes());
        }
        return total;
    }

    @Override
    public void collectTasks(Collection<? super DeploymentTask> target) {
        Objects.requireNonNull(target, "target");
        for (PlanComponent component : components) {
            component.collectTasks(target);
        }
    }

    @Override
    public <R> R accept(PlanVisitor<R> visitor) {
        return Objects.requireNonNull(visitor, "visitor").visitGroup(this);
    }
}
