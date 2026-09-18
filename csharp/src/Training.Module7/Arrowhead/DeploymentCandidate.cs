namespace Training.Module7.Arrowhead;

public sealed record DeploymentCandidate(
    string? ReleaseId,
    bool Approved,
    bool TestsPassed,
    bool WindowOpen);
