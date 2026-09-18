using Training.Module6.State.After;
using Training.Module6.State.Before;

namespace Training.Module6.Tests;

public sealed class StateEquivalenceTest
{
    [Fact]
    public void PreservesApprovalAndDeploymentTransitions()
    {
        var before = new LegacyRelease();
        var after = new Release();

        Assert.Equal(before.Status.ToString(), after.Status.ToString());
        before.Approve();
        after.Approve();
        Assert.Equal(before.Status.ToString(), after.Status.ToString());
        before.Deploy();
        after.Deploy();
        Assert.Equal(before.Status.ToString(), after.Status.ToString());
    }

    [Fact]
    public void PreservesBothAllowedCancellationPaths()
    {
        var draftBefore = new LegacyRelease();
        var draftAfter = new Release();
        draftBefore.Cancel();
        draftAfter.Cancel();
        Assert.Equal(draftBefore.Status.ToString(), draftAfter.Status.ToString());

        var approvedBefore = new LegacyRelease();
        var approvedAfter = new Release();
        approvedBefore.Approve();
        approvedAfter.Approve();
        approvedBefore.Cancel();
        approvedAfter.Cancel();
        Assert.Equal(approvedBefore.Status.ToString(), approvedAfter.Status.ToString());
    }

    [Fact]
    public void EveryInvalidTransitionKeepsStateAndExceptionMessage()
    {
        AssertInvalid(_ => { }, _ => { }, r => r.Deploy(), r => r.Deploy());
        AssertInvalid(r => r.Approve(), r => r.Approve(), r => r.Approve(), r => r.Approve());

        AssertInvalid(Deploy, Deploy, r => r.Approve(), r => r.Approve());
        AssertInvalid(Deploy, Deploy, r => r.Deploy(), r => r.Deploy());
        AssertInvalid(Deploy, Deploy, r => r.Cancel(), r => r.Cancel());

        AssertInvalid(r => r.Cancel(), r => r.Cancel(), r => r.Approve(), r => r.Approve());
        AssertInvalid(r => r.Cancel(), r => r.Cancel(), r => r.Deploy(), r => r.Deploy());
        AssertInvalid(r => r.Cancel(), r => r.Cancel(), r => r.Cancel(), r => r.Cancel());
    }

    private static void AssertInvalid(
        Action<LegacyRelease> prepareBefore,
        Action<Release> prepareAfter,
        Action<LegacyRelease> actionBefore,
        Action<Release> actionAfter)
    {
        var before = new LegacyRelease();
        var after = new Release();
        prepareBefore(before);
        prepareAfter(after);
        var status = before.Status.ToString();
        Assert.Equal(status, after.Status.ToString());

        var beforeFailure = Assert.Throws<InvalidOperationException>(() => actionBefore(before));
        var afterFailure = Assert.Throws<InvalidOperationException>(() => actionAfter(after));

        Assert.Equal(beforeFailure.Message, afterFailure.Message);
        Assert.Equal(status, before.Status.ToString());
        Assert.Equal(status, after.Status.ToString());
    }

    private static void Deploy(LegacyRelease release)
    {
        release.Approve();
        release.Deploy();
    }

    private static void Deploy(Release release)
    {
        release.Approve();
        release.Deploy();
    }
}
