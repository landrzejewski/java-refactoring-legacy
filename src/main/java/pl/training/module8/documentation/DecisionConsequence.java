package pl.training.module8.documentation;

import java.util.Objects;

public record DecisionConsequence(ConsequenceKind kind, String description) {
    public DecisionConsequence {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(description, "description");
        if (description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }
}
