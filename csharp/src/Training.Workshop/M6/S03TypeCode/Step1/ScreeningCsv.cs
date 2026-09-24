using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S03TypeCode.Step1;

/// <summary>
/// Krok 1: Replace Type Code with Class - int zamieniany na Format na granicy (odczyt CSV),
/// metody przyjmują Format. Do CSV nadal trafia ten sam int (format.Code).
/// </summary>
public sealed class ScreeningCsv
{
    /// <summary>Wiersz "tytuł;kod" - np. "Diuna;3".</summary>
    public string Describe(string line)
    {
        var parts = line.Split(';');
        var title = parts[0];
        var format = Format.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
        return title + "|" + Label(format) + "|" + BasePrice(format)
            + "|okulary:" + (NeedsGlasses(format) ? "tak" : "nie")
            + "|csv=" + ToCsv(title, format);
    }

    private static string Label(Format format)
    {
        return format switch
        {
            Format.TwoD => "2D",
            Format.ThreeD => "3D",
            Format.Imax => "IMAX",
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };
    }

    private static Money BasePrice(Format format)
    {
        return format switch
        {
            Format.TwoD => Money.Of("25.00"),
            Format.ThreeD => Money.Of("32.00"),
            Format.Imax => Money.Of("40.00"),
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };
    }

    private static bool NeedsGlasses(Format format)
    {
        return format == Format.ThreeD;
    }

    private static string ToCsv(string title, Format format)
    {
        return title + ";" + format.Code;
    }
}
