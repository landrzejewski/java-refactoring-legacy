using System.Globalization;

namespace Training.Patterns;

/// <summary>
/// Helpers reproducing Java's <c>toString()</c> conventions where they differ from .NET
/// (<c>double</c> always has a fractional part, <c>boolean</c> is lowercase, <c>LocalDateTime</c> ISO format).
/// </summary>
public static class JavaText
{
    public static string Of(double value)
    {
        if (double.IsNaN(value))
        {
            return "NaN";
        }
        if (double.IsInfinity(value))
        {
            return value > 0 ? "Infinity" : "-Infinity";
        }
        var magnitude = Math.Abs(value);
        if (magnitude == 0 || (magnitude >= 1e-3 && magnitude < 1e7))
        {
            var text = value.ToString("R", CultureInfo.InvariantCulture);
            if (text == "-0")
            {
                return "-0.0";
            }
            return text.Contains('.') ? text : text + ".0";
        }
        return value.ToString("0.0################E0", CultureInfo.InvariantCulture);
    }

    public static string Of(bool value) => value ? "true" : "false";

    public static string Of(DateTime localDateTime)
    {
        var text = localDateTime.ToString("yyyy-MM-dd'T'HH:mm", CultureInfo.InvariantCulture);
        var nanos = localDateTime.Ticks % TimeSpan.TicksPerSecond * 100;
        if (localDateTime.Second == 0 && nanos == 0)
        {
            return text;
        }
        text += ":" + localDateTime.Second.ToString("00", CultureInfo.InvariantCulture);
        if (nanos == 0)
        {
            return text;
        }
        if (nanos % 1_000_000 == 0)
        {
            return text + "." + (nanos / 1_000_000).ToString("000", CultureInfo.InvariantCulture);
        }
        if (nanos % 1_000 == 0)
        {
            return text + "." + (nanos / 1_000).ToString("000000", CultureInfo.InvariantCulture);
        }
        return text + "." + nanos.ToString("000000000", CultureInfo.InvariantCulture);
    }
}
