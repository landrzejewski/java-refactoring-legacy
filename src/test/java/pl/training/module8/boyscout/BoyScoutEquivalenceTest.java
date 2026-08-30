package pl.training.module8.boyscout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class BoyScoutEquivalenceTest {
    private final pl.training.module8.boyscout.before.ReleaseSummaryFormatter
            before = new pl.training.module8.boyscout.before
                    .ReleaseSummaryFormatter();
    private final pl.training.module8.boyscout.after.ReleaseSummaryFormatter
            after = new pl.training.module8.boyscout.after
                    .ReleaseSummaryFormatter();

    @ParameterizedTest(name = "{0}")
    @MethodSource("representativeResults")
    void localCleanupPreservesRepresentativeOutputs(
            String description,
            String releaseId,
            List<DeploymentResult> results) {
        assertEquals(
                before.format(releaseId, results),
                after.format(releaseId, results));
    }

    @Test
    void refactoredCodeProducesTheIndependentlySpecifiedSummary() {
        List<DeploymentResult> results = List.of(
                success("test", "deployed"),
                failure("production", "timeout"));

        assertEquals(
                "Release release-42\n"
                        + "[OK] test: deployed\n"
                        + "[ERROR] production: timeout\n"
                        + "Successful: 1/2",
                after.format(" release-42 ", results));
    }

    @Test
    void localCleanupPreservesValidationFailuresAndTheirOrder() {
        assertSameFailure(
                () -> before.format(null, List.of(success("test", "ok"))),
                () -> after.format(null, List.of(success("test", "ok"))));
        assertSameFailure(
                () -> before.format("release-1", null),
                () -> after.format("release-1", null));
        assertSameFailure(
                () -> before.format("   ", List.of(success("test", "ok"))),
                () -> after.format("   ", List.of(success("test", "ok"))));
        assertSameFailure(
                () -> before.format("release-1", List.of()),
                () -> after.format("release-1", List.of()));
        assertSameFailure(
                () -> before.format(
                        "release-1", Collections.singletonList(null)),
                () -> after.format(
                        "release-1", Collections.singletonList(null)));
        assertSameFailure(
                () -> before.format(" ", null),
                () -> after.format(" ", null));
    }

    @Test
    void bothVersionsExposeTheSameCallableContract() {
        SummaryContract oldContract = before::format;
        SummaryContract cleanedContract = after::format;
        List<DeploymentResult> results = List.of(success("test", "ok"));

        assertEquals(
                oldContract.format("release-1", results),
                cleanedContract.format("release-1", results));
    }

    @Test
    void sharedInputModelRejectsIncompleteResults() {
        assertFailure(
                NullPointerException.class,
                "status",
                () -> new DeploymentResult(null, "test", "ok"));
        assertFailure(
                NullPointerException.class,
                "environment",
                () -> new DeploymentResult(
                        DeploymentStatus.SUCCESS, null, "ok"));
        assertFailure(
                IllegalArgumentException.class,
                "environment must not be blank",
                () -> new DeploymentResult(
                        DeploymentStatus.SUCCESS, " ", "ok"));
        assertFailure(
                NullPointerException.class,
                "description",
                () -> new DeploymentResult(
                        DeploymentStatus.SUCCESS, "test", null));
        assertFailure(
                IllegalArgumentException.class,
                "description must not be blank",
                () -> new DeploymentResult(
                        DeploymentStatus.SUCCESS, "test", " "));
    }

    private static Stream<Arguments> representativeResults() {
        return Stream.of(
                Arguments.of(
                        "single success",
                        "release-1",
                        List.of(success("test", "deployed"))),
                Arguments.of(
                        "single failure",
                        "release-2",
                        List.of(failure("production", "timeout"))),
                Arguments.of(
                        "mixed result in stable order",
                        " release-3 ",
                        List.of(
                                success("test", "deployed"),
                                failure("staging", "health check failed"),
                                success("production", "deployed"))));
    }

    private static DeploymentResult success(
            String environment,
            String description) {
        return new DeploymentResult(
                DeploymentStatus.SUCCESS, environment, description);
    }

    private static DeploymentResult failure(
            String environment,
            String description) {
        return new DeploymentResult(
                DeploymentStatus.FAILURE, environment, description);
    }

    private static void assertSameFailure(
            Runnable beforeAction,
            Runnable afterAction) {
        RuntimeException beforeFailure = assertThrows(
                RuntimeException.class, beforeAction::run);
        RuntimeException afterFailure = assertThrows(
                RuntimeException.class, afterAction::run);

        assertEquals(beforeFailure.getClass(), afterFailure.getClass());
        assertEquals(beforeFailure.getMessage(), afterFailure.getMessage());
    }

    private static void assertFailure(
            Class<? extends RuntimeException> expectedType,
            String expectedMessage,
            Runnable action) {
        RuntimeException failure = assertThrows(expectedType, action::run);
        assertEquals(expectedMessage, failure.getMessage());
    }

    @FunctionalInterface
    private interface SummaryContract {
        String format(String releaseId, List<DeploymentResult> results);
    }
}
