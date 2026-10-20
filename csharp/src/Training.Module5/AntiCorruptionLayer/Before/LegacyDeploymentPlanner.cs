using System.Globalization;

namespace Training.Module5.AntiCorruptionLayer.Before;

/// <summary>
/// Logika domenowa mówi językiem CMDB: porównuje kody <c>"P"</c>, flagi <c>1</c>
/// i parsuje daty. Każda zmiana w modelu CMDB przecieka do reguł planowania.
/// </summary>
public sealed class LegacyDeploymentPlanner
{
    private const string CmdbDate = "yyyyMMdd";

    private readonly LegacyCmdbClient _cmdb;

    public LegacyDeploymentPlanner(LegacyCmdbClient cmdb)
    {
        ArgumentNullException.ThrowIfNull(cmdb);
        _cmdb = cmdb;
    }

    public IReadOnlyList<string> CanaryTargets() =>
        _cmdb()
            .Where(record => record.StatFlg == 1)
            .Where(record => record.EnvCd == "T" || record.EnvCd == "D")
            .Select(record => record.Nm)
            .Order(StringComparer.Ordinal)
            .ToList();

    public IReadOnlyList<string> StaleEnvironments(DateOnly today)
    {
        var threshold = today.AddDays(-30);
        return _cmdb()
            .Where(record => record.StatFlg == 1)
            .Where(record => DateOnly.ParseExact(record.LstDplyDt, CmdbDate, CultureInfo.InvariantCulture) < threshold)
            .Select(record => record.Nm)
            .Order(StringComparer.Ordinal)
            .ToList();
    }

    public bool IsProduction(string name) =>
        _cmdb().Any(record => record.Nm == name && record.EnvCd == "P");
}
