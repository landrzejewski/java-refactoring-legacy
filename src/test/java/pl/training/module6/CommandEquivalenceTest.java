package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.module6.command.after.DeploymentAction;
import pl.training.module6.command.after.DeploymentCommand;
import pl.training.module6.command.after.DeploymentCommandDispatcher;
import pl.training.module6.command.after.PauseDeployment;
import pl.training.module6.command.after.RollbackDeployment;
import pl.training.module6.command.before.LegacyDeploymentDispatcher;

final class CommandEquivalenceTest {
    @Test
    void lookupPreservesEveryLegacyDispatchBranch() {
        var before = new LegacyDeploymentDispatcher();
        var after = new DeploymentCommandDispatcher(Map.of(
                DeploymentAction.PAUSE, new PauseDeployment(),
                DeploymentAction.ROLLBACK, new RollbackDeployment()));

        assertEquals(
                before.dispatch(
                        LegacyDeploymentDispatcher.DeploymentAction.PAUSE,
                        "rel-42"),
                after.dispatch(DeploymentAction.PAUSE, "rel-42"));
        assertEquals(
                before.dispatch(
                        LegacyDeploymentDispatcher.DeploymentAction.ROLLBACK,
                        "rel-42"),
                after.dispatch(DeploymentAction.ROLLBACK, "rel-42"));
    }

    @Test
    void registryIsDefensivelyCopiedAndMustBeComplete() {
        EnumMap<DeploymentAction, DeploymentCommand> source =
                new EnumMap<>(DeploymentAction.class);
        source.put(DeploymentAction.PAUSE, new PauseDeployment());
        source.put(DeploymentAction.ROLLBACK, new RollbackDeployment());
        var dispatcher = new DeploymentCommandDispatcher(source);
        source.put(DeploymentAction.PAUSE, ignored -> "changed");
        source.remove(DeploymentAction.ROLLBACK);

        assertEquals(
                "paused:rel-42",
                dispatcher.dispatch(DeploymentAction.PAUSE, "rel-42"));
        assertEquals(
                "rolled-back:rel-42",
                dispatcher.dispatch(DeploymentAction.ROLLBACK, "rel-42"));
        assertEquals(
                "missing commands: [ROLLBACK]",
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new DeploymentCommandDispatcher(Map.of(
                                DeploymentAction.PAUSE,
                                new PauseDeployment())))
                        .getMessage());
    }
}
