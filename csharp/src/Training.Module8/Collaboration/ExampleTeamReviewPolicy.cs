namespace Training.Module8.Collaboration;

/// <summary>
/// An example team policy, not a universal definition of review readiness.
/// </summary>
public sealed class ExampleTeamReviewPolicy
{
    public ReviewReadiness Assess(ChangeSet changeSet)
    {
        ArgumentNullException.ThrowIfNull(changeSet);

        var problems = new List<ReadinessProblem>();
        if (changeSet.Intents.Count == 0)
        {
            problems.Add(ReadinessProblem.MissingIntent);
        }
        else if (changeSet.Intents.Count > 1)
        {
            problems.Add(ReadinessProblem.MixedPrimaryIntents);
        }
        if (changeSet.VerificationEvidence.IsEmpty)
        {
            problems.Add(ReadinessProblem.MissingVerificationEvidence);
        }
        if (!changeSet.IndependentlyGreenBuild)
        {
            problems.Add(ReadinessProblem.BuildNotIndependentlyGreen);
        }
        return new ReviewReadiness(problems);
    }
}
