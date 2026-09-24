using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step2;

/// <summary>Krok 2: Price() odziedziczone z Ticket, tu tylko zniżka.</summary>
public sealed class StudentTicket : Ticket
{
    public StudentTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    protected override int DiscountPercent()
    {
        return 25;
    }
}
