namespace Training.Module5.Chain.After;

/// <summary>
/// Miejsce, w którym łańcuch jest jawnie złożony. Kolejność ogniw jest widoczna
/// w jednym wyrażeniu i może być inna dla innego procesu zatwierdzania.
/// </summary>
public sealed class ApprovalRouter
{
    private readonly ApprovalHandler _chain;

    public ApprovalRouter(ApprovalHandler chain)
    {
        ArgumentNullException.ThrowIfNull(chain);
        _chain = chain;
    }

    public static ApprovalRouter Standard()
    {
        ApprovalHandler first = new SizeLimitHandler();
        first.Then(new AutomaticApprovalHandler())
            .Then(new TeamLeadHandler())
            .Then(new ReleaseManagerHandler())
            .Then(new ChangeAdvisoryBoardHandler());
        return new ApprovalRouter(first);
    }

    public Decision Route(ChangeRequest request)
    {
        ArgumentNullException.ThrowIfNull(request);
        return _chain.Handle(request);
    }
}
