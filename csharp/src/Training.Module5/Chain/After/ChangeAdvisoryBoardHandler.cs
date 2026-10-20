namespace Training.Module5.Chain.After;

public sealed class ChangeAdvisoryBoardHandler : ApprovalHandler
{
    public override Decision Handle(ChangeRequest request)
    {
        if (request.LinesChanged <= 2_000)
        {
            return Decision.Of(Approver.ChangeAdvisoryBoard, "high-risk production change");
        }
        return PassOn(request);
    }
}
