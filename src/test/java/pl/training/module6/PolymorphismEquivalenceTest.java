package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module6.polymorphism.after.ApprovalStep;
import pl.training.module6.polymorphism.after.DeploymentStep;
import pl.training.module6.polymorphism.after.ScriptStep;
import pl.training.module6.polymorphism.before.LegacyDeploymentStep;

final class PolymorphismEquivalenceTest {
    @Test
    void dispatchesEachStableVariantThroughItsOwnType() {
        List<String> before = List.of(
                LegacyDeploymentStep.script("deploy.sh").execute(),
                LegacyDeploymentStep.approval("anna").execute());
        List<DeploymentStep> steps = List.of(
                new ScriptStep("deploy.sh"),
                new ApprovalStep("anna"));

        assertEquals(before, steps.stream().map(DeploymentStep::execute).toList());
    }

    @Test
    void preservesInvalidValueContract() {
        assertEquals(
                assertThrows(
                        IllegalArgumentException.class,
                        () -> LegacyDeploymentStep.script(" ")).getMessage(),
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new ScriptStep(" ")).getMessage());
    }
}
