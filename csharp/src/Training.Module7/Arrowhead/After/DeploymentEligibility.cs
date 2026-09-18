namespace Training.Module7.Arrowhead.After;

public sealed class DeploymentEligibility
{
    public Eligibility Evaluate(DeploymentCandidate? candidate)
    {
        if (candidate is null)
        {
            return Eligibility.MissingCandidate;
        }
        if (string.IsNullOrWhiteSpace(candidate.ReleaseId))
        {
            return Eligibility.InvalidReleaseId;
        }
        if (!candidate.Approved)
        {
            return Eligibility.NotApproved;
        }
        if (!candidate.TestsPassed)
        {
            return Eligibility.TestsFailed;
        }
        if (!candidate.WindowOpen)
        {
            return Eligibility.WindowClosed;
        }
        return Eligibility.Eligible;
    }
}
