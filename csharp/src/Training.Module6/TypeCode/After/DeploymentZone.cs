using System.Collections.Frozen;

namespace Training.Module6.TypeCode.After;

public sealed class DeploymentZone
{
    public static readonly DeploymentZone Test = new("TEST", false);
    public static readonly DeploymentZone Production = new("PROD", true);
    public static readonly DeploymentZone DisasterRecovery = new("DR", true);

    private static readonly FrozenDictionary<string, DeploymentZone> ByCode =
        new[] { Test, Production, DisasterRecovery }
            .ToFrozenDictionary(zone => zone.Code, StringComparer.Ordinal);

    private readonly bool _approvalRequired;

    private DeploymentZone(string code, bool approvalRequired)
    {
        Code = code;
        _approvalRequired = approvalRequired;
    }

    public string Code { get; }

    public static DeploymentZone FromCode(string? code)
    {
        if (string.IsNullOrWhiteSpace(code))
        {
            throw new ArgumentException("zoneCode must not be blank");
        }
        var normalized = code.ToUpperInvariant();
        if (!ByCode.TryGetValue(normalized, out var zone))
        {
            throw new ArgumentException("unknown zone code: " + normalized);
        }
        return zone;
    }

    public bool RequiresApproval() => _approvalRequired;

    public override string ToString() => Code;
}
