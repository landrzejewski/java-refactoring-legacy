namespace Training.Module7.Arrowhead.Before;

public sealed class LegacyDeploymentEligibility
{
    public Eligibility Evaluate(DeploymentCandidate? candidate)
    {
        Eligibility result;
        if (candidate != null)
        {
            if (candidate.ReleaseId != null
                && !string.IsNullOrWhiteSpace(candidate.ReleaseId))
            {
                if (candidate.Approved)
                {
                    if (candidate.TestsPassed)
                    {
                        if (candidate.WindowOpen)
                        {
                            result = Eligibility.Eligible;
                        }
                        else
                        {
                            result = Eligibility.WindowClosed;
                        }
                    }
                    else
                    {
                        result = Eligibility.TestsFailed;
                    }
                }
                else
                {
                    result = Eligibility.NotApproved;
                }
            }
            else
            {
                result = Eligibility.InvalidReleaseId;
            }
        }
        else
        {
            result = Eligibility.MissingCandidate;
        }
        return result;
    }
}
