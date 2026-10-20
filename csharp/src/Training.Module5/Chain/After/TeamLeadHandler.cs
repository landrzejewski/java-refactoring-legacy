namespace Training.Module5.Chain.After;

public sealed class TeamLeadHandler : ApprovalHandler
{
    public override Decision Handle(ChangeRequest request)
    {
        if (request.Risk != Risk.High && request.LinesChanged <= 500)
        {
            return Decision.Of(Approver.TeamLead, "moderate change");
        }
        return PassOn(request);
    }
}
