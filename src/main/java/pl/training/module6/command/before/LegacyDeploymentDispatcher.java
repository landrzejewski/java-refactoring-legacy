package pl.training.module6.command.before;

import java.util.Objects;

public final class LegacyDeploymentDispatcher {
    public String dispatch(DeploymentAction action, String releaseId) {
        Objects.requireNonNull(action, "action");
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }

        return switch (action) {
            case PAUSE -> "paused:" + releaseId;
            case ROLLBACK -> "rolled-back:" + releaseId;
        };
    }

    public enum DeploymentAction {
        PAUSE,
        ROLLBACK
    }
}
