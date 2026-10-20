namespace Training.Module5.Chain.After;

public sealed class SizeLimitHandler : ApprovalHandler
{
    public override Decision Handle(ChangeRequest request)
    {
        if (request.LinesChanged > 5_000)
        {
            return Decision.Of(Approver.Rejected, "change too large");
        }
        return PassOn(request);
    }
}
