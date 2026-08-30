package pl.training.module6.decorator.before;

import java.util.Objects;
import java.util.function.Consumer;

public final class LegacyDeploymentRunner {
    private final Consumer<String> audit;

    public LegacyDeploymentRunner(Consumer<String> audit) {
        this.audit = Objects.requireNonNull(audit, "audit");
    }

    public String run(String releaseId) {
        audit.accept("start:" + releaseId);
        try {
            if (releaseId == null || releaseId.isBlank()) {
                throw new IllegalArgumentException("releaseId must not be blank");
            }
            String result = "deployed:" + releaseId;
            audit.accept("success:" + releaseId);
            return result;
        } catch (RuntimeException exception) {
            audit.accept("failure:" + releaseId + ":" + exception.getClass().getSimpleName());
            throw exception;
        }
    }
}
