using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Start;

/// <summary>Start: bilet studencki - zniżka zapisana nie tutaj, lecz w przeciążeniu PriceList.Price(StudentTicket).</summary>
public class StudentTicket : Ticket
{
    public StudentTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }
}
