namespace Training.Workshop.M3.S08Ocp.Step2;

/// <summary>
/// Krok 2: wiedza o formacie przeniesiona do typu formatu (Move Method / Replace Conditional
/// with Polymorphism w wersji "dane zamiast gałęzi"). Nowy format = jedna linia tutaj.
/// Enum C# nie niesie danych, więc Format staje się klasą ze stałymi instancjami.
/// </summary>
public sealed class Format
{
    // pierwsze w kolejności inicjalizacji: każda instancja dopisuje się tu w konstruktorze
    private static readonly List<Format> All = [];

    public static readonly Format TwoD = new("2D", 25.00m, false, "2D");
    public static readonly Format ThreeD = new("3D", 32.00m, true, "3D - okulary");
    public static readonly Format Imax = new("IMAX", 40.00m, false, "IMAX - ekran laserowy");

    private readonly string _code;

    private Format(string code, decimal basePrice, bool needsGlasses, string label)
    {
        _code = code;
        BasePrice = basePrice;
        NeedsGlasses = needsGlasses;
        Label = label;
        All.Add(this);
    }

    public static IReadOnlyList<Format> Values => All.AsReadOnly();

    public decimal BasePrice { get; }

    public bool NeedsGlasses { get; }

    public string Label { get; }

    public static Format Parse(string code)
    {
        foreach (var format in Values)
        {
            if (format._code == code)
            {
                return format;
            }
        }
        throw new ArgumentException("nieznany format: " + code);
    }
}
