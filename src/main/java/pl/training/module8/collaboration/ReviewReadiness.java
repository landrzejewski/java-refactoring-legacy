package pl.training.module8.collaboration;

import java.util.List;
import java.util.Objects;

public record ReviewReadiness(List<ReadinessProblem> problems) {
    public ReviewReadiness {
        problems = List.copyOf(Objects.requireNonNull(problems, "problems"));
    }

    public boolean ready() {
        return problems.isEmpty();
    }
}
