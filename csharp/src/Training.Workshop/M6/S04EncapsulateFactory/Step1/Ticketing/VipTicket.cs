using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step1.Ticketing;

/// <summary>Krok 1: bez zmian. Bilet na miejsce VIP: +10.00. Publiczna klasa z publicznym konstruktorem.</summary>
public sealed class VipTicket : ITicket
{
    private readonly string _title;
    private readonly Money _base;
    private readonly int _row;

    public VipTicket(string title, Money basePrice, int row)
    {
        _title = title;
        _base = basePrice;
        _row = row;
    }

    public Money Price()
    {
        return _base.Plus(Money.Of("10.00"));
    }

    public string Describe()
    {
        return _title + " r" + _row + " VIP " + Price();
    }
}
