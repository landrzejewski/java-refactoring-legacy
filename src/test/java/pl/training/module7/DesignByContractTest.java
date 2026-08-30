package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.module7.contract.after.Contracts;
import pl.training.module7.contract.after.DeploymentCapacity;
import pl.training.module7.contract.before.LegacyDeploymentCapacity;

final class DesignByContractTest {
    @Test
    void explicitContractsPreserveEveryValidStateTransition() {
        var before = new LegacyDeploymentCapacity(10);
        var after = new DeploymentCapacity(10);

        assertSameRemaining(before, after, 10);
        before.reserve(3);
        after.reserve(3);
        assertSameRemaining(before, after, 7);
        before.reserve(7);
        after.reserve(7);
        assertSameRemaining(before, after, 0);
        before.release(4);
        after.release(4);
        assertSameRemaining(before, after, 4);
        before.release(6);
        after.release(6);
        assertSameRemaining(before, after, 10);

        assertEquals(0, new LegacyDeploymentCapacity(0).remaining());
        assertEquals(0, new DeploymentCapacity(0).remaining());
    }

    @Test
    void preconditionsRejectInvalidOperationsBeforeMutation() {
        assertEquals(
                "totalSlots must not be negative",
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new DeploymentCapacity(-1))
                        .getMessage());

        var capacity = new DeploymentCapacity(5);
        assertFailureWithoutMutation(
                capacity,
                () -> capacity.reserve(0),
                "slots must be positive",
                5);
        assertFailureWithoutMutation(
                capacity,
                () -> capacity.reserve(-1),
                "slots must be positive",
                5);
        assertFailureWithoutMutation(
                capacity,
                () -> capacity.reserve(6),
                "cannot reserve more slots than remain",
                5);

        capacity.reserve(3);
        assertFailureWithoutMutation(
                capacity,
                () -> capacity.release(0),
                "slots must be positive",
                2);
        assertFailureWithoutMutation(
                capacity,
                () -> capacity.release(-1),
                "slots must be positive",
                2);
        assertFailureWithoutMutation(
                capacity,
                () -> capacity.release(4),
                "cannot release more slots than are reserved",
                2);
    }

    @Test
    void contractHelpersUseRuntimeExceptionsWithoutJavaAssertions() {
        assertEquals(
                "precondition",
                assertThrows(
                        IllegalArgumentException.class,
                        () -> Contracts.require(false, "precondition"))
                        .getMessage());
        assertEquals(
                "postcondition",
                assertThrows(
                        IllegalStateException.class,
                        () -> Contracts.ensure(false, "postcondition"))
                        .getMessage());
        assertEquals(
                "invariant",
                assertThrows(
                        IllegalStateException.class,
                        () -> Contracts.invariant(false, "invariant"))
                        .getMessage());

        Contracts.require(true, "ignored");
        Contracts.ensure(true, "ignored");
        Contracts.invariant(true, "ignored");
    }

    private static void assertSameRemaining(
            LegacyDeploymentCapacity before,
            DeploymentCapacity after,
            int expected) {
        assertEquals(expected, before.remaining());
        assertEquals(expected, after.remaining());
        assertEquals(before.remaining(), after.remaining());
    }

    private static void assertFailureWithoutMutation(
            DeploymentCapacity capacity,
            Runnable operation,
            String expectedMessage,
            int expectedRemaining) {
        int before = capacity.remaining();
        assertEquals(expectedRemaining, before);
        assertEquals(
                expectedMessage,
                assertThrows(IllegalArgumentException.class, operation::run)
                        .getMessage());
        assertEquals(before, capacity.remaining());
    }
}
