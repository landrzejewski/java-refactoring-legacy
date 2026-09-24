using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S03TypeCode.Start;

/// <summary>
/// Start: format seansu jako surowy int (1=2D, 2=3D, 3=IMAX). Wiedza o kodzie rozproszona
/// w trzech metodach, a NeedsGlasses w ogóle nie waliduje kodu. Plik CSV przechowuje int.
/// </summary>
public sealed class ScreeningCsv
{
    public const int Format2D = 1;
    public const int Format3D = 2;
    public const int FormatImax = 3;

    /// <summary>Wiersz "tytuł;kod" - np. "Diuna;3".</summary>
    public string Describe(string line)
    {
        var parts = line.Split(';');
        var title = parts[0];
        var formatCode = int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture);
        return title + "|" + Label(formatCode) + "|" + BasePrice(formatCode)
            + "|okulary:" + (NeedsGlasses(formatCode) ? "tak" : "nie")
            + "|csv=" + ToCsv(title, formatCode);
    }

    private static string Label(int formatCode)
    {
        return formatCode switch
        {
            Format2D => "2D",
            Format3D => "3D",
            FormatImax => "IMAX",
            _ => throw new ArgumentException("unknown format code: " + formatCode),
        };
    }

    private static Money BasePrice(int formatCode)
    {
        return formatCode switch
        {
            Format2D => Money.Of("25.00"),
            Format3D => Money.Of("32.00"),
            FormatImax => Money.Of("40.00"),
            _ => throw new ArgumentException("unknown format code: " + formatCode),
        };
    }

    private static bool NeedsGlasses(int formatCode)
    {
        return formatCode == Format3D;
    }

    private static string ToCsv(string title, int formatCode)
    {
        return title + ";" + formatCode;
    }
}
