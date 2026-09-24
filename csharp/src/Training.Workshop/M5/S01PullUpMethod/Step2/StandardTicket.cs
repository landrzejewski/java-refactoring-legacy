using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step2;

/// <summary>Krok 2: Price() nadpisuje teraz metodę abstrakcyjną z Ticket (override).</summary>
public sealed class StandardTicket : Ticket
{
    public StandardTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public override Money Price()
    {
        return BasePrice;
    }

    public string Label()
    {
        return Title + ": " + Price();
    }
}
