using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step1.Ticketing;

/// <summary>Krok 1: bez zmian. Bilet na zwykłe miejsce. Publiczny konstruktor - klienci robią new.</summary>
public sealed class StandardTicket : ITicket
{
    private readonly string _title;
    private readonly Money _base;
    private readonly int _row;

    public StandardTicket(string title, Money basePrice, int row)
    {
        _title = title;
        _base = basePrice;
        _row = row;
    }

    public Money Price()
    {
        return _base;
    }

    public string Describe()
    {
        return _title + " r" + _row + " " + Price();
    }
}
