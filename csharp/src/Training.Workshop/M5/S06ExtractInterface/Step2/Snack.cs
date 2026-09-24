using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Step2;

/// <summary>Krok 2: bez zmian.</summary>
public sealed class Snack : IPriceable
{
    private readonly string _name;
    private readonly Money _price;

    public Snack(string name, Money price)
    {
        _name = name;
        _price = price;
    }

    public string Name => _name;

    public Money Price => _price;

    /// <summary>Bar (popcorn, napoje): VAT 23%.</summary>
    public int VatPercent => 23;
}
