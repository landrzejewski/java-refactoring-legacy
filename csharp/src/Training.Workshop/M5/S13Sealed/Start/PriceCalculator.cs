using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Start;

/// <summary>
/// Start: łańcuch <c>is</c> zakończony cichym "return 0". Nowy typ biletu (np. dziecięcy)
/// skompiluje się bez słowa i dostanie 0% zniżki.
/// </summary>
public sealed class PriceCalculator
{
    public int DiscountPercent(Ticket ticket)
    {
        if (ticket is StudentTicket)
        {
            return 25;
        }
        else if (ticket is SeniorTicket)
        {
            return 30;
        }
        return 0;
    }

    public Money Price(Ticket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(DiscountPercent(ticket)));
    }
}
