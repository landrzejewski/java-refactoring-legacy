namespace Training.Workshop.M6.S03TypeCode.Step3;

/// <summary>
/// Krok 3: mapper migracyjny int &lt;-&gt; Format. Baza i CSV zostają przy kodzie int;
/// gdy kiedyś przejdziemy na kody tekstowe, zmieni się tylko ta klasa.
/// </summary>
public static class FormatCodes
{
    private static readonly IReadOnlyDictionary<Format, int> Codes = new Dictionary<Format, int>
    {
        [Format.TwoD] = 1,
        [Format.ThreeD] = 2,
        [Format.Imax] = 3,
    };

    public static Format FromCode(int code)
    {
        foreach (var entry in Codes)
        {
            if (entry.Value == code)
            {
                return entry.Key;
            }
        }
        throw new ArgumentException("unknown format code: " + code);
    }

    public static int ToCode(Format format)
    {
        return Codes[format];
    }
}
