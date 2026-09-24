using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Step2;

/// <summary>Krok 2: bez zmian.</summary>
public class StudentTicket : Ticket
{
    public StudentTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public override int DiscountPercent => 25;
}
