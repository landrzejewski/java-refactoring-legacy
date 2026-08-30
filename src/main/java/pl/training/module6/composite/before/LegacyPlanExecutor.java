package pl.training.module6.composite.before;

import java.util.List;

public final class LegacyPlanExecutor {
    public List<String> execute(Task task) {
        return List.of("executed:" + task.name());
    }

    public List<String> executeAll(List<Task> tasks) {
        return tasks.stream()
                .map(task -> "executed:" + task.name())
                .toList();
    }

    public record Task(String name) {
        public Task {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
        }
    }
}
