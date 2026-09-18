namespace Training.Module8.BoyScout.Before;

public sealed class ReleaseSummaryFormatter
{
    public string Format(
        string releaseId,
        IReadOnlyList<DeploymentResult> results)
    {
        ArgumentNullException.ThrowIfNull(releaseId, "releaseId");
        ArgumentNullException.ThrowIfNull(results, "results");
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException(
                "releaseId must not be blank");
        }
        if (results.Count == 0)
        {
            throw new ArgumentException(
                "results must not be empty");
        }

        string s = "Release " + releaseId.Trim() + "\n";
        int n = 0;
        foreach (DeploymentResult r in results)
        {
            ArgumentNullException.ThrowIfNull(r, "result");
            if (r.Status == DeploymentStatus.Success)
            {
                s = s + "[OK] " + r.Environment + ": "
                    + r.Description + "\n";
                n++;
            }
            else
            {
                s = s + "[ERROR] " + r.Environment + ": "
                    + r.Description + "\n";
            }
        }
        return s + "Successful: " + n + "/" + results.Count;
    }
}
