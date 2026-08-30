package pl.training.module8.documentation;

import java.util.Objects;

public record DecisionOption(String name, String rationale) {
    public DecisionOption {
        name = requireNonBlank(name, "name");
        rationale = requireNonBlank(rationale, "rationale");
    }

    private static String requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
