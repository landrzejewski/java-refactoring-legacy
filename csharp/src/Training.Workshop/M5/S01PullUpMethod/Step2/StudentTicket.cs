using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step2;

/// <summary>Krok 2: Price() nadpisuje teraz metodę abstrakcyjną z Ticket (override).</summary>
public sealed class StudentTicket : Ticket
{
    public StudentTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public override Money Price()
    {
        return BasePrice.Minus(BasePrice.Percent(25));
    }

    public string Label()
    {
        return Title + ": " + Price();
    }
}
