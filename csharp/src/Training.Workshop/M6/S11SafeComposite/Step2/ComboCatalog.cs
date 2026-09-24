namespace Training.Workshop.M6.S11SafeComposite.Step2;

/// <summary>Krok 2: drzewo zapisane deklaratywnie - kształt kodu to kształt zestawu.</summary>
public sealed class ComboCatalog
{
    public MenuComponent Find(string code) => code switch
    {
        "family" => Combo.Of("Zestaw Rodzinny",
            new Product("Popcorn XL", "24.00"),
            Combo.Of("Napoje",
                new Product("Cola", "9.00"),
                new Product("Cola", "9.00"),
                new Product("Woda", "7.00"))),
        "duo" => Combo.Of("Zestaw Duo",
            new Product("Popcorn L", "18.00"),
            new Product("Cola", "9.00"),
            new Product("Cola", "9.00")),
        "nachos" => new Product("Nachos", "14.00"),
        _ => throw new ArgumentException("unknown combo: " + code),
    };
}
