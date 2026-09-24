namespace Training.Workshop.M3.S08Ocp.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): nowy format 4DX (45.00, okulary 3D) to jedna nowa stała.
/// ScreeningOffer nie zmieniła się ani o znak - to jest OCP na wybranej osi.
/// </summary>
public sealed class Format
{
    // pierwsze w kolejności inicjalizacji: każda instancja dopisuje się tu w konstruktorze
    private static readonly List<Format> All = [];

    public static readonly Format TwoD = new("2D", 25.00m, false, "2D");
    public static readonly Format ThreeD = new("3D", 32.00m, true, "3D - okulary");
    public static readonly Format Imax = new("IMAX", 40.00m, false, "IMAX - ekran laserowy");
    public static readonly Format FourDx = new("4DX", 45.00m, true, "4DX - ruchome fotele");

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
