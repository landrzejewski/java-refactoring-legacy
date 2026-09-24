using System.Globalization;

namespace Training.Workshop.M6.S03TypeCode.Step3;

/// <summary>Krok 3: CSV rozmawia z mapperem, reszta kodu wyłącznie z typem Format.</summary>
public sealed class ScreeningCsv
{
    /// <summary>Wiersz "tytuł;kod" - np. "Diuna;3".</summary>
    public string Describe(string line)
    {
        var parts = line.Split(';');
        var title = parts[0];
        var format = FormatCodes.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
        return title + "|" + format.Label + "|" + format.BasePrice
            + "|okulary:" + (format.RequiresGlasses ? "tak" : "nie")
            + "|csv=" + title + ";" + FormatCodes.ToCode(format);
    }
}
