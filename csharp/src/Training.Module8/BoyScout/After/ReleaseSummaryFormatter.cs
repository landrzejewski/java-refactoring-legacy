using System.Text;

namespace Training.Module8.BoyScout.After;

public sealed class ReleaseSummaryFormatter
{
    public string Format(
        string releaseId,
        IReadOnlyList<DeploymentResult> results)
    {
        Validate(releaseId, results);

        var summary = new StringBuilder()
            .Append("Release ")
            .Append(releaseId.Trim())
            .Append('\n');
        int successfulDeployments = 0;
        foreach (DeploymentResult result in results)
        {
            ArgumentNullException.ThrowIfNull(result, "result");
            summary.Append(FormatResult(result)).Append('\n');
            if (result.Status == DeploymentStatus.Success)
            {
                successfulDeployments++;
            }
        }
        return summary.Append("Successful: ")
            .Append(successfulDeployments)
            .Append('/')
            .Append(results.Count)
            .ToString();
    }

    private static void Validate(
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
    }

    private static string FormatResult(DeploymentResult result)
    {
        string label = result.Status == DeploymentStatus.Success
            ? "[OK]"
            : "[ERROR]";
        return label + " " + result.Environment + ": "
            + result.Description;
    }
}
