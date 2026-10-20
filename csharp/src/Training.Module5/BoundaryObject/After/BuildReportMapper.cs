using System.Globalization;

namespace Training.Module5.BoundaryObject.After;

/// <summary>
/// Jedyne miejsce, które zna format ładunku CI. Tłumaczy go na <see cref="BuildReport"/>
/// albo odrzuca na granicy z jednym, czytelnym wyjątkiem.
/// </summary>
internal static class BuildReportMapper
{
    internal static BuildReport FromPayload(IReadOnlyDictionary<string, string>? payload)
    {
        if (payload is null)
        {
            throw new ArgumentException("invalid CI payload: missing");
        }
        try
        {
            return new BuildReport(
                Required(payload, "id"),
                ParseStatus(Required(payload, "status")),
                double.Parse(Required(payload, "coverage"), NumberStyles.Float, CultureInfo.InvariantCulture),
                int.Parse(Required(payload, "failed"), NumberStyles.AllowLeadingSign, CultureInfo.InvariantCulture),
                Required(payload, "commit").ToLowerInvariant());
        }
        // Java: NumberFormatException is an IllegalArgumentException; .NET FormatException is not.
        catch (Exception exception) when (exception is ArgumentException or FormatException)
        {
            throw new ArgumentException(
                "invalid CI payload: " + exception.Message, exception);
        }
    }

    private static Status ParseStatus(string text)
    {
        var name = text.ToUpperInvariant();
        foreach (var status in Enum.GetValues<Status>())
        {
            if (status.ToString().ToUpperInvariant() == name)
            {
                return status;
            }
        }
        throw new ArgumentException("unknown status: " + name);
    }

    private static string Required(IReadOnlyDictionary<string, string> payload, string key)
    {
        if (!payload.TryGetValue(key, out var value) || string.IsNullOrWhiteSpace(value))
        {
            throw new ArgumentException("missing " + key);
        }
        return value.Trim();
    }
}
