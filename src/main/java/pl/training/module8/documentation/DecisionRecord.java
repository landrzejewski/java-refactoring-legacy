package pl.training.module8.documentation;

import java.util.List;
import java.util.Objects;

public record DecisionRecord(
        DecisionId id,
        String title,
        DecisionStatus status,
        String context,
        String decision,
        List<DecisionOption> consideredOptions,
        List<DecisionConsequence> consequences,
        String verificationMethod) {

    public DecisionRecord {
        Objects.requireNonNull(id, "id");
        title = requireNonBlank(title, "title");
        Objects.requireNonNull(status, "status");
        context = requireNonBlank(context, "context");
        decision = requireNonBlank(decision, "decision");
        consideredOptions = nonEmptyCopy(consideredOptions, "consideredOptions");
        consequences = nonEmptyCopy(consequences, "consequences");
        verificationMethod = requireNonBlank(
                verificationMethod,
                "verificationMethod");
    }

    private static String requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    private static <T> List<T> nonEmptyCopy(List<T> values, String name) {
        List<T> copy = List.copyOf(Objects.requireNonNull(values, name));
        if (copy.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return copy;
    }
}
