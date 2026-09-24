using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step3;

/// <summary>Krok 3: bez zmian.</summary>
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
