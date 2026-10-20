package pl.training.module5.templatetostrategy;

import java.util.Objects;

public record ChangeEntry(Kind kind, String description) {
    public ChangeEntry {
        Objects.requireNonNull(kind, "kind");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }

    public enum Kind {
        FEATURE,
        FIX,
        BREAKING
    }
}
