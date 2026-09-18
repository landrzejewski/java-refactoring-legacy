using Training.Module7.Contract.After;
using Training.Module7.Contract.Before;

namespace Training.Module7.Tests;

public sealed class DesignByContractTest
{
    [Fact]
    public void ExplicitContractsPreserveEveryValidStateTransition()
    {
        var before = new LegacyDeploymentCapacity(10);
        var after = new DeploymentCapacity(10);

        AssertSameRemaining(before, after, 10);
        before.Reserve(3);
        after.Reserve(3);
        AssertSameRemaining(before, after, 7);
        before.Reserve(7);
        after.Reserve(7);
        AssertSameRemaining(before, after, 0);
        before.Release(4);
        after.Release(4);
        AssertSameRemaining(before, after, 4);
        before.Release(6);
        after.Release(6);
        AssertSameRemaining(before, after, 10);

        Assert.Equal(0, new LegacyDeploymentCapacity(0).Remaining);
        Assert.Equal(0, new DeploymentCapacity(0).Remaining);
    }

    [Fact]
    public void PreconditionsRejectInvalidOperationsBeforeMutation()
    {
        Assert.Equal(
            "totalSlots must not be negative",
            Assert.Throws<ArgumentException>(() => new DeploymentCapacity(-1)).Message);

        var capacity = new DeploymentCapacity(5);
        AssertFailureWithoutMutation(
            capacity, () => capacity.Reserve(0), "slots must be positive", 5);
        AssertFailureWithoutMutation(
            capacity, () => capacity.Reserve(-1), "slots must be positive", 5);
        AssertFailureWithoutMutation(
            capacity, () => capacity.Reserve(6), "cannot reserve more slots than remain", 5);

        capacity.Reserve(3);
        AssertFailureWithoutMutation(
            capacity, () => capacity.Release(0), "slots must be positive", 2);
        AssertFailureWithoutMutation(
            capacity, () => capacity.Release(-1), "slots must be positive", 2);
        AssertFailureWithoutMutation(
            capacity, () => capacity.Release(4), "cannot release more slots than are reserved", 2);
    }

    [Fact]
    public void ContractHelpersUseRuntimeExceptionsWithoutJavaAssertions()
    {
        Assert.Equal(
            "precondition",
            Assert.Throws<ArgumentException>(
                () => Contracts.Require(false, "precondition")).Message);
        Assert.Equal(
            "postcondition",
            Assert.Throws<InvalidOperationException>(
                () => Contracts.Ensure(false, "postcondition")).Message);
        Assert.Equal(
            "invariant",
            Assert.Throws<InvalidOperationException>(
                () => Contracts.Invariant(false, "invariant")).Message);

        Contracts.Require(true, "ignored");
        Contracts.Ensure(true, "ignored");
        Contracts.Invariant(true, "ignored");
    }

    private static void AssertSameRemaining(
        LegacyDeploymentCapacity before,
        DeploymentCapacity after,
        int expected)
    {
        Assert.Equal(expected, before.Remaining);
        Assert.Equal(expected, after.Remaining);
        Assert.Equal(before.Remaining, after.Remaining);
    }

    private static void AssertFailureWithoutMutation(
        DeploymentCapacity capacity,
        Action operation,
        string expectedMessage,
        int expectedRemaining)
    {
        var before = capacity.Remaining;
        Assert.Equal(expectedRemaining, before);
        Assert.Equal(
            expectedMessage,
            Assert.Throws<ArgumentException>(operation).Message);
        Assert.Equal(before, capacity.Remaining);
    }
}
