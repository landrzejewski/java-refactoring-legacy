using System.Globalization;

namespace Training.Module5.BoundaryObject.Before;

/// <summary>
/// Usługa domenowa, która pracuje bezpośrednio na surowym ładunku z CI. Parsowanie
/// i nazwy kluczy są rozsiane po metodach, a błędny ładunek wybucha głęboko w domenie
/// (Java: <c>NumberFormatException</c>, <c>NullPointerException</c>; tu
/// <see cref="FormatException"/>, <see cref="KeyNotFoundException"/>).
/// </summary>
public sealed class LegacyReleaseGate
{
    private readonly LegacyCiClient _ciClient;

    public LegacyReleaseGate(LegacyCiClient ciClient)
    {
        ArgumentNullException.ThrowIfNull(ciClient);
        _ciClient = ciClient;
    }

    public bool CanRelease(string buildId)
    {
        var payload = _ciClient(buildId);
        var succeeded = "SUCCESS" == payload["status"].Trim().ToUpperInvariant();
        var failed = int.Parse(payload["failed"].Trim(), NumberStyles.AllowLeadingSign, CultureInfo.InvariantCulture);
        var coverage = double.Parse(payload["coverage"].Trim(), NumberStyles.Float, CultureInfo.InvariantCulture);
        return succeeded && failed == 0 && coverage >= 80.0;
    }

    public string Summary(string buildId)
    {
        var payload = _ciClient(buildId);
        return payload["id"].Trim()
            + " [" + payload["commit"].Trim().ToLowerInvariant() + "] "
            + payload["status"].Trim().ToUpperInvariant()
            + " coverage=" + JavaDouble(double.Parse(payload["coverage"].Trim(), NumberStyles.Float, CultureInfo.InvariantCulture)) + "%"
            + " failed=" + int.Parse(payload["failed"].Trim(), NumberStyles.AllowLeadingSign, CultureInfo.InvariantCulture);
    }

    // Java Double.toString: always at least one fractional digit (80 -> "80.0").
    private static string JavaDouble(double value)
    {
        var text = value.ToString(CultureInfo.InvariantCulture);
        return text.Contains('.') || text.Contains('E') ? text : text + ".0";
    }
}
