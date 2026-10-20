using Training.Module5.Chain;
using Training.Module5.Chain.After;
using Training.Module5.Chain.Before;

namespace Training.Module5.Tests;

public sealed class ChainEquivalenceTest
{
    [Fact]
    public void ChainPreservesEveryLegacyRoutingDecision()
    {
        var before = new LegacyApprovalRouter();
        var after = ApprovalRouter.Standard();
        var compared = 0;

        foreach (var lines in new[] { 0, 50, 51, 500, 501, 2_000, 2_001, 5_000, 5_001 })
        {
            foreach (var risk in Enum.GetValues<Risk>())
            {
                foreach (var production in new[] { true, false })
                {
                    var request = new ChangeRequest("CR-" + lines, lines, risk, production);
                    Assert.True(
                        before.Route(request) == after.Route(request),
                        request.ToString());
                    compared++;
                }
            }
        }

        Assert.Equal(54, compared);
    }

    [Fact]
    public void EveryApproverIsReachable()
    {
        var router = ApprovalRouter.Standard();
        var approvers = new List<Approver>
        {
            router.Route(new ChangeRequest("a", 10, Risk.Low, true)).Approver,
            router.Route(new ChangeRequest("b", 300, Risk.Medium, true)).Approver,
            router.Route(new ChangeRequest("c", 900, Risk.Medium, true)).Approver,
            router.Route(new ChangeRequest("d", 900, Risk.High, true)).Approver,
            router.Route(new ChangeRequest("e", 3_000, Risk.High, true)).Approver
        };

        Assert.Equal(
            [
                Approver.Automatic,
                Approver.TeamLead,
                Approver.ReleaseManager,
                Approver.ChangeAdvisoryBoard,
                Approver.Rejected
            ],
            approvers);
    }

    [Fact]
    public void ExactlyOneHandlerHandlesTheRequest()
    {
        var passing = new CountingHandler(false);
        var handling = new CountingHandler(true);
        var neverReached = new CountingHandler(true);
        passing.Then(handling).Then(neverReached);

        var decision = new ApprovalRouter(passing)
            .Route(new ChangeRequest("CR-1", 10, Risk.Low, true));

        Assert.Equal(Decision.Of(Approver.TeamLead, "handled"), decision);
        Assert.Equal([1, 1, 0], [passing.Calls, handling.Calls, neverReached.Calls]);
    }

    [Fact]
    public void EndOfTheChainRejectsUnhandledRequests()
    {
        var only = new CountingHandler(false);

        Assert.Equal(
            Decision.Of(Approver.Rejected, "no approver for CR-2"),
            new ApprovalRouter(only).Route(new ChangeRequest("CR-2", 10, Risk.Low, true)));
        Assert.Equal(1, only.Calls);
    }

    private sealed class CountingHandler(bool handles) : ApprovalHandler
    {
        public int Calls { get; private set; }

        public override Decision Handle(ChangeRequest request)
        {
            Calls++;
            return handles ? Decision.Of(Approver.TeamLead, "handled") : PassOn(request);
        }
    }
}
