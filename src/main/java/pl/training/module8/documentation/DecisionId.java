package pl.training.module8.documentation;

import java.util.Objects;
import java.util.regex.Pattern;

public record DecisionId(String value) {
    private static final Pattern FORMAT = Pattern.compile("ADR-[0-9]{4}");

    public DecisionId {
        Objects.requireNonNull(value, "value");
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "value must use the format ADR-NNNN");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
