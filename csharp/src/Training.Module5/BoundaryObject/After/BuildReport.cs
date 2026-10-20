using System.Text.RegularExpressions;

namespace Training.Module5.BoundaryObject.After;

/// <summary>
/// Obiekt graniczny (Boundary Object): niezmienny, zwalidowany obraz wyniku buildu.
/// Jeśli istnieje, jest poprawny - domena nie musi już niczego parsować ani sprawdzać.
/// </summary>
public sealed record BuildReport
{
    private static readonly Regex CommitPattern = new("^[0-9a-f]{7,40}\\z");

    public BuildReport(string? buildId, Status status, double coverage, int failedTests, string? commit)
    {
        if (string.IsNullOrWhiteSpace(buildId))
        {
            throw new ArgumentException("buildId must not be blank");
        }
        if (coverage < 0 || coverage > 100)
        {
            throw new ArgumentException("coverage must be between 0 and 100");
        }
        if (failedTests < 0)
        {
            throw new ArgumentException("failedTests must not be negative");
        }
        if (commit is null || !CommitPattern.IsMatch(commit))
        {
            throw new ArgumentException("commit must be a hex sha");
        }
        BuildId = buildId;
        Status = status;
        Coverage = coverage;
        FailedTests = failedTests;
        Commit = commit;
    }

    public string BuildId { get; }

    public Status Status { get; }

    public double Coverage { get; }

    public int FailedTests { get; }

    public string Commit { get; }

    public bool Succeeded() => Status == Status.Success;
}
