using System.Globalization;

namespace Training.Module5.ExtractSubclass;

/// <summary>
/// Formats a point in time like Java's <c>Instant.toString()</c> (ISO-8601, UTC, e.g. <c>2030-06-15T10:15:30Z</c>).
/// </summary>
internal static class InstantFormat
{
    public static string ToIsoString(DateTimeOffset instant)
    {
        return instant.UtcDateTime.ToString(
            "yyyy-MM-dd'T'HH:mm:ss.FFFFFFF'Z'",
            CultureInfo.InvariantCulture);
    }
}
