package pl.training.module5.chain;

import java.util.Objects;

public record ChangeRequest(String id, int linesChanged, Risk risk, boolean production) {
    public ChangeRequest {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (linesChanged < 0) {
            throw new IllegalArgumentException("linesChanged must not be negative");
        }
        Objects.requireNonNull(risk, "risk");
    }

    public enum Risk {
        LOW,
        MEDIUM,
        HIGH
    }
}
