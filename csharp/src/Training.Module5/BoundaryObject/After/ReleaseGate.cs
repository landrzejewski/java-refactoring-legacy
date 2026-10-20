using System.Globalization;

namespace Training.Module5.BoundaryObject.After;

/// <summary>
/// Usługa domenowa operująca wyłącznie na <see cref="BuildReport"/>.
/// </summary>
public sealed class ReleaseGate
{
    private readonly IBuildReports _reports;

    public ReleaseGate(IBuildReports reports)
    {
        ArgumentNullException.ThrowIfNull(reports);
        _reports = reports;
    }

    public bool CanRelease(string buildId)
    {
        var report = _reports.Find(buildId);
        return report.Succeeded() && report.FailedTests == 0 && report.Coverage >= 80.0;
    }

    public string Summary(string buildId)
    {
        var report = _reports.Find(buildId);
        return report.BuildId
            + " [" + report.Commit + "] "
            + report.Status.ToString().ToUpperInvariant()
            + " coverage=" + JavaDouble(report.Coverage) + "%"
            + " failed=" + report.FailedTests;
    }

    // Java Double.toString: always at least one fractional digit (80 -> "80.0").
    private static string JavaDouble(double value)
    {
        var text = value.ToString(CultureInfo.InvariantCulture);
        return text.Contains('.') || text.Contains('E') ? text : text + ".0";
    }
}
