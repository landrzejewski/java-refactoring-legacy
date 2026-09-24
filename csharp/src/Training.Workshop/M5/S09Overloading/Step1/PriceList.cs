using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Step1;

/// <summary>Krok 1: jedno Price(Ticket) - przeciążenie dla StudentTicket usunięte (Safe Delete).</summary>
public sealed class PriceList
{
    public Money Price(Ticket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(ticket.DiscountPercent));
    }
}
