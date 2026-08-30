package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

final class StateEquivalenceTest {
    @Test
    void preservesApprovalAndDeploymentTransitions() {
        var before = new pl.training.module6.state.before.LegacyRelease();
        var after = new pl.training.module6.state.after.Release();

        assertEquals(before.status().name(), after.status().name());
        before.approve();
        after.approve();
        assertEquals(before.status().name(), after.status().name());
        before.deploy();
        after.deploy();
        assertEquals(before.status().name(), after.status().name());
    }

    @Test
    void preservesBothAllowedCancellationPaths() {
        var draftBefore = new pl.training.module6.state.before.LegacyRelease();
        var draftAfter = new pl.training.module6.state.after.Release();
        draftBefore.cancel();
        draftAfter.cancel();
        assertEquals(draftBefore.status().name(), draftAfter.status().name());

        var approvedBefore = new pl.training.module6.state.before.LegacyRelease();
        var approvedAfter = new pl.training.module6.state.after.Release();
        approvedBefore.approve();
        approvedAfter.approve();
        approvedBefore.cancel();
        approvedAfter.cancel();
        assertEquals(approvedBefore.status().name(), approvedAfter.status().name());
    }

    @Test
    void everyInvalidTransitionKeepsStateAndExceptionMessage() {
        assertInvalid(
                release -> { },
                release -> { },
                pl.training.module6.state.before.LegacyRelease::deploy,
                pl.training.module6.state.after.Release::deploy);
        assertInvalid(
                pl.training.module6.state.before.LegacyRelease::approve,
                pl.training.module6.state.after.Release::approve,
                pl.training.module6.state.before.LegacyRelease::approve,
                pl.training.module6.state.after.Release::approve);

        assertInvalid(
                StateEquivalenceTest::deploy,
                StateEquivalenceTest::deploy,
                pl.training.module6.state.before.LegacyRelease::approve,
                pl.training.module6.state.after.Release::approve);
        assertInvalid(
                StateEquivalenceTest::deploy,
                StateEquivalenceTest::deploy,
                pl.training.module6.state.before.LegacyRelease::deploy,
                pl.training.module6.state.after.Release::deploy);
        assertInvalid(
                StateEquivalenceTest::deploy,
                StateEquivalenceTest::deploy,
                pl.training.module6.state.before.LegacyRelease::cancel,
                pl.training.module6.state.after.Release::cancel);

        assertInvalid(
                pl.training.module6.state.before.LegacyRelease::cancel,
                pl.training.module6.state.after.Release::cancel,
                pl.training.module6.state.before.LegacyRelease::approve,
                pl.training.module6.state.after.Release::approve);
        assertInvalid(
                pl.training.module6.state.before.LegacyRelease::cancel,
                pl.training.module6.state.after.Release::cancel,
                pl.training.module6.state.before.LegacyRelease::deploy,
                pl.training.module6.state.after.Release::deploy);
        assertInvalid(
                pl.training.module6.state.before.LegacyRelease::cancel,
                pl.training.module6.state.after.Release::cancel,
                pl.training.module6.state.before.LegacyRelease::cancel,
                pl.training.module6.state.after.Release::cancel);
    }

    private static void assertInvalid(
            Consumer<pl.training.module6.state.before.LegacyRelease> prepareBefore,
            Consumer<pl.training.module6.state.after.Release> prepareAfter,
            Consumer<pl.training.module6.state.before.LegacyRelease> actionBefore,
            Consumer<pl.training.module6.state.after.Release> actionAfter) {
        var before = new pl.training.module6.state.before.LegacyRelease();
        var after = new pl.training.module6.state.after.Release();
        prepareBefore.accept(before);
        prepareAfter.accept(after);
        String status = before.status().name();
        assertEquals(status, after.status().name());

        IllegalStateException beforeFailure = assertThrows(
                IllegalStateException.class, () -> actionBefore.accept(before));
        IllegalStateException afterFailure = assertThrows(
                IllegalStateException.class, () -> actionAfter.accept(after));

        assertEquals(beforeFailure.getMessage(), afterFailure.getMessage());
        assertEquals(status, before.status().name());
        assertEquals(status, after.status().name());
    }

    private static void deploy(
            pl.training.module6.state.before.LegacyRelease release) {
        release.approve();
        release.deploy();
    }

    private static void deploy(
            pl.training.module6.state.after.Release release) {
        release.approve();
        release.deploy();
    }
}
