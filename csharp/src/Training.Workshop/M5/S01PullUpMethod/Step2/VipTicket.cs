using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step2;

/// <summary>Krok 2: Price() nadpisuje teraz metodę abstrakcyjną z Ticket (override).</summary>
public sealed class VipTicket : Ticket
{
    public VipTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public override Money Price()
    {
        return BasePrice.Plus(Money.Of("10.00"));
    }

    public string Label()
    {
        return Title + ": " + Price();
    }
}
