using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step3;

/// <summary>Krok 3: override rzucający NotSupportedException zniknął - nie ma czego odmawiać.</summary>
public sealed class StudentTicket : Ticket
{
    public StudentTicket(Money basePrice) : base(basePrice)
    {
    }

    protected override int DiscountPercent()
    {
        return 25;
    }
}
