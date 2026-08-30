package pl.training.module6.decorator.after;

import java.util.Objects;
import java.util.function.Consumer;

public final class AuditedDeploymentRunner implements DeploymentRunner {
    private final DeploymentRunner delegate;
    private final Consumer<String> audit;

    public AuditedDeploymentRunner(DeploymentRunner delegate, Consumer<String> audit) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.audit = Objects.requireNonNull(audit, "audit");
    }

    @Override
    public String run(String releaseId) {
        audit.accept("start:" + releaseId);
        try {
            String result = delegate.run(releaseId);
            audit.accept("success:" + releaseId);
            return result;
        } catch (RuntimeException exception) {
            audit.accept("failure:" + releaseId + ":" + exception.getClass().getSimpleName());
            throw exception;
        }
    }
}
