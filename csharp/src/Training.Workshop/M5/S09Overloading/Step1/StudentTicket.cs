using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Step1;

/// <summary>Krok 1: zniżka studencka jako override - dyspozycja dynamiczna, a nie wybór kompilatora.</summary>
public class StudentTicket : Ticket
{
    public StudentTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public override int DiscountPercent => 25;
}
