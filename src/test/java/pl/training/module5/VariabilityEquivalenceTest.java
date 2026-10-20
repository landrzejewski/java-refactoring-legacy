package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import pl.training.module5.variability.CustomerTier;
import pl.training.module5.variability.after.ReleaseConsole;
import pl.training.module5.variability.after.SupportCostPolicy;
import pl.training.module5.variability.before.LegacyReleaseConsole;

final class VariabilityEquivalenceTest {
    @ParameterizedTest
    @EnumSource(CustomerTier.class)
    void strategyAxisPreservesEveryTierCost(CustomerTier tier) {
        var before = new LegacyReleaseConsole(tier);
        var after = ReleaseConsole.forTier(tier);

        for (int hours : new int[] {0, 1, 10, 33, 100}) {
            assertEquals(before.supportCost(hours), after.supportCost(hours));
        }
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> before.supportCost(-1)).getMessage(),
                assertThrows(IllegalArgumentException.class, () -> after.supportCost(-1)).getMessage());
    }

    @Test
    void strategyCanBeSuppliedByTheClientWithoutTouchingTheConsole() {
        var flatRate = new ReleaseConsole(hours -> 1_000);

        assertEquals(1_000, flatRate.supportCost(7));
        assertEquals(1_300, new ReleaseConsole(SupportCostPolicy.PREMIUM).supportCost(10));
    }

    @Test
    void commandAxisPreservesQueueLogAndUndo() {
        var before = new LegacyReleaseConsole(CustomerTier.STANDARD);
        var after = ReleaseConsole.forTier(CustomerTier.STANDARD);

        for (String action : List.of("scale-up", "maintenance", "scale-down", "scale-down")) {
            before.submit(action);
            after.submit(action);
        }
        assertEquals(before.log(), after.log());
        before.runQueued();
        after.runQueued();
        before.undoLast();
        after.undoLast();
        before.undoLast();
        after.undoLast();

        assertEquals(
                List.of(
                        "scale-up -> instances=3, maintenance=off",
                        "maintenance -> instances=3, maintenance=on",
                        "scale-down -> instances=2, maintenance=on",
                        "scale-down -> instances=1, maintenance=on",
                        "undo scale-down -> instances=2, maintenance=on",
                        "undo scale-down -> instances=3, maintenance=on"),
                after.log());
        assertEquals(before.log(), after.log());
    }

    @Test
    void commandAxisPreservesFailures() {
        assertSameFailure(console -> console.submit("reboot"), console -> console.submit("reboot"));
        assertSameFailure(LegacyReleaseConsole::undoLast, ReleaseConsole::undoLast);
        assertSameFailure(
                console -> {
                    console.submit("scale-down");
                    console.submit("scale-down");
                    console.runQueued();
                },
                console -> {
                    console.submit("scale-down");
                    console.submit("scale-down");
                    console.runQueued();
                });
    }

    @Test
    void stateAxisPreservesLifecycleAndBanners() {
        var before = new LegacyReleaseConsole(CustomerTier.STANDARD);
        var after = ReleaseConsole.forTier(CustomerTier.STANDARD);
        assertSameLifecycle(before, after);

        before.advance();
        after.advance();
        assertSameLifecycle(before, after);
        before.advance();
        after.advance();
        assertSameLifecycle(before, after);
        assertSameFailure(before, after, LegacyReleaseConsole::advance, ReleaseConsole::advance);
        before.rollback();
        after.rollback();
        assertSameLifecycle(before, after);
        assertSameFailure(before, after, LegacyReleaseConsole::rollback, ReleaseConsole::rollback);
        assertSameFailure(before, after, LegacyReleaseConsole::advance, ReleaseConsole::advance);
    }

    @Test
    void plannedReleaseCannotBeRolledBackInBothVersions() {
        assertSameFailure(LegacyReleaseConsole::rollback, ReleaseConsole::rollback);
    }

    private static void assertSameLifecycle(LegacyReleaseConsole before, ReleaseConsole after) {
        assertEquals(before.status(), after.status());
        assertEquals(before.banner(), after.banner());
    }

    private static void assertSameFailure(
            Consumer<LegacyReleaseConsole> beforeAction,
            Consumer<ReleaseConsole> afterAction) {
        assertSameFailure(
                new LegacyReleaseConsole(CustomerTier.STANDARD),
                ReleaseConsole.forTier(CustomerTier.STANDARD),
                beforeAction,
                afterAction);
    }

    private static void assertSameFailure(
            LegacyReleaseConsole before,
            ReleaseConsole after,
            Consumer<LegacyReleaseConsole> beforeAction,
            Consumer<ReleaseConsole> afterAction) {
        RuntimeException beforeFailure =
                assertThrows(RuntimeException.class, () -> beforeAction.accept(before));
        RuntimeException afterFailure =
                assertThrows(RuntimeException.class, () -> afterAction.accept(after));

        assertEquals(beforeFailure.getClass(), afterFailure.getClass());
        assertEquals(beforeFailure.getMessage(), afterFailure.getMessage());
        assertEquals(before.status(), after.status());
        assertEquals(before.log(), after.log());
    }
}
