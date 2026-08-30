package pl.training.module6.polymorphism.before;

import java.util.Objects;

public record LegacyDeploymentStep(Kind kind, String value) {
    public LegacyDeploymentStep {
        Objects.requireNonNull(kind, "kind");
        if (value == null || value.isBlank()) {
            String field = kind == Kind.SCRIPT ? "command" : "approver";
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }

    public static LegacyDeploymentStep script(String command) {
        return new LegacyDeploymentStep(Kind.SCRIPT, command);
    }

    public static LegacyDeploymentStep approval(String approver) {
        return new LegacyDeploymentStep(Kind.APPROVAL, approver);
    }

    public String execute() {
        return switch (kind) {
            case SCRIPT -> "executed:" + value;
            case APPROVAL -> "approved-by:" + value;
        };
    }

    public enum Kind {
        SCRIPT,
        APPROVAL
    }
}
