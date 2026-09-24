using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Step2;

/// <summary>Krok 2: bez zmian.</summary>
public sealed class PriceList
{
    public Money Price(Ticket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(ticket.DiscountPercent));
    }
}
