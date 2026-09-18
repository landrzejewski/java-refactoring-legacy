namespace Training.Module6.TypeCode.Before;

public sealed record LegacyDeploymentRequest
{
    public LegacyDeploymentRequest(string? releaseId, string? zoneCode)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        if (string.IsNullOrWhiteSpace(zoneCode))
        {
            throw new ArgumentException("zoneCode must not be blank");
        }
        zoneCode = zoneCode.ToUpperInvariant();
        if (zoneCode != "TEST" && zoneCode != "PROD" && zoneCode != "DR")
        {
            throw new ArgumentException("unknown zone code: " + zoneCode);
        }
        ReleaseId = releaseId;
        ZoneCode = zoneCode;
    }

    public string ReleaseId { get; }

    public string ZoneCode { get; }

    public bool RequiresApproval() => ZoneCode == "PROD" || ZoneCode == "DR";
}
