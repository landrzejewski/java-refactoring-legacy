package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import pl.training.module7.booleanparameter.after.DeploymentExecutor;
import pl.training.module7.booleanparameter.before.LegacyDeploymentExecutor;

final class BooleanParameterEquivalenceTest {
    @Test
    void namedOperationsPreserveBothLegacyBranches() {
        var before = new LegacyDeploymentExecutor();
        var after = new DeploymentExecutor();

        assertEquals(before.execute("dep-42", true), after.preview("dep-42"));
        assertEquals(before.execute("dep-42", false), after.deploy("dep-42"));
    }

    @Test
    void validationIsIdenticalForBothNamedOperations() {
        var before = new LegacyDeploymentExecutor();
        var after = new DeploymentExecutor();

        for (String invalidId : new String[] {null, "", "  \t"}) {
            String previewMessage = assertThrows(
                    IllegalArgumentException.class,
                    () -> before.execute(invalidId, true))
                    .getMessage();
            String deployMessage = assertThrows(
                    IllegalArgumentException.class,
                    () -> before.execute(invalidId, false))
                    .getMessage();

            assertEquals(
                    previewMessage,
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> after.preview(invalidId))
                            .getMessage());
            assertEquals(
                    deployMessage,
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> after.deploy(invalidId))
                            .getMessage());
        }
    }

    @Test
    void publicApiContainsNoBooleanParameterAndExecutorHasNoMutableState() {
        boolean hasPublicBooleanParameter = Arrays.stream(
                        DeploymentExecutor.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .flatMap(method -> Arrays.stream(method.getParameterTypes()))
                .anyMatch(parameter -> parameter == boolean.class);

        assertFalse(hasPublicBooleanParameter);

        var executor = new DeploymentExecutor();
        assertEquals("preview:dep-42", executor.preview("dep-42"));
        assertEquals("deployed:dep-42", executor.deploy("dep-42"));
        assertEquals("preview:dep-42", executor.preview("dep-42"));
    }
}
