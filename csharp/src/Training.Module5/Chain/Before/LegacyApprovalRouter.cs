namespace Training.Module5.Chain.Before;

/// <summary>
/// Długi łańcuch <c>if/else</c>: kolejność reguł jest istotna, ale ukryta w jednej metodzie.
/// Dodanie lub przestawienie zatwierdzającego wymaga edycji tej metody.
/// </summary>
public sealed class LegacyApprovalRouter
{
    public Decision Route(ChangeRequest request)
    {
        if (request.LinesChanged > 5_000)
        {
            return Decision.Of(Approver.Rejected, "change too large");
        }
        else if (request.Risk == Risk.Low
                 && (request.LinesChanged <= 50 || !request.Production))
        {
            return Decision.Of(Approver.Automatic, "small low-risk change");
        }
        else if (request.Risk != Risk.High && request.LinesChanged <= 500)
        {
            return Decision.Of(Approver.TeamLead, "moderate change");
        }
        else if (request.Risk != Risk.High || !request.Production)
        {
            return Decision.Of(Approver.ReleaseManager, "large or non-production change");
        }
        else if (request.LinesChanged <= 2_000)
        {
            return Decision.Of(Approver.ChangeAdvisoryBoard, "high-risk production change");
        }
        else
        {
            return Decision.Of(Approver.Rejected, "no approver for " + request.Id);
        }
    }
}
