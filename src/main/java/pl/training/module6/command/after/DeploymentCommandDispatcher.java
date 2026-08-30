package pl.training.module6.command.after;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

public final class DeploymentCommandDispatcher {
    private final Map<DeploymentAction, DeploymentCommand> commands;

    public DeploymentCommandDispatcher(Map<DeploymentAction, DeploymentCommand> commands) {
        Map<DeploymentAction, DeploymentCommand> copy =
                Map.copyOf(Objects.requireNonNull(commands, "commands"));
        EnumSet<DeploymentAction> missing =
                EnumSet.allOf(DeploymentAction.class);
        missing.removeAll(copy.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("missing commands: " + missing);
        }
        this.commands = copy;
    }

    public String dispatch(DeploymentAction action, String releaseId) {
        Objects.requireNonNull(action, "action");
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }

        return commands.get(action).execute(releaseId);
    }
}
