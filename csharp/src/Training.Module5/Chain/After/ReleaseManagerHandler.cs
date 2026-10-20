namespace Training.Module5.Chain.After;

public sealed class ReleaseManagerHandler : ApprovalHandler
{
    public override Decision Handle(ChangeRequest request)
    {
        if (request.Risk != Risk.High || !request.Production)
        {
            return Decision.Of(Approver.ReleaseManager, "large or non-production change");
        }
        return PassOn(request);
    }
}
