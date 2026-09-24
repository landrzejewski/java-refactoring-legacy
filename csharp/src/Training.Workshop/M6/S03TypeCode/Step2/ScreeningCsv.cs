using System.Globalization;

namespace Training.Workshop.M6.S03TypeCode.Step2;

/// <summary>Krok 2: klient pyta obiekt formatu zamiast wykonywać switch.</summary>
public sealed class ScreeningCsv
{
    /// <summary>Wiersz "tytuł;kod" - np. "Diuna;3".</summary>
    public string Describe(string line)
    {
        var parts = line.Split(';');
        var title = parts[0];
        var format = Format.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
        return title + "|" + format.Label + "|" + format.BasePrice
            + "|okulary:" + (format.RequiresGlasses ? "tak" : "nie")
            + "|csv=" + title + ";" + format.Code;
    }
}
