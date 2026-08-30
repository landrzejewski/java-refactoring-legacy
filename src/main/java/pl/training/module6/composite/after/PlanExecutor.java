package pl.training.module6.composite.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PlanExecutor {
    public List<String> execute(PlanComponent component) {
        Objects.requireNonNull(component, "component");
        List<DeploymentTask> tasks = new ArrayList<>();
        component.collectTasks(tasks);
        return tasks.stream()
                .map(task -> "executed:" + task.name())
                .toList();
    }
}
