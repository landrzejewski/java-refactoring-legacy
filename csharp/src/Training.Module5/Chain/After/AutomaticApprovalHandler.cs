namespace Training.Module5.Chain.After;

public sealed class AutomaticApprovalHandler : ApprovalHandler
{
    public override Decision Handle(ChangeRequest request)
    {
        if (request.Risk == Risk.Low
            && (request.LinesChanged <= 50 || !request.Production))
        {
            return Decision.Of(Approver.Automatic, "small low-risk change");
        }
        return PassOn(request);
    }
}
