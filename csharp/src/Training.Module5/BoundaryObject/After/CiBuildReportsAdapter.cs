namespace Training.Module5.BoundaryObject.After;

/// <summary>
/// Adapter na granicy: implementuje port domenowy, woła zewnętrzny klient i od razu
/// zamienia ładunek na obiekt graniczny.
/// </summary>
public sealed class CiBuildReportsAdapter : IBuildReports
{
    private readonly LegacyCiClient _ciClient;

    public CiBuildReportsAdapter(LegacyCiClient ciClient)
    {
        ArgumentNullException.ThrowIfNull(ciClient);
        _ciClient = ciClient;
    }

    public BuildReport Find(string buildId) => BuildReportMapper.FromPayload(_ciClient(buildId));
}
